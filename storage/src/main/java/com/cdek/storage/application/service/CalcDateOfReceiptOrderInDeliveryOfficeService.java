package com.cdek.storage.application.service;

import com.cdek.storage.application.model.OrderStorage;
import com.cdek.storage.application.ports.input.CalcDateOfReceiptOrderInDeliveryOffice;
import com.cdek.storage.application.ports.input.CalcLastStorageDate;
import com.cdek.storage.application.ports.output.PackageRepository;
import com.cdek.storage.application.service.helper.CalcHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

/**
 * Вычисление даты получения заказа в офисе доставки (ОЗД/Офис до востребования) или закладки в постамат.
 * <a href="https://confluence.cdek.ru/pages/viewpage.action?pageId=78910227">Аналитика</a>
 * <p>
 * 1. Находим в БД все неудаленные ГМ заказа.
 * 2. Находим в БД сам заказ.
 * 3. Проверяем, что все ГМ имеют корректные статусы
 * ("Заложено в постамат"/"Принято на склад доставки"/"Принято на склад до востребования").
 * 3.1. Если нет, то завершаем выполнение метода.
 * 4. Записывам дату получения статуса последним из ГМ этого заказа в сущность OrderStorage (предварительно нашли ее
 * в БД, а если не нашли, то создали).
 * 5. Далее расчитывается крайняя дата хранения.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CalcDateOfReceiptOrderInDeliveryOfficeService implements CalcDateOfReceiptOrderInDeliveryOffice {

    private final PackageRepository packageRepository;
    private final CalcLastStorageDate calcLastStorageDate;
    private final CalcHelper calcHelper;

    @Override
    public void calcDateOfReceiptOrder(@Nonnull String orderUuid, @Nonnull String deliveryModeCode,
            @Nonnull OrderStorage orderStorage) {
        var packages = packageRepository.getNotDeletedPackagesByOrderUuid(orderUuid);
        boolean isAllStatusesCorrect = calcHelper.isAllStatusesOfPackagesCorrect(deliveryModeCode, packages);

        if (isAllStatusesCorrect) {
            var maxDateTime = calcHelper.getMaxDateTimeChangeStatus(packages);
            orderStorage.setDateOfReceiptInDeliveryOfficeOrPostamat(maxDateTime);
            calcLastStorageDate.calcLastStorageDate(orderStorage);
        } else {
            log.info("Not all cargo places of order with uuid:{} have expected status.", orderUuid);
        }
    }
}
