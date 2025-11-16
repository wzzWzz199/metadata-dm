package com.hayden.hap.common.permisson.service;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.enumerate.ButtonTypeEnum;
import com.hayden.hap.common.enumerate.FuncTypeEnum;
import com.hayden.hap.common.permisson.entity.PermissionUrlVO;
import com.hayden.hap.common.permisson.itf.IPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限服务实现类
 * 
 * @author zhangfeng
 * @date 2018年2月7日
 */
@Service("permissonService")
public class PermissionServiceImpl implements IPermissionService {

	@Autowired
	private IBaseService baseService;
	
	/**
	 * 
	 *
	 * @see com.hayden.hap.common.permisson.itf.IPermissionService#getUrlFuncAndButton()
	 * @author lianghua
	 * @date 2016年4月14日
	 */
	@Cacheable(value="urlFuncAndBtnCache",key="#tenantid")
	@Override
	public Map<String,List<PermissionUrlVO>> getUrlFuncAndButton(Long tenantid) {
		StringBuffer sb = new StringBuffer("select * from (select func_code,null as btn_code,func_info as url from sy_func where tenantid=");
		sb.append("{0,number,#}");
		sb.append(" and func_type=''{1}'' and func_info is not null");
		sb.append(" and func_isbuy=1");
		sb.append(" union all ");
		sb.append("select fu.func_code,b.btn_code,btn_property as url from (select * from sy_form_button where btn_type='")
		.append("'{2}'")
		.append("' and btn_property is not null and tenantid=")
		.append("{0,number,#}")
		.append(") b inner join (select * from sy_form where tenantid=")
		.append("{0,number,#}")
		.append(") f on b.formid=f.formid inner join (select * from sy_func where func_isbuy=1 and tenantid=")
		.append("{0,number,#}")
		.append(") fu on f.form_code=fu.func_info");
		sb.append(") as tempt order by url");
		
		String sql = MessageFormat.format(sb.toString(), tenantid, FuncTypeEnum.URL.getId(), ButtonTypeEnum.URL.getCode());
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		
		
		VOSet<AbstractVO> voset = baseService.executeQuery(sql, dynaSqlVO);
		Map<String,List<PermissionUrlVO>> resultMap = new HashMap<String,List<PermissionUrlVO>>();
		if(voset!=null){
			List<AbstractVO> volist = voset.getVoList();
			if(volist!=null && volist.size()>0){
				List<PermissionUrlVO> permissonurlvoList = new ArrayList<PermissionUrlVO>();
				String oldurl="";
				PermissionUrlVO oldPermissonUrlVO = new PermissionUrlVO();
				for(int i=0;i<volist.size();i++){
					AbstractVO abstractVO = volist.get(i);
					PermissionUrlVO permissonUrlVO = new PermissionUrlVO();
					permissonUrlVO.setFunc_code(abstractVO.getString("func_code"));
					permissonUrlVO.setBtn_code(abstractVO.getString("btn_code"));
					if(i==0){
						permissonurlvoList.add(permissonUrlVO);
					}else{
						if(oldurl.equals(abstractVO.getString("url")) 
								&& !this.equalsPermissonurlVO(permissonUrlVO, oldPermissonUrlVO)){
							permissonurlvoList.add(permissonUrlVO);
						}else if(!oldurl.equals(abstractVO.getString("url"))){
							resultMap.put(oldurl, permissonurlvoList);
							permissonurlvoList = new ArrayList<PermissionUrlVO>();
							permissonurlvoList.add(permissonUrlVO);
						}
					}
					oldurl = abstractVO.getString("url");
					oldPermissonUrlVO.setFunc_code(abstractVO.getString("func_code"));
					oldPermissonUrlVO.setBtn_code(abstractVO.getString("func_code"));
				}
			}
		}
		return resultMap;
	}
	
	/**
	 * 较验两个permissonurlvo是否相同
	 * @param permissonUrlVO1
	 * @param permissonUrlVO2
	 * @return 
	 * @author lianghua
	 * @date 2016年4月14日
	 */
	private boolean equalsPermissonurlVO(PermissionUrlVO permissonUrlVO1,PermissionUrlVO permissonUrlVO2){
		boolean result = false;
		if(permissonUrlVO1!=null && permissonUrlVO2!=null){
			if(permissonUrlVO1.getString("func_code")!=null && permissonUrlVO1.getString("func_code").equals(permissonUrlVO2.getString("func_code"))
					&& (permissonUrlVO1.getString("btn_code")!=null && permissonUrlVO1.getString("btn_code").equals(permissonUrlVO2.getString("btn_code")) 
					|| permissonUrlVO1.getString("btn_code")==null && permissonUrlVO2.getString("btn_code")==null) ){
				result = true;
			}
		}
		return result;
	}
}
