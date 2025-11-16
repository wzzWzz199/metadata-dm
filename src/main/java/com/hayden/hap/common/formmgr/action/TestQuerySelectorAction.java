package com.hayden.hap.common.formmgr.action;

import com.hayden.hap.common.formmgr.annotation.BeforeQSListQuery;
import com.hayden.hap.common.formmgr.annotation.ItemMapping;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import org.springframework.stereotype.Component;

/**
 * 
 * 查询选择action样例
 * 
 * 这个类由表单action的getQuerySelectorAction接口返回
 * @author zhangfeng
 * @date 2017年10月10日
 */
@Component("testQuerySelectorAction")//此注解可选，当你需要依赖注入时候，该注解是需要的
public class TestQuerySelectorAction {

	/**
	 * 例如我当前功能的orgname是一个查询选择字段，我需要对部门查询选择的数据进行特殊的过滤规则，
	 * 那么我就需要通过查询操作对查询条件进行修改
	 * 
	 * @param formParamVO 
	 * @author zhangfeng
	 * @date 2017年10月10日
	 */
	@ItemMapping("orgname")//这是你查询选择的字段编码，当这是多级的查询选择时，从底到顶字段按逗号隔开
	@BeforeQSListQuery//查询前操作
	public void ttt(FormParamVO formParamVO) {//参数选填，目前支持FormParamVO对象，以后会支持更多；方法名随便定义
		//你的逻辑，可以对formParamVO进行修改
		String where = " orgid in (1,2,3)";
		formParamVO.setExtWhere(where);
	}
}
