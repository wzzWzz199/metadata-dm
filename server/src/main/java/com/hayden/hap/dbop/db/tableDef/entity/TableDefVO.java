package com.hayden.hap.dbop.db.tableDef.entity;

import com.hayden.hap.dbop.entity.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
public class TableDefVO extends BaseVO {

    private List<TableColumnVO> columnList = null;

    private TableColumnVO pkColumnVO = null;

    //当存在联合主键时需要记录所有涉及的字段
    private List<TableColumnVO> pkColumnVOList = null;


    private Long tabledefid;


    private String table_code;


    private String modulecode;


    private Integer issqllog;


    private String table_desc;


    private String ddlsql;


    private String classname;


    private Integer table_type;


    private String table_name;


    private String table_delwhere;


    private Integer isenable;


    public TableDefVO() {
        super("SY_TABLE_DEF");
    }


    public void setTable_code(String table_code) {
        this.table_code = table_code == null ? null : table_code.trim();
    }


    public void setModulecode(String modulecode) {
        this.modulecode = modulecode == null ? null : modulecode.trim();
    }


    public void setTable_desc(String table_desc) {
        this.table_desc = table_desc == null ? null : table_desc.trim();
    }


    public void setDdlsql(String ddlsql) {
        this.ddlsql = ddlsql == null ? null : ddlsql.trim();
    }


    public void setClassname(String classname) {
        this.classname = classname == null ? null : classname.trim();
    }


    public void setTable_name(String table_name) {
        this.table_name = table_name == null ? null : table_name.trim();
    }


    public void setTable_delwhere(String table_delwhere) {
        this.table_delwhere = table_delwhere == null ? null : table_delwhere.trim();
    }

}
