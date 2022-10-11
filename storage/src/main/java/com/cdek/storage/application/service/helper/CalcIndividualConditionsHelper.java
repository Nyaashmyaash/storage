package com.cdek.storage.application.service.helper;

import com.cdek.catalog.common.entity.OrderType;
import com.cdek.storage.application.ports.output.ContractRepository;
import com.cdek.storage.application.ports.output.SellerRepository;
import com.cdek.storage.model.contract.Seller;
import com.cdek.storage.model.order.Order;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Сервис для определения кол-ва дней хранения заказа по индивидуальным условиям из контракта.
 */
@Component
@RequiredArgsConstructor
public class CalcIndividualConditionsHelper {

    private final SellerRepository sellerRepository;
    private final ContractRepository contractRepository;

    public int getIndividualConditionsForFreeStoragePeriod(@Nonnull Order order) {

        //Индивидуальные условия расчитываются только для заказа типа ИМ.
        if (!OrderType.ORDER_TYPE_ONLINE_SHOP.equals(order.getOrderTypeCode())) {
            return 0;
        }

        String sellerNameFromOrder = order.getSellerName();
        //Из бд достается список продавцов, содержащихся в контракте.
        List<Seller> sellerList = getSellerList(order);
        //Дефолтный продавец.
        var defaultSeller = sellerList.stream()
                .filter(itm -> itm.getSellerName() == null)
                .findFirst()
                .orElse(new Seller());

        var seller = sellerList.stream()
                .filter(sellerFromContract -> isSellerNamesEqual(
                        sellerFromContract.getSellerName(),
                        sellerNameFromOrder))
                .findFirst()
                .orElse(defaultSeller);

        return getIndividualConditionsByTrueDeliveryMode(seller, order);
    }

    private List<Seller> getSellerList(Order order) {
        //Из заказа достается идентификатор контракта.
        //Если идентификатора контракта нет у заказа, то он ищется в БД по номеру контракта.
        //Делается так из-за того, что по шине заказ летит с идентификатором контракта,
        //а при получении заказа по апи, он приходит только с номером контракта.
        String contractUuid = Optional.of(order)
                .map(Order::getPayerContractUuid)
                .orElse(contractRepository.getContractUuidByContractNumber(order.getPayerContractNumber()));

        return sellerRepository.findSellerListByContractUuid(contractUuid);
    }

    private String cleanSellerName(String name) {
        if (name != null) {
            return name.replaceAll("[\\s,.;:`\\-\"')+(!«»]+", "").toLowerCase();
        }
        return StringUtils.EMPTY;
    }

    private int getIndividualConditionsByTrueDeliveryMode(Seller seller, Order order) {
        if (TariffModeLists.getTariffModeToPostamatList().contains(order.getTrueDeliveryModeCode())) {
            return seller.getPostamatOrderStorageDaysCount();
        } else {
            return seller.getFreeStorageDaysCount();
        }
    }

    private boolean isSellerNamesEqual(String sellerNameFromList, String sellerNameFromOrder) {
        return Objects.equals(cleanSellerName(sellerNameFromList), cleanSellerName(sellerNameFromOrder));
    }
}
