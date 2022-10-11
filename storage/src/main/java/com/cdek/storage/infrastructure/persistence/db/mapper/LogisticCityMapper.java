package com.cdek.storage.infrastructure.persistence.db.mapper;

import com.cdek.storage.model.logistic.LogisticCity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.Instant;

@Mapper
public interface LogisticCityMapper {

    @Insert("INSERT INTO public.logistic_city (city_uuid," +
                                              "city_code," +
                                              "region_uuid," +
                                              "country_uuid," +
                                              "update_timestamp, " +
                                              "time_zone) " +
            "VALUES (#{city.cityUuid}," +
                    "#{city.cityCode}," +
                    "#{city.regionUuid}," +
                    "#{city.countryUuid}," +
                    "#{city.updateTimestamp}," +
                    "#{city.timeZone})" )
    void save(@Param("city") LogisticCity city);

    @Update("UPDATE public.logistic_city " +
            "SET city_code        = #{city.cityCode}, " +
                "region_uuid      = #{city.regionUuid}, " +
                "country_uuid     = #{city.countryUuid}, " +
                "update_timestamp = #{city.updateTimestamp}, " +
                "time_zone        = #{city.timeZone} " +
            "WHERE city_uuid = #{city.cityUuid}")
    void update(@Param("city") LogisticCity city);

    @Select("SELECT update_timestamp " +
            "FROM public.logistic_city " +
            "WHERE city_uuid = #{cityUuid}")
    Instant getTimestamp(@Param("cityUuid") String cityUuid);

    @Select("SELECT * " +
            "FROM public.logistic_city " +
            "WHERE city_code = #{cityCode}")
    @Results(id = "cityResultMap", value = {
            @Result(property = "cityUuid", column = "city_uuid"),
            @Result(property = "cityCode", column = "city_code"),
            @Result(property = "regionUuid", column = "region_uuid"),
            @Result(property = "countryUuid", column = "country_uuid"),
            @Result(property = "updateTimestamp", column = "update_timestamp"),
            @Result(property = "timeZone", column = "time_zone") })
    LogisticCity getLogisticCity(@Param("cityCode") String cityCode);
}
