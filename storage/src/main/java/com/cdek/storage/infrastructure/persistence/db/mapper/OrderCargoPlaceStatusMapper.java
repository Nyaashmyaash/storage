package com.cdek.storage.infrastructure.persistence.db.mapper;

import com.cdek.storage.model.order.CargoPlaceStatus;
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
public interface OrderCargoPlaceStatusMapper {

    @Insert("INSERT INTO public.package_status (status_uuid," +
                                       "order_uuid, " +
                                       "package_uuid, " +
                                       "location, " +
                                       "location_office_uuid, " +
                                       "next_location, " +
                                       "status, " +
                                       "timestamp) " +
            "VALUES (#{cargoPlaceStatus.statusUuid}," +
                    "#{cargoPlaceStatus.orderId}," +
                    "#{cargoPlaceStatus.packageId}," +
                    "#{cargoPlaceStatus.location}," +
                    "#{cargoPlaceStatus.locationOfficeUuid}," +
                    "#{cargoPlaceStatus.nextLocation}," +
                    "#{cargoPlaceStatus.status}," +
                    "#{cargoPlaceStatus.timestamp})")
    void insert(@Param("cargoPlaceStatus") CargoPlaceStatus cargoPlaceStatus);

    @Select("SELECT timestamp " +
            "FROM public.package_status " +
            "WHERE status_uuid = #{statusUuid}")
    Instant getTimestamp(@Param("statusUuid") String statusUuid);

    @Select("SELECT * " +
            "FROM public.package_status " +
            "WHERE status_uuid = #{statusUuid}")
    @Results(id = "statusResultMap", value = {
            @Result(property = "statusUuid", column = "status_uuid"),
            @Result(property = "orderId", column = "order_uuid"),
            @Result(property = "packageId", column = "package_uuid"),
            @Result(property = "location", column = "location"),
            @Result(property = "locationOfficeUuid", column = "location_office_uuid"),
            @Result(property = "nextLocation", column = "next_location"),
            @Result(property = "status", column = "status"),
            @Result(property = "timestamp", column = "timestamp"),
    })
    CargoPlaceStatus findStatusByUuid(@Param("statusUuid") String statusUuid);

    @Select("SELECT status " +
            "FROM public.package_status " +
            "WHERE package_uuid = #{packageUuid} " +
            "ORDER BY \"timestamp\" DESC " +
            "LIMIT 1")
    String findCurrentStatusCodeByPackageUuid(@Param("packageUuid") String packageUuid);

    @Select("SELECT * " +
            "FROM public.package_status " +
            "WHERE package_uuid = #{packageUuid} " +
            "ORDER BY \"timestamp\" DESC " +
            "LIMIT 1")
    @ResultMap("statusResultMap")
    CargoPlaceStatus findCurrentStatusByPackageUuid(@Param("packageUuid") String packageUuid);

    @Select("SELECT location_office_uuid " +
            "FROM public.package_status " +
            "WHERE order_uuid = #{orderUuid} " +
            "ORDER BY \"timestamp\" DESC " +
            "LIMIT 1")
    String getCurrentOfficeUuidByOrderUuid(@Param("orderUuid") String orderUuid);

    @Update("UPDATE public.package_status " +
            "SET order_uuid            = #{cargoPlaceStatus.orderId}," +
                "package_uuid          = #{cargoPlaceStatus.packageId}," +
                "location              = #{cargoPlaceStatus.location}," +
                "location_office_uuid  = #{cargoPlaceStatus.locationOfficeUuid}," +
                "next_location         = #{cargoPlaceStatus.nextLocation}," +
                "status                = #{cargoPlaceStatus.status}," +
                "timestamp             = #{cargoPlaceStatus.timestamp} " +
            "WHERE status_uuid = #{cargoPlaceStatus.statusUuid}")
    void update(@Param("cargoPlaceStatus") CargoPlaceStatus cargoPlaceStatus);

    @Select("SELECT ps.status " +
            "FROM package_status ps " +
            "WHERE ps.package_uuid = #{packageUuid} " +
            "AND ps.status = #{status}")
    List<String> getAllStatusListByPackageUuidAndStatus(@Param("packageUuid") String packageUuid,
            @Param("status") String status);
}
