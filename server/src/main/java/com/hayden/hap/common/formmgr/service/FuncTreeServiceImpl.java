package com.hayden.hap.common.formmgr.service;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.dict.entity.DictDataWarperVO;
import com.hayden.hap.common.dict.entity.DictVO;
import com.hayden.hap.common.dict.itf.IDictService;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.form.entity.FormVO;
import com.hayden.hap.common.form.itf.IFormItemService;
import com.hayden.hap.common.form.itf.IFormService;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.entity.FuncTreeParamNodeVO;
import com.hayden.hap.common.formmgr.inputconfig.DictInputConfigVO;
import com.hayden.hap.common.formmgr.itf.IFuncTreeService;
import com.hayden.hap.common.formmgr.utils.InputConfigUtils;
import com.hayden.hap.common.func.entity.FuncPCVO;
import com.hayden.hap.common.func.entity.FuncVO;
import com.hayden.hap.common.func.itf.IFuncService;
import com.hayden.hap.common.serial.JsonUtils;
import com.hayden.hap.common.utils.SyConstant;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 全息树相关服务接口
 * @author zhangfeng
 * @date 2016年12月20日
 */
@Service("funcTreeService")
public class FuncTreeServiceImpl implements IFuncTreeService{

	private static final int QUERY_FIELD_ID=1;
	private static final int QUERY_FIELD_CODE=2;
	private static final int QUERY_FIELD_INNERCODE=3;
	private static final int QUERY_FIELD_LEVELCODE=4;
	@Autowired
	private IFuncService funcService;
	
	@Autowired
	private IFormService formService;
	
	@Autowired
	private IFormItemService formItemService;
	
	@Autowired
	private IDictService dictService;
	
	@Autowired
	private IBaseService baseService;
	
	
	/**
	 * 解析全息查询条件
	 * @param queryString
	 * @param funcCode
	 * @param formItemVOs
	 * @param tenantid
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2016年12月22日
	 */
	public String parseFuncTreeQueryStr(String queryString,String funcCode,
			List<? extends FormItemVO> formItemVOs,Long tenantid) throws HDException {
		List<FuncTreeParamNodeVO> nodeList = JsonUtils.parseArray(queryString, FuncTreeParamNodeVO.class);
		
		StringBuilder sb = new StringBuilder();
		for(FuncTreeParamNodeVO node : nodeList) {
			if(node==null)
				continue;
			FormItemVO formItemVO = matchFormItemVO(node.getFitemCode(), formItemVOs);
			DictInputConfigVO inputConfigVO = InputConfigUtils.getDictInputConfigVO(formItemVO.getFitem_input_config());
			if(inputConfigVO==null) {//字典数据功能，特殊处理
				inputConfigVO = getDictDataInputConfigVO();
			}
			int queryField = getQueryField(node.getFitemCode(), inputConfigVO);
			sb.append("(");
			for(DictDataWarperVO dictData : node.getNodes()) {
				if(QUERY_FIELD_ID==queryField) {
					if(inputConfigVO.isIsmulti()) {//如果配置的是多选，则用like，前后都有百分号
						sb.append("(");
						sb.append(node.getFitemCode());
						sb.append(" like '");
						sb.append(dictData.getDictdataid());
						sb.append(",%'");
						
						sb.append(" or ");
						sb.append(node.getFitemCode());
						sb.append(" like '%,");
						sb.append(dictData.getDictdataid());
						sb.append(",%'");
						
						sb.append(" or ");
						sb.append(node.getFitemCode());
						sb.append(" like '%,");
						sb.append(dictData.getDictdataid());
						sb.append("'");
						sb.append(")");						 
					}else {
						sb.append(node.getFitemCode());
						sb.append(" = ");
						sb.append(dictData.getDictdataid());
					}
				}else if(QUERY_FIELD_CODE==queryField) {
					String queryStr = assemblingQueryStr(formItemVO, inputConfigVO, node.getFitemCode(), dictData.getCode());
					sb.append(queryStr);
				}else if(QUERY_FIELD_INNERCODE==queryField) {
					String innercodeField = inputConfigVO.getInnercode();
					FormVO formVO = formService.getFormVOByFunccode(funcCode, tenantid);
					AbstractVO vo = baseService.queryByPKAndTenantid(formVO.getQuery_table_code(), dictData.getDictdataid(), tenantid);
					String innercode = null;
					if(vo==null) {//当是删除了选中节点时候，vo就空了，那就按固定的查吧
						innercode = "";
					}else {
						innercode = vo.getString(innercodeField);
					}					
					sb.append(innercodeField);
					sb.append(" like '");
					sb.append(innercode);
					sb.append("%'");
				}
				else if (QUERY_FIELD_LEVELCODE==queryField) {
					String levelcode = inputConfigVO.getLevelcode();
					String dictCode = inputConfigVO.getDictcode();
					DictVO dictVO = dictService.getDictByCode_Cache(dictCode, tenantid);
					AbstractVO vo = baseService.queryByPKAndTenantid(dictVO.getDict_t_table(), dictData.getDictdataid(), tenantid);
					String levelcodeValue = vo.getString(levelcode);
					DynaSqlVO sql = new DynaSqlVO();
					sql.addWhereClause(levelcode+" like '"+levelcodeValue+"%'");
					sql.addWhereParam(SyConstant.TENANT_STR, tenantid);
					VOSet<AbstractVO> vos = baseService.query(dictVO.getDict_t_table(), sql);
					sb.append(node.getFitemCode());
					sb.append(" in ");
					sb.append("(");
					sb.append("'"+dictData.getDictdataid()+"'");
					for (AbstractVO abstractVo : vos.getVoList()) {
						sb.append(",'"+abstractVo.getString(inputConfigVO.getQuerycode())+"'");
					}
					sb.append(")");
				}
				sb.append(" or ");
			}
			sb.delete(sb.length()-3, sb.length());
			sb.append(")");
			sb.append(" and ");
		}
		
		if(sb.length()>3) {
			sb.delete(sb.length()-4, sb.length());
		}
		
		return sb.toString();
	}
	
	/**
	 * 获取字典数据功能的输入设定
	 * 由于其功能特殊性，
	 * @return 
	 * @author zhangfeng
	 * @date 2017年4月19日
	 */
	private DictInputConfigVO getDictDataInputConfigVO() {
		DictInputConfigVO inputConfigVO = new DictInputConfigVO();
		return inputConfigVO;
	}
	
	private static FormItemVO matchFormItemVO(String fitemCode, List<? extends FormItemVO> formItemVOs) {
		for(FormItemVO itemVO : formItemVOs) {
			if(fitemCode.equals(itemVO.getFitem_code()))
				return itemVO;
		}
		return null;
	}
	
	/**
	 * 查询方式
	 * @param fitemCode
	 * @param configVO
	 * @return 
	 * @author zhangfeng
	 * @date 2016年12月20日
	 */
	private int getQueryField(String fitemCode, DictInputConfigVO configVO) {
		if(StringUtils.isNotEmpty(configVO.getInnercode()))
			return QUERY_FIELD_INNERCODE;
		
		if(configVO.getMap()==null)
			return QUERY_FIELD_CODE;
		if (StringUtils.isNotEmpty(configVO.getLevelcode())&&StringUtils.isNotEmpty(configVO.getQuerycode())) {
			return QUERY_FIELD_LEVELCODE;
		}
		for(Entry<String, String> entry : configVO.getMap().entrySet()) {
			if(fitemCode.equals(entry.getValue())) {
				if("id".equalsIgnoreCase(entry.getKey().toLowerCase()))
					return QUERY_FIELD_ID;
				return QUERY_FIELD_CODE;
			}
		}
		return QUERY_FIELD_CODE;
	}
	
	private String assemblingQueryStr(FormItemVO itemVO, DictInputConfigVO configVO, String key, String value) {
		StringBuilder sb = new StringBuilder();
		
		if(configVO.isIsmulti()) {//如果配置的是多选，则用like，前后都有百分号
			sb.append("(");
			sb.append(key);
			sb.append(" like '");
			sb.append(value);
			sb.append(",%'");
			
			sb.append(" or ");
			sb.append(key);
			sb.append(" like '%,");
			sb.append(value);
			sb.append(",%'");
			
			sb.append(" or ");
			sb.append(key);
			sb.append(" like '%,");
			sb.append(value);
			sb.append("'");
			sb.append(")");
			return sb.toString();
		}
		
		if("eq".equals(itemVO.getFitem_query_one())) {//如果字段配置的查询方式是等于，则就等于
			sb.append(key);
			sb.append(" ='");
			sb.append(value);
			sb.append("'");
			return sb.toString();
		}
		
		//其它情况，就like，后边有百分号
		sb.append(key);
		sb.append(" like '");
		sb.append(value);
		sb.append("%'");
		return sb.toString();	
	}
	
	/**
	 * 当前功能配置的全息查询字段，那个字段对应的字典是当前功能维护的，哪些不是<br/>
	 * 键为表单字段的字段编码，值为是or否
	 * @param funcCode
	 * @param tenantid
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2016年12月20日
	 */
	@Override
	public Map<String,Boolean> getTreeMaintenanceMap(String funcCode, Long tenantid) throws HDException {
		Map<String,Boolean> result = new HashMap<>();
		if(funcCode.toUpperCase().equals("SY_DICT_DATA")) {//字典数据功能都是树维护
			result.put("dict_data_code", Boolean.TRUE);
			return result;
		}

		FuncPCVO funcVO = (FuncPCVO) funcService.queryByFunccode(funcCode, tenantid);
		if(StringUtils.isNotEmpty(funcVO.getFunc_tree())) {//如果配置了全息查询字段
			String[] fitemCodes = funcVO.getFunc_tree().split(",");
			List<? extends FormItemVO> formItemVOs = formItemService.getFormItemsByFunccode(funcCode, tenantid);
			out:for(String fitemCode : fitemCodes) {
				for(FormItemVO formItemVO : formItemVOs) {
					if(fitemCode.equals(formItemVO.getFitem_code())) {//找到对应的表单字段
						String inputConfig = formItemVO.getFitem_input_config();
						if(StringUtils.isNotEmpty(inputConfig)) {//如果该表单字段配置了输入设定
							DictInputConfigVO inputConfigVO = InputConfigUtils.getDictInputConfigVO(inputConfig);
							String dictCode = inputConfigVO.getDictcode();
							DictVO dictVO = dictService.getDictByCode_Cache(dictCode, tenantid);

							FormVO formVO = formService.getFormVOByFunccode(funcCode, tenantid);
							if(dictVO==null || formVO==null) {//如果没找到字典或表单，判定不是树维护
								result.put(fitemCode, Boolean.FALSE);
								continue out;
							}

							//如果两个表名相同，则是树的维护啦
							if(dictVO.getDict_t_table().toLowerCase().equals(formVO.getOpera_table_code().toLowerCase())) {
								result.put(fitemCode, Boolean.TRUE);
								continue out;
							}else {
								result.put(fitemCode, Boolean.FALSE);
								continue out;
							}
						}
						continue out;
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * 根据全息查询条件，赋值一些属性初始值
	 * @param vo
	 * @param formParamVO
	 * @return 
	 * @author zhangfeng
	 * @throws HDException 
	 * @date 2016年12月20日
	 */
	@Override
	public AbstractVO assignFunctreeParam(AbstractVO vo, FormParamVO formParamVO, 
			Long tenantid) throws HDException {
		String funcTreeQueryParam = formParamVO.getReqParamVO().getFuncTreeQueryParam();
		if(StringUtils.isEmpty(funcTreeQueryParam))
			return vo;
		
		List<FuncTreeParamNodeVO> nodeVOs = JsonUtils.parseArray(funcTreeQueryParam, FuncTreeParamNodeVO.class);
		
		Map<String,Boolean> treeMaintenanceMap = getTreeMaintenanceMap(formParamVO.getFuncCode(), tenantid);
		for(Entry<String, Boolean> entry : treeMaintenanceMap.entrySet()) {
			FuncTreeParamNodeVO nodeVO = matchByFitemCode(nodeVOs, entry.getKey());
			if(nodeVO==null)
				continue;
			if(entry.getValue()) {
				//设置parentid值
				String parentidColumnName = getParentidColumnName(formParamVO.getFuncCode(),tenantid);
				if(StringUtils.isNotEmpty(parentidColumnName)) {
					if(nodeVO.getNodes()!=null && nodeVO.getNodes().size()==1) {
						vo.set(parentidColumnName, nodeVO.getNodes().get(0).getDictdataid());
					}
				}
				
				//设置code值
				String codeColumnName = getCodeColumnName(formParamVO.getFuncCode(),tenantid);
				if(StringUtils.isNotEmpty(codeColumnName)) {
					if(nodeVO.getNodes()!=null && nodeVO.getNodes().size()==1) {
						vo.set(codeColumnName, nodeVO.getNodes().get(0).getCode());
					}				
				}
			}else {
				List<? extends FormItemVO> formItemVOs = formItemService.getFormItemsByFunccode(formParamVO.getFuncCode(), tenantid);
				FormItemVO formItemVO = matchFormItemVO(nodeVO.getFitemCode(), formItemVOs);
				DictInputConfigVO inputConfigVO = InputConfigUtils.getDictInputConfigVO(formItemVO.getFitem_input_config());
				if(inputConfigVO.getMap()==null) {
					vo.set(nodeVO.getFitemCode(), nodeVO.getNodes().get(0).getCode());
				}
				else {
					for(Entry<String, String> dictInputConfig : inputConfigVO.getMap().entrySet()) {
						if("id".equalsIgnoreCase(dictInputConfig.getKey().toLowerCase()))
						{
							vo.set(dictInputConfig.getValue().toLowerCase(), nodeVO.getNodes().get(0).getDictdataid());
						}
						if("code".equalsIgnoreCase(dictInputConfig.getKey().toLowerCase()))
						{
							vo.set(dictInputConfig.getValue().toLowerCase(), nodeVO.getNodes().get(0).getCode());
						}
						if("name".equalsIgnoreCase(dictInputConfig.getKey().toLowerCase()))
						{
							vo.set(dictInputConfig.getValue().toLowerCase(), nodeVO.getNodes().get(0).getName());
						}
					}
				}
			}
			
		}
		
		return vo;
	}
	
	/**
	 * 根据字段编码匹配查询参数节点
	 * @param list
	 * @param fitemCode
	 * @return 
	 * @author zhangfeng
	 * @date 2016年12月20日
	 */
	private FuncTreeParamNodeVO matchByFitemCode(List<FuncTreeParamNodeVO> list, String fitemCode) {
		for(FuncTreeParamNodeVO nodeVO : list) {
			if(nodeVO==null)
				continue;
			if(fitemCode.equals(nodeVO.getFitemCode()))
				return nodeVO;
		}
		return null;
	}
	
	@Override
	public String getParentidColumnName(String funcCode,Long tenantid) throws HDException {
		FuncVO funcVO = funcService.queryByFunccode(funcCode, tenantid);
		ObjectUtil.validNotNull(funcVO, "根据功能编码："+funcCode+"没有找到功能");
		
		Map<String,Boolean> treeMaintenanceMap = getTreeMaintenanceMap(funcCode, tenantid);
		out:for(Entry<String, Boolean> entry : treeMaintenanceMap.entrySet()) {
			if(entry.getValue()) {
				List<? extends FormItemVO> formItemVOs = formItemService.getFormItemsByFunccode(funcCode, tenantid);
				for(FormItemVO formItemVO : formItemVOs) {
					if(entry.getKey().equals(formItemVO.getFitem_code())) {//找到对应的表单字段
						String inputConfig = formItemVO.getFitem_input_config();
						if(StringUtils.isNotEmpty(inputConfig)) {//如果该表单字段配置了输入设定
							DictInputConfigVO inputConfigVO = InputConfigUtils.getDictInputConfigVO(inputConfig);
							String dictCode = inputConfigVO.getDictcode();
							DictVO dictVO = dictService.getDictByCode_Cache(dictCode, tenantid);
							
							return dictVO.getDict_f_parent();
						}
						break out;
					}
				}
				break;
			}
		}		
		return null;
	}
	
	@Override
	public String getCodeColumnName(String funcCode,Long tenantid) throws HDException {
		FuncVO funcVO = funcService.queryByFunccode(funcCode, tenantid);
		ObjectUtil.validNotNull(funcVO, "根据功能编码："+funcCode+"没有找到功能");
		
		Map<String,Boolean> treeMaintenanceMap = getTreeMaintenanceMap(funcCode, tenantid);
		out:for(Entry<String, Boolean> entry : treeMaintenanceMap.entrySet()) {
			if(entry.getValue()) {
				List<? extends FormItemVO> formItemVOs = formItemService.getFormItemsByFunccode(funcCode, tenantid);
				for(FormItemVO formItemVO : formItemVOs) {
					if(entry.getKey().equals(formItemVO.getFitem_code())) {//找到对应的表单字段
						String inputConfig = formItemVO.getFitem_input_config();
						if(StringUtils.isNotEmpty(inputConfig)) {//如果该表单字段配置了输入设定
							DictInputConfigVO inputConfigVO = InputConfigUtils.getDictInputConfigVO(inputConfig);
							String dictCode = inputConfigVO.getDictcode();
							DictVO dictVO = dictService.getDictByCode_Cache(dictCode, tenantid);
							
							return dictVO.getDict_f_code();
						}
						break out;
					}
				}
				break;
			}
		}
		
		return null;
	}
}
