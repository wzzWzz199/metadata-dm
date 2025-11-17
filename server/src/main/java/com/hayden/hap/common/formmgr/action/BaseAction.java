package com.hayden.hap.common.formmgr.action;

import com.hayden.hap.common.attach.entity.AttachDataVO;
import com.hayden.hap.common.button.entity.ButtonPCVO;
import com.hayden.hap.common.button.itf.IButtonPCService;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.dbop.exception.ShouldBeCatchException;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.excel.entity.ExcelTemplateVO;
import com.hayden.hap.common.formmgr.entity.BatchUpdateParamVO;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.entity.MetaData;
import com.hayden.hap.common.formmgr.entity.PureVO;
import com.hayden.hap.common.formmgr.itf.IAction;
import com.hayden.hap.common.formmgr.itf.ICardFormService;
import com.hayden.hap.common.formmgr.message.Message;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.utils.session.CurrentEnvUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author zhangfeng
 * @date 2015年11月11日
 */
public class BaseAction implements IAction {

	/** 
	 *
	 * @see com.hayden.hap.sy.formmgr.action.IAction#beforSave(jakarta.servlet.http.HttpServletRequest, com.hayden.hap.common.common.entity.AbstractVO)
	 * @author zhangfeng
	 * @date 2015年11月11日
	 */
	@Override
	public List<Message> beforeCardSave(FormParamVO formParamVO, AbstractVO vo) throws HDException {
		// TODO Auto-generated method stub
		return null;
	}

	/** 
	 *
	 * @see com.hayden.hap.sy.formmgr.action.IAction#afterSave(jakarta.servlet.http.HttpServletRequest, com.hayden.hap.common.common.entity.AbstractVO)
	 * @author zhangfeng
	 * @date 2015年11月11日
	 */
	@Override
	public List<Message> afterCardSave(FormParamVO formParamVO, AbstractVO vo,boolean isAdd) throws HDException {
		// TODO Auto-generated method stub
		return null;
	}

	/** 
	 *
	 * @see com.hayden.hap.sy.formmgr.action.IAction#beforeUpdateBatch(com.hayden.hap.common.formmgr.entity.FormParamVO, com.hayden.hap.common.formmgr.entity.BatchUpdateParamVO)
	 * @author zhangfeng
	 * @date 2015年11月11日
	 */
	@Override
	public List<Message> beforeListUpdate(FormParamVO formParamVO,
			BatchUpdateParamVO batchUpdateParamVO) throws HDException {
//		for(AbstractVO vo : batchUpdateParamVO.getVoList()) {
//			beforeCardSave(formParamVO, vo);
//		}
		return null;
	}

	/** 
	 *
	 * @see com.hayden.hap.sy.formmgr.action.IAction#afterUpdateBatch(com.hayden.hap.common.formmgr.entity.FormParamVO, com.hayden.hap.common.formmgr.entity.BatchUpdateParamVO)
	 * @author zhangfeng
	 * @date 2015年11月11日
	 */
	@Override
	public List<Message> afterListUpdate(FormParamVO formParamVO,
			BatchUpdateParamVO batchUpdateParamVO) throws HDException {
//		for(AbstractVO vo : batchUpdateParamVO.getVoList()) {
//			afterCardSave(formParamVO, vo, false);
//		}
		return null;
	}

	/** 
	 *
	 * @see com.hayden.hap.sy.formmgr.action.IAction#beforDeleteBatch(com.hayden.hap.common.formmgr.entity.FormParamVO)
	 * @author zhangfeng
	 * @date 2015年11月11日
	 */
	@Override
	public List<Message> beforeListDeleteBatch(FormParamVO formParamVO,Collection<Long> primaryKeys)
			throws HDException {
		// TODO Auto-generated method stub
		return null;
	}

	/** 
	 *
	 * @see com.hayden.hap.sy.formmgr.action.IAction#afterDeleteBatch(com.hayden.hap.common.formmgr.entity.FormParamVO)
	 * @author zhangfeng
	 * @date 2015年11月11日
	 */
	@Override
	public List<Message> afterListDeleteBatch(FormParamVO formParamVO,Collection<Long> primaryKeys)
			throws HDException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Message> beforeCardCopy(FormParamVO formParamVO,
			AbstractVO copyVO) throws HDException {
		String changeItemCodes = formParamVO.getRequest().getParameter(ICardFormService.CARD_COPY_CHANGE_PARAM);
		//后端获取平台级按钮参数
		if (StringUtils.isEmpty(changeItemCodes)) {
			IButtonPCService buttonService = (IButtonPCService) AppServiceHelper.findBean("buttonPCService");
			List<ButtonPCVO> buttons = (List<ButtonPCVO>) buttonService.getBtnsByFormcode(formParamVO.getFuncCode(), CurrentEnvUtils.getTenantId());
			for (ButtonPCVO buttonPCVO : buttons) {
				if (buttonPCVO.getBtn_code().equals("saveAndCopy")) {
					String param = buttonPCVO.getBtn_param();
					if (!StringUtils.isEmpty(param)) {
						changeItemCodes = param.split("=")[1];
					}
				}
			}
		}
		if(StringUtils.isEmpty(changeItemCodes)) {
			throw new HDException("复制前操作必须重写，请联系管理员...");
		}
		return null;
	}
	
	/**
	 * 卡片复制后操作
	 * @param formParamVO
	 * @param copyVO
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年4月18日
	 */
	@Override
	public List<Message> afterCardCopy(FormParamVO formParamVO,AbstractVO copyVO) throws HDException {
		return null;
	}

	@Override
	public List<Message> beforeListCopy(
			FormParamVO formParamVO, List<? extends AbstractVO> copyList)
			throws HDException {
		throw new HDException("复制前操作必须重写，请联系管理员...");
	}

	@Override
	public List<Message> afterListCopy(
			FormParamVO formParamVO, List<? extends AbstractVO> copyList)
			throws HDException {
		return null;
	}

	@Override
	public AbstractVO initCardVO(FormParamVO formParamVO) throws HDException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 获取卡片VO后操作
	 * @param vo
	 * @param formParamVO
	 * @param readonly
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年2月9日
	 */
	public void afterGetCardVO(AbstractVO vo, FormParamVO formParamVO, boolean readonly) throws HDException {}
	
	
	/**
	 * 改变卡片只读状态
	 * @param vo
	 * @param formParamVO
	 * @param readonly
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年9月22日
	 */
	public boolean changeCardReadonly(AbstractVO vo, FormParamVO formParamVO, boolean readonly) throws HDException {
		return readonly;
	}
	
	/**
	 * 获取卡片VO前操作
	 * @param vo
	 * @param formParamVO
	 * @param readonly
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年2月9日
	 */
	public void beforeGetCardVO(AbstractVO vo, FormParamVO formParamVO, boolean readonly) throws HDException {}
	
	@Override
	public void beforeListQuery(FormParamVO formParamVO) throws HDException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 重写此方法时候，不要调用super.changedListQuery
	 * 这里抛出来ShouldBeCatchException，就是告诉平台，需要接着走平台的列表查询
	 *
	 * @see com.hayden.hap.common.formmgr.itf.IAction#changedListQuery(com.hayden.hap.common.db.orm.sql.DynaSqlVO, com.hayden.hap.common.formmgr.entity.FormParamVO)
	 * @author zhangfeng
	 * @date 2016年9月26日
	 */
	@Override
	public VOSet<? extends AbstractVO> changedListQuery(DynaSqlVO dynaSqlVO,FormParamVO formParamVO) 
			throws ShouldBeCatchException,HDException {
		throw new ShouldBeCatchException("如果重写此方法，请不要调用super.changedListQuery");
	}
	
	@Override
	public void afterListQuery(FormParamVO formParamVO,
			VOSet<? extends AbstractVO> voset) throws HDException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String beforeShowPrintView(FormParamVO formParamVO)
			throws HDException {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * 获取结构数据前操作
	 * @param formParamVO
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年3月21日
	 */
	public void beforeGetMetaData(FormParamVO formParamVO) throws HDException {}
	
	/**
	 * 获取结构数据后操作
	 * @param formParamVO
	 * @param metaData
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年3月21日
	 */
	public void afterGetMetaData(FormParamVO formParamVO,MetaData metaData) throws HDException {}
	
	/**
	 * 是否需要部门权限
	 * @param formParamVO
	 * @return 
	 * @author zhangfeng
	 * @date 2017年6月20日
	 */
	@Override
	public boolean isNeedOrgPermission(FormParamVO formParamVO) {
		return true;
	}
	
	@Override
	public void beforeExportAttachImage(List<AttachDataVO> attachDataVos, Long tenantid) throws HDException {}

	/**
	 * 业务应该重写此方法，返回业务实现的工作流实现类
	 *
	 * @see com.hayden.hap.common.formmgr.itf.IAction#getWfAction()
	 * @author liyan
	 * @date 2018年1月22日
	 */
	@Override
	public Class<?> getWfAction() throws ClassNotFoundException {
		return null;
	}

	@Override
	public String changeOrgPermissionClause(String orgPermissionClause, FormParamVO formParamVO) {
		return orgPermissionClause;
	}
	
	@Override
	public Class<?> getBusinessAction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void afterGetMetaData4M(FormParamVO formParamVO, PureVO metaData4M) throws HDException {
		// TODO Auto-generated method stub
		
	}


	/**
	 * 导入：批量更新前处理主子表
	 * @param formParamVO
	 * @param excelTemplateVO
	 * @param mainList 主表volist
	 * @param mainAndSubMap 主子表vo对应关系，key值为主功能编码+“.”+子功能编码，value是每个主表vo对应的子表voList
	 * @return
	 * @throws HDException
	 * @author liyan
	 * @date 2017年7月20日
	 */
	@Override
	public List<Message> beforeMegerBatch(
			FormParamVO formParamVO,
			ExcelTemplateVO excelTemplateVO,
			List<AbstractVO> mainList,
			LinkedHashMap<String, LinkedHashMap<AbstractVO, List<AbstractVO>>> mainAndSubMap)
			throws HDException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 导入：对excel转换的vo进行插入更新赋主键等处理后，但是再插入数据库之前做的一些业务操作
	 * @param formParamVO
	 * @param excelTemplateVO
	 * @param mainList 处理前的主表list
	 * @param mainAndSubMap 处理前的主子表vo对应关系，key值为主功能编码+“.”+子功能编码，value是每个主表vo对应的子表voList
	 * @param funcToVoInsertListMap  处理后的每个功能需要插入的list
	 * @param funcToVoAllListMap 处理后的每个功能需要处理的list，包括插入和更新
	 * @return
	 * @author liyan
	 * @date 2018年5月23日
	 */
	@Override
	public List<Message> afterDbBatch(
			FormParamVO formParamVO,
			ExcelTemplateVO excelTemplateVO,
			List<AbstractVO> mainList,
			LinkedHashMap<String, LinkedHashMap<AbstractVO, List<AbstractVO>>> mainAndSubMap,
			Map<String, List<AbstractVO>> funcToVoInsertListMap,
			Map<String, List<AbstractVO>> funcToVoAllListMap) throws HDException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 导入：批量更新后处理主子表
	 * @param formParamVO
	 * @param excelTemplateVO
	 * @param mainList
	 * @param mainAndSubMap 主子表vo对应关系，key值为主功能编码+“.”+子功能编码，value是每个主表vo对应的子表voList
	 * @return
	 * @throws HDException
	 * @author liyan
	 * @date 2017年7月20日
	 */
	@Override
	public List<Message> afterMegerBatch(
			FormParamVO formParamVO,
			ExcelTemplateVO excelTemplateVO,
			List<AbstractVO> mainList,
			LinkedHashMap<String, LinkedHashMap<AbstractVO, List<AbstractVO>>> mainAndSubMap)
			throws HDException {
		// TODO Auto-generated method stub
		return null;
	}
}
