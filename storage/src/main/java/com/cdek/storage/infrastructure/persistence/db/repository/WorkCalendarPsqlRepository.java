package com.cdek.storage.infrastructure.persistence.db.repository;

import com.cdek.storage.application.ports.output.WorkCalendarRepository;
import com.cdek.storage.buffer.ports.output.WorkCalendarBufferRepository;
import com.cdek.storage.infrastructure.persistence.db.mapper.WorkCalendarMapper;
import com.cdek.storage.model.calendar.WorkCalendar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class WorkCalendarPsqlRepository implements WorkCalendarBufferRepository, WorkCalendarRepository {

    private final WorkCalendarMapper workCalendarMapper;

    @Override
    public void saveNewWorkCalendar(@Nonnull WorkCalendar calendar) {
        workCalendarMapper.insert(calendar);
    }

    @Override
    public void updateWorkCalendar(@Nonnull WorkCalendar calendar) {
        workCalendarMapper.update(calendar);
    }

    @Nullable
    @Override
    public Instant getTimestamp(@Nonnull String calendarUuid) {
        return workCalendarMapper.getTimestamp(calendarUuid);
    }

    @Nullable
    @Override
    public WorkCalendar findWorkCalendarByUuid(@Nonnull String calendarUuid) {
        return workCalendarMapper.findWorkCalendarByUuid(calendarUuid);
    }

    @Override
    public boolean isWorkCalendarExists(@Nonnull String regionUuid, @Nonnull Integer year) {
        return workCalendarMapper.isWorkCalendarExists(regionUuid, year);
    }
}
