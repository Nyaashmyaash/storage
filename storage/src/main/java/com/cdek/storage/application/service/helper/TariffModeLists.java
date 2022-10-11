package com.cdek.storage.application.service.helper;

import com.cdek.catalog.common.entity.TariffMode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TariffModeLists {

    public static List<String> getTariffModeToDoorOrToWarehouseList() {
        List<String> list = new ArrayList<>();
        list.add(TariffMode.TARIFF_MODE_DD);
        list.add(TariffMode.TARIFF_MODE_DW);
        list.add(TariffMode.TARIFF_MODE_WD);
        list.add(TariffMode.TARIFF_MODE_WW);
        list.add(TariffMode.TARIFF_MODE_PD);
        list.add(TariffMode.TARIFF_MODE_PW);

        return list;
    }

    public static List<String> getTariffModeToDoorList() {
        List<String> list = new ArrayList<>();
        list.add(TariffMode.TARIFF_MODE_DD);
        list.add(TariffMode.TARIFF_MODE_WD);
        list.add(TariffMode.TARIFF_MODE_PD);

        return list;
    }

    public static List<String> getTariffModeToWarehouseList() {
        List<String> list = new ArrayList<>();
        list.add(TariffMode.TARIFF_MODE_WW);
        list.add(TariffMode.TARIFF_MODE_DW);
        list.add(TariffMode.TARIFF_MODE_PW);

        return list;
    }

    public static List<String> getTariffModeToPostamatList() {
        List<String> list = new ArrayList<>();
        list.add(TariffMode.TARIFF_MODE_DP);
        list.add(TariffMode.TARIFF_MODE_WP);
        list.add(TariffMode.TARIFF_MODE_PP);

        return list;
    }
}
