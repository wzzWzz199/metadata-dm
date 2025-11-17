package com.hayden.hap.db.dataSource.itf;

import com.hayden.hap.dbop.db.orm.sql.DynaSqlVO;
import com.hayden.hap.dbop.db.tableDef.entity.TableDefVO;
import com.hayden.hap.db.dataSource.entity.SqlResultVO;

public interface ISimpleSqlBuilder {

    SqlResultVO getUpdateSql(TableDefVO tableDefVO, DynaSqlVO dynaSqlVO,String uniqueCols);
    SqlResultVO getInsertSql(TableDefVO tableDefVO, DynaSqlVO dynaSqlVO, String uniqueCols);
}
