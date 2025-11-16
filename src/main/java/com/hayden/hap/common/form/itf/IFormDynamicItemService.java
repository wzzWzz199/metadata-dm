package com.hayden.hap.common.form.itf;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.form.entity.FormConditionDetailVO;
import com.hayden.hap.common.form.entity.FormConditionVO;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.spring.service.IService;

import java.util.List;

/**
 * @author yinbinchen
 * @date 2021年6月11日
 */
@IService("formDynamicItemService")
public interface IFormDynamicItemService {

    /**
     * 获取表单动态字段配置列表
     * @param form_code 表单编码
     * @param tenantid
     * @return
     */
    public List<FormConditionVO> getFormDynamicConfig(String form_code,Long tenantid);

    /**
     * 获取表单动态字段配置详情列表
     * @param form_code 表单编码
     * @param condition_codes 动态字段编码集合
     * @param tenantid
     * @return
     */
    public List<FormConditionDetailVO> getFormDynamicConfigDetailInfo(String form_code,List<String> condition_codes,Long tenantid);


    /**
     * 获取表单动态字段配置下的表单字段列表
     * @param form_code 表单编码
     * @param group_code 分组框编码
     * @param express 条件表达式
     * @param tenantid
     * @return
     */
    public List<? extends FormItemVO> getFormDynamicConfigItems(String form_code, String group_code,String express, Long tenantid) throws HDException;

    /**
     * 对表达式进行排序，按照所有符合条件的父级在前子集在后的顺序
     * @param list
     */
    void orderFormConditionDetailVOS(List<FormConditionDetailVO> list);

    void packageMetaData(AbstractVO metaData, String formcode, Long tenantid);
}
