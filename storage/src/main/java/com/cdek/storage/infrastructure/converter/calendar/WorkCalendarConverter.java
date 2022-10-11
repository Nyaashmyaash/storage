package com.cdek.storage.infrastructure.converter.calendar;

import com.cdek.storage.infrastructure.converter.common.UuidConverter;
import com.cdek.storage.model.calendar.WorkCalendar;
import com.cdek.storage.model.calendar.WorkCalendarDay;
import com.cdek.work.calendar.client.dto.CalendarEsbDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring",
        uses = { UuidConverter.class })
public abstract class WorkCalendarConverter {

    @Mapping(target = "calendarUuid", source = "uuid")
    @Mapping(target = "active", source = "active", defaultValue = "true")
    @Mapping(target = "dateUpdated", source = "updateTimestamp")
    public abstract WorkCalendar fromDto(CalendarEsbDto dto);

    @AfterMapping
    protected void after(@MappingTarget WorkCalendar calendarEntity, CalendarEsbDto dto) {
        List<WorkCalendarDay> days = new ArrayList<>(dto.getSchedule().size());

        dto.getSchedule().forEach((day, typeCode) ->
                days.add(new WorkCalendarDay(day, typeCode, dto.getUuid().toString()))
        );

        calendarEntity.setDays(days);
    }
}
