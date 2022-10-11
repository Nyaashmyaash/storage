package com.cdek.storage.application.service.helper;

import com.cdek.storage.application.model.OrderStorage;
import com.cdek.storage.application.ports.output.OrderCargoPlaceStatusRepository;
import com.cdek.storage.application.ports.output.WorkCalendarDayRepository;
import com.cdek.storage.application.ports.output.WorkCalendarRepository;
import com.cdek.storage.infrastructure.persistence.db.repository.LogisticCityPsqlRepository;
import com.cdek.storage.infrastructure.persistence.db.repository.OfficePsqlRepository;
import com.cdek.storage.infrastructure.persistence.db.repository.OrderPsqlRepository;
import com.cdek.storage.model.logistic.LogisticCity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Сервис, который высчитывает крайний день хранения заказа на складе:
 * Если истинный режим доставки заказа "до постамата", то к дате и времени закладки в постамат
 * прибавляется срок хранения заказа в днях.
 * Иначе:
 * 1. Из статуса ГМ получаем текущее местоположение заказа - идентификатор офиса.
 * 2. По идентификатору находим офис в БД.
 * 3. Из найденого офиса берется код города ЭК4 - по коду находится сущность "логистический город".
 * 4. Из логистического города берется идентификатор страны и региона (если есть).
 * 5. Из сущности "сроки хранения" берется срок хранения в днях и дата прибытия заказа на склад (в постамат).
 * 6. По идетификатору страны, идетификатору региона, сроку хранения в днях и дате прибытия высчитывается
 * крайняя дата хранения заказа, которая указывается в формате "до конца дня", т.е. до 23:59:59 по местному времени.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CalcDeadlineHelper {

    private static final String HOLIDAY_TYPE = "4";

    private final WorkCalendarRepository workCalendarRepository;
    private final WorkCalendarDayRepository workCalendarDayRepository;
    private final OrderCargoPlaceStatusRepository placeStatusRepository;
    private final OfficePsqlRepository officeRepository;
    private final LogisticCityPsqlRepository logisticCityRepository;
    private final OrderPsqlRepository orderRepository;

    public Instant calcDeadlineOfStoragePeriod(@Nonnull OrderStorage orderStorage) {
        var storagePeriod = orderStorage.getShelfLifeOrderInDays();
        var dateReceipt = orderStorage.getDateOfReceiptInDeliveryOfficeOrPostamat();

        if (TariffModeLists.getTariffModeToPostamatList()
                .contains(orderRepository.getTrueDeliveryModeCodeByOrderUuid(orderStorage.getOrderUuid()))) {
            return dateReceipt.plus(storagePeriod, ChronoUnit.DAYS);
        }

        var city = getLogisticCity(orderStorage);
        String countryUuid = city.getCountryUuid();
        String regionUuid = Optional.of(city).map(LogisticCity::getRegionUuid).orElse(null);
        var estimatedLastDateOfStorage =
                dateReceipt.plus(storagePeriod, ChronoUnit.DAYS).atZone(city.getTimeZone()).toLocalDate();

        //если у города есть идентификатор региона, то нужно проверить существует ли календарь для данного региона
        //на конкретный год.
        //Если существует, то в запрос на получение крайней даты передается идентификатор региона. Иначе передается null
        //и тогда календарь ищется только для конкретной страны.
        if (regionUuid != null) {
            boolean existWorkCalendarByRegionUuid =
                    workCalendarRepository.isWorkCalendarExists(regionUuid, estimatedLastDateOfStorage.getYear());
            if (!existWorkCalendarByRegionUuid) {
                regionUuid = null;
            }
        }

        //Далее берется информация из производственного календаря
        var dateFromCalendar = workCalendarDayRepository
                .getDateInformation(countryUuid, regionUuid, estimatedLastDateOfStorage.toString());
        //Пока в производственном календаре эта дата числится как праздник
        //или относится к списку праздничных дней из встроенного списка HolidaysLists, то прибавляем один день.
        while (dateFromCalendar.getDayTypeCode().equals(HOLIDAY_TYPE)
                || isHoliday(dateFromCalendar.getDate())) {
            estimatedLastDateOfStorage = estimatedLastDateOfStorage.plusDays(1);

            dateFromCalendar = workCalendarDayRepository
                    .getDateInformation(countryUuid, regionUuid, estimatedLastDateOfStorage.toString());
        }

        return estimatedLastDateOfStorage.atTime(LocalTime.MAX).atZone(city.getTimeZone()).toInstant()
                .truncatedTo(ChronoUnit.SECONDS);
    }

    private LogisticCity getLogisticCity(@Nonnull OrderStorage orderStorage) {
        String officeUuid = placeStatusRepository.getCurrentOfficeUuidByOrderUuid(orderStorage.getOrderUuid());
        var office = officeRepository.getOfficeByUuid(officeUuid);
        return logisticCityRepository.getLogisticCity(office.getCityCode());
    }

    private boolean isHoliday(LocalDate estimatedDate) {
        return HolidaysLists.getWeekendsOfFirstMay(estimatedDate).contains(estimatedDate)
                || HolidaysLists.getWeekendsOfNinthOfMay(estimatedDate).contains(estimatedDate)
                || HolidaysLists.getWeekendsOfNewYear(estimatedDate).contains(estimatedDate);
    }
}
