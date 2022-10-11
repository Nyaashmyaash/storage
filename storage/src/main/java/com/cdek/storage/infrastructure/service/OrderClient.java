package com.cdek.storage.infrastructure.service;

import com.cdek.order.dto.order.request.RequestGetOrderDto;
import com.cdek.order.dto.order.response.ResponseOrderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(url = "${order.url}", name = "order")
public interface OrderClient {

    @PostMapping(value = "/api/order/get")
    ResponseOrderDto getOrderByUuid(@RequestBody RequestGetOrderDto request);
}
