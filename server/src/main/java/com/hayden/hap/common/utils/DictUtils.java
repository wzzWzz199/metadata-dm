package com.hayden.hap.common.utils;

import com.hayden.hap.common.cache.constant.CacheConstant;
import com.hayden.hap.dbop.db.util.ObjectUtil;
import com.hayden.hap.common.entity.dict.DictDataVO;
import com.hayden.hap.common.entity.dict.DictDataWarperVO;
import com.hayden.hap.common.entity.dict.TreeNodeVO;
import com.hayden.hap.common.dict.itf.IDictDataService;
import com.hayden.hap.common.dict.itf.IDictService;
import com.hayden.hap.common.formmgr.inputconfig.DictInputConfigVO;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.tenant.utils.TenantUtil;
import com.hayden.hap.common.utils.session.CurrentEnvUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author zhangfeng
 * @date 2015年9月15日
 */
public class DictUtils {
	
	/**
	 * 根据字典编码获取字典数据
	 * @param dictCode
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月7日
	 */
	public static List<DictDataWarperVO> getDictData(String dictCode) {
		IDictDataService service = (IDictDataService)AppServiceHelper.findBean("dictDataService");	
		Long tenantid = CurrentEnvUtils.getTenantId();
		List<DictDataVO> dictDataVOs = service.getDictData(dictCode, tenantid);
		
		List<DictDataWarperVO> list = new ArrayList<DictDataWarperVO>();
		if(dictDataVOs!=null) {
			for(DictDataVO dictDataVO:dictDataVOs) {
				list.add(new DictDataWarperVO(dictDataVO));
			}
		}
		return list;
	}
	
	/**
	 * 根据字典编码获取字典数据
	 * @param dictCode
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2016年5月16日
	 */
	public static List<DictDataWarperVO> getDictData(String dictCode,Long tenantid) {
		IDictDataService service = (IDictDataService)AppServiceHelper.findBean("dictDataService");	
		List<DictDataVO> dictDataVOs = service.getDictData(dictCode, tenantid);
		
		List<DictDataWarperVO> list = new ArrayList<DictDataWarperVO>();
		if(dictDataVOs!=null) {
			for(DictDataVO dictDataVO:dictDataVOs) {
				list.add(new DictDataWarperVO(dictDataVO));
			}
		}
		return list;
	}
	
	/**
	 * 根据输入设定获取字典数据
	 * @param inputConfigVO
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2016年8月31日
	 */
	public static List<DictDataWarperVO> getDictData(DictInputConfigVO inputConfigVO,Long tenantid) {
		if(inputConfigVO.getDictdata()!=null)
			return inputConfigVO.getDictdata();
		
		return getDictData(inputConfigVO.getDictcode(), tenantid);
	}
	
	/**
	 * 获取带有顶点节点的字典数据（顶点节点为虚拟节点，name为字典名称，id为0，code为'@-1'）
	 * @param dictCode
	 * @return 
	 * @author zhangfeng
	 * @date 2016年4月26日
	 */
	public static List<DictDataWarperVO> getDictDataWithTopnode(String dictCode) {
		IDictDataService service = (IDictDataService)AppServiceHelper.findBean("dictDataService");	
		Long tenantid = CurrentEnvUtils.getTenantId();
		List<DictDataVO> dictDataVOs = service.getDictDataWithTopnode(dictCode, tenantid);
		
		List<DictDataWarperVO> list = new ArrayList<DictDataWarperVO>();
		if(dictDataVOs!=null) {
			for(DictDataVO dictDataVO:dictDataVOs) {
				list.add(new DictDataWarperVO(dictDataVO));
			}
		}
		return list;
	}
	
	/**
	 * 获取带有顶点节点的树节点数据（顶点节点为虚拟节点，name为字典名称，id为0，code为'@-1'）
	 * @param dictCode
	 * @return 
	 * @author zhangfeng
	 * @date 2016年4月26日
	 */
	public static List<TreeNodeVO> getTreeDataWithTopnode(String dictCode,String itemCode) {
		IDictDataService service = (IDictDataService)AppServiceHelper.findBean("dictDataService");	
		Long tenantid = CurrentEnvUtils.getTenantId();
		List<DictDataVO> dictDataVOs = service.getDictDataWithTopnode(dictCode, tenantid);
		
		List<TreeNodeVO> list = new ArrayList<TreeNodeVO>();
		if(dictDataVOs!=null) {
			for(DictDataVO dictDataVO:dictDataVOs) {
				list.add(new TreeNodeVO(dictDataVO,itemCode));
			}
		}
		return list;
	}
	
	public static List<TreeNodeVO> getTreeDataWithTopnode(DictInputConfigVO inputConfigVO,String itemCode,String itemName) {
		IDictDataService service = (IDictDataService)AppServiceHelper.findBean("dictDataService");
		Long sessionTenantid = CurrentEnvUtils.getTenantId();
		Long tenantid = TenantUtil.getCurrentDataTenantid(sessionTenantid);
		
		List<TreeNodeVO> list = new ArrayList<TreeNodeVO>();
		
		if(inputConfigVO.getDictdata()!=null) {
			List<DictDataWarperVO> dataWarperVOs = service.addTopnode(inputConfigVO.getDictdata(), itemCode, itemName);
//			List<DictDataWarperVO> dataWarperVOs = inputConfigVO.getDictdata();
			for(DictDataWarperVO dictDataWarperVO : dataWarperVOs) {
				list.add(new TreeNodeVO(dictDataWarperVO,itemCode));
			}
		}else {		
			List<DictDataVO> dictDataVOs = service.getDictDataWithTopnode(inputConfigVO.getDictcode(), tenantid);		
			if(dictDataVOs!=null) {
				for(DictDataVO dictDataVO:dictDataVOs) {
					list.add(new TreeNodeVO(dictDataVO,itemCode));
				}
			}
		}
		return list;
	}
	public static List<TreeNodeVO> getTreeDataWithTopnode(DictInputConfigVO inputConfigVO,String funcCode,String itemCode,String itemName) {
		IDictDataService service = (IDictDataService)AppServiceHelper.findBean("dictDataService");
		Long sessionTenantid = CurrentEnvUtils.getTenantId();
		Long tenantid = TenantUtil.getCurrentDataTenantid(sessionTenantid);
		
		List<TreeNodeVO> list = new ArrayList<TreeNodeVO>();
		
		if(inputConfigVO.getDictdata()!=null) {
			List<DictDataWarperVO> dataWarperVOs = service.addTopnode(inputConfigVO.getDictdata(), itemCode, itemName);
//			List<DictDataWarperVO> dataWarperVOs = inputConfigVO.getDictdata();
			for(DictDataWarperVO dictDataWarperVO : dataWarperVOs) {
				list.add(new TreeNodeVO(dictDataWarperVO,itemCode));
			}
		}else {		
			List<DictDataVO> dictDataVOs = service.getDictDataWithTopnode(funcCode,inputConfigVO.getDictcode(), tenantid);		
			if(dictDataVOs!=null) {
				for(DictDataVO dictDataVO:dictDataVOs) {
					list.add(new TreeNodeVO(dictDataVO,itemCode));
				}
			}
		}
		return list;
	}
	
	/**
	 * 根据字典编码和配置条件获取字典数据<br/>
	 * 不建议开发人员调用<br/>
	 * 请调用 com.hayden.hap.sy.utils.DictUtils.getDictData(String dictCode,Long tenantid)<br/>
	 * @param dictCode
	 * @param extWhere 配置条件
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月7日
	 */
	@Deprecated
	public static List<DictDataWarperVO> getDictData(String dictCode,String extWhere) {
		IDictDataService service = (IDictDataService)AppServiceHelper.findBean("dictDataService");	
		Long tenantid = CurrentEnvUtils.getTenantId();
		if(extWhere==null) {
			extWhere = "";
		}
		List<DictDataVO> dictDataVOs = service.getDictData(dictCode, tenantid,extWhere);
		
		List<DictDataWarperVO> list = new ArrayList<DictDataWarperVO>();
		if(dictDataVOs!=null) {
			for(DictDataVO dictDataVO:dictDataVOs) {
				list.add(new DictDataWarperVO(dictDataVO));
			}
		}
		return list;
	}
	
	/**
	 * 将字典数据对象转换为包装类对象
	 * @param list
	 * @return 
	 * @author zhangfeng
	 * @date 2016年1月14日
	 */
	public static List<DictDataWarperVO> warpDictData(List<DictDataVO> list) {
		if(list==null) return null;
		
		List<DictDataWarperVO> result = new ArrayList<DictDataWarperVO>();
		for(DictDataVO dictDataVO : list) {
			result.add(new DictDataWarperVO(dictDataVO));
		}
		return result;
	}
	
	/**
	 * 将字典数据对象转换为树节点对象
	 * @param list
	 * @return 
	 * @author zhangfeng
	 * @date 2016年1月14日
	 */
	public static List<TreeNodeVO> dictData2TreeNode(List<DictDataVO> list,String itemCode) {
		if(list==null) return null;
		
		List<TreeNodeVO> result = new ArrayList<TreeNodeVO>();
		for(DictDataVO dictDataVO : list) {
			result.add(new TreeNodeVO(dictDataVO,itemCode));
		}
		return result;
	}
	
	/**
	 * 将字典数据包装对象转换为树节点对象
	 * @param list
	 * @param itemCode
	 * @return 
	 * @author zhangfeng
	 * @date 2016年9月1日
	 */
	public static List<TreeNodeVO> dictDataWarper2TreeNode(List<DictDataWarperVO> list,String itemCode) {
		if(list==null) return null;
		
		List<TreeNodeVO> result = new ArrayList<TreeNodeVO>();
		for(DictDataWarperVO dictDataWarperVO : list) {
			result.add(new TreeNodeVO(dictDataWarperVO,itemCode));
		}
		return result;
	}
	
	/**
	 * 根据字典数据名称获取字典数据编码
	 * @param dictCode 字典编码
	 * @param dictDataName 字典数据名称
	 * @return 
	 * @author zhangfeng
	 * @date 2016年1月19日
	 */
	public static String getDictDataCodeByName(String dictCode, String dictDataName) {
		if(dictDataName==null) return null;
		
		IDictDataService service = (IDictDataService)AppServiceHelper.findBean("dictDataService");	
		Long tenantid = CurrentEnvUtils.getTenantId();
		List<DictDataVO> dictDataVOs = service.getDictData(dictCode, tenantid);
		
		DictDataVO dictDataVO = getDictDataByName(dictDataVOs, dictDataName);
		if(dictDataVO!=null) 
			return dictDataVO.getDict_data_code();
		
		return null;
	}
	
	/**
	 * 根据字典数据编码获取字典数据名称
	 * @param dictCode 字典编码
	 * @param dictDataCode 字典数据编码
	 * @return 
	 * @author zhangfeng
	 * @date 2016年1月19日
	 */
	public static String getDictDataNameByCode(String dictCode, String dictDataCode) {
		if(dictDataCode==null) return null;
		
		IDictDataService service = (IDictDataService)AppServiceHelper.findBean("dictDataService");	
		Long tenantid = CurrentEnvUtils.getTenantId();
		List<DictDataVO> dictDataVOs = service.getDictData(dictCode, tenantid);
		
		DictDataVO dictDataVO = getDictDataByCode(dictDataVOs, dictDataCode);
		if(dictDataVO!=null)
			return dictDataVO.getDict_data_name();
		
		return null;
	}
	
	private static DictDataVO getDictDataByCode(List<DictDataVO> list,String code) {
		for(DictDataVO dictDataVO : list) {
			if(code.equals(dictDataVO.getDict_data_code())) {
				return dictDataVO;
			}
			if(ObjectUtil.isNotEmpty(dictDataVO.getChildren())) {
				DictDataVO result = getDictDataByCode(dictDataVO.getChildren(),code);
				if(result!=null)
					return result;
			}	
		}
		return null;
	}
	
	private static DictDataVO getDictDataByName(List<DictDataVO> list,String name) {
		for(DictDataVO dictDataVO : list) {
			if(name.equals(dictDataVO.getDict_data_name())) {
				return dictDataVO;
			}
			if(ObjectUtil.isNotEmpty(dictDataVO.getChildren())) {
				DictDataVO result = getDictDataByName(dictDataVO.getChildren(),name);
				if(result!=null)
					return result;
			}	
		}
		return null;
	}
	
	
//	public static boolean isInnerDict(String dictCode)
//	{
//		boolean isInner = true;
//		IDictService dictService = AppServiceHelper.findBean(IDictService.class,"dictService");	
//		Long tenantid = CurrentEnvUtils.getTenantId();
//		DictVO dictVO = dictService.getDictByCode(dictCode, tenantid);
//		if(dictVO==null)
//		{
//			
//		}else if(!dictVO.getDict_t_table().equalsIgnoreCase(SyConstant.INNER_DICT_TABLE))
//		{
//			isInner = false;
//		}
//		return isInner;
//	}
	
	/**
	 * 根据表名删除字典缓存
	 * @param tablename
	 * @param tenantid 
	 * @author zhangfeng
	 * @date 2016年3月10日
	 */
	public static void evictByTablename(String tablename,Long tenantid) {
		
		IDictService dictService = AppServiceHelper.findBean(IDictService.class);
		List<String> dictcodes = dictService.getDictcodesByTablecode_Cache(tablename, tenantid);
		for(String dictcode : dictcodes) {
			String cacheKey = dictcode+SyConstant.CACHE_SEPARATOR+tenantid;
			CacheUtils.getInstance().evict(CacheConstant.CACHE_DICT_DATA, cacheKey);
		}
	}
}
