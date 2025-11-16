package com.hayden.hap.common.form.service;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.express.itf.IExpressService;
import com.hayden.hap.common.form.entity.FormConditionDetailVO;
import com.hayden.hap.common.form.entity.FormConditionVO;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.form.itf.IFormDynamicItemService;
import com.hayden.hap.common.form.itf.IFormItemService;
import com.hayden.hap.common.serial.JsonUtils;
import com.hayden.hap.common.utils.SyConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("formDynamicItemService")
public class FormDynamicItemServiceImpl implements IFormDynamicItemService {
    @Autowired
    private IBaseService baseService;

    @Autowired
    private IFormItemService formItemService;

    @Autowired
    @Qualifier("simpleExpressService")
    private IExpressService expressService;

    @Override
    @Cacheable(value="SY_FORM_ITEM",key="#form_code.concat('|').concat(#tenantid).concat('|condition')")
    public List<FormConditionVO> getFormDynamicConfig(String form_code, Long tenantid) {
        DynaSqlVO sql = new DynaSqlVO();
        sql.addWhereParam("form_code",form_code);
        sql.addWhereParam("tenantid", tenantid);
        sql.addWhereParam("isenable", SyConstant.SY_TRUE);
        sql.setOrderByClause("condition_order asc");
        return baseService.query(FormConditionVO.class,sql).getVoList();
    }

    @Override
    @Cacheable(value="SY_FORM_ITEM",key="#form_code.concat('|').concat(#tenantid).concat('|condition_detail')")
    public List<FormConditionDetailVO> getFormDynamicConfigDetailInfo(String form_code, List<String> condition_codes, Long tenantid) {
        DynaSqlVO sql = new DynaSqlVO();
        sql.addWhereParam("form_code",form_code);
        sql.addWhereParam("tenantid", tenantid);
        sql.addWhereParam("condition_code", condition_codes);
        List<FormConditionDetailVO> formConditionDetailVOS = baseService.query(FormConditionDetailVO.class, sql).getVoList();
        //按照条件字段顺序、分组框顺序排序条件动态字段数据
        //1.先根据条件字段顺序组装
        List<FormConditionDetailVO> firstOrderByCondition = new ArrayList<>();
        List<FormConditionVO> conditionVOS = getFormDynamicConfig(form_code, tenantid);
        for (FormConditionVO conditionVO : conditionVOS) {
            firstOrderByCondition.addAll(formConditionDetailVOS.stream().filter(x->conditionVO.getCondition_code().equals(x.getCondition_code())).collect(Collectors.toList()));
        }
        //2.根据分组框顺序排序
        List<FormConditionDetailVO> secondOrderByCondition = new ArrayList<>();
        List<? extends FormItemVO> groups = formItemService.getFormItemsByFormcode(form_code, tenantid).stream().filter(x -> x.getFitem_input_element().equals("9")).collect(Collectors.toList());
        secondOrderByCondition.addAll(firstOrderByCondition.stream().filter(x->"none".equals(x.getGroup_code())).collect(Collectors.toList()));
        for (FormItemVO itemVO: groups) {
            secondOrderByCondition.addAll(firstOrderByCondition.stream().filter(x->itemVO.getFitem_code().equals(x.getGroup_code())).collect(Collectors.toList()));
        }
        return secondOrderByCondition;
    }

    @Override
    public List<? extends FormItemVO> getFormDynamicConfigItems(String form_code, String group_code, String express, Long tenantid) throws HDException {
        DynaSqlVO sql = new DynaSqlVO();
        sql.addWhereParam("form_code", form_code);
        sql.addWhereParam("tenantid",tenantid);
        sql.addWhereParam("condition_info",express);
        sql.addWhereParam("group_code", group_code);
        List<FormConditionDetailVO> conditions = baseService.query(FormConditionDetailVO.class, sql).getVoList();
        if (conditions != null) {
            List<? extends FormItemVO> formItems = formItemService.getFormItemsByFormcode(form_code, tenantid);
            StringBuilder checkedItems = new StringBuilder();
            List<HashMap> fitems = new ArrayList<>();
            for (FormConditionDetailVO c:conditions) {
                checkedItems.append(c.getFitem_codes());
                fitems.addAll(JsonUtils.parseArray(c.getFitem_info(),HashMap.class));
            }
            formItems = formItems.stream().filter(x->checkedItems.toString().contains(","+x.getFitem_code()+",")).collect(Collectors.toList());
            for (HashMap dynaItem : fitems) {
                for (FormItemVO f : formItems) {
                    if(f.getFitem_code().equals(dynaItem.get("fitem_code").toString())){
                        Set set = dynaItem.keySet();
                        Iterator iterator = set.iterator();
                        while (iterator.hasNext()){
                            Object next = iterator.next();
                            f.set(next.toString(), dynaItem.get(next));
                        }
                    }
                }
            }
            return formItems;
        }else{
            return null;
        }
    }

    public void orderFormConditionDetailVOS(List<FormConditionDetailVO> list){
        expressService.orderFormConditionDetailVOS(list);
    }

    @Override
    public void packageMetaData(AbstractVO metaData, String formcode, Long tenantid) {
        List<String> conditions=new ArrayList<>();

        List<FormConditionVO> formConditionVOS=this.getFormDynamicConfig(formcode,tenantid);
        formConditionVOS.forEach(x->conditions.add(x.getCondition_code()));

        List<FormConditionDetailVO> formConditionDetailVOS=this.getFormDynamicConfigDetailInfo(formcode,conditions,tenantid);
        metaData.set("conditionVOs",formConditionVOS);
        metaData.set("conditionDetail",formConditionDetailVOS);
    }
}
