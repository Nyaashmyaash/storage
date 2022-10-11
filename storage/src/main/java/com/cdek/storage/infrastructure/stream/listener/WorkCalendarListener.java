package com.cdek.storage.infrastructure.stream.listener;

import com.cdek.messaging.processing.ExchangeObjectMessageProcessor;
import com.cdek.storage.buffer.ports.input.WorkCalendarRefresher;
import com.cdek.storage.buffer.ports.output.WorkCalendarBufferRepository;
import com.cdek.stream.lib.MessageProcessorProperties;
import com.cdek.work.calendar.client.dto.CalendarEsbDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Function;

@Component
@Slf4j
public class WorkCalendarListener extends ExchangeObjectMessageProcessor<CalendarEsbDto, Instant> {

    private final WorkCalendarBufferRepository workCalendarRepository;
    private final WorkCalendarRefresher workCalendarRefresher;

    public WorkCalendarListener(MessageProcessorProperties properties,
            WorkCalendarBufferRepository workCalendarRepository,
            WorkCalendarRefresher workCalendarRefresher) {
        super(properties, Function.identity());
        this.workCalendarRepository = workCalendarRepository;
        this.workCalendarRefresher = workCalendarRefresher;
    }

    @Override
    protected boolean isValidMessage(@Nonnull Message<CalendarEsbDto> message) {
        return Optional.of(message.getPayload())
                .filter(dto -> dto.getUuid() != null)
                .filter(dto -> dto.getUpdateTimestamp() != null)
                .isPresent();
    }

    @Override
    protected Instant getCurrent(@Nonnull Message<CalendarEsbDto> message) {
        return workCalendarRepository.getTimestamp(message.getPayload().getUuid().toString());
    }

    @Override
    protected void processMessage(@Nonnull Message<CalendarEsbDto> message, Instant current) {
        workCalendarRefresher.refreshIfNeeded(message.getPayload(), current);
    }

    @Override
    protected boolean processValidation(@Nonnull Message<CalendarEsbDto> message) {
        if (!isValidMessage(message)) {
            var dto = message.getPayload();
            log.info("Invalid CalendarEsbDto, cause calendarUuid: {}, updateTimestamp: {}", dto.getUuid(),
                    dto.getUpdateTimestamp());
            return false;
        }
        return true;
    }
}
