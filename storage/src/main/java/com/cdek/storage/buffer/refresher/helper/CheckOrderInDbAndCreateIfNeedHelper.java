package com.cdek.storage.buffer.refresher.helper;

import com.cdek.catalog.common.entity.TariffMode;
import com.cdek.order.dto.order.request.RequestGetOrderDto;
import com.cdek.order.dto.order.response.ResponseOrderDto;
import com.cdek.storage.application.ports.input.CalcStoragePeriodInDays;
import com.cdek.storage.application.ports.output.OrderRepository;
import com.cdek.storage.buffer.ports.output.OrderBufferRepository;
import com.cdek.storage.buffer.ports.output.PackageBufferRepository;
import com.cdek.storage.infrastructure.converter.order.OrderDtoConverter;
import com.cdek.storage.infrastructure.security.provider.UserProvider;
import com.cdek.storage.infrastructure.service.OrderClient;
import com.cdek.storage.model.order.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Сервис для проверки существования в БД заказа:
 * 1. если заказ не существует, то отправляется запрос на получение данных по заказу.
 * 2. далее происходит сохранение заказа и его пакетов.
 * 3. если истинный режим доставки заказа не имеет тип "терминал-терминал",
 * то происходит подсчет сроков хранения (максимальное кол-во дней хранения на складе).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CheckOrderInDbAndCreateIfNeedHelper {

    private final OrderDtoConverter orderDtoConverter;
    private final OrderRepository orderRepository;
    private final OrderBufferRepository orderBufferRepository;
    private final PackageBufferRepository packageBufferRepository;
    private final OrderClient orderClient;
    private final CalcStoragePeriodInDays calcStoragePeriodInDays;
    private final UserProvider userProvider;

    public void checkOrderInDbAndCreateIfNeed(@Nonnull String orderUuid) {
        boolean isOrderExist = orderRepository.isOrderExists(orderUuid);

        if (!isOrderExist) {
            this.getOrderAndCalcPeriod(orderUuid);
        }
    }

    public void getOrderAndCalcPeriod(@Nonnull String orderUuid) {
        var newOrder = sendRequestAndGetOrder(orderUuid);

        if (Objects.isNull(newOrder)) {
            return;
        }

        //Необходима повторная проверка существования заказа в БД,
        //т.к. за время получения заказа по API, заказ мог прилететь по шине и успеть сохраниться.
        if (!orderRepository.isOrderExists(orderUuid)) {
            orderBufferRepository.saveNewOrder(newOrder);
            newOrder.getPackages().forEach(packageBufferRepository::saveOrUpdatePackage);

            if (!TariffMode.TARIFF_MODE_TT.equals(newOrder.getTrueDeliveryModeCode())) {
                calcStoragePeriodInDays.calcStoragePeriod(newOrder);
            }
        }
    }

    @Nullable
    private Order sendRequestAndGetOrder(@Nonnull String orderUuid) {
        var request = new RequestGetOrderDto();
        request.setOrderId(orderUuid);
        request.setValue("1");
        request.setLang(LocaleContextHolder.getLocale().getISO3Language());
        request.setToken(userProvider.getToken());

        var response = new ResponseOrderDto();

        try {
            response = orderClient.getOrderByUuid(request);
        } catch (Exception e) {
            log.info("Couldn't receive order with uuid:{} by API.", orderUuid, e);
        }

        if (response.getOrder() != null) {
            log.info("Order with uuid:{} received by API.", orderUuid);
            return orderDtoConverter.fromOrderDto(response.getOrder());
        }

        return null;
    }
}
