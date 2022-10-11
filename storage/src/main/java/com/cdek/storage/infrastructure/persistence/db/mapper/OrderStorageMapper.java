package com.cdek.storage.infrastructure.persistence.db.mapper;

import com.cdek.storage.application.model.OrderStorage;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.Instant;
import java.util.List;

@Mapper
public interface OrderStorageMapper {

    @Insert("INSERT INTO public.order_storage (order_storage_uuid," +
                                          "order_uuid," +
                                          "order_number," +
                                          "deadline_for_storage," +
                                          "date_of_receipt," +
                                          "shelf_life_order_in_days," +
                                          "timestamp) " +
            "VALUES (#{orderStorage.orderStorageUuid}," +
                    "#{orderStorage.orderUuid}," +
                    "#{orderStorage.orderNumber}," +
                    "#{orderStorage.deadlineForStorage}," +
                    "#{orderStorage.dateOfReceiptInDeliveryOfficeOrPostamat}," +
                    "#{orderStorage.shelfLifeOrderInDays}," +
                    "#{orderStorage.timestamp})" )
    void insert(@Param("orderStorage") OrderStorage orderStorage);

    @Update("UPDATE public.order_storage " +
            "SET order_uuid               = #{orderStorage.orderUuid}," +
                "order_number             = #{orderStorage.orderNumber}," +
                "deadline_for_storage     = #{orderStorage.deadlineForStorage}," +
                "date_of_receipt          = #{orderStorage.dateOfReceiptInDeliveryOfficeOrPostamat}," +
                "shelf_life_order_in_days = #{orderStorage.shelfLifeOrderInDays}," +
                "timestamp                = #{orderStorage.timestamp} " +
            "WHERE order_storage_uuid = #{orderStorage.orderStorageUuid}")
    void update(@Param("orderStorage") OrderStorage orderStorage);

    @Select("SELECT timestamp " +
            "FROM public.order_storage " +
            "WHERE order_storage_uuid = #{orderStorageUuid}")
    Instant getTimestamp(@Param("orderStorageUuid") String orderStorageUuid);

    @Select("SELECT * " +
            "FROM public.order_storage " +
            "WHERE order_storage_uuid = #{orderStorageUuid}")
    @ResultMap("orderStorageResultMap")
    OrderStorage findOrderStorageByUuid(@Param("orderStorageUuid") String orderStorageUuid);

    @Select("SELECT EXISTS ( " +
            "SELECT 1 " +
            "FROM public.order_storage " +
            "WHERE shelf_life_order_in_days IS NOT NULL " +
            "AND order_uuid = #{orderUuid})")
    boolean isOrderStorageExists(String orderUuid);

    @Select("SELECT * " +
            "FROM public.order_storage " +
            "WHERE order_uuid = #{orderUuid}" +
            "ORDER BY timestamp " +
            "LIMIT 1")
    @Results(id = "orderStorageResultMap", value = {
            @Result(property = "orderStorageUuid", column = "order_storage_uuid"),
            @Result(property = "orderUuid", column = "order_uuid"),
            @Result(property = "orderNumber", column = "order_number"),
            @Result(property = "deadlineForStorage", column = "deadline_for_storage"),
            @Result(property = "dateOfReceiptInDeliveryOfficeOrPostamat", column = "date_of_receipt"),
            @Result(property = "shelfLifeOrderInDays", column = "shelf_life_order_in_days"),
            @Result(property = "timestamp", column = "timestamp")
    })
    OrderStorage findOrderStorageByOrderUuid(@Param("orderUuid") String orderUuid);

    @Select("SELECT * " +
            "FROM public.order_storage " +
            "WHERE order_uuid = #{orderUuid}" +
            "ORDER BY timestamp " +
            "LIMIT 1")
    @ResultMap("orderStorageResultMap")
    OrderStorage getOrderStorageByOrderUuid(@Param("orderUuid") String orderUuid);

    @Select("SELECT * " +
            "FROM public.order_storage " +
            "WHERE order_number = #{orderNumber}")
    @ResultMap("orderStorageResultMap")
    OrderStorage findOrderStorageByOrderNumber(@Param("orderNumber") String orderNumber);

    @Select("SELECT * " +
            "FROM public.order_storage " +
            "WHERE timestamp between #{dateFrom} and #{dateTo} " +
            "ORDER BY timestamp " +
            "LIMIT #{limit}")
    @ResultMap("orderStorageResultMap")
    List<OrderStorage> getOrderStorageList(@Param("dateFrom") Instant dateFrom, @Param("dateTo") Instant dateTo,
            @Param("limit") int limit);

    @Delete("DELETE " +
            "FROM public.order_storage " +
            "WHERE order_number = #{orderNumber}")
    void deleteOrderStoragePeriod(@Param("orderNumber") String orderNumber);

    @Select("SELECT order_number " +
            "FROM public.order_storage " +
            "WHERE timestamp between #{dateFrom} and #{dateTo} " +
            "GROUP BY order_number " +
            "HAVING count(order_number) > 1 " +
            "LIMIT #{limit}")
    List<String> getOrderNumberWithDuplicateStoragePeriod(@Param("dateFrom") Instant dateFrom, @Param("dateTo") Instant dateTo,
            @Param("limit") int limit);
}
