package com.yami.shop.common.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;
import java.util.Objects;

/**
 * mybatis Long-DateTime类型处理器
 * @author gaozijie
 * @since 2024-03-13
 */
@MappedTypes({Long.class})
@MappedJdbcTypes(JdbcType.DATETIMEOFFSET)
public class LongToDateTypeHandler extends BaseTypeHandler<Long> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Long parameter, JdbcType jdbcType) throws SQLException {
        Timestamp timestamp = Objects.isNull(parameter) ? null : new Timestamp(parameter);
        ps.setTimestamp(i , timestamp);
    }

    @Override
    public Long getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.timestampToLong(rs.getTimestamp(columnName));
    }

    @Override
    public Long getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.timestampToLong(rs.getTimestamp(columnIndex));
    }

    @Override
    public Long getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.timestampToLong(cs.getTimestamp(columnIndex));
    }

    private Long timestampToLong(Timestamp timestamp) {
        return Objects.isNull(timestamp) ? null : timestamp.getTime();
    }
}
