package com.hayden.hap.common.form.conver.mobile;

import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.common.form.conver.mobile.codeconver.DiscardMConverter;
import com.hayden.hap.common.form.conver.pc.ConverterFactory;
import com.hayden.hap.common.form.entity.FormMVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 表字段到移动端表单字段转换器工厂
 * @author zhangfeng
 * @date 2018年5月10日
 */
public class ConverterMFactory {

	private static final Logger logger = LoggerFactory.getLogger(ConverterFactory.class);

	private static final Map<String,Class<? extends IMConverter>> codeConverterMap = new HashMap<>();
	static {
		codeConverterMap.put("created_by", DiscardMConverter.class);
		codeConverterMap.put("tenantid", DiscardMConverter.class);
		codeConverterMap.put("updated_by", DiscardMConverter.class);
		codeConverterMap.put("created_dt", DiscardMConverter.class);
		codeConverterMap.put("tenantid", DiscardMConverter.class);
		codeConverterMap.put("updated_dt", DiscardMConverter.class);
		codeConverterMap.put("ver", DiscardMConverter.class);
		codeConverterMap.put("ts", DiscardMConverter.class);
		
//		codeConverterMap.put("wf_current_user", DiscardMConverter.class);
//		codeConverterMap.put("wf_audit_state", DiscardMConverter.class);
//		codeConverterMap.put("wf_create_user", DiscardMConverter.class);
//		codeConverterMap.put("wf_instance", DiscardMConverter.class);
//		codeConverterMap.put("wf_type", DiscardMConverter.class);
//		codeConverterMap.put("wf_current_nodeid", DiscardMConverter.class);
//		codeConverterMap.put("wf_audit_time", DiscardMConverter.class);
	}

	private TableColumnVO columnVO;
	
	private FormMVO formVO;

	public ConverterMFactory(TableColumnVO columnVO, FormMVO formVO) {
		this.columnVO = columnVO;
		this.formVO = formVO;
	}

	@SuppressWarnings("unchecked")
	public IMConverter getConverter() throws HDException {
		Class<? extends IMConverter> clazz = null;
		if(codeConverterMap.containsKey(columnVO.getColcode())) {
			clazz = codeConverterMap.get(columnVO.getColcode());
		}else {
			String upcase = columnVO.getColtype();
			String correctCase = captureName(upcase.toLowerCase());
			String className = "com.hayden.hap.common.form.conver.mobile.typeconver."+correctCase+"MConverter";
			try {
				clazz = (Class<? extends IMConverter>) Class.forName(className);
			} catch (ClassNotFoundException e) {
				logger.info("没有找到"+upcase+"对应的转换器，故采用默认转换器...");
				clazz = DefaultMConverter.class;
			}
		}
		try {
			IMConverter converter = clazz.newInstance();
			converter.setColumnVO(columnVO);
			converter.setFormVO(formVO);
			return converter;
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			throw new HDException(e);
		}
	}
	

	/**
	 * 首字母大写
	 * @param name
	 * @return 
	 * @author zhangfeng
	 * @date 2017年3月7日
	 */
	private static String captureName(String name) {
		char[] cs=name.toCharArray();
		cs[0]-=32;
		return String.valueOf(cs);

	}
}
