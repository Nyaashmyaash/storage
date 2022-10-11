package com.cdek.storage.infrastructure.persistence.db.mapper;

import com.cdek.storage.model.contract.Seller;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SellerMapper {

    @Insert("INSERT INTO public.sellers (id," +
                                        "contract_uuid," +
                                        "seller_name," +
                                        "free_storage_days_count," +
                                        "timestamp," +
                                        "postamat_order_storage_days_count) " +
            "VALUES " +
                "(#{seller.id}," +
                 "#{seller.contractUuid}," +
                 "#{seller.sellerName}," +
                 "#{seller.freeStorageDaysCount}," +
                 "#{seller.timestamp}," +
                 "#{seller.postamatOrderStorageDaysCount}) " +
            "ON CONFLICT (id) DO UPDATE SET " +
                "contract_uuid                     = EXCLUDED.contract_uuid, " +
                "seller_name                       = EXCLUDED.seller_name, " +
                "free_storage_days_count           = EXCLUDED.free_storage_days_count, " +
                "timestamp                         = EXCLUDED.timestamp, " +
                "postamat_order_storage_days_count = EXCLUDED.postamat_order_storage_days_count")
    void insertOrUpdateSeller(@Param("seller") Seller seller);

    @Select("SELECT * " +
            "FROM public.sellers " +
            "WHERE id = #{sellerId}")
    @Results(id = "sellerResultMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "contractUuid", column = "contract_uuid"),
            @Result(property = "sellerName", column = "seller_name"),
            @Result(property = "freeStorageDaysCount", column = "free_storage_days_count"),
            @Result(property = "timestamp", column = "timestamp"),
            @Result(property = "postamatOrderStorageDaysCount", column = "postamat_order_storage_days_count")
    })
    Seller findSellerById(@Param("sellerId") Long sellerId);

    @Select("SELECT * " +
            "FROM public.sellers " +
            "WHERE contract_uuid = #{contractUuid}")
    @ResultMap("sellerResultMap")
    List<Seller> findSellerListByContractUuid(@Param("contractUuid") String contractUuid);

    @Select("SELECT id " +
            "FROM public.sellers " +
            "WHERE contract_uuid = #{contractUuid}")
    List<Long> getAllSellerId(@Param("contractUuid") String contractUuid);

    @Delete("<script>" +
            "<foreach item='sellerId' collection='sellerIdList' open='' separator=';' close=''>" +
                "DELETE " +
                "FROM public.sellers " +
                "WHERE id = #{sellerId}" +
            "</foreach>" +
            "</script>")
    void deleteSellerList(@Param("sellerIdList") List<Long> sellerIdList);
}
