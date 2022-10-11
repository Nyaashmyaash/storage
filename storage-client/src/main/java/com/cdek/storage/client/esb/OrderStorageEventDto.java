package com.cdek.storage.client.esb;

import com.cdek.messaging.serialize.InstantIsoDeserializer;
import com.cdek.messaging.serialize.InstantIsoSerializer;
import com.cdek.stream.lib.ExchangeObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Транспорт для публикации отправки по шине.
 */
@Getter
@Setter
@JsonSerialize
@JsonDeserialize
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString(callSuper = true)
public class OrderStorageEventDto implements ExchangeObject, Serializable {

    /**
     * Тип сообщения в RabbitMQ.
     */
    public static final String ORDER_STORAGE_PERIOD_MESSAGE_TYPE = "obj.storage.period";

    /**
     * Идентификатор сущности "Сроки хранения заказа".
     */
    private UUID uuid;

    /**
     * Идентификатор заказа.
     */
    private String orderUuid;

    /**
     * Номер заказа.
     */
    private String orderNumber;

    /**
     * Крайняя дата хранения на складе.
     */
    @JsonDeserialize(using = InstantIsoDeserializer.class)
    @JsonSerialize(using = InstantIsoSerializer.class)
    private Instant deadlineForStorage;

    /**
     * Дата получения в офисе доставки (или дата закладки в постамат).
     */
    @JsonDeserialize(using = InstantIsoDeserializer.class)
    @JsonSerialize(using = InstantIsoSerializer.class)
    private Instant dateOfReceiptInDeliveryOfficeOrPostamat;

    /**
     * Срок хранения заказа в днях на складе или при закладке в постамат.
     */
    private Integer shelfLifeOrderInDays;

    /**
     * Дата и время создания/редактирования сущности
     */
    private Instant timestamp;

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public Instant getUpdateTimestamp() {
        return timestamp;
    }
}
