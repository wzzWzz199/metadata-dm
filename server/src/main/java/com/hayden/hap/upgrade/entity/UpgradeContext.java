package com.hayden.hap.upgrade.entity;

import com.hayden.hap.dbop.entity.AbstractVO;
import com.hayden.hap.dbop.db.tableDef.entity.TableDefVO;
import com.hayden.hap.db.dataSource.entity.MetaRelationVO;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/10 18:03
 */
public class UpgradeContext {
    private String dataSourceId;
    private MetaRelationVO metaRelationVO;
    private TableDefVO tableDefVO;
    private MetaRelationVO parentMetaRelationVO;
    private AbstractVO parentVO;
    private TableDefVO parentTableDefVO;
    private AbstractVO vo;
    private Long tenantid;
    private String[] uniqueColumns;
    private String jsonData;

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public String[] getUniqueColumns() {
        return uniqueColumns;
    }

    public void setUniqueColumns(String[] uniqueColumns) {
        this.uniqueColumns = uniqueColumns;
    }

    public Long getTenantid() {
        return tenantid;
    }

    public void setTenantid(Long tenantid) {
        this.tenantid = tenantid;
    }

    public AbstractVO getVo() {
        return vo;
    }

    public void setVo(AbstractVO vo) {
        this.vo = vo;
    }

    public String getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public MetaRelationVO getMetaRelationVO() {
        return metaRelationVO;
    }

    public void setMetaRelationVO(MetaRelationVO metaRelationVO) {
        this.metaRelationVO = metaRelationVO;
    }

    public TableDefVO getTableDefVO() {
        return tableDefVO;
    }

    public void setTableDefVO(TableDefVO tableDefVO) {
        this.tableDefVO = tableDefVO;
    }

    public MetaRelationVO getParentMetaRelationVO() {
        return parentMetaRelationVO;
    }

    public void setParentMetaRelationVO(MetaRelationVO parentMetaRelationVO) {
        this.parentMetaRelationVO = parentMetaRelationVO;
    }

    public AbstractVO getParentVO() {
        return parentVO;
    }

    public void setParentVO(AbstractVO parentVO) {
        this.parentVO = parentVO;
    }

    public TableDefVO getParentTableDefVO() {
        return parentTableDefVO;
    }

    public void setParentTableDefVO(TableDefVO parentTableDefVO) {
        this.parentTableDefVO = parentTableDefVO;
    }
}
