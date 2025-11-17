package com.hayden.hap.common.formmgr.service;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.dbop.exception.HDRuntimeException;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.dict.entity.DictDataWarperVO;
import com.hayden.hap.common.enumerate.InputTypeEnum;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.formmgr.inputconfig.DictInputConfigVO;
import com.hayden.hap.common.formmgr.utils.InputConfigUtils;
import com.hayden.hap.common.spring.service.IService;
import com.hayden.hap.common.utils.DictUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 字典无名称处理器
 * @author zhangfeng
 * @date 2016年4月20日
 * 
 */
@IService("dictNNHandler")
@Component("dictNNHandler")
public class DictNNHandler {
	
	protected static final String MULTI = "multi";
	
	/**
	 * 
	 * @param vo
	 * @param formItemVO 
	 * @author zhangfeng
	 * @throws HDException 
	 * @date 2016年4月20日
	 */
	public void code2Name(AbstractVO vo,FormItemVO formItemVO, Long tenantid) {
		if(vo==null)
			return;//不是编辑或查看页，用不着处理
		
		if(!InputTypeEnum.DICT_NEW.getCode().equals(formItemVO.getFitem_input_type())) 
			return;//不是字典无名称项，用不着处理
		
		DictInputConfigVO inputConfigVO;
		try {
			inputConfigVO = InputConfigUtils.getDictInputConfigVO(formItemVO.getFitem_input_config());
		} catch (HDException e) {
			throw new HDRuntimeException(e);
		}
		
		code2Name(vo, inputConfigVO, tenantid);
	}
	
	private AbstractVO code2Name(AbstractVO vo, DictInputConfigVO inputConfigVO, Long tenantid) {
		Map<String,String> map = inputConfigVO.getMap();
		if(map==null || map.isEmpty())
			return vo;
		
		if(!map.containsKey("name"))
			return vo;
		
		List<DictDataWarperVO> dictList = DictUtils.getDictData(inputConfigVO,tenantid);
		return code2Name(vo, dictList, inputConfigVO);
	}
	
	private AbstractVO code2Name(AbstractVO vo, List<DictDataWarperVO> dictList, DictInputConfigVO inputConfigVO) {
		Map<String,String> map = inputConfigVO.getMap();
		if(map.containsKey("id")) {
			if(inputConfigVO.isIsmulti()) {
				String idField = map.get("id");
				String value = vo.getString(idField);
				if(StringUtils.isEmpty(value)) {
					return vo;
				}
				String[] ids = value.split(",");
				List<DictDataWarperVO> list = matchDictDataById(ids, dictList);
				StringBuilder sb = new StringBuilder();
				for(DictDataWarperVO dictDataWarperVO : list) {
					sb.append(dictDataWarperVO.getName());
					sb.append(",");
				}
				if(sb.length()>0) {
					sb.deleteCharAt(sb.length()-1);
				}
				String nameField = map.get("name");
				vo.set(nameField, sb.toString());
			}else {
				Long id = vo.getLong(map.get("id"));
				if(id==null)
					return vo;
				DictDataWarperVO dictData = matchDictDataById(id, dictList);
				if(dictData!=null) {
					String nameField = map.get("name");
					vo.set(nameField, dictData.getName());
				}
			}
		}else {
			if(inputConfigVO.isIsmulti()) {
				String codeField = map.get("code");
				String value = vo.getString(codeField);
				if(StringUtils.isEmpty(value)) {
					return vo;
				}
				String[] codes = value.split(",");
				List<DictDataWarperVO> list = matchDictDataByCode(codes, dictList);
				StringBuilder sb = new StringBuilder();
				for(DictDataWarperVO dictDataWarperVO : list) {
					sb.append(dictDataWarperVO.getName());
					sb.append(",");
				}
				if(sb.length()>0) {
					sb.deleteCharAt(sb.length()-1);
				}
				String nameField = map.get("name");
				vo.set(nameField, sb.toString());
			}else {
				String code = vo.getString(map.get("code"));
				DictDataWarperVO dictData = matchDictDataByCode(code, dictList);
				if(dictData!=null) {
					String nameField = map.get("name");
					vo.set(nameField, dictData.getName());
				}
			}
		}
		return vo;
	}
	
	private List<? extends AbstractVO> code2Name(List<? extends AbstractVO> voList, List<DictDataWarperVO> dictList, DictInputConfigVO inputConfigVO) {
		for(AbstractVO vo : voList) {
			code2Name(vo, dictList, inputConfigVO);
		}
		return voList;
	}
	
	/**
	 * 获取字典名称
	 * @param inputConfig
	 * @param dictDataCode
	 * @param tenantid
	 * @param isMulti
	 * @return
	 * @throws HDException
	 */
	public String getDictName(String inputConfig, String dictDataCode, Long tenantid, boolean isMulti) throws HDException {
		if(StringUtils.isEmpty(dictDataCode))
			return "";
		
		DictInputConfigVO inputConfigVO = InputConfigUtils.getDictInputConfigVO(inputConfig);
		 
		List<DictDataWarperVO> dictList = DictUtils.getDictData(inputConfigVO,tenantid);
		
		if(isMulti || inputConfigVO.isIsmulti()) {
			String[] dictCodes = dictDataCode.split(",");
			StringBuilder sb = new StringBuilder();
			for(String dictCode : dictCodes) {
				DictDataWarperVO dictData = matchDictDataByCode(dictCode, dictList);
				sb.append(dictData.getName());
				sb.append(",");
			}
			sb.deleteCharAt(sb.length()-1);
			return sb.toString();
		}else {
			DictDataWarperVO dictData = matchDictDataByCode(dictDataCode, dictList);
			return dictData.getName();
		}
	}
	
	
	/**
	 * 
	 * @param List
	 * @param formItemVO 
	 * @author zhangfeng
	 * @date 2016年8月31日
	 */
	public void code2Name(List<? extends AbstractVO> list,FormItemVO formItemVO,Long tenantid) {
		if(!ObjectUtil.isNotEmpty(list)) return;
		
		if(!InputTypeEnum.DICT_NEW.getCode().equals(formItemVO.getFitem_input_type())) return;//不是字典，用不着处理
		
		DictInputConfigVO inputConfigVO;
		try {
			inputConfigVO = InputConfigUtils.getDictInputConfigVO(formItemVO.getFitem_input_config());
		} catch (HDException e) {
			throw new HDRuntimeException(e);
		}		
		
		Map<String,String> map = inputConfigVO.getMap();
		if(map==null || map.isEmpty())
			return;
		
		if(!map.containsKey("name"))
			return;
		
		List<DictDataWarperVO> dictList = DictUtils.getDictData(inputConfigVO,tenantid);		
		code2Name(list, dictList, inputConfigVO);
	}
	
	private DictDataWarperVO matchDictDataById(Long id, List<DictDataWarperVO> dictList) {
		if(ObjectUtil.isNotEmpty(dictList)) {
			for(DictDataWarperVO dictData:dictList) {
				if(dictData.getDictdataid().equals(id)) {
					return dictData;
				}
				
				DictDataWarperVO dictDataTemp = matchDictDataById(id,dictData.getChildren());
				if(dictDataTemp!=null) {
					return dictDataTemp;
				}
			}
		}
		return null;
	}
	
	private DictDataWarperVO matchDictDataByCode(String code, List<DictDataWarperVO> dictList) {
		if(ObjectUtil.isNotEmpty(dictList)) {
			for(DictDataWarperVO dictData:dictList) {
				if(dictData.getCode().equals(code)) {
					return dictData;
				}
				
				DictDataWarperVO dictDataTemp = matchDictDataByCode(code,dictData.getChildren());
				if(dictDataTemp!=null) {
					return dictDataTemp;
				}
			}
		}
		return null;
	}
	
	private List<DictDataWarperVO> matchDictDataById(String[] ids, List<DictDataWarperVO> dictList) {
		List<DictDataWarperVO> result = new ArrayList<>();
		if(!ObjectUtil.isNotEmpty(dictList))
			return result;
		
		if(ids==null || ids.length==0) 
			return result;
		
		out:for(String idStr : ids) {
			long id = Long.parseLong(idStr);
			for(DictDataWarperVO dictData:dictList) {
				if(dictData.getDictdataid().equals(id)) {
					result.add(dictData);
					continue out;
				}
				
				DictDataWarperVO dictDataTemp = matchDictDataById(id,dictData.getChildren());
				if(dictDataTemp!=null) {
					result.add(dictDataTemp);
					continue out;
				}
			}
		}
		return result;
	}
	
	private List<DictDataWarperVO> matchDictDataByCode(String[] codes, List<DictDataWarperVO> dictList) {
		List<DictDataWarperVO> result = new ArrayList<>();
		if(!ObjectUtil.isNotEmpty(dictList))
			return result;
		
		if(codes==null || codes.length==0) 
			return result;
		
		out:for(String code : codes) {
			for(DictDataWarperVO dictData:dictList) {
				if(dictData.getCode().equals(code)) {
					result.add(dictData);
					continue out;
				}
				
				DictDataWarperVO dictDataTemp = matchDictDataByCode(code,dictData.getChildren());
				if(dictDataTemp!=null) {
					result.add(dictDataTemp);
					continue out;
				}
			}
		}
		return result;
	}
}
