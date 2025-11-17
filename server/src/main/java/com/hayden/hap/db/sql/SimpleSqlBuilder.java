package com.hayden.hap.db.sql;

import com.hayden.hap.dbop.db.orm.sql.DynaSqlVO;
import com.hayden.hap.dbop.db.orm.sql.JdbcType;
import com.hayden.hap.dbop.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.dbop.db.tableDef.entity.TableDefVO;
import com.hayden.hap.db.dataSource.entity.SqlResultVO;
import com.hayden.hap.db.dataSource.itf.ISimpleSqlBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/10 9:18
 */
@Component("mdSqlBuilder")
public class SimpleSqlBuilder implements ISimpleSqlBuilder {

    public SqlResultVO getUpdateSql(TableDefVO tableDefVO, DynaSqlVO dynaSqlVO, String uniqueCols) {
        StringBuffer sf = new StringBuffer();
        SqlResultVO sqlResultVO = initSqlResultVO();

        String tableName = tableDefVO.getTable_code();
        sf.append("update " + tableName.toLowerCase());
        sf.append(getPrepareUpdateSetSqlString(tableDefVO, sqlResultVO, dynaSqlVO, uniqueCols));
        sqlResultVO.setSql(sf.toString());
        return sqlResultVO;
    }

    private String getPrepareUpdateSetSqlString(TableDefVO tableDefVO, SqlResultVO sqlResultVO, DynaSqlVO dynaSqlVO, String uniqueCols) {
        uniqueCols = uniqueCols + ",";
        StringBuffer sf = new StringBuffer();
        StringBuffer wheresf = new StringBuffer();
        List<String> whereCols = new ArrayList<>();
        List<Integer> whereType = new ArrayList<>();

        sf.append(" set");

        List<TableColumnVO> tableColumnVOS = tableDefVO.getColumnList();
        for (TableColumnVO tableColumnVO : tableColumnVOS) {
            JdbcType jdbcType = JdbcType.forName(tableColumnVO.getColtype());
            if (!tableDefVO.getPkColumnVO().getColcode().equals(tableColumnVO.getColcode()) && !uniqueCols.contains(tableColumnVO.getColcode() + ",")) {
                sf.append(" " + tableColumnVO.getColcode().trim() + " = ?,");
                sqlResultVO.getPreStatementCols().add(tableColumnVO.getColcode().trim());
                sqlResultVO.getPreStatementParamJdbcTypes().add(jdbcType == null ? null : jdbcType.getTypeCode());
            }

            if (dynaSqlVO.getWhereParamMap().containsKey(tableColumnVO.getColcode().trim())) {
                wheresf.append(tableColumnVO.getColcode().trim() + " = ?").append(" and ");
                whereCols.add(tableColumnVO.getColcode().trim());
                whereType.add(jdbcType == null ? null : jdbcType.getTypeCode());
            }
        }

        sqlResultVO.getPreStatementCols().addAll(whereCols);
        sqlResultVO.getPreStatementParamJdbcTypes().addAll(whereType);

        int sfLength = sf.length();
        sf.delete(sfLength - 1, sfLength);

        if (whereCols.size() > 0) {
            int whereLength = wheresf.length();
            wheresf.delete(whereLength - 5, whereLength);
            sf.append(" where ").append(wheresf);
        }
        return sf.toString();
    }

    @Override
    public SqlResultVO getInsertSql(TableDefVO tableDefVO, DynaSqlVO dynaSqlVO, String uniqueCols) {
        StringBuffer sf = new StringBuffer();
        SqlResultVO sqlResultVO = initSqlResultVO();

        sf.append(" insert into " + tableDefVO.getTable_code().toLowerCase());
        sf.append(getInsertSqlValues(tableDefVO, sqlResultVO));
        sqlResultVO.setSql(sf.toString());
        return sqlResultVO;
    }


    private String getInsertSqlValues(TableDefVO tableDefVO, SqlResultVO sqlResultVO) {

        StringBuffer sf = new StringBuffer();
        StringBuffer valuesf = new StringBuffer();

        sf.append(" (");
        valuesf.append(" values (");

        List<TableColumnVO> tableColumnVOS = tableDefVO.getColumnList();
        for (TableColumnVO tableColumnVO : tableColumnVOS) {
            JdbcType jdbcType = JdbcType.forName(tableColumnVO.getColtype());
            sf.append(tableColumnVO.getColcode().trim() + ",");
            valuesf.append("?,");
            sqlResultVO.getPreStatementCols().add(tableColumnVO.getColcode().trim());
            sqlResultVO.getPreStatementParamJdbcTypes().add(jdbcType == null ? null : jdbcType.getTypeCode());
        }
        int sfLength = sf.length();
        sf.delete(sfLength - 1, sfLength);
        sf.append(")");

        int valuesfLength = valuesf.length();
        valuesf.delete(valuesfLength - 1, valuesfLength);
        valuesf.append(")");

        sf.append(valuesf);

        return sf.toString();
    }

    private SqlResultVO initSqlResultVO() {
        SqlResultVO sqlResultVO = new SqlResultVO();
        List<String> preStatementCols = new ArrayList<>();
        List<Integer> preStatementParamJdbcTypes = new ArrayList<>();

        sqlResultVO.setPreStatementCols(preStatementCols);
        sqlResultVO.setPreStatementParamJdbcTypes(preStatementParamJdbcTypes);
        return sqlResultVO;
    }

}
