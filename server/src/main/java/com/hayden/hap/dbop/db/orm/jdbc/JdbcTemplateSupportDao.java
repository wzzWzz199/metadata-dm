package com.hayden.hap.dbop.db.orm.jdbc;

import com.hayden.hap.dbop.entity.AbstractVO;
import com.hayden.hap.dbop.entity.BaseVO;
import com.hayden.hap.dbop.entity.VOSet;
import com.hayden.hap.dbop.db.orm.jdbc.*;
import com.hayden.hap.dbop.db.orm.sql.*;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.dbop.exception.HDRuntimeException;
import com.hayden.hap.dbop.db.orm.entity.ClobInfoVO;
import com.hayden.hap.dbop.db.orm.typeHandler.TypeHandlerRegistry;
import com.hayden.hap.dbop.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.dbop.db.tableDef.entity.TableDefVO;
import com.hayden.hap.dbop.db.tableDef.itf.ITableDefService;
import com.hayden.hap.dbop.db.util.DBSqlUtil;
import com.hayden.hap.dbop.db.util.DBType;
import com.hayden.hap.dbop.db.util.JdbcUtil;
import com.hayden.hap.dbop.db.util.ObjectUtil;
import com.hayden.hap.dbop.reflect.ClassInfo;
import org.apache.commons.beanutils.ConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;

@Component
public class JdbcTemplateSupportDao {
	private static final Logger logger = LoggerFactory.getLogger(JdbcTemplateSupportDao.class);

	private String defaultDataSourceId = null;

	private JdbcTemplateManager jdbcTemplateManager;

	private SqlBuilderManager sqlBuilderManager = new SqlBuilderManager();
	private TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();
	@Autowired
	private ITableDefService tableDefService;

    public TableDefVO getTableDefVO(String tableName) {
        return tableDefService.queryDetailedTableByTbname(tableName);
    }

    public void setVOPkColValue(AbstractVO vo, Object pkValue,
                                TableDefVO tableDefVO) {
        if (vo == null)
            return;
        String tableName = vo.getTableName();
        if (tableDefVO == null)
            tableDefVO = tableDefService.queryDetailedTableByTbname(tableName);
        if (!ObjectUtil.isNotNull(getVOPkColValue(tableDefVO, vo))) {
            String pkColName = tableDefVO.getPkColumnVO() != null ? tableDefVO
                    .getPkColumnVO().getColcode() : null;
            vo.set(pkColName, pkValue);
        }
    }

    public Object getVOPkColValue(TableDefVO tableDefVO, AbstractVO vo) {
        ObjectUtil.validNotNull(vo, "vo is required.");
        if (tableDefVO == null) {
            String tableName = vo.getTableName();
            tableDefVO = tableDefService.queryDetailedTableByTbname(tableName);
        }
        String pkColName = tableDefVO.getPkColumnVO() != null ? tableDefVO
                .getPkColumnVO().getColcode() : null;
        Object pkValue = null;
        if (ObjectUtil.isNotNull(pkColName))
            pkValue = vo.get(pkColName.toLowerCase());
        //当pkValue类型不为Long时，转换成Long类型
        if(pkValue != null && !(pkValue instanceof Long)){
            pkValue = ConvertUtils.convert(pkValue, Long.class);
        }
        return pkValue;
    }
}