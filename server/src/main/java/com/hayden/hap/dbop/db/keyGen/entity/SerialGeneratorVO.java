package com.hayden.hap.dbop.db.keyGen.entity;

import com.hayden.hap.dbop.entity.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class SerialGeneratorVO extends BaseVO {
    /**
     * serialVersionUID:TODO().
     */
    private static final long serialVersionUID = 1L;
    private Long serialgenid;
    private String gencode;
    private String gennext;
    private Long gencache;
    private String genprefix;
    private long counter;

    public SerialGeneratorVO() {
        super("SY_SERIAL_GENERATOR");
    }

}
