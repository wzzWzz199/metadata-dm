package com.hayden.hap.dbop.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;


@EqualsAndHashCode(callSuper = true)
@Data
public class BaseVO extends CommonVO {
    /**
     * serialVersionUID:TODO().
     */
    private static final long serialVersionUID = 1L;

    /**
     * 乐观锁版本
     */
    private Integer ver = 1;

    /**
     * 创建者(登录帐号)
     */
    private Long created_by;

    /**
     * 创建时间
     */
    private Date created_dt = new Date();

    /**
     * 最后更新者(登录帐号)
     */
    private Long updated_by;

    /**
     * 最后更新时间
     */
    private Date updated_dt = new Date();

    /**
     * 逻辑删除标识
     */
    private Integer df = 0;

    /**
     * 租户id
     */
    private Long tenantid;

    /**
     * 时间戳
     */
    private Long ts;

    public BaseVO(String tableName) {
        super(tableName.toLowerCase());
    }

    public BaseVO() {

    }
}
