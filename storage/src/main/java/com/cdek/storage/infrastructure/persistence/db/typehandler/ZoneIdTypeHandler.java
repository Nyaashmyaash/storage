package com.cdek.storage.infrastructure.persistence.db.typehandler;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;

@Slf4j
public class ZoneIdTypeHandler extends BaseTypeHandler<ZoneId> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, ZoneId zoneId, JdbcType jdbcType)
            throws SQLException {
        preparedStatement.setString(i, zoneId.toString());
    }

    @Override
    public ZoneId getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        return getZoneId(resultSet.getString(columnName));
    }

    @Override
    public ZoneId getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        return getZoneId(resultSet.getString(columnIndex));
    }

    @Override
    public ZoneId getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        return getZoneId(callableStatement.getString(columnIndex));
    }

    private static ZoneId getZoneId(String zoneId) {
        try {
            return ZoneId.of(zoneId);
        } catch (Exception e) {
            log.debug("Can't read ZoneId [{}] from DB", zoneId, e);
            return null;
        }
    }
}
