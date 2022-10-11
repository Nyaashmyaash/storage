package com.cdek.storage.infrastructure.persistence.db.mapper;

import com.cdek.storage.model.order.Package;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface PackageMapper {

    @Insert("INSERT INTO public.packages (package_uuid," +
                                         "order_uuid," +
                                         "package_number," +
                                         "bar_code," +
                                         "itm_bar_code," +
                                         "deleted," +
                                         "timestamp) " +
            "VALUES" +
               "(#{pack.packageUuid}," +
                "#{pack.orderUuid}," +
                "#{pack.packageNumber}," +
                "#{pack.barCode}," +
                "#{pack.itmBarCode}," +
                "#{pack.deleted}," +
                "#{pack.timestamp})" +
            "ON CONFLICT (package_uuid) DO UPDATE SET " +
                "order_uuid     = EXCLUDED.order_uuid, " +
                "package_number = EXCLUDED.package_number, " +
                "bar_code       = EXCLUDED.bar_code, " +
                "itm_bar_code   = EXCLUDED.itm_bar_code, " +
                "deleted        = EXCLUDED.deleted, " +
                "timestamp      = EXCLUDED.timestamp")
    void insertOrUpdatePackage(@Param("pack") Package pack);

    @Select("SELECT * " +
            "FROM public.packages " +
            "WHERE package_uuid = #{packageUuid}")
    @Results(id = "packageResultMap", value = {
            @Result(property = "packageUuid", column = "package_uuid"),
            @Result(property = "orderUuid", column = "order_uuid"),
            @Result(property = "packageNumber", column = "package_number"),
            @Result(property = "barCode", column = "bar_code"),
            @Result(property = "itmBarCode", column = "itm_bar_code"),
            @Result(property = "deleted", column = "deleted"),
            @Result(property = "timestamp", column = "timestamp"),
    })
    Package findPackageByUuid(@Param("packageUuid") String packageUuid);

    @Select("SELECT * " +
            "FROM public.packages " +
            "WHERE deleted = 'false' " +
            "AND order_uuid = #{orderUuid}")
    @ResultMap("packageResultMap")
    List<Package> findNotDeletedPackagesByOrderUuid(@Param("orderUuid") String orderUuid);

    @Select("SELECT * " +
            "FROM public.packages " +
            "WHERE deleted = 'false' " +
            "AND order_uuid = #{orderUuid}")
    @ResultMap("packageResultMap")
    List<Package> getNotDeletedPackagesByOrderUuid(@Param("orderUuid") String orderUuid);

    @Select("SELECT * " +
            "FROM public.packages " +
            "WHERE order_uuid = #{orderUuid}")
    @ResultMap("packageResultMap")
    List<Package> findAllPackagesByOrderUuid(@Param("orderUuid") String orderUuid);

    @Update("<script>" +
            "UPDATE public.packages " +
            "SET deleted = 'true' " +
            "WHERE package_uuid IN " +
            "<foreach item = 'uuid' collection = 'packsToMarkAsDeleted' open = '(' separator = ',' close = ')'>" +
            "#{uuid}" +
            "</foreach>" +
            "</script>")
    void setPackagesIsDeleted(@Param("packsToMarkAsDeleted") List<String> packsToMarkAsDeleted);

    @Update("UPDATE public.packages " +
            "SET deleted = true " +
            "WHERE order_uuid = #{orderUuid}")
    void setAllPackagesIsDeleted(@Param("orderUuid") String orderUuid);

    @Select("SELECT package_uuid " +
            "FROM public.packages " +
            "WHERE deleted = 'false' " +
            "AND order_uuid = #{orderUuid}")
    List<String> getAllNotDeletedPackageUuids(@Param("orderUuid") String orderUuid);
}
