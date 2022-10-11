package com.cdek.storage.buffer.refresher;

import com.cdek.storage.buffer.ports.input.WorkCalendarRefresher;
import com.cdek.storage.buffer.ports.output.WorkCalendarBufferRepository;
import com.cdek.storage.buffer.ports.output.WorkCalendarDayBufferRepository;
import com.cdek.storage.infrastructure.converter.calendar.WorkCalendarConverter;
import com.cdek.storage.utils.WorkCalendarTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class WorkCalendarStreamRefresherTest {
    @MockBean
    WorkCalendarConverter converter;
    @MockBean
    WorkCalendarBufferRepository workCalendarRepository;
    @MockBean
    WorkCalendarDayBufferRepository workCalendarDayRepository;

    WorkCalendarRefresher refresher;

    @BeforeEach
    void before() {
        refresher = new WorkCalendarStreamRefresher(converter, workCalendarRepository, workCalendarDayRepository);
        Mockito.clearInvocations(converter, workCalendarRepository, workCalendarDayRepository);
    }

    @Test
    void refreshIfNeeded_NewCalendar_Save() {
        Mockito.when(converter.fromDto(Mockito.any())).thenReturn(WorkCalendarTestUtils.createWorkCalendarModel());
        Mockito.doNothing().when(workCalendarRepository).saveNewWorkCalendar(Mockito.any());
        Mockito.doNothing().when(workCalendarDayRepository).saveNewCalendarDays(Mockito.any());

        refresher.refreshIfNeeded(WorkCalendarTestUtils.crateCalendarEsbDto(), null);

        Mockito.verify(workCalendarRepository, Mockito.times(1)).saveNewWorkCalendar(Mockito.any());
        Mockito.verify(workCalendarDayRepository, Mockito.times(1)).saveNewCalendarDays(Mockito.any());

        Mockito.verify(workCalendarRepository, Mockito.never()).updateWorkCalendar(Mockito.any());
        Mockito.verify(workCalendarDayRepository, Mockito.never()).updateCalendarDays(Mockito.any());
    }

    @Test
    void refreshIfNeeded_UpdatedCalendar_Update() {
        Mockito.when(converter.fromDto(Mockito.any())).thenReturn(WorkCalendarTestUtils.createWorkCalendarModel());
        Mockito.doNothing().when(workCalendarRepository).updateWorkCalendar(Mockito.any());
        Mockito.doNothing().when(workCalendarDayRepository).updateCalendarDays(Mockito.anyList());

        refresher.refreshIfNeeded(WorkCalendarTestUtils.crateCalendarEsbDto(), Instant.now());

        Mockito.verify(workCalendarRepository, Mockito.never()).saveNewWorkCalendar(Mockito.any());
        Mockito.verify(workCalendarDayRepository, Mockito.never()).saveNewCalendarDays(Mockito.any());

        Mockito.verify(workCalendarRepository, Mockito.times(1)).updateWorkCalendar(Mockito.any());
        Mockito.verify(workCalendarDayRepository, Mockito.times(1)).updateCalendarDays(Mockito.any());
    }
}
