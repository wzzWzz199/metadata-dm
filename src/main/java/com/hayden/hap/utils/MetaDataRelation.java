package com.hayden.hap.utils;

import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.db.dataSource.entity.MetaRelationVO;
import com.hayden.hap.serial.JsonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component("metaDataRelation")
public class MetaDataRelation {

	 @Value("${META.RELATION}")
    private String RELATION;
    private List<MetaRelationVO> metaRelations;
    
    /**
     * 
     * @param metaType 元数据类型
     * @throws HDException 
     */
	public MetaRelationVO getMetaDataRelation(String metaType) throws HDException {
		metaRelations = JsonUtils.parseArrayInit(RELATION,MetaRelationVO.class);
		if (metaRelations==null) {
			throw new HDException("加载元数据关系异常，请检查apollo配置");
		}
		for (Iterator iterator = metaRelations.iterator(); iterator.hasNext();) {
			MetaRelationVO metaRelationVO = (MetaRelationVO) iterator.next();
			if (metaRelationVO.getNodetype().equals(metaType)) {
				return metaRelationVO;
			}
			if (metaRelationVO.getChildNodes()!=null) {
				MetaRelationVO m =  getMetaDataRelationRecursion(metaRelationVO,metaType,null);
				if (m!=null) {
					return m;
				}
			}
		}
		return null;
	}

	/**
     * 
     * @param metaType 元数据类型
     * @throws HDException 
     */
	public MetaRelationVO getMetaDataRelationByClass(String metaDataClass) throws HDException {
		metaRelations = JsonUtils.parseArrayInit(RELATION,MetaRelationVO.class);
		if (metaRelations==null) {
			throw new HDException("加载元数据关系异常，请检查apollo配置");
		}
		for (Iterator iterator = metaRelations.iterator(); iterator.hasNext();) {
			MetaRelationVO metaRelationVO = (MetaRelationVO) iterator.next();
			if (metaRelationVO.getNodevo().equals(metaDataClass)) {
				return metaRelationVO;
			}
			if (metaRelationVO.getChildNodes()!=null) {
				MetaRelationVO m =  getMetaDataRelationRecursion(metaRelationVO,null,metaDataClass);
				if (m!=null) {
					return m;
				}
			}
		}
		return null;
	}
	private MetaRelationVO getMetaDataRelationRecursion(MetaRelationVO metaRelationVO, String metaType,String metaDataClass) {
		for (MetaRelationVO metaRelationVO2 : metaRelationVO.getChildNodes()) {
			if (metaRelationVO2.getNodetype().equals(metaType)||metaRelationVO2.getNodevo().equals(metaDataClass)) {
				return metaRelationVO2;
			}
			if (metaRelationVO2.getChildNodes()!=null) {
				MetaRelationVO m =  getMetaDataRelationRecursion(metaRelationVO2,metaType,metaDataClass);
				if (m!=null) {
					return m;
				}
			}
		}
		return null;
	}
	
	/**
     * 
     * @param metaType 元数据类型
     * @throws HDException 
     */
	public MetaRelationVO getMetaDataParentRelation(String metaType) throws HDException {
		metaRelations = JsonUtils.parseArrayInit(RELATION,MetaRelationVO.class);
		if (metaRelations==null) {
			throw new HDException("加载元数据关系异常，请检查apollo配置");
		}
		for (Iterator iterator = metaRelations.iterator(); iterator.hasNext();) {
			MetaRelationVO metaRelationVO = (MetaRelationVO) iterator.next();
			if (metaRelationVO.getChildNodes()!=null) {
				MetaRelationVO m =  getMetaDataParentRelationRecursion(metaRelationVO,metaType);
				if (m!=null) {
					return m;
				}
			}
		}
		return null;
	}
	
	private MetaRelationVO getMetaDataParentRelationRecursion(MetaRelationVO metaRelationVO, String metaType) {
		for (MetaRelationVO metaRelationVO2 : metaRelationVO.getChildNodes()) {
			if (metaRelationVO2.getNodetype().equals(metaType)) {
				return metaRelationVO;
			}
			if (metaRelationVO2.getChildNodes()!=null) {
				MetaRelationVO m =  getMetaDataParentRelationRecursion(metaRelationVO2,metaType);
				if (m!=null) {
					return m;
				}
			}
		}
		return null;
	}
}
