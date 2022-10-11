package com.cdek.storage.infrastructure.service.fix;

import com.cdek.storage.infrastructure.controller.api.dto.request.FixDateOfReceiptRequest;
import com.cdek.storage.infrastructure.service.fix.helper.FixOrderStorageAndCreateIfNeedHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.time.Instant;

/**
 * Сервис для перерасчета даты прибытия ГМ на склад/постамат и крайней даты хранения заказа.
 * <p>
 * Логика:
 * 1. По каждому заказу берется список всех неудаленных ГМ.
 * 2. По каждому ГМ ищется статус соответствующий режиму доставки заказа, который был проставлен самым первым.
 * 3. Данный статус добавляется в общий список статусов всех ГМ.
 * 4. Из этого списка берется самый последний по времени статус - он будет использовн для перерасчета даты
 * хранения заказа.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FixDateOfReceiptAndDateDeadlineService {

    private final FixOrderStorageAndCreateIfNeedHelper fixOrderStorageHelper;
    private final CreateStoragePeriodService createStoragePeriodService;

    public void fixDateOfReceiptAndDateDeadline(@Nonnull FixDateOfReceiptRequest fixDateOfReceiptRequest) {
        log.info("Start fix date of receipt and deadline of storage.");

        var orderNumberList = fixDateOfReceiptRequest.getOrderNumbers();

        if (!orderNumberList.isEmpty()) {
            String tariffModeCode = fixDateOfReceiptRequest.getTariffMode();
            orderNumberList
                    .forEach(number -> fixOrderStorageHelper
                            .analyseStatusesOfPackagesAndCalcDateOfReceiptAndCreateStorageIfNeed(number, tariffModeCode));
        } else {
            createStoragePeriodService.createStoragePeriod(Instant.parse(fixDateOfReceiptRequest.getDateFrom()),
                    Instant.parse(fixDateOfReceiptRequest.getDateTo()));
        }

        log.info("Finish fix date of receipt and deadline of storage.");
    }
}
