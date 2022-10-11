package com.cdek.storage.infrastructure.persistence.db.mapper;

import com.cdek.storage.model.contract.Contract;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import javax.annotation.Nonnull;
import java.time.Instant;

@Mapper
public interface ContractMapper {

    @Insert("INSERT INTO public.contract (contract_uuid," +
                                          "id," +
                                          "contract_number," +
                                          "type_code," +
                                          "status_code," +
                                          "contragent_uuid," +
                                          "timestamp) " +
            "VALUES (#{contract.contractUuid}," +
                    "#{contract.id}," +
                    "#{contract.number}," +
                    "#{contract.typeCode}," +
                    "#{contract.statusCode}," +
                    "#{contract.contragentUuid}," +
                    "#{contract.timestamp})" )
    void insert(@Param("contract") Contract contract);

    @Update("UPDATE public.contract " +
            "SET id              = #{contract.id}," +
                "contract_number = #{contract.number}," +
                "type_code       = #{contract.typeCode}," +
                "status_code     = #{contract.statusCode}," +
                "contragent_uuid = #{contract.contragentUuid}," +
                "timestamp       = #{contract.timestamp} " +
            "WHERE contract_uuid = #{contract.contractUuid}")
    void update(@Param("contract") Contract contract);

    @Select("SELECT timestamp " +
            "FROM public.contract " +
            "WHERE contract_uuid = #{contractUuid}")
    Instant getTimestamp(@Param("contractUuid") String contractUuid);

    @Select("SELECT * " +
            "FROM public.contract " +
            "WHERE contract_uuid = #{contractUuid}")
    @Results(id = "contractResultMap", value = {
            @Result(property = "contractUuid", column = "contract_uuid"),
            @Result(property = "id", column = "id"),
            @Result(property = "number", column = "contract_number"),
            @Result(property = "typeCode", column = "type_code"),
            @Result(property = "statusCode", column = "status_code"),
            @Result(property = "contragentUuid", column = "contragent_uuid"),
            @Result(property = "timestamp", column = "timestamp"),
    })
    Contract findContractByUuid(@Param("contractUuid") String contractUuid);

    @Select("SELECT contract_uuid " +
            "FROM public.contract " +
            "WHERE contract_number = #{contractNumber}")
    String getContractUuidByContractNumber(@Param("contractNumber") String contractNumber);
}
