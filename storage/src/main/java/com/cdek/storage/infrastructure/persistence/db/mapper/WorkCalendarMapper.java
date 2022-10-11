package com.cdek.storage.infrastructure.persistence.db.mapper;

import com.cdek.storage.model.calendar.WorkCalendar;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.Instant;

@Mapper
public interface WorkCalendarMapper {

    @Insert("INSERT INTO public.work_calendar (" +
                                        "calendar_uuid," +
                                        "active," +
                                        "date_updated," +
                                        "year," +
                                        "country_code, " +
                                        "country_uuid, " +
                                        "region_code, " +
                                        "region_uuid) " +
            "VALUES (#{calendar.calendarUuid}," +
                    "#{calendar.active}," +
                    "#{calendar.dateUpdated}," +
                    "#{calendar.year}," +
                    "#{calendar.countryCode}," +
                    "#{calendar.countryUuid}," +
                    "#{calendar.regionCode}," +
                    "#{calendar.regionUuid})")
    void insert(@Param("calendar") WorkCalendar calendar);

    @Update("UPDATE public.work_calendar " +
            "SET active       = #{calendar.active}," +
                "date_updated = #{calendar.dateUpdated}," +
                "year         = #{calendar.year}," +
                "country_code = #{calendar.countryCode}," +
                "country_uuid = #{calendar.countryUuid}," +
                "region_code  = #{calendar.regionCode}," +
                "region_uuid  = #{calendar.regionUuid} " +
            "WHERE calendar_uuid = #{calendar.calendarUuid}")
    void update(@Param("calendar") WorkCalendar calendar);

    @Select("SELECT date_updated " +
            "FROM public.work_calendar " +
            "WHERE calendar_uuid = #{calendarUuid}")
    Instant getTimestamp(@Param("calendarUuid") String calendarUuid);

    @Select("SELECT * " +
            "FROM public.work_calendar " +
            "WHERE calendar_uuid = #{calendarUuid}")
    @Results(id = "calendarResultMap", value = {
            @Result(property = "calendarUuid", column = "calendar_uuid"),
            @Result(property = "active", column = "active"),
            @Result(property = "dateUpdated", column = "date_updated"),
            @Result(property = "year", column = "year"),
            @Result(property = "countryCode", column = "country_code"),
            @Result(property = "countryUuid", column = "country_uuid"),
            @Result(property = "regionCode", column = "region_code"),
            @Result(property = "regionUuid", column = "region_uuid")
    })
    WorkCalendar findWorkCalendarByUuid(@Param("calendarUuid") String calendarUuid);

    @Select("SELECT EXISTS ( " +
            "SELECT 1 " +
            "FROM public.work_calendar " +
            "WHERE region_uuid = #{regionUuid} " +
            "AND year = #{year} " +
            "AND active = true)")
    boolean isWorkCalendarExists(@Param("regionUuid") String regionUuid, @Param("year") Integer year);
}
