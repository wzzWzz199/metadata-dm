package com.hayden.hap.common.form.service;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.form.entity.TestDate;
import com.hayden.hap.common.formmgr.action.BaseAction;
import com.hayden.hap.common.formmgr.annotation.BeforeQSListQuery;
import com.hayden.hap.common.formmgr.annotation.ItemMapping;
import com.hayden.hap.common.formmgr.annotation.LastCardSave;
import com.hayden.hap.common.formmgr.annotation.LastGetCardVO;
import com.hayden.hap.common.formmgr.control.CardCtrlVO;
import com.hayden.hap.common.formmgr.entity.BatchUpdateParamVO;
import com.hayden.hap.common.formmgr.entity.CardDataVO;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.message.Message;

import java.util.Collection;
import java.util.List;
//import com.hayden.hap.common.workflow.annotation.LastWfSend;
//import com.hayden.hap.common.workflow.entity.WfReturnResultVO;
//import com.hayden.hap.common.workflow.entity.WorkflowParamVO;

public class TestDateAction extends BaseAction {
	
	@Override
	public List<Message> beforeCardSave(FormParamVO formParamVO, AbstractVO vo) {
//		IFuncService funcService = (IFuncService)AppServiceHelper.findBean("funcService");
		System.out.println("IorU前操作...");
//		funcService.queryByFunccode("abc");
		return null;
	}

	@Override
	public List<Message> afterCardSave(FormParamVO formParamVO, AbstractVO vo,boolean isAdd) throws HDException {
		System.out.println("IorU后操作...");
//		if(1==1) {
//			throw new HDException("保存后操作异常...");
//		}
		return null;
	}

	@Override
	public List<Message> beforeListDeleteBatch(FormParamVO formParamVO,Collection<Long> primaryKeys) {
		System.out.println("删除前操作...");
		return null;
	}

	@Override
	public List<Message> afterListDeleteBatch(FormParamVO formParamVO,Collection<Long> primaryKeys) throws HDException {
		System.out.println("删除后操作...");
//		if(1==0) {
//			throw new HDException("");
//		}
		return null;
	}

	@Override
	public List<Message> beforeListUpdate(FormParamVO formParamVO,
			BatchUpdateParamVO batchUpdateParamVO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Message> afterListUpdate(FormParamVO formParamVO,
			BatchUpdateParamVO batchUpdateParamVO) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public List<Message> beforeWfSendShow(WorkflowParamVO workflowParamVO, AbstractVO abstractVO)
//			throws HDException {
////		throw new HDException("出现了某异常...");
//		return null;
//	}
	
	@LastCardSave
	public void lastCardSave(FormParamVO formParamVO, TestDate vo, boolean isAdd, CardDataVO cardDataVO) {
		System.out.println(formParamVO.getFuncCode());
		System.out.println(vo);
		System.out.println(isAdd);
		System.out.println(cardDataVO.getData());
		
		CardCtrlVO cardCtrlVO = cardDataVO.getCardCtrlVO();
		if(cardCtrlVO==null) {
			cardCtrlVO = new CardCtrlVO();
		}
		if(vo.getVer()==2) {			
			cardCtrlVO.addHideItem("df").addReadonlyItem("wf_instance_seq").addRequireItem("a");
		}else {
			cardCtrlVO.addShowItem("df").addEditeableItem("wf_instance_seq").addNorequireItem("a");
		}
		cardDataVO.setCardCtrlVO(cardCtrlVO);
	}
	
	@LastGetCardVO
	public void lastGetCardVO(CardDataVO cardDataVO) {
		
	}
	
//	@LastWfSend
//	public void lastWfSend(WfReturnResultVO wfReturnResultVO) {
//		
//	}
	
	/**
	 * 例如我当前功能的orgname是一个查询选择字段，我需要对部门查询选择的数据进行特殊的过滤规则，
	 * 那么我就需要通过查询操作对查询条件进行修改
	 * 
	 * @param formParamVO 
	 * @author zhangfeng
	 * @date 2017年10月10日
	 */
	@ItemMapping("orgname")//这是你查询选择的字段编码，当这是多级的查询选择时，从底到顶字段按逗号隔开
	@BeforeQSListQuery//查询选择查询前操作
	public void ttt(FormParamVO formParamVO) {//参数选填，目前支持FormParamVO对象，以后会支持更多；方法名随便定义
		//你的逻辑，可以对formParamVO进行修改
		String where = " orgid in (1,2,3)";
		formParamVO.setExtWhere(where);
	}
}
