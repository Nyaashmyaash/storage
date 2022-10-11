package com.cdek.storage.infrastructure.controller.api;

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

@Tag(name = "API для публикации события сроков хранения заказа", description = "Event Controller")
public interface ApiSendOrderStoragePeriodEvent {

    @Operation(summary = "Опубликовать событие создания/редактирования срока хранения заказа по номеру заказа.",
            description = "Запрос на публикацию события создания/редактирования срока хранения заказа.")
    @Parameter(name = "orderNumber", in = ParameterIn.QUERY, required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Событие опубликовано.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderStorageEventDto.class)) }),
            @ApiResponse(responseCode = "404", description = "Событие не опубликовано.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseEntity.class)) }) })
    void sendOrderStoragePeriodEventByOrderNumber(@RequestParam String orderNumber);

    @Operation(summary = "Опубликовать событие создания/редактирования срока хранения заказа по uuid заказа.",
            description = "Запрос на публикацию события создания/редактирования срока хранения заказа.")
    @Parameter(name = "orderUuid", in = ParameterIn.QUERY, required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Событие опубликовано.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderStorageEventDto.class)) }),
            @ApiResponse(responseCode = "404", description = "Событие не опубликовано.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseEntity.class)) }) })
    void sendOrderStoragePeriodEventByOrderUuid(@RequestParam String orderUuid);
}
