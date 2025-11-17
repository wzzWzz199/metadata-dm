package com.hayden.hap.common.form.conver.pc;

import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.common.form.conver.pc.codeconver.CreatedByConverter;
import com.hayden.hap.common.form.conver.pc.codeconver.DiscardConverter;
import com.hayden.hap.common.form.conver.pc.codeconver.TenantidConverter;
import com.hayden.hap.common.form.conver.pc.codeconver.UpdatedByConverter;
import com.hayden.hap.common.form.entity.FormPCVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author zhangfeng
 * @date 2017年3月7日
 */
public class ConverterFactory {

	private static final Logger logger = LoggerFactory.getLogger(ConverterFactory.class);

	private static final Map<String,Class<? extends IPCConverter>> codeConverterMap = new HashMap<>();
	static {
		codeConverterMap.put("created_by", CreatedByConverter.class);
		codeConverterMap.put("tenantid", TenantidConverter.class);
		codeConverterMap.put("updated_by", UpdatedByConverter.class);
		
		codeConverterMap.put("ver", DiscardConverter.class);
		codeConverterMap.put("ts", DiscardConverter.class);
//		codeConverterMap.put("wf_current_user", DiscardConverter.class);
//		codeConverterMap.put("wf_audit_state", DiscardConverter.class);
//		codeConverterMap.put("wf_create_user", DiscardConverter.class);
//		codeConverterMap.put("wf_instance", DiscardConverter.class);
//		codeConverterMap.put("wf_type", DiscardConverter.class);
//		codeConverterMap.put("wf_current_nodeid", DiscardConverter.class);
//		codeConverterMap.put("wf_audit_time", DiscardConverter.class);
	}

	private TableColumnVO columnVO;
	
	private FormPCVO formVO;

	public ConverterFactory(TableColumnVO columnVO, FormPCVO formVO) {
		this.columnVO = columnVO;
		this.formVO = formVO;
	}

	@SuppressWarnings("unchecked")
	public IPCConverter getConverter() throws HDException {
		Class<? extends IPCConverter> clazz = null;
		if(codeConverterMap.containsKey(columnVO.getColcode())) {
			clazz = codeConverterMap.get(columnVO.getColcode());
		}else {
			String upcase = columnVO.getColtype();
			String correctCase = captureName(upcase.toLowerCase());
			String className = "com.hayden.hap.common.form.conver.pc.typeconver."+correctCase+"Converter";
			try {
				clazz = (Class<? extends IPCConverter>) Class.forName(className);
			} catch (ClassNotFoundException e) {
				logger.info("没有找到"+upcase+"对应的转换器，故采用默认转换器...");
				clazz = DefaultConverter.class;
			}
		}
		try {
			IPCConverter converter = clazz.newInstance();
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
