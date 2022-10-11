package com.cdek.storage.infrastructure.service;

import com.cdek.cargoplacelogisticstatus.common.dto.CargoPlaceStatusHistoryResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(url = "${cargoplace.logistic.status.url}", name = "cargoplaceLogisticStatus")
public interface CargoplaceLogisticStatusClient {

    @GetMapping(value = "/api/v1/cargoplacelogisticstatus/history/{packageId}")
    ResponseEntity<CargoPlaceStatusHistoryResponseDto> getCargoplaceLogisticStatusByPackageUuid(
            @PathVariable String packageId);
}
