package com.hayden.hap.common.utils;

import com.hayden.hap.dbop.entity.AbstractVO;
import com.hayden.hap.dbop.entity.VOSet;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.dbop.itf.IBaseService;
import com.hayden.hap.dbop.db.orm.sql.DynaSqlVO;
import com.hayden.hap.dbop.db.tableDef.entity.TableDefVO;
import com.hayden.hap.dbop.db.tableDef.itf.ITableDefService;
import com.hayden.hap.common.spring.service.AppServiceHelper;

import java.util.ArrayList;
import java.util.List;

public class TreeDBUtils {
	/**
	 * 判断树节点是否为叶子节点
	 * @param vo
	 * @param id
	 * @param tenantid
	 * @return
	 * @throws HDException 
	 * @author lianghua
	 * @date 2015年11月23日
	 */
	public static <T extends AbstractVO> boolean isLeaf(T vo,Long id,Long tenantid) throws HDException{
		try{
			IBaseService baseService = (IBaseService)AppServiceHelper.findBean("baseService");
			ITableDefService tableDefService = (ITableDefService)AppServiceHelper.findBean("tableDefService");
			
			//查询isleaf的值
			TableDefVO tableDefVO = tableDefService.queryDetailedTableByTbname(vo.getTableName());
			if(tableDefVO==null)
				return false;
			String primayKey = tableDefVO.getPkColumnVO() != null ? tableDefVO.getPkColumnVO()
					.getColcode() : null;
			StringBuffer whereSb = new StringBuffer();
			if(primayKey!=null){
				whereSb = new StringBuffer(primayKey);
				whereSb.append("=");
				whereSb.append(id);
			}
			if(tenantid!=null){
				whereSb.append(" and tenantid=");
				whereSb.append(tenantid);
			}
			List<String> sqlcolsList = new ArrayList<String>();
			sqlcolsList.add("isleaf");
			DynaSqlVO dynaSqlVO = new DynaSqlVO();
			dynaSqlVO.setWhereClause(whereSb.toString());
			dynaSqlVO.setSqlColumnList(sqlcolsList);
			VOSet<T> voset = baseService.query(vo, dynaSqlVO);
			T selectedvo = voset.getVO(0);
//			int isleaf = selectedvo.getInt("isleaf");
			int isleaf = selectedvo.getInt("isleaf");
//			if(SyConstant.SY_TRUE==isleaf){
			if(SyConstant.SY_TRUE==isleaf){
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			throw new HDException(e.getMessage());
		}
	}
}
