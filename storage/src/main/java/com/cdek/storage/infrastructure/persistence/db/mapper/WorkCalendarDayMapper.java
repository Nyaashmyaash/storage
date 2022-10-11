package com.cdek.storage.infrastructure.persistence.db.mapper;

import com.cdek.storage.model.calendar.WorkCalendarDay;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface WorkCalendarDayMapper {

    @Insert("<script>" +
                "INSERT INTO public.work_calendar_day " +
                   "(date," +
                    "day_type_code," +
                    "calendar_uuid) " +
                "VALUES" +
                    "<foreach item='day' index='index' collection='days' open='' separator=',' close=''>" +
                       "(#{day.date}," +
                        "#{day.dayTypeCode}," +
                        "#{day.calendarUuid})" +
                    "</foreach>" +
            "</script>")
    void insertList(@Param("days") List<WorkCalendarDay> days);

    @Update("<script>" +
                "<foreach item='day' collection='days' open='' separator=';' close=''>" +
                    "UPDATE public.work_calendar_day " +
                    "SET day_type_code = #{day.dayTypeCode} " +
                    "WHERE date = '${day.date}' " +
                    "AND calendar_uuid = #{day.calendarUuid}" +
                "</foreach>" +
            "</script>")
    void updateList(@Param("days") List<WorkCalendarDay> days);

    @Select("SELECT * " +
            "FROM public.work_calendar_day " +
            "WHERE calendar_uuid = #{calendarUuid} " +
            "AND date = '${date}'")
    @Results(id = "calendarDayResultMap", value = {
            @Result(property = "date", column = "date"),
            @Result(property = "dayTypeCode", column = "day_type_code"),
            @Result(property = "calendarUuid", column = "calendar_uuid")
    })
    WorkCalendarDay findWorkCalendarDayByCalendarUuidAndDate(
            @Param("calendarUuid") String calendarUuid,
            @Param("date") String date
    );

    @Select("<script> " +
                "SELECT * " +
                "FROM public.work_calendar_day " +
                    "INNER JOIN public.work_calendar " +
                    "ON public.work_calendar.calendar_uuid = public.work_calendar_day.calendar_uuid " +
                "WHERE date = '${date}' " +
                    "<if test='regionUuid != null'>" +
                        "AND region_uuid = #{regionUuid} " +
                    "</if>" +
                    "<if test='regionUuid == null'>" +
                        "AND region_uuid is null " +
                    "</if>" +
                "AND country_uuid = #{countryUuid} " +
                "AND active = true" +
            "</script>")
    WorkCalendarDay getDateInformation(
            @Param("countryUuid") String countryUuid,
            @Param("regionUuid") String regionUuid,
            @Param("date") String date
    );
}
