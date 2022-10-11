package com.cdek.storage.utils;

import com.cdek.storage.application.service.helper.HolidaysLists;
import com.cdek.storage.model.calendar.WorkCalendar;
import com.cdek.storage.model.calendar.WorkCalendarDay;
import com.cdek.work.calendar.client.dto.CalendarEsbDto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WorkCalendarTestUtils {

    public static final UUID CALENDAR_WITH_REGION = UUID.randomUUID();
    public static final UUID CALENDAR_WITHOUT_REGION = UUID.randomUUID();
    public static final UUID COUNTRY_UUID = UUID.randomUUID();
    public static final UUID ANY_COUNTRY_UUID = UUID.randomUUID();
    public static final UUID REGION_UUID = UUID.randomUUID();
    public static final String REGION_CODE = "regionCode";
    public static final String COUNTRY_CODE = "countryCode";
    public static final String DAY_TYPE_CODE_1 = "1";
    public static final String DAY_TYPE_CODE_2 = "2";
    public static final Instant TIMESTAMP = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    public static final LocalDate DATE_1 = LocalDate.now().plus(1, ChronoUnit.DAYS);
    public static final LocalDate DATE_2 = LocalDate.now().plus(2, ChronoUnit.DAYS);
    public static final LocalDate DATE_3 = LocalDate.now().plus(3, ChronoUnit.DAYS);
    public static final LocalDate DATE_4 = LocalDate.now().plus(4, ChronoUnit.DAYS);
    public static final int YEAR = 2222;

    public static CalendarEsbDto crateCalendarEsbDto() {
        CalendarEsbDto dto = new CalendarEsbDto();
        dto.setUuid(CALENDAR_WITH_REGION);
        dto.setUpdateTimestamp(TIMESTAMP);
        dto.setCountryCode(COUNTRY_CODE);
        dto.setCountryUuid(COUNTRY_UUID);
        dto.setRegionCode(REGION_CODE);
        dto.setRegionUuid(REGION_UUID);
        dto.setYear(YEAR);
        dto.setActive(true);
        dto.setSchedule(createSchedule());

        return dto;
    }

    public static CalendarEsbDto crateCalendarEsbDtoWithoutUuid() {
        CalendarEsbDto dto = crateCalendarEsbDto();
        dto.setUuid(null);

        return dto;
    }

    public static Map<LocalDate, String> createSchedule() {
        Map<LocalDate, String> schedule = new HashMap<>();
        schedule.put(DATE_1, DAY_TYPE_CODE_1);
        schedule.put(DATE_2, DAY_TYPE_CODE_1);

        return schedule;
    }

    public static WorkCalendar createWorkCalendarModel() {
        return WorkCalendar.builder()
                .calendarUuid(CALENDAR_WITH_REGION.toString())
                .active(true)
                .dateUpdated(TIMESTAMP)
                .year(YEAR)
                .countryCode(COUNTRY_CODE)
                .countryUuid(COUNTRY_UUID.toString())
                .regionCode(REGION_CODE)
                .regionUuid(REGION_UUID.toString())
                .days(getDayList())
                .build();
    }

    public static WorkCalendar createWorkCalendarModelForDb() {
        return WorkCalendar.builder()
                .calendarUuid(CALENDAR_WITH_REGION.toString())
                .active(true)
                .dateUpdated(TIMESTAMP)
                .year(YEAR)
                .countryCode(COUNTRY_CODE)
                .countryUuid(COUNTRY_UUID.toString())
                .regionCode(REGION_CODE)
                .regionUuid(REGION_UUID.toString())
                .build();
    }

    public static WorkCalendar createUpdatedWorkCalendarModelForDb() {
        WorkCalendar calendar = createWorkCalendarModelForDb();
        calendar.setCountryUuid(ANY_COUNTRY_UUID.toString());

        return calendar;
    }

    public static WorkCalendarDay createWorkCalendarDay1() {
        return WorkCalendarDay.builder()
                .date(DATE_1)
                .dayTypeCode(DAY_TYPE_CODE_1)
                .calendarUuid(CALENDAR_WITH_REGION.toString())
                .build();
    }

    public static WorkCalendarDay createNotHolidayDate() {
        return WorkCalendarDay.builder()
                .date(getNotHolidayDate(DATE_1))
                .dayTypeCode(DAY_TYPE_CODE_1)
                .calendarUuid(CALENDAR_WITH_REGION.toString())
                .build();
    }

    public static WorkCalendarDay getUpdatedWorkCalendarDay4() {
        WorkCalendarDay day = createWorkCalendarForRegionDay4();
        day.setDayTypeCode(DAY_TYPE_CODE_2);

        return day;
    }

    public static WorkCalendarDay createWorkCalendarDay2() {
        return WorkCalendarDay.builder()
                .date(DATE_2)
                .dayTypeCode(DAY_TYPE_CODE_1)
                .calendarUuid(CALENDAR_WITH_REGION.toString())
                .build();
    }

    public static List<WorkCalendarDay> getDayList() {
        List<WorkCalendarDay> dayList = new ArrayList<>();
        dayList.add(createWorkCalendarDay1());
        dayList.add(createWorkCalendarDay2());

        return dayList;
    }

    //Проверка корректной работы запроса на крайнюю дату хранения заказа.
    //Создаются два календаря: один на регион, другой - только на страну
    //Каждому делается индивидуальное расписание работы.
    public static WorkCalendar createWorkCalendarWithRegion() {
        return WorkCalendar.builder()
                .calendarUuid(CALENDAR_WITH_REGION.toString())
                .countryUuid(COUNTRY_UUID.toString())
                .regionUuid(REGION_UUID.toString())
                .year(2030)
                .active(true)
                .dateUpdated(TIMESTAMP)
                .build();
    }

    public static WorkCalendar createWorkCalendarWithoutRegion() {
        return WorkCalendar.builder()
                .calendarUuid(CALENDAR_WITHOUT_REGION.toString())
                .countryUuid(COUNTRY_UUID.toString())
                .regionUuid(null)
                .year(2030)
                .active(true)
                .dateUpdated(TIMESTAMP)
                .build();
    }

    public static List<WorkCalendarDay> getDayListForCalendarWithRegion() {
        List<WorkCalendarDay> dayList = new ArrayList<>();
        dayList.add(createWorkCalendarForRegionDay3());
        dayList.add(createWorkCalendarForRegionDay4());

        return dayList;
    }
    public static WorkCalendarDay createWorkCalendarForRegionDay3() {
        return WorkCalendarDay.builder()
                .date(DATE_3)
                .dayTypeCode("2")
                .calendarUuid(CALENDAR_WITH_REGION.toString())
                .build();
    }
    public static WorkCalendarDay createWorkCalendarForRegionDay4() {
        return WorkCalendarDay.builder()
                .date(DATE_4)
                .dayTypeCode("1")
                .calendarUuid(CALENDAR_WITH_REGION.toString())
                .build();
    }

    public static List<WorkCalendarDay> getDayListForCalendarWithoutRegion() {
        List<WorkCalendarDay> dayList = new ArrayList<>();
        dayList.add(createWorkCalendarForCountryDay3());
        dayList.add(createWorkCalendarForCountryDay4());

        return dayList;
    }

    public static WorkCalendarDay createWorkCalendarForCountryDay3() {
        return WorkCalendarDay.builder()
                .date(DATE_3)
                .dayTypeCode("4")
                .calendarUuid(CALENDAR_WITHOUT_REGION.toString())
                .build();
    }

    public static WorkCalendarDay createWorkCalendarForCountryDay4() {
        return WorkCalendarDay.builder()
                .date(DATE_4)
                .dayTypeCode("2")
                .calendarUuid(CALENDAR_WITHOUT_REGION.toString())
                .build();
    }

    private static LocalDate getNotHolidayDate(LocalDate date) {
        while (isHoliday(date)) {
            date = date.plusDays(1);
        }
        return date;
    }

    private static boolean isHoliday(LocalDate date) {
        return HolidaysLists.getWeekendsOfFirstMay(date).contains(date)
                || HolidaysLists.getWeekendsOfNinthOfMay(date).contains(date)
                || HolidaysLists.getWeekendsOfNewYear(date).contains(date);
    }


}
