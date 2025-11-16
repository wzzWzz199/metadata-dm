package com.hayden.hap.common.formmgr.query;

import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.enumerate.InputTypeEnum;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.formmgr.inputconfig.DictInputConfigVO;
import com.hayden.hap.common.formmgr.inputconfig.QueryselectorInputConfigVO;
import com.hayden.hap.common.serial.JsonUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 多选的查询解析
 * @author zhangfeng
 * @date 2017年11月10日
 */
public class MultSelectWhereclausParser implements IWhereclausParser{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(MultSelectWhereclausParser.class);

	@Override
	public String parse(FormItemVO fieldVO, Object oldValue, String queryMethodStr, String key) {
		if(oldValue==null || StringUtils.isEmpty(oldValue+""))
			return "";
		
		if("eq".equals(queryMethodStr)) {
			//不考虑全排列，我们认为你选择的顺序就是正确的
			return key+" = '"+oldValue+"'";
		}else if("in".equals(queryMethodStr)) {
			String[] values = oldValue.toString().split(",");
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			for(String single : values) {
				sb.append("(");
				sb.append(key);
				sb.append(" like ");
				sb.append("'");
				sb.append(single);
				sb.append(",%'");
				
				sb.append(" or ");
				sb.append(key);
				sb.append(" like ");
				sb.append("'%,");
				sb.append(single);
				sb.append(",%'");
				
				sb.append(" or ");
				sb.append(key);
				sb.append(" like ");
				sb.append("'%,");
				sb.append(single);
				sb.append("'");
				sb.append(")");
				
				sb.append(" or ");
				sb.append(key);
				sb.append(" = ");
				sb.append("'");
				sb.append(single);
				sb.append("'");
				
				sb.append(" or ");
			}
			sb.delete(sb.length()-3, sb.length());
			sb.append(")");
			return sb.toString();
		}
		
		return null;
	}

	public boolean support(FormItemVO fieldVO, Object oldValue, String queryMethodStr, String key) {
		String type = fieldVO.getFitem_input_type();
		String inputConfigStr = fieldVO.getFitem_input_config();
		if(StringUtils.isBlank(inputConfigStr))
			return false;
		
		if(!("eq".equals(queryMethodStr) || "in".equals(queryMethodStr))) {
			return false;
		}
		
		if(InputTypeEnum.DICT_NEW.getCode().equals(type)) {
			try {
				DictInputConfigVO configVO = JsonUtils.parse(inputConfigStr, DictInputConfigVO.class);
				if(configVO!=null)
					return configVO.isIsmulti();
			} catch (HDException e) {
				logger.error(e.getMessage(),e);
				logger.error("字典输入设定不正确，以上异常将导致查询结果过滤不符合预期...");
				return false;
			}
		}else if(InputTypeEnum.QUERY_SELECT.getCode().equals(type)) {
			try {
				QueryselectorInputConfigVO configVO = JsonUtils.parse(inputConfigStr, QueryselectorInputConfigVO.class);
				if(configVO!=null)
					return configVO.isIsmulti();
			} catch (HDException e) {
				logger.error(e.getMessage(),e);
				logger.error("查询选择输入设定不正确，以上异常将导致查询结果过滤不符合预期...");
				return false;
			}
		}
		return false;
	}
}
