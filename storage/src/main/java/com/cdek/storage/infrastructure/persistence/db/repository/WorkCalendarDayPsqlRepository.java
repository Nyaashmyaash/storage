package com.cdek.storage.infrastructure.persistence.db.repository;

import com.cdek.storage.application.ports.output.WorkCalendarDayRepository;
import com.cdek.storage.buffer.ports.output.WorkCalendarDayBufferRepository;
import com.cdek.storage.infrastructure.persistence.db.mapper.WorkCalendarDayMapper;
import com.cdek.storage.model.calendar.WorkCalendarDay;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class WorkCalendarDayPsqlRepository implements WorkCalendarDayBufferRepository, WorkCalendarDayRepository {

    private final WorkCalendarDayMapper workCalendarDayMapper;

    @Override
    public void saveNewCalendarDays(@Nonnull List<WorkCalendarDay> days) {
        workCalendarDayMapper.insertList(days);
    }

    @Override
    public void updateCalendarDays(@Nonnull List<WorkCalendarDay> days) {
        workCalendarDayMapper.updateList(days);
    }

    @Nullable
    @Override
    public WorkCalendarDay findWorkCalendarDayByCalendarUuidAndDate(@Nonnull String calendarUuid, @Nonnull String date) {
        return workCalendarDayMapper.findWorkCalendarDayByCalendarUuidAndDate(calendarUuid, date);
    }

    @Nonnull
    @Override
    public WorkCalendarDay getDateInformation(
            @Nonnull String countryUuid,
            @Nullable String regionUuid,
            @Nonnull String date) {
        return workCalendarDayMapper.getDateInformation(countryUuid, regionUuid, date);
    }
}
