package com.cdek.storage.buffer.refresher;

import com.cdek.cargoplacelogisticstatus.common.domain.OrderCargoPlaceLogisticStatus;
import com.cdek.catalog.common.entity.TariffMode;
import com.cdek.storage.application.ports.input.CalcDateOfReceiptOrderInDeliveryOffice;
import com.cdek.storage.application.service.helper.TariffModeLists;
import com.cdek.storage.buffer.ports.input.OrderCargoPlaceStatusRefresher;
import com.cdek.storage.buffer.refresher.helper.CalcAndGetOrderStorageHelper;
import com.cdek.storage.buffer.refresher.helper.CheckOrderInDbAndCreateIfNeedHelper;
import com.cdek.storage.infrastructure.converter.order.CargoPlaceStatusConverter;
import com.cdek.storage.infrastructure.persistence.db.repository.OrderCargoPlaceStatusPsqlRepository;
import com.cdek.storage.infrastructure.persistence.db.repository.OrderPsqlRepository;
import com.cdek.storage.infrastructure.stream.dto.OrderCargoPlaceStreamDto;
import com.cdek.storage.infrastructure.stream.listener.OrderCargoPlaceListener;
import com.cdek.storage.model.order.CargoPlaceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис обновляет данные по статусам заказов в режиме "До двери" или "До склада".
 * Статусы заказов в режиме "До постамата" обрабатываются в сервисе в {@link PostamatStatusStreamRefresher}
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCargoPlaceStatusStreamRefresher implements OrderCargoPlaceStatusRefresher {

    @Value("${check.order.in.db.and.create.if.need}")
    boolean checkOrderInDbAndCreate;

    private final List<OrderCargoPlaceLogisticStatus> finalStatusList = new ArrayList<>();

    private final CargoPlaceStatusConverter converter;
    private final OrderCargoPlaceStatusPsqlRepository statusPsqlRepository;
    private final CalcDateOfReceiptOrderInDeliveryOffice calcDateOfReceiptOrder;
    private final CheckOrderInDbAndCreateIfNeedHelper checkOrderHelper;
    private final OrderPsqlRepository orderPsqlRepository;
    private final CalcAndGetOrderStorageHelper calcAndGetOrderStorage;

    @PostConstruct
    public void init() {
        finalStatusList.add(OrderCargoPlaceLogisticStatus.POSTOMAT_POSTED);
        finalStatusList.add(OrderCargoPlaceLogisticStatus.ACCEPTED_AT_DELIVERY_WAREHOUSE);
        finalStatusList.add(OrderCargoPlaceLogisticStatus.ACCEPTED_AT_WAREHOUSE_ON_DEMAND);
    }

    /**
     * Логика работы:
     * 1) проверяется наличие заказа и пакетов в БД, если их нет, то отправляется запрос на их получение,
     * далее они сохраняются в БД.
     * 2) проверяется истинный режим доставки заказа: если режим "терминал-терминал",
     * то статус ГМ не сохраняется для этого заказа.
     * 3) иначе статус требует сохранения в локальной бд.
     * <p>
     * 4) Если статус валидный и его сохранили в БД, то далее происходит проверка существования даты получения заказа
     * в офисе доставки: если дата уже была известна, то прекращается выполнение метода, иначе, происходит подсчет даты
     * поступления заказа в офисе доставки.
     * <p>
     * Валидность сообщения проверяется в {@link OrderCargoPlaceListener}
     *
     * @param newDto новое сообщение с шины\кролика.
     */
    @Transactional
    @Override
    public void saveIfNeeded(@Nonnull OrderCargoPlaceStreamDto newDto, @Nullable Instant current) {
        if (finalStatusList.contains(newDto.getStatus())) {
            checkOrderHelper.checkOrderInDbAndCreateIfNeed(newDto.getOrderId());
            final var tariffMode = orderPsqlRepository.getTrueDeliveryModeCodeByOrderUuid(newDto.getOrderId());

            if (TariffMode.TARIFF_MODE_TT.equals(tariffMode)) {
                log.info(
                        "Skip cargoplace status, with package uuid:{}, cause true delivery mode of order is " +
                                "terminal-terminal.",
                        newDto.getPackageId());
                return;
            }

            final var cargoPlaceStatus = converter.fromDto(newDto);
            if (current == null) {
                statusPsqlRepository.saveNewStatus(cargoPlaceStatus);
                log.info("Successfully save CargoPlace status with status uuid:{} and package uuid:{}.",
                        cargoPlaceStatus.getStatusUuid(), cargoPlaceStatus.getPackageId());
            } else {
                statusPsqlRepository.updateStatus(cargoPlaceStatus);
            }

            checkTariffModeAndCalcDateOfReceiptIfNeed(cargoPlaceStatus, tariffMode);

        }
    }

    private void checkTariffModeAndCalcDateOfReceiptIfNeed(@Nonnull CargoPlaceStatus cargoPlaceStatus,
            @Nonnull String tariffMode) {
        final var orderUuid = cargoPlaceStatus.getOrderId();
        if (TariffModeLists.getTariffModeToDoorOrToWarehouseList().contains(tariffMode)) {
            var orderStorage = calcAndGetOrderStorage.getOrderStoragePeriod(orderUuid);
            if (orderStorage.getDateOfReceiptInDeliveryOfficeOrPostamat() == null) {
                calcDateOfReceiptOrder.calcDateOfReceiptOrder(orderUuid, tariffMode, orderStorage);
            }
        }
    }
}

