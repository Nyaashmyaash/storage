package com.cdek.storage.infrastructure.service.fix;

import com.cdek.storage.application.model.OrderStorage;
import com.cdek.storage.application.ports.output.OrderRepository;
import com.cdek.storage.application.ports.output.OrderStorageRepository;
import com.cdek.storage.application.service.helper.CalcDeadlineHelper;
import com.cdek.storage.application.service.helper.CalcHelper;
import com.cdek.storage.infrastructure.service.fix.tasks.FixOrderStorageTask;
import com.cdek.storage.infrastructure.stream.publisher.OrderStorageEventPublisher;
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
 * Сервис для перерасчета крайней даты хранения заказа.
 *
 * Логика:
 * 1. Из базы берется список сущностей "срок хранения", у которых известна дата поступления заказа на склад.
 * 2. Для каждой сущности перерасчитывается крайняя дата хранения.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FixOrderStoragePeriodService {

    private static final int BATCH_LIMIT = 1000;

    private final OrderStorageRepository orderStorageRepository;
    private final CalcDeadlineHelper calcDeadlineHelper;
    private final OrderStorageEventPublisher eventPublisher;
    private final OrderRepository orderRepository;
    private final CalcHelper calcHelper;
    private ExecutorService executorService;

    @PostConstruct
    public void postConstruct() {
        executorService = Executors.newFixedThreadPool(4);
    }

    public void fixDeadlineOfStoragePeriod(@Nonnull Instant dateFrom, @Nonnull Instant dateTo) {
        log.info("Start fix order storages.");

        var orderStorages = orderStorageRepository.getOrderStorageList(dateFrom, dateTo, BATCH_LIMIT);

        while (!orderStorages.isEmpty()) {
            log.info("For order storages fix was found {} items", orderStorages.size());
            var fixTasks = orderStorages.stream()
                    .map(this::runFixAsync)
                    .toArray(CompletableFuture[]::new);

            CompletableFuture.allOf(fixTasks).join();
            orderStorages = orderStorageRepository.getOrderStorageList(dateFrom, dateTo, BATCH_LIMIT);
        }

        log.info("Finish fix order storages.");
    }

    private CompletableFuture<Void> runFixAsync(@Nonnull OrderStorage orderStorage) {
        var fixTask = new FixOrderStorageTask(calcDeadlineHelper, orderStorageRepository, orderStorage, eventPublisher,
                orderRepository, calcHelper);
        return CompletableFuture.runAsync(fixTask, executorService);
    }
}
