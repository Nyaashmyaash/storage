package com.cdek.storage.infrastructure.controller.web;

import com.cdek.storage.client.esb.OrderStorageEventDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "API получения события сроков хранения заказа", description = "Event Controller")
public interface WebOrderStoragePeriodEvent {

    @Operation(summary = "Получить событие создания/редактирования срока хранения заказа по номеру заказа.",
            description = "Запрос на получение события создания/редактирования срока хранения заказа.")
    @Parameter(name = "orderNumber", in = ParameterIn.QUERY, required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Событие найдено.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderStorageEventDto.class)) }),
            @ApiResponse(responseCode = "404", description = "Событие не найдено.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseEntity.class)) }) })
    ResponseEntity<OrderStorageEventDto> getOrderStoragePeriodEventByOrderNumber(@RequestParam String orderNumber);

    @Operation(summary = "Получить событие создания/редактирования срока хранения заказа по uuid заказа.",
            description = "Запрос на получение события создания/редактирования срока хранения заказа.")
    @Parameter(name = "orderUuid", in = ParameterIn.QUERY, required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Событие найдено.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderStorageEventDto.class)) }),
            @ApiResponse(responseCode = "404", description = "Событие не найдено.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseEntity.class)) }) })
    ResponseEntity<OrderStorageEventDto> getOrderStoragePeriodEventByOrderUuid(@RequestParam String orderUuid);
}
