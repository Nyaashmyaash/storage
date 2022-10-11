package com.cdek.storage.infrastructure.service.fix.tasks;

import com.cdek.storage.infrastructure.service.fix.helper.FixOrderStorageAndCreateIfNeedHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CreateOrderStorageTask implements Runnable {

    private final FixOrderStorageAndCreateIfNeedHelper helper;
    private final String orderNumber;

    @Override
    public void run() {
        try {
            helper.analyseStatusesOfPackagesAndCalcDateOfReceiptAndCreateStorageIfNeed(orderNumber, null);
        } catch (Exception e) {
            log.error("An error occurred at createOrderStorageTask with number {}", orderNumber, e);
        }
    }
}
