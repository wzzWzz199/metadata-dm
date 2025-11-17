package com.hayden.hap.common.utils;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.innercode.itf.IInnerCodeService;
import com.hayden.hap.common.spring.service.AppServiceHelper;

import java.util.List;

public class InnerCodeUtil {

	
//	public static String getInnerCode(AbstractVO vo,Long tenantid){
//		IBaseService baseService = AppServiceHelper.findBean(IBaseService.class, "baseService");
//		IInnerCodeService innerCodeService = AppServiceHelper.findBean(IInnerCodeService.class, "innerCodeService");
//		if(vo.getLong("parentid")==null){
//			return innerCodeService.getPreInnerCode__RequiresNew(vo, null,tenantid);
//		}else{
//			AbstractVO parentVO = baseService.queryByPKAndTenantid(vo.getTableName(), vo.getLong("parentid"),CurrentEnvUtils.getTenantId());
//			String parentInnerCode = parentVO.getString("innercode");
//			String innerCode = innerCodeService.getPreInnerCode__RequiresNew(vo, vo.getLong("parentid"),tenantid);
//			return parentInnerCode+innerCode;
//		}
//	}
	
//	private static List<AbstractVO> updateTreeList(List<AbstractVO> voList){
//		IBaseService baseService = AppServiceHelper.findBean(IBaseService.class, "baseService");
//		IInnerCodeService innerCodeService = AppServiceHelper.findBean(IInnerCodeService.class, "innerCodeService");
//		ITableDefService tableDefService = AppServiceHelper.findBean(ITableDefService.class, "tableDefService");
//		DynaSqlVO dynaSqlVO = new DynaSqlVO();
//		AbstractVO abstractVO = voList.get(0);
//		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, abstractVO.getLong(SyConstant.TENANT_STR));
//		String pkColName = tableDefService.getPkColName(abstractVO.getTableName());
//		dynaSqlVO.setOrderByClause(pkColName+" desc");
//		List<AbstractVO> allVOs = baseService.query(abstractVO, dynaSqlVO).getVoList();
//		Map<Long,String> idInnerCode = new HashMap<Long,String>();
//		if(allVOs.size()==voList.size()){
//			//第一次导入
//			for (AbstractVO vo : voList) {
//				innerCodeService.getChildren(voList, vo,idInnerCode,"parentid",vo.getLong(SyConstant.TENANT_STR));
//			}
//		}else{
//			//第二次增量导入
//			for(AbstractVO addVO:voList){
//				for (AbstractVO vo : allVOs) {
//					if(vo.getLong(pkColName).longValue()==addVO.getLong(pkColName).longValue()){
//						vo.set("parentid", addVO.getLong("parentid"));
//						break;
//					}
//				}
//			}
//			for (AbstractVO vo : voList) {
//				innerCodeService.getChildren(allVOs, vo,idInnerCode,"parentid",vo.getLong(SyConstant.TENANT_STR));
//			}
//			return allVOs;
//		}
//		return voList;
//	}
	
//	public static AbstractVO getDbVO(AbstractVO vo,Long pk){
//		IBaseService baseService = AppServiceHelper.findBean(IBaseService.class, "baseService");
//		AbstractVO dbVO = baseService.queryByPKAndTenantid(vo.getTableName(), pk,CurrentEnvUtils.getTenantId());
//		return dbVO;
//	}

	/**
	 * 赋值内部编码
	 * @param vo
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2017年7月24日
	 */
	public static <T extends AbstractVO> T assignInnercode(T t, Long tenantid) {
		IInnerCodeService service = AppServiceHelper.findBean(IInnerCodeService.class);
		return service.assignInnercode(t, tenantid);		
	}
	
	/**
	 * <p>赋值内部编码</p>
	 * 如果你的集合中的元素本身有父子关系，那么需要先对其按parentid排序，升序<br/>
	 * 可以调用： com.hayden.hap.common.utils.ListSortUtil.sortByParent(List<T>, String, String) 进行排序<br/>
	 * 此方法的效率并不高，综合考虑业务场景，是否可以重置该租户的所有内部编码，重置内部编码效率更高<br/>
	 * 是否可以重置内部编码判定建议：<br/>
	 * 1. 如果内部编码已作为业务字段含有其它业务逻辑，那么不要重置；<br/>
	 * 2. 如果该租户已有大量的数据，而此时只是赋值少量的内部编码，那么不建议重置；<br/>
	 * 3. 如果该租户还没有数据，或者少量数据，而此时要赋值大量的内部编码，那么建议先不设置内部编码把数据入库，再重置内部编码；<br/>
	 * 4. 你说一样多怎么办？一样多也是重置更划算。<br/>
	 * @param list
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2017年7月24日
	 */
	public static <T extends AbstractVO> List<T> assignInnercode(List<T> list, Long tenantid) {
		IInnerCodeService service = AppServiceHelper.findBean(IInnerCodeService.class);
		return service.assignInnercode(list, tenantid);
	}
	
	/**
	 * 重置内部编码
	 * @param tableCode
	 * @param tenantid
	 * @param isenforce
	 * @return 
	 * @author zhangfeng
	 * @date 2017年8月16日
	 */
	public static int reset(String tableCode, Long tenantid, boolean isenforce) {
		IInnerCodeService service = AppServiceHelper.findBean(IInnerCodeService.class);
		return service.reset(tableCode, tenantid, isenforce);
	}
	
}
