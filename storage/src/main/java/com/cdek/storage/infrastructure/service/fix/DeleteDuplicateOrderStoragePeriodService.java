package com.cdek.storage.infrastructure.service.fix;

import com.cdek.storage.application.ports.output.OrderStorageRepository;
import com.cdek.storage.infrastructure.service.fix.helper.FixOrderStorageAndCreateIfNeedHelper;
import com.cdek.storage.infrastructure.service.fix.tasks.DeleteDuplicateAndRecalculateOrderStorageTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Сервис удаления дублирующих СХ и их перерасчета.
 * <p>
 * Логика:
 * <p>1) берутся все заказы, у которых есть больше одного СХ (срока хранения),</p>
 * <p>2) удаляются все СХ данных заказов,</p>
 * <p>3) создаются новые СХ и публикуются на шину.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteDuplicateOrderStoragePeriodService {

    private static final int BATCH_LIMIT = 1000;

    private final OrderStorageRepository orderStorageRepository;
    private final FixOrderStorageAndCreateIfNeedHelper helper;
    private ExecutorService executorService;

    @PostConstruct
    public void postConstruct() {
        executorService = Executors.newFixedThreadPool(4);
    }

    public void deleteDuplicateOrderStoragePeriodService(@Nonnull Instant dateFrom, @Nonnull Instant dateTo) {
        log.info("Start delete duplicate and recalculation order storages.");

        var orderNumberList = orderStorageRepository
                .getOrderNumberWithDuplicateStoragePeriod(dateFrom, dateTo, BATCH_LIMIT);

        while (!orderNumberList.isEmpty()) {
            log.info("For delete duplicate and recalculation order storages was found {} items",
                    orderNumberList.size());
            var deleteAndRecalculateTasks = orderNumberList.stream()
                    .map(this::runDeleteAndRecalculateAsync)
                    .toArray(CompletableFuture[]::new);

            CompletableFuture.allOf(deleteAndRecalculateTasks).join();
            orderNumberList = orderStorageRepository
                    .getOrderNumberWithDuplicateStoragePeriod(dateFrom, dateTo, BATCH_LIMIT);
        }

        log.info("Finish delete duplicate and recalculation order storages.");
    }

    private CompletableFuture<Void> runDeleteAndRecalculateAsync(@Nonnull String orderNumber) {
        var deleteDuplicateAndRecalculateTask =
                new DeleteDuplicateAndRecalculateOrderStorageTask(orderStorageRepository, helper, orderNumber);
        return CompletableFuture.runAsync(deleteDuplicateAndRecalculateTask, executorService);
    }
}
