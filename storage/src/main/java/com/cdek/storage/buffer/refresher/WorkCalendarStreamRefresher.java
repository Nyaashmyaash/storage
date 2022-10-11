package com.cdek.storage.buffer.refresher;

import com.cdek.storage.buffer.ports.input.WorkCalendarRefresher;
import com.cdek.storage.buffer.ports.output.WorkCalendarBufferRepository;
import com.cdek.storage.buffer.ports.output.WorkCalendarDayBufferRepository;
import com.cdek.storage.infrastructure.converter.calendar.WorkCalendarConverter;
import com.cdek.storage.infrastructure.stream.listener.WorkCalendarListener;
import com.cdek.storage.model.calendar.WorkCalendar;
import com.cdek.storage.model.calendar.WorkCalendarDay;
import com.cdek.work.calendar.client.dto.CalendarEsbDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkCalendarStreamRefresher implements WorkCalendarRefresher {

    private final WorkCalendarConverter converter;
    private final WorkCalendarBufferRepository workCalendarRepository;
    private final WorkCalendarDayBufferRepository workCalendarDayRepository;

    /**
     * Метод проверяет нужно ли обновить существующий календарь, либо сохранить новый.
     * Логика работы:
     * 1) если current != null, то это не новый календарь(мы нашли в бд предыдущий таймстамп),
     * поэтому календарь нужно обновить (обновляются и дни календаря).
     * 2) иначе календарь новый и требует сохранения в локальной бд (сохраняются и дни календаря).
     * <p>
     * Валидность сообщения проверяется в {@link WorkCalendarListener}
     *
     * @param newDto  новое сообщение с шины\кролика.
     * @param current таймстамп из бд.
     */
    @Transactional
    @Override
    public void refreshIfNeeded(@Nonnull CalendarEsbDto newDto, @Nullable Instant current) {
        var calendar = converter.fromDto(newDto);
        var days = calendar.getDays();
        if (current != null) {
            this.updateCalendarAndDays(calendar, days);
            log.info("Calendar, with uuid:{} successfully updated.", calendar.getCalendarUuid());
        } else {
            this.saveCalendarAndDays(calendar, days);
            log.info("Successfully save calendar, with uuid:{}.", calendar.getCalendarUuid());
        }
    }

    private void updateCalendarAndDays(@Nonnull WorkCalendar calendar, @Nonnull List<WorkCalendarDay> days) {
        workCalendarRepository.updateWorkCalendar(calendar);
        workCalendarDayRepository.updateCalendarDays(days);
    }

    private void saveCalendarAndDays(@Nonnull WorkCalendar calendar, @Nonnull List<WorkCalendarDay> days) {
        workCalendarRepository.saveNewWorkCalendar(calendar);
        workCalendarDayRepository.saveNewCalendarDays(days);
    }
}
