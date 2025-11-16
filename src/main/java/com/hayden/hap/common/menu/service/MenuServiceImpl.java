package com.hayden.hap.common.menu.service;

import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.util.DBSqlUtil;
import com.hayden.hap.common.menu.entity.MenuVO;
import com.hayden.hap.common.menu.itf.IMenuService;
import com.hayden.hap.common.utils.SyConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service("menuService")
public class MenuServiceImpl implements IMenuService {

	@Autowired
	IBaseService baseService;

	/**
	 * 
	 *
	 * @see com.hayden.hap.common.menu.itf.IMenuService#getEnableMenuListByTenant()
	 * @author lianghua
	 * @date 2015年11月17日
	 */
	@Override
	public List<MenuVO> getEnableMenuListByTenant(Long tenantid,String clientType) {
		MenuVO menuVO = new MenuVO();
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		StringBuffer whereStringBuffer = new StringBuffer("isenable=1 and tenantid=");
		whereStringBuffer.append(tenantid);
		if(clientType!=null){
			whereStringBuffer.append(" and menuclienttype=");
			whereStringBuffer.append(clientType);
		}
		String orderByClause="menuorder";
		dynaSqlVO.setWhereClause(whereStringBuffer.toString());
		dynaSqlVO.setOrderByClause(orderByClause);
		VOSet<MenuVO> setvo = baseService.query(menuVO, dynaSqlVO);
		List<MenuVO> list = setvo.getVoList();
		return list;
	}
	
	/**
	 * 
	 *
	 * @see com.hayden.hap.common.menu.itf.IMenuService#getAllMenuListByTenant()
	 * @author wushuangyang
	 * @date 2015年11月17日
	 */
	@Override
	public List<MenuVO> getAllMenuListByTenant(Long tenantid,String clientType) {
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam("tenantid", tenantid);
		dynaSqlVO.addWhereParam("isenable", 1);
		if(clientType!=null){
			dynaSqlVO.addWhereParam("menuclienttype",clientType);
		}
		dynaSqlVO.setOrderByClause("menuorder");
		VOSet<MenuVO> setvo = baseService.query(MenuVO.class, dynaSqlVO);
		List<MenuVO> list = setvo.getVoList();
		return list;
	}

	@Override
	public List<MenuVO> listMenuVOByCodeAndTenantId(Collection<String> menuCodes, Long tenantid) {
		String conditionSql = DBSqlUtil.getConditionSql("menucode", menuCodes, true);
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereClause(conditionSql);
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
		VOSet<MenuVO> voSet = baseService.query(MenuVO.class, dynaSqlVO);
		return voSet.getVoList();
	}
}
