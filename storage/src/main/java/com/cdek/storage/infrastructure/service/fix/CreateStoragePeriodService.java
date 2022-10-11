package com.cdek.storage.infrastructure.service.fix;

import com.cdek.storage.infrastructure.persistence.db.repository.OrderPsqlRepository;
import com.cdek.storage.infrastructure.service.fix.helper.FixOrderStorageAndCreateIfNeedHelper;
import com.cdek.storage.infrastructure.service.fix.tasks.CreateOrderStorageTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateStoragePeriodService {

    private static final int BATCH_LIMIT = 1000;

    private final OrderPsqlRepository orderRepository;
    private final FixOrderStorageAndCreateIfNeedHelper helper;
    private ExecutorService executorService;

    @PostConstruct
    public void postConstruct() {
        executorService = Executors.newFixedThreadPool(4);
    }

    public void createStoragePeriod(@Nonnull Instant dateFrom, @Nonnull Instant dateTo) {
        log.info("Start create order storages.");

        var orderNumberList = orderRepository.getOrderNumberListWithoutStoragePeriod(dateFrom, dateTo, BATCH_LIMIT);

        while (!orderNumberList.isEmpty()) {
            log.info("For order storage create was found {} orders", orderNumberList.size());
            var fixTasks = orderNumberList.stream()
                    .map(this::runFixAsync)
                    .toArray(CompletableFuture[]::new);

            CompletableFuture.allOf(fixTasks).join();
            orderNumberList = orderRepository.getOrderNumberListWithoutStoragePeriod(dateFrom, dateTo, BATCH_LIMIT);
        }

        log.info("Finish create order storages.");
    }

    private CompletableFuture<Void> runFixAsync(@Nonnull String orderNumber) {
        var fixTask = new CreateOrderStorageTask(helper, orderNumber);
        return CompletableFuture.runAsync(fixTask, executorService);
    }
}
