package com.cdek.storage.infrastructure.persistence.db.mapper;

import com.cdek.storage.model.office.Office;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.Instant;

@Mapper
public interface OfficeMapper {

    @Insert("INSERT INTO public.office (office_uuid," +
                                       "city_code," +
                                       "update_timestamp) " +
            "VALUES (#{office.officeUuid}," +
                    "#{office.cityCode}," +
                    "#{office.updateTimestamp})" )
    void save(@Param("office") Office office);

    @Update("UPDATE public.office " +
            "SET city_code        = #{office.cityCode}, " +
                "update_timestamp = #{office.updateTimestamp} " +
            "WHERE office_uuid = #{office.officeUuid}")
    void update(@Param("office") Office office);

    @Select("SELECT update_timestamp " +
            "FROM public.office " +
            "WHERE office_uuid = #{officeUuid}")
    Instant getTimestamp(@Param("officeUuid") String officeUuid);

    @Select("SELECT * " +
            "FROM public.office " +
            "WHERE office_uuid = #{officeUuid}")
    @Results(id = "officeResultMap", value = {
            @Result(property = "officeUuid", column = "office_uuid"),
            @Result(property = "cityCode", column = "city_code"),
            @Result(property = "updateTimestamp", column = "update_timestamp") })
    Office getOfficeByUuid(@Param("officeUuid") String officeUuid);
}
