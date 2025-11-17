package com.hayden.hap.common.utils;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.BaseVO;
import com.hayden.hap.dbop.exception.HDRuntimeException;
import com.hayden.hap.common.tenant.utils.TenantUtil;
import com.hayden.hap.common.utils.session.CurrentEnvUtils;

import java.util.Date;
import java.util.List;

/**
 * 基础属性设置
 * @author zhangfeng
 * @date 2015年11月12日
 */
public class BaseSettingUtils {

	private static final String CREATE_BY_NAME = "created_by_name";
	private static final String UPDATE_BY_NAME = "updated_by_name";
	
	private static Long getTenantId() {
		return TenantUtil.getCurrentDataTenantid(CurrentEnvUtils.getTenantId());
	}
	
	private static Long getUserId() {
		return TenantUtil.getCurrentDataUserid(CurrentEnvUtils.getUserId());
	}
	
	private static String getUserName() {
		return CurrentEnvUtils.getUserName();
	}
	
	
	/**
	 * 设置租户id为当前租户
	 * @param vo
	 * @param tenantid 
	 * @author zhangfeng
	 * @date 2015年11月12日
	 */
	public static AbstractVO setTenantid(AbstractVO vo) {
		if(vo.get(SyConstant.TENANT_STR)!=null) 
			return vo;
		
		Long tenantid = getTenantId();
		if(tenantid==null) 
			throw new HDRuntimeException("租户id为空...");	
		if(vo instanceof BaseVO) {
			((BaseVO)vo).setTenantid(tenantid);
		}else {
			vo.set(SyConstant.TENANT_STR, tenantid);
		}
		return vo;
	}

	/**
	 * 设置租户id为当前租户
	 * @param list
	 * @param tenantid 
	 * @author zhangfeng
	 * @date 2015年11月12日
	 */
	public static List<? extends AbstractVO> setTenantid(List<? extends AbstractVO> list) {
		if(list==null || list.size()==0)return list;

		Long tenantid = getTenantId();
		if(tenantid==null) 
			throw new HDRuntimeException("租户id为空...");	

		setTenantid(list, tenantid);

		return list;
	}

	/**
	 * 设置租户id
	 * @param vo
	 * @param tenantid 
	 * @author zhangfeng
	 * @date 2015年11月12日
	 */
	public static AbstractVO setTenantid(AbstractVO vo,Long tenantid) {
		if(vo.get(SyConstant.TENANT_STR)!=null ) 
			return vo;
		if(vo instanceof BaseVO) {
			((BaseVO)vo).setTenantid(tenantid);
		}else {
			vo.set(SyConstant.TENANT_STR, tenantid);
		}
		return vo;
	}

	/**
	 * 设置租户id
	 * @param list
	 * @param tenantid 
	 * @author zhangfeng
	 * @date 2015年11月12日
	 */
	public static List<? extends AbstractVO> setTenantid(List<? extends AbstractVO> list,Long tenantid) {
		if(list==null || list.size()==0)return list;

		for(AbstractVO vo:list) {
			if(vo.get(SyConstant.TENANT_STR)!=null ) 
				continue;
			if(vo instanceof BaseVO) {
				((BaseVO)vo).setTenantid(tenantid);
			}else {
				vo.set(SyConstant.TENANT_STR, tenantid);
			}
		}

		return list;
	}

	/**
	 * 设置修改人为当前用户
	 * @param vo
	 * @return 
	 * @author zhangfeng
	 * @date 2015年11月12日
	 */
	public static AbstractVO setU_P(AbstractVO vo) {
		Long userId = getUserId();
		if(userId==null) 
			throw new HDRuntimeException("当前用户id为空...");
		if(vo instanceof BaseVO) {
			((BaseVO)vo).setUpdated_by(userId);
		}else {
			vo.set("updated_by", userId);			
		}
		vo.set(UPDATE_BY_NAME, getUserName());
		return vo;
	}

	/**
	 * 设置修改人为当前用户
	 * @param list
	 * @return 
	 * @author zhangfeng
	 * @date 2015年11月12日
	 */
	public static List<? extends AbstractVO> setU_P(List<? extends AbstractVO> list) {
		if(list==null || list.size()==0)return list;

		Long userId = getUserId();
		if(userId==null) 
			throw new HDRuntimeException("当前用户id为空...");
		setU_P(list, userId);
		return list;
	}

	/**
	 * 设置修改人
	 * @param vo
	 * @param userId
	 * @return 
	 * @author zhangfeng
	 * @date 2015年11月12日
	 */
	public static AbstractVO setU_P(AbstractVO vo,Long userId) {
		if(vo instanceof BaseVO) {
			((BaseVO)vo).setUpdated_by(userId);
		}else {
			vo.set("updated_by", userId);
		}
		vo.set(UPDATE_BY_NAME, getUserName());
		return vo;
	}

	/**
	 * 设置修改人
	 * @param list
	 * @return 
	 * @author zhangfeng
	 * @date 2015年11月12日
	 */
	public static List<? extends AbstractVO> setU_P(List<? extends AbstractVO> list,Long userId) {
		if(list==null || list.size()==0)return list;

		for(AbstractVO vo:list) {
			if(vo instanceof BaseVO) {
				((BaseVO)vo).setUpdated_by(userId);
			}else {
				vo.set("updated_by", userId);
			}
			vo.set(UPDATE_BY_NAME, getUserName());
		}

		return list;
	}

	/**
	 * 设置添加以及修改人为当前用户
	 * @param vo
	 * @return 
	 * @author zhangfeng
	 * @date 2015年11月12日
	 */
	public static AbstractVO setCU_P(AbstractVO vo) {
		Long userId = getUserId();
		if(userId==null) 
			throw new HDRuntimeException("当前用户id为空...");

		if(vo instanceof BaseVO) {
			((BaseVO)vo).setCreated_by(userId);
			((BaseVO)vo).setUpdated_by(userId);
		}else {
			vo.set("created_by", userId);
			vo.set("updated_by", userId);
		}
		vo.set(CREATE_BY_NAME, getUserName());
		vo.set(UPDATE_BY_NAME, getUserName());
		return vo;
	}

	/**
	 * 设置添加以及修改人为当前用户
	 * @param list
	 * @return 
	 * @author zhangfeng
	 * @date 2015年11月12日
	 */
	public static List<? extends AbstractVO> setCU_P(List<? extends AbstractVO> list) {
		if(list==null || list.size()==0)return list;

		Long userId = getUserId();
		if(userId==null) 
			throw new HDRuntimeException("当前用户id为空...");

		setCU_P(list, userId);

		return list;
	}

	/**
	 * 设置添加修改人
	 * @param vo
	 * @return 
	 * @author zhangfeng
	 * @date 2015年11月12日
	 */
	public static AbstractVO setCU_P(AbstractVO vo, Long userId) {
		if(vo instanceof BaseVO) {
			((BaseVO)vo).setCreated_by(userId);
			((BaseVO)vo).setUpdated_by(userId);
		}else {
			vo.set("created_by", userId);
			vo.set("updated_by", userId);
		}
		
		String sessionUsername = getUserName();
		vo.set(CREATE_BY_NAME, sessionUsername);
		vo.set(UPDATE_BY_NAME, sessionUsername);
		return vo;
	}

	/**
	 * 设置添加修改人
	 * @param list
	 * @param userId
	 * @return 
	 * @author zhangfeng
	 * @date 2015年11月12日
	 */
	public static List<? extends AbstractVO> setCU_P(List<? extends AbstractVO> list, Long userId) {
		if(list==null || list.size()==0)return list;

		for(AbstractVO vo:list) {
			setCU_P(vo, userId);
		}

		return list;
	}

	/**
	 * 设置添加修改人以及租户id为当前用户租户
	 * @param vo
	 * @return 
	 * @author zhangfeng
	 * @date 2015年11月12日
	 */
	public static AbstractVO setCU_PT(AbstractVO vo) {
		Long userId = getUserId();
		if(userId==null) 
			throw new HDRuntimeException("当前用户id为空...");

		Long tenantid = getTenantId();
		if(tenantid==null) 
			throw new HDRuntimeException("租户id为空...");		

		setCU_P(vo);
		setTenantid(vo,tenantid);
		return vo;
	}

	/**
	 * 设置添加修改人以及租户id为当前用户租户
	 * @param list
	 * @return 
	 * @author zhangfeng
	 * @date 2015年11月12日
	 */
	public static List<? extends AbstractVO> setCU_PT(List<? extends AbstractVO> list) {
		if(list==null || list.size()==0)return list;

		Long userId = getUserId();
		if(userId==null) 
			throw new HDRuntimeException("当前用户id为空...");

		Long tenantid = getTenantId();
		if(tenantid==null) 
			throw new HDRuntimeException("租户id为空...");	

		setCU_PT(list, userId, tenantid);
		return list;
	}

	/**
	 * 设置添加修改人以及租户id
	 * @param vo
	 * @return 
	 * @author zhangfeng
	 * @date 2015年11月12日
	 */
	public static AbstractVO setCU_PT(AbstractVO vo,Long userId,Long tenantid) {
		setCU_P(vo,userId);
		setTenantid(vo,tenantid);
		return vo;
	}

	/**
	 * 设置添加修改人以及租户id
	 * @param list
	 * @return 
	 * @author zhangfeng
	 * @date 2015年11月12日
	 */
	public static List<? extends AbstractVO> setCU_PT(List<? extends AbstractVO> list,Long userId,Long tenantid) {
		if(list==null || list.size()==0)return list;

		for(AbstractVO vo:list) {
			setCU_PT(vo, userId, tenantid);
		}

		return list;
	}

	/**
	 * 设置添加修改时间
	 * @param vo
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月9日
	 */
	public static AbstractVO setCU_D(AbstractVO vo) {
		Date date = new Date();				
		if(vo instanceof BaseVO) {
			BaseVO baseVO = (BaseVO)vo;
			if(baseVO.getCreated_dt()==null) {
				baseVO.setCreated_dt(date);
			}
			((BaseVO)vo).setUpdated_dt(date);
		}else {
			if(vo.get("created_dt")==null) {
				vo.set("created_dt", date);
			}			
			vo.set("updated_dt", date);
		}
		
		return vo;
	}

	/**
	 * 设置添加修改时间
	 * @param list
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月9日
	 */
	public static List<? extends AbstractVO> setCU_D(List<? extends AbstractVO> list) {
		if(list==null || list.size()==0)return list;

		for(AbstractVO vo:list) {
			setCU_D(vo);
		}

		return list;
	}
	
	/**
	 * 设置修改时间
	 * @param vo
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月9日
	 */
	public static AbstractVO setU_D(AbstractVO vo) {
		if(vo instanceof BaseVO) {
			((BaseVO)vo).setUpdated_dt(new Date());
		}else {
			vo.set("updated_dt", new Date());
		}
		return vo;
	}
	
	/**
	 * 设置修改时间
	 * @param list
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月9日
	 */
	public static List<? extends AbstractVO> setU_D(List<? extends AbstractVO> list) {
		if(list==null || list.size()==0)return list;

		for(AbstractVO vo:list) {
			setU_D(vo);
		}

		return list;
	}
	
	/**
	 * 设置添加修改时候的租户、用户、时间
	 * @param vo
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月9日
	 */
	public static AbstractVO setCU_TPD(AbstractVO vo) {
		setCU_PT(vo);
		setCU_D(vo);
		return vo;
	}
	
	/**
	 * 设置添加修改时候的租户、用户、时间
	 * @param vo
	 * @param userId
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月9日
	 */
	public static AbstractVO setCU_TPD(AbstractVO vo,Long userId,Long tenantid) {
		setCU_PT(vo, userId, tenantid);
		setCU_D(vo);
		return vo;
	}
	
	/**
	 * 设置添加修改时候的租户、用户、时间
	 * @param list
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月9日
	 */
	public static List<? extends AbstractVO> setCU_TPD(List<? extends AbstractVO> list) {
		Long userId = getUserId();
		if(userId==null) 
			throw new HDRuntimeException("当前用户id为空...");

		Long tenantid = getTenantId();
		if(tenantid==null) 
			throw new HDRuntimeException("租户id为空...");		
		
		setCU_TPD(list, userId, tenantid);
		
		return list;
	}
	
	/**
	 * 设置添加修改时候的租户、用户、时间
	 * @param list
	 * @param userId
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月9日
	 */
	public static List<? extends AbstractVO> setCU_TPD(List<? extends AbstractVO> list,Long userId,Long tenantid) {
		if(list==null || list.size()==0)return list;

		for(AbstractVO vo:list) {
			setCU_TPD(vo, userId, tenantid);
		}

		return list;
	}
	
	/**
	 * 设置修改时候的租户、用户和时间
	 * @param vo
	 * @param userId
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月9日
	 */
	public static AbstractVO setU_TPD(AbstractVO vo,Long userId,Long tenantid) {
		setU_P(vo,userId);
		setU_D(vo);
		setTenantid(vo, tenantid);
		return vo;
	}
	
	/**
	 * 设置修改时候的租户、用户和时间
	 * @param vo
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月9日
	 */
	public static AbstractVO setU_TPD(AbstractVO vo) {
		setU_P(vo);
		setU_D(vo);
		setTenantid(vo);
		return vo;
	}
	
	/**
	 * 设置修改时候的租户、用户和时间
	 * @param list
	 * @param userId
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月9日
	 */
	public static List<? extends AbstractVO> setU_TPD(List<? extends AbstractVO> list,Long userId,Long tenantid) {
		if(list==null || list.size()==0)return list;

		for(AbstractVO vo:list) {
			setU_TPD(vo, userId, tenantid);
		}

		return list;
	}
	
	/**
	 * 设置修改时候的租户、用户和时间
	 * @param list
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月11日
	 */
	public static List<? extends AbstractVO> setU_TPD(List<? extends AbstractVO> list) {
		if(list==null || list.size()==0)return list;

		Long userId = getUserId();
		if(userId==null) 
			throw new HDRuntimeException("当前用户id为空...");
		
		Long tenantid = getTenantId();
		if(tenantid==null) 
			throw new HDRuntimeException("租户id为空...");	
		
		for(AbstractVO vo:list) {
			setU_TPD(vo, userId,tenantid);
		}

		return list;
	}
}
