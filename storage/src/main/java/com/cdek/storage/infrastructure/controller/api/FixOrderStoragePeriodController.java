package com.cdek.storage.infrastructure.controller.api;

import com.cdek.storage.infrastructure.controller.api.dto.request.FixDateOfReceiptRequest;
import com.cdek.storage.infrastructure.controller.transport.validation.ControllerParameterAspect;
import com.cdek.storage.infrastructure.service.fix.DeleteDuplicateOrderStoragePeriodService;
import com.cdek.storage.infrastructure.service.fix.FixDateOfReceiptAndDateDeadlineService;
import com.cdek.storage.infrastructure.service.fix.FixOrderStoragePeriodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FixOrderStoragePeriodController {

    private final FixOrderStoragePeriodService fixOrderStoragePeriodService;
    private final FixDateOfReceiptAndDateDeadlineService fixDateOfReceiptAndDateDeadlineService;
    private final DeleteDuplicateOrderStoragePeriodService deleteDuplicateOrderStoragePeriodService;

    /**
     * Запрос для запуска перерасчета крайней даты хранения заказа и кол-ва дней хранения.
     * В качестве параметров принимаются две даты в формате Instant - за какой период делать перерасчет.
     * Привер даты: 2022-03-21T14:56:41.215117Z
     */
    @GetMapping("/api/storage/fix-storage-period")
    @ControllerParameterAspect.Applied
    public void fixOrderStoragePeriod(@RequestParam final String dateFrom, @RequestParam final String dateTo) {
        fixOrderStoragePeriodService.fixDeadlineOfStoragePeriod(Instant.parse(dateFrom), Instant.parse(dateTo));
    }

    /**
     * Запрос для запуска перерасчета даты прибытия ГМ на склад/постамат и крайней даты хранения заказа.
     * В теле запроса передается список заказов и режим доставки для которого будут находиться определенные статусы.
     * Пример: режим доставки "Склад-Склад", значит, дата принятия на склад будет считаться та,
     * в которую прилетел первый раз статус "Принято в оф.-получателе до востребования".
     */
    @PostMapping("/api/storage/fix-date-of-receipt-and-date-deadline")
    public void fixDateOfReceiptAndDateDeadline(@RequestBody FixDateOfReceiptRequest fixDateOfReceiptRequest) {
        fixDateOfReceiptAndDateDeadlineService.fixDateOfReceiptAndDateDeadline(fixDateOfReceiptRequest);
    }

    /**
     * Запрос для запуска удаления дублирующих СХ и их перерасчета.
     */
    @GetMapping("/api/storage/delete-duplicate-storage-period")
    public void deleteDuplicateStoragePeriod(@RequestParam final String dateFrom, @RequestParam final String dateTo) {
        deleteDuplicateOrderStoragePeriodService
                .deleteDuplicateOrderStoragePeriodService(Instant.parse(dateFrom), Instant.parse(dateTo));
    }
}
