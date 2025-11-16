package com.hayden.hap.common.formmgr.action;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.form.itf.IFormService;
import com.hayden.hap.common.formmgr.annotation.*;
import com.hayden.hap.common.formmgr.entity.CardDataVO;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.entity.ListDataVO;
import com.hayden.hap.common.formmgr.itf.IAction;
import com.hayden.hap.common.spring.service.AppServiceHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * action处理器
 * @author zhangfeng
 * @date 2017年10月31日
 */
public class ActionHandler {
	

	/**
	 * 卡片保存最后处理
	 * @param action
	 * @param cardDataVO
	 * @param formParamVO
	 * @param vo
	 * @param isAdd 
	 * @author zhangfeng
	 * @throws HDException 
	 * @date 2017年10月31日
	 */
	public static void handleLastCardSave(IAction action, CardDataVO cardDataVO, 
			FormParamVO formParamVO, AbstractVO vo, boolean isAdd) throws HDException {
		handle(action, LastCardSave.class, cardDataVO, formParamVO, vo, isAdd);
	}
	
	/**
	 * 获取卡片VO最后处理
	 * @param action
	 * @param cardDataVO
	 * @param formParamVO
	 * @param vo
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年11月1日
	 */
	public static void handleLastGetCardVO(IAction action, CardDataVO cardDataVO, 
			FormParamVO formParamVO, AbstractVO vo) throws HDException {
		handle(action, LastGetCardVO.class, cardDataVO,formParamVO, vo);
	}
	
	/**
	 * 列表删除后最后操作
	 * @param action
	 * @param listDataVO
	 * @param formParamVO
	 * @param primaryKeys
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2018年1月10日
	 */
	public static void handleLastListDelete(IAction action, ListDataVO listDataVO,
			FormParamVO formParamVO,Collection<Long> primaryKeys) throws HDException {
		handle(action, LastListDelete.class, listDataVO, formParamVO, primaryKeys);
	}

	/**
	 * 列表批量修改保存后操作
	 * @param action
	 * @param listDataVO
	 * @param formParamVO
	 * @throws HDException
	 */
	public static void handleLastListUpdate(IAction action, ListDataVO listDataVO,
											FormParamVO formParamVO) throws HDException {
		handle(action, LastListUpdate.class, listDataVO, formParamVO);
	}
	
	/**
	 * 处理查询前动作
	 * @param formParamVO
	 * @param funcCode
	 * @param itemCode
	 * @param tenantid
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年10月10日
	 */
	public static void handleQuerySelectorBeforeQuery(FormParamVO formParamVO, String funcCode, String itemCode, Long tenantid) throws HDException {
		IFormService formService = AppServiceHelper.findBean(IFormService.class);
		IAction action = formService.getActionByFuncCode(funcCode, tenantid);
		if(action==null)
			return;
			
		Method[] methods = action.getClass().getMethods();
		
		for(Method method : methods) {
			BeforeQSListQuery beforeQuery = method.getAnnotation(BeforeQSListQuery.class);
			if(beforeQuery==null)
				continue;
			ItemMapping mapping = method.getAnnotation(ItemMapping.class);
			if(mapping!=null && itemCode.equals(mapping.value())) {
				Class<?>[] paramterTypes = method.getParameterTypes();
				Object[] params = getParamter(paramterTypes, formParamVO, funcCode, itemCode, tenantid);
				
				try {
					method.invoke(action, params);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new HDException(e);
				}
			}
		}
	}
	
	/**
	 * 调用action对应方法
	 * @param action
	 * @param annotation
	 * @param params
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年11月1日
	 */
	public static <T extends Annotation> void handle(IAction action, Class<T> annotation, Object ...params) throws HDException {
		if(action==null)
			return;

		Method[] methods = action.getClass().getMethods();

		for(Method method : methods) {
			T t = method.getAnnotation(annotation);
			if(t==null)
				continue;

			Class<?>[] paramterTypes = method.getParameterTypes();
			Object[] paramValues = getParamter(paramterTypes, params);
			try {
				method.invoke(action, paramValues);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				if (e instanceof InvocationTargetException) {
					throw new HDException(((InvocationTargetException)e).getTargetException().getMessage(),e);
				}else {
					throw new HDException(e);
				}
				
			}
			break;
		}
	}
	
	/**
	 * 获取参数
	 * @param formParamVO
	 * @param paramterTypes
	 * @return 
	 * @author zhangfeng
	 * @date 2017年10月10日
	 */
	private static Object[] getParamter(Class<?>[] paramterTypes, Object ...params ) {
		Object[] paramValues = new Object[paramterTypes.length];
		if(params==null || params.length==0) {
			return paramValues;
		}
		out:for(int i=0; i<paramterTypes.length; i++) {
			Class<?> paramterType = paramterTypes[i];
			for(Object param : params) {
				if(paramterType.isInstance(param) || isSameType(paramterType, param)) {					
					paramValues[i] = param;
					continue out;
				}else {
					paramValues[i] = null;
				}				
			}
		}
		return paramValues;
	}
	
	/**
	 * 判定基本类型和包装类型一致，如int跟Integer返回true
	 * @param paramterType
	 * @param param
	 * @return 
	 * @author zhangfeng
	 * @date 2017年11月1日
	 */
	private static boolean isSameType(Class<?> paramterType, Object param) {
		try {
			return paramterType.isPrimitive() && paramterType==param.getClass().getField("TYPE").get(null);
		} catch (Exception e) {
			
		}
		return false;
	}	
}

