package com.hayden.hap.db.dataSource.itf;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.common.db.tableDef.entity.TableDefVO;
import com.hayden.hap.db.dataSource.entity.SqlResultVO;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.List;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/10 11:03
 */
public interface ISimpleJdbcTemplateSupportDao {

    <T extends AbstractVO> int update(T vo, SqlResultVO sqlResultVO, String dataSourceId);

    <T extends AbstractVO> List<TableColumnVO> getTableColVoList(String catalog, String schema, String tableName, Long parentid, String dataSourceId);


    TableDefVO queryDetailedTableByTbname(String tbname, List<String> colList, String dataSourceId);

    TableDefVO queryDetailedTableByTbname(String tbname, String dataSourceId);

    <T> List<T> executeQuery(String sql, DynaSqlVO dynaSqlVO, Object[] preStatementParam, ResultSetExtractor<T> resultSetExtractor, String dataSourceId);


    <T> List<T> executeQueryByNameParameter(String sql, MapSqlParameterSource mapSqlParameterSource, ResultSetExtractor<T> resultSetExtractor, String dataSourceId);

    <T extends AbstractVO> VOSet<T> query(String tableName, DynaSqlVO dynaSqlVO, String dataSourceId);

    <T extends AbstractVO> VOSet<T> query(AbstractVO vo, DynaSqlVO dynaSqlVO, String dataSourceId);

    <T extends AbstractVO> VOSet<T> query(Class clz, DynaSqlVO dynaSqlVO, String dataSourceId) throws HDException;

    int queryCount(String countSql, String dataSourceId, PreparedStatementSetter pss);

    <T extends AbstractVO> int update(T vo, DynaSqlVO dynaSqlVO, String dataSourceId);

    <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList, DynaSqlVO dynaSqlVO, String dataSourceId);

    int executeUpate(String sql, String tableName, String dataSourceId);

    void executeSql(String sql, String dataSourceId);

    <T extends AbstractVO> int insert(T vo, DynaSqlVO dynaSqlVO, String dataSourceId);
}
