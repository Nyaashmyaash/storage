package com.cdek.storage.application.service.helper;

import com.cdek.cargoplacelogisticstatus.common.domain.OrderCargoPlaceLogisticStatus;
import com.cdek.storage.application.ports.output.OrderCargoPlaceStatusRepository;
import com.cdek.storage.buffer.ports.input.PostamatStatusRefresher;
import com.cdek.storage.model.order.CargoPlaceStatus;
import com.cdek.storage.model.order.Order;
import com.cdek.storage.model.order.Package;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CalcHelper {

    private static final Integer STANDARD_SHELF_LIFE_FOR_POSTAMAT = 3;
    private static final Integer STANDARD_SHELF_LIFE_FOR_DELIVERY = 7;

    private final OrderCargoPlaceStatusRepository placeStatusRepository;
    private final CalcIndividualConditionsHelper calcIndividualConditionsHelper;

    public int calcCountDay(@Nonnull Order order) {
        String deliveryMode = order.getTrueDeliveryModeCode();
        var calcCountDay = 0;
        var individualConditions = calcIndividualConditionsHelper.getIndividualConditionsForFreeStoragePeriod(order);

        //Если режим доставки  "До постамата",
        // то срок хранения = индивидуальные условия хранения из договора, либо 3 дня.
        if (TariffModeLists.getTariffModeToPostamatList().contains(deliveryMode)) {
            if (individualConditions != 0) {
                calcCountDay = individualConditions;
            } else {
                calcCountDay = STANDARD_SHELF_LIFE_FOR_POSTAMAT;
            }
        }

        //Если режим доставки "до двери" или "до склада",
        // то срок хранения = индивидуальные условия хранения из договора, либо 7 дней.
        if (TariffModeLists.getTariffModeToDoorOrToWarehouseList().contains(deliveryMode)) {
            if (individualConditions != 0) {
                calcCountDay = individualConditions;
            } else {
                calcCountDay = STANDARD_SHELF_LIFE_FOR_DELIVERY;
            }
        }

        //Если у заказа присутствует доп. услуга "Хранение на складе",
        //то к кол-ву бесплатных дней хранения прибавляем оплаченные дни.
        if (Objects.nonNull(order.getCountDay())) {
            calcCountDay = calcCountDay + order.getCountDay();
        }

        return calcCountDay;
    }

    /**
     * В зависимости от истинного режима доставки (TariffMode) проверяется, что все статусы ГМ корректны.
     * Не происходит проверка для режимов "Дверь-Постамат", "Склад-Постамат", "Постамат-Постамат",
     * для них есть отдельная проверка в {@link PostamatStatusRefresher}.
     */
    public boolean isAllStatusesOfPackagesCorrect(@Nonnull String deliveryModeCode, @Nonnull List<Package> packages) {
        //Дверь-Дверь, Склад-Дверь, Постамат-Дверь
        if (TariffModeLists.getTariffModeToDoorList().contains(deliveryModeCode)) {
            return checkPackageList(packages, OrderCargoPlaceLogisticStatus.ACCEPTED_AT_DELIVERY_WAREHOUSE);
        }

        //Дверь-Склад, Склад-Склад, Постамат-Склад
        if (TariffModeLists.getTariffModeToWarehouseList().contains(deliveryModeCode)) {
            return checkPackageList(packages, OrderCargoPlaceLogisticStatus.ACCEPTED_AT_WAREHOUSE_ON_DEMAND);
        }

        return false;
    }

    public Instant getMaxDateTimeChangeStatus(@Nonnull List<Package> packages) {
        return packages.stream()
                .map(pack -> placeStatusRepository.findCurrentStatusByPackageUuid(pack.getPackageUuid()))
                .filter(Objects::nonNull)
                .map(CargoPlaceStatus::getTimestamp)
                .max(Instant::compareTo)
                .orElse(null);
    }

    private boolean checkPackageList(@Nonnull List<Package> packages,
            @Nonnull OrderCargoPlaceLogisticStatus expectedStatus) {

        List<CargoPlaceStatus> currentStatuses = packages.stream()
                .map(pack -> placeStatusRepository.findCurrentStatusByPackageUuid(pack.getPackageUuid()))
                .filter(Objects::nonNull)
                .filter(cargoPlaceStatus -> expectedStatus.getMessageNamePostfix()
                        .equals(cargoPlaceStatus.getStatus().getMessageNamePostfix()))
                .collect(Collectors.toList());

        return currentStatuses.size() == packages.size();
    }
}
