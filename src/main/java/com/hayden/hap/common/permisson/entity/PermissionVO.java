package com.hayden.hap.common.permisson.entity;

import com.hayden.hap.common.common.entity.CommonVO;
import lombok.Data;

@Data
public class PermissionVO extends CommonVO {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private String id;// 该字段主要用于保存时，得到菜单和按钮id，保存到关系表中，不参与构建树
    private String code;
    private String pcode;// 构建树时，该字段为pid
    private String name;
    private String codepath;// 构建树时，该字段为id
    private boolean checked;
    private boolean nocheck;
    private String permissionType;
    private String topFuncCode;
    private String funcode;// 如果是button该值为其所属的func，如果是func该值为自己的code,如果是menu该值为所关联的func
    private String btntype;// 按钮类别
    private String url;// 按钮是url类别时，请求的url地址
    private String iconSkin;// 图标样式
    private Integer isleaf;// 针对菜单，标记是否叶子节点
    private Integer isBuy;// 是否已经购买此功能（0否1是）
    private String menucode;// 菜单编码

}
