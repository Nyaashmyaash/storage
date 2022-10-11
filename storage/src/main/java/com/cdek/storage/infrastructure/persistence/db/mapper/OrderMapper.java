package com.cdek.storage.infrastructure.persistence.db.mapper;

import com.cdek.storage.model.order.Order;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.Instant;
import java.util.List;

@Mapper
public interface OrderMapper {

    @Insert("INSERT INTO public.orders (order_uuid," +
                                        "order_number," +
                                        "order_status_code," +
                                        "order_type_code," +
                                        "true_delivery_mode_code, " +
                                        "payer_code," +
                                        "payer_uuid," +
                                        "payer_contract_uuid," +
                                        "seller_name," +
                                        "count_day, " +
                                        "deleted," +
                                        "timestamp," +
                                        "payer_contract_number) " +
            "VALUES (#{order.orderUuid}," +
                    "#{order.orderNumber}," +
                    "#{order.orderStatusCode}," +
                    "#{order.orderTypeCode}," +
                    "#{order.trueDeliveryModeCode}," +
                    "#{order.payerCode}," +
                    "#{order.payerUuid}," +
                    "#{order.payerContractUuid}," +
                    "#{order.sellerName}," +
                    "#{order.countDay}," +
                    "#{order.deleted}," +
                    "#{order.timestamp}," +
                    "#{order.payerContractNumber}) " +
            "ON CONFLICT (order_uuid) DO UPDATE SET " +
                    "order_number = EXCLUDED.order_number, " +
                    "order_status_code = EXCLUDED.order_status_code, " +
                    "order_type_code = EXCLUDED.order_type_code, " +
                    "true_delivery_mode_code = EXCLUDED.true_delivery_mode_code, " +
                    "payer_code = EXCLUDED.payer_code, " +
                    "payer_uuid = EXCLUDED.payer_uuid, " +
                    "payer_contract_uuid = EXCLUDED.payer_contract_uuid, " +
                    "seller_name = EXCLUDED.seller_name, " +
                    "count_day = EXCLUDED.count_day, " +
                    "deleted = EXCLUDED.deleted, " +
                    "timestamp = EXCLUDED.timestamp, " +
                    "payer_contract_number = EXCLUDED.payer_contract_number")
    void insert(@Param("order") Order order);

    @Update("UPDATE public.orders " +
            "SET order_number            = #{order.orderNumber}," +
                "order_status_code       = #{order.orderStatusCode}," +
                "order_type_code         = #{order.orderTypeCode}," +
                "true_delivery_mode_code = #{order.trueDeliveryModeCode}," +
                "payer_code              = #{order.payerCode}," +
                "payer_uuid              = #{order.payerUuid}," +
                "payer_contract_uuid     = #{order.payerContractUuid}," +
                "seller_name             = #{order.sellerName}," +
                "count_day               = #{order.countDay}," +
                "deleted                 = #{order.deleted}," +
                "timestamp               = #{order.timestamp}, " +
                "payer_contract_number   = #{order.payerContractNumber} " +
            "WHERE order_uuid = #{order.orderUuid}")
    void update(@Param("order") Order order);

    @Select("SELECT timestamp " +
            "FROM public.orders " +
            "WHERE order_uuid = #{orderUuid}")
    Instant getTimestamp(@Param("orderUuid") String orderUuid);

    @Select("SELECT * " +
            "FROM public.orders " +
            "WHERE order_uuid = #{orderUuid}")
    @Results(id = "contractResultMap", value = {
            @Result(property = "orderUuid", column = "order_uuid"),
            @Result(property = "orderNumber", column = "order_number"),
            @Result(property = "orderStatusCode", column = "order_status_code"),
            @Result(property = "orderTypeCode", column = "order_type_code"),
            @Result(property = "trueDeliveryModeCode", column = "true_delivery_mode_code"),
            @Result(property = "payerCode", column = "payer_code"),
            @Result(property = "payerUuid", column = "payer_uuid"),
            @Result(property = "payerContractUuid", column = "payer_contract_uuid"),
            @Result(property = "sellerName", column = "seller_name"),
            @Result(property = "countDay", column = "count_day"),
            @Result(property = "deleted", column = "deleted"),
            @Result(property = "timestamp", column = "timestamp"),
            @Result(property = "payerContractNumber", column = "payer_contract_number")
    })
    Order findOrderByUuid(@Param("orderUuid") String orderUuid);

    @Delete("DELETE " +
            "FROM public.orders " +
            "WHERE order_uuid = #{orderUuid}")
    void deleteOrderAndPackages(@Param("orderUuid") String orderUuid);

    @Select("SELECT true_delivery_mode_code " +
            "FROM public.orders " +
            "WHERE order_uuid = #{orderUuid}")
    String getTrueDeliveryModeCodeByOrderUuid(@Param("orderUuid") String orderUuid);

    @Select("SELECT EXISTS ( " +
            "SELECT 1 " +
            "FROM public.orders " +
            "WHERE order_uuid = #{orderUuid})")
    boolean isOrderExists(String orderUuid);

    @Select("SELECT count_day " +
            "FROM public.orders " +
            "WHERE order_uuid = #{orderUuid}")
    String findCountDay(@Param("orderUuid") String orderUuid);

    @Select("SELECT order_uuid " +
            "FROM public.orders " +
            "WHERE order_number = #{orderNumber}")
    String getOrderUuidByOrderNumber(@Param("orderNumber") String orderNumber);

    @Select("SELECT o.order_number " +
            "FROM orders o " +
            "LEFT JOIN order_storage os ON o.order_uuid = os.order_uuid " +
            "WHERE os.order_uuid is null " +
            "AND o.true_delivery_mode_code <> '5' " +
            "AND o.timestamp between #{dateFrom} and #{dateTo} " +
            "LIMIT #{limit}")
    List<String> getOrderNumberListWithoutStoragePeriod(
            @Param("dateFrom") Instant dateFrom,
            @Param("dateTo") Instant dateTo,
            @Param("limit") int limit
    );
}
