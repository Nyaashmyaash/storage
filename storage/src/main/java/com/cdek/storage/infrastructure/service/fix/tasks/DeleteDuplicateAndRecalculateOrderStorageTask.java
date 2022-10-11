package com.cdek.storage.infrastructure.service.fix.tasks;

import com.cdek.storage.application.ports.output.OrderStorageRepository;
import com.cdek.storage.infrastructure.service.fix.helper.FixOrderStorageAndCreateIfNeedHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DeleteDuplicateAndRecalculateOrderStorageTask implements Runnable {

    private final OrderStorageRepository orderStorageRepository;
    private final FixOrderStorageAndCreateIfNeedHelper helper;
    private final String orderNumber;

    @Override
    public void run() {
        try {
            orderStorageRepository.deleteOrderStoragePeriod(orderNumber);
            helper.analyseStatusesOfPackagesAndCalcDateOfReceiptAndCreateStorageIfNeed(orderNumber, null);
        } catch (Exception e) {
            log.error("An error occurred at createOrderStorageTask with number {}", orderNumber, e);
        }
    }
}
