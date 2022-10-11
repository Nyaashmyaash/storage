package com.cdek.storage.infrastructure.service.fix.helper;

import com.cdek.cargoplacelogisticstatus.common.domain.OrderCargoPlaceLogisticStatus;
import com.cdek.cargoplacelogisticstatus.common.dto.CargoPlaceStatusHistoryDto;
import com.cdek.catalog.common.entity.TariffMode;
import com.cdek.storage.application.ports.input.CalcStoragePeriodInDays;
import com.cdek.storage.application.ports.output.OrderRepository;
import com.cdek.storage.application.ports.output.OrderStorageRepository;
import com.cdek.storage.application.ports.output.PackageRepository;
import com.cdek.storage.application.service.helper.CalcDeadlineHelper;
import com.cdek.storage.application.service.helper.TariffModeLists;
import com.cdek.storage.buffer.ports.output.OrderCargoPlaceStatusBufferRepository;
import com.cdek.storage.infrastructure.converter.order.CargoPlaceStatusConverter;
import com.cdek.storage.infrastructure.service.CargoplaceLogisticStatusClient;
import com.cdek.storage.infrastructure.stream.publisher.OrderStorageEventPublisher;
import com.cdek.storage.model.order.CargoPlaceStatus;
import com.cdek.storage.model.order.Package;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FixOrderStorageAndCreateIfNeedHelper {

    private final OrderStorageRepository orderStorageRepository;
    private final CalcDeadlineHelper calcDeadlineHelper;
    private final OrderStorageEventPublisher eventPublisher;
    private final OrderRepository orderRepository;
    private final PackageRepository packageRepository;
    private final CargoplaceLogisticStatusClient logisticStatusClient;
    private final CargoPlaceStatusConverter converter;
    private final OrderCargoPlaceStatusBufferRepository statusRepository;
    private final CalcStoragePeriodInDays calcStoragePeriodInDays;

    public void analyseStatusesOfPackagesAndCalcDateOfReceiptAndCreateStorageIfNeed(@Nonnull String orderNumber,
            @Nullable String tariffModeCode) {
        final var orderUuid = orderRepository.getOrderUuidByOrderNumber(orderNumber);
        final var packageList = packageRepository.getNotDeletedPackagesByOrderUuid(orderUuid);
        List<CargoPlaceStatusHistoryDto> correctStatusListOfAllPackages = new ArrayList<>();

        final var finalTariffModeCode = Optional.ofNullable(tariffModeCode)
                .orElseGet(() -> orderRepository.getTrueDeliveryModeCodeByOrderUuid(orderUuid));

        createStoragePeriodIfNotExist(orderUuid);

        for (Package pack : packageList) {
            final var logisticStatusHistoryOfPackage =
                    Objects.requireNonNull(
                            logisticStatusClient.getCargoplaceLogisticStatusByPackageUuid(pack.getPackageUuid())
                                    .getBody())
                            .getCargoPlaceStatusHistoryDtos();

            var correctStatusListOfPack = logisticStatusHistoryOfPackage.stream()
                    .sorted(Comparator.comparing(CargoPlaceStatusHistoryDto::getTimestamp))
                    .filter(status -> status.getLocationOfficeUuid() != null)
                    .filter(st -> getStatusByTariffModeCode(finalTariffModeCode).equals(st.getStatus()))
                    .collect(Collectors.toList());

            if (!correctStatusListOfPack.isEmpty()) {
                correctStatusListOfAllPackages.add(correctStatusListOfPack.get(0));
            } else {
                log.info(
                        "Not all packages of order with number {} has correct status for calculating arrival date. " +
                                "Pack uuid {}.", orderNumber, pack.getPackageUuid());
                return;
            }
        }

        var statusListOfAllPackages = correctStatusListOfAllPackages.stream()
                .sorted(Comparator.comparing(CargoPlaceStatusHistoryDto::getTimestamp))
                .map(converter::fromDto)
                .collect(Collectors.toList());

        calcDeadlineOfStoragePeriodAndPublishEvent(statusListOfAllPackages);
    }

    private void createStoragePeriodIfNotExist(@Nonnull String orderUuid) {
        if (!orderStorageRepository.isOrderStorageExists(orderUuid)) {
            var order = orderRepository.getOrderByUuid(orderUuid);
            calcStoragePeriodInDays.calcStoragePeriod(order);
        }
    }

    private OrderCargoPlaceLogisticStatus getStatusByTariffModeCode(@Nonnull String tariffModeCode) {
        if (TariffModeLists.getTariffModeToDoorList().contains(tariffModeCode)) {
            return OrderCargoPlaceLogisticStatus.ACCEPTED_AT_DELIVERY_WAREHOUSE;
        } else if (TariffModeLists.getTariffModeToWarehouseList().contains(tariffModeCode)) {
            return OrderCargoPlaceLogisticStatus.ACCEPTED_AT_WAREHOUSE_ON_DEMAND;
        } else {
            return OrderCargoPlaceLogisticStatus.POSTOMAT_POSTED;
        }
    }

    private void calcDeadlineOfStoragePeriodAndPublishEvent(@Nonnull List<CargoPlaceStatus> statusListOfAllPackages) {
        final var orderUuid = statusListOfAllPackages.get(0).getOrderId();
        var orderStorage = orderStorageRepository.getOrderStorageByOrderUuid(orderUuid);
        final int numberOfLastStatus = statusListOfAllPackages.size() - 1;
        statusListOfAllPackages.forEach(this::saveOrUpdateStatusInDb);

        orderStorage.setDateOfReceiptInDeliveryOfficeOrPostamat(
                statusListOfAllPackages.get(numberOfLastStatus).getTimestamp());
        final var newLastDate = calcDeadlineHelper.calcDeadlineOfStoragePeriod(orderStorage);

        if (!newLastDate.equals(orderStorage.getDeadlineForStorage())) {
            orderStorage.setDeadlineForStorage(newLastDate);
            orderStorage.setTimestamp(Instant.now());

            orderStorageRepository.updateOrderStorage(orderStorage);
            eventPublisher.publish(orderStorage);
        }
    }

    private void saveOrUpdateStatusInDb(@Nonnull CargoPlaceStatus status) {
        final var current = statusRepository.getTimestamp(status.getStatusUuid());
        if (current == null) {
            statusRepository.saveNewStatus(status);
        } else {
            statusRepository.updateStatus(status);
        }
    }
}
