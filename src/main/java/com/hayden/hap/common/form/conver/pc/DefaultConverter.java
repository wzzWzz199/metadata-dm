package com.hayden.hap.common.form.conver.pc;

import com.hayden.hap.common.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.common.enumerate.*;
import com.hayden.hap.common.form.entity.FormItemPCVO;
import com.hayden.hap.common.form.entity.FormPCVO;
import com.hayden.hap.common.utils.SyConstant;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author zhangfeng
 * @date 2017年3月3日
 */
public class DefaultConverter implements IPCConverter {

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getCode()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public String getCode() {
		return columnVO.getColcode();
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getName()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public String getName() {
		String name = columnVO.getColname();
		return name==null?columnVO.getColcode():name;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getDataType()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public String getDataType() {
		return DataTypeEnum.STRING.getCode();
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getLength()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public Integer getLength() {
		return columnVO.getCollen();
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getInputElement()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public String getInputElement() {
		return ElementTypeEnum.INPUT.getCode();
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getInputType()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public String getInputType() {
		return InputTypeEnum.MANUAL.getCode();
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getInputConfig()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public String getInputConfig() {
		return null;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getType()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public String getType() {
		return FitemTypeEnum.TABLE.getCode();
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getCardDefault()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public String getCardDefault() {
		return columnVO.getColdefault();
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getProductFlag()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public String getProductFlag() {
		if(StringUtils.isNotEmpty(formVO.getProduct_flag())) {
			return formVO.getProduct_flag();
		}
		return ProductFlagEnum.PRODUCT.getCode();
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getIsenable()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public Integer getIsenable() {
		return SyConstant.SY_TRUE;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getReadonly()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public Integer getReadonly() {
		return SyConstant.SY_FALSE;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getNotnull()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public Integer getNotnull() {
		return columnVO.getIsnotnull();
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getShowList()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public Integer getShowList() {
		return SyConstant.SY_FALSE;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getQuickList()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public Integer getQuickList() {
		return SyConstant.SY_FALSE;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getBatch()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public Integer getBatch() {
		return SyConstant.SY_FALSE;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getComQuery()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public Integer getComQuery() {
		return SyConstant.SY_FALSE;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getQuickQuery()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public Integer getQuickQuery() {
		return SyConstant.SY_FALSE;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getSortable()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public Integer getSortable() {
		return SyConstant.SY_TRUE;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getValueRegexp()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public String getValueRegexp() {
		return null;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getValueRegexpMsg()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public String getValueRegexpMsg() {
		return null;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getValueScope()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public String getValueScope() {
		return null;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getValueScopeMsg()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public String getValueScopeMsg() {
		return null;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getUniqueGroup()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public String getUniqueGroup() {
		return null;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getUniqueInfo()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public Integer getUniqueInfo() {
		return SyConstant.SY_FALSE;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getQueryOne()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public String getQueryOne() {
		return null;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getQueryTwo()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public String getQueryTwo() {
		return null;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getCardRow()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public Integer getCardRow() {
		return 1;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getCardColumn()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public Integer getCardColumn() {
		return 1;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getColumnOrder()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public Integer getColumnOrder() {
		return columnVO.getColorder()!=null?columnVO.getColorder():1;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getQueryOrder()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public Integer getQueryOrder() {
		return columnVO.getColorder()!=null?columnVO.getColorder():1;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getOrder()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public Integer getOrder() {
		return columnVO.getColorder()!=null?columnVO.getColorder():1;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getQueryDefault()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public String getQueryDefault() {
		return null;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getListWidth()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public String getListWidth() {
		return "100";
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getCardFormat()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public String getCardFormat() {
		return null;
	}

	@Override
	public Integer getListColWrap() {
		return SyConstant.SY_FALSE;
	};
	
	/** 
	 *
	 * @see com.hayden.hap.common.form.conver.pc.IPCConverter#getListFormat()
	 * @author zhangfeng
	 * @date 2017年3月3日
	 */
	@Override
	public String getListFormat() {
		return null;
	}
	
	@Override
	public Integer getQueryNotnull() {
		return SyConstant.SY_FALSE;
	}

	@Override
	public String getInputTypeQuery() {
		return getInputType();
	}
	
	@Override
	public String getInputElementQuery() {
		return getInputElement();
	}

	@Override
	public String getInputConfigQuery() {
		return getInputConfig();
	}

	@Override
	public String getTextAlign() {
		return TextAlignEnum.LEFT.getCode();
	}
	
	private TableColumnVO columnVO;

	public TableColumnVO getColumnVO() {
		return columnVO;
	}

	public void setColumnVO(TableColumnVO columnVO) {
		this.columnVO = columnVO;
	}
	
	private FormPCVO formVO;	

	public FormPCVO getFormVO() {
		return formVO;
	}

	public void setFormVO(FormPCVO formVO) {
		this.formVO = formVO;
	}

	@Override
	public List<FormItemPCVO> getItems() {
		List<FormItemPCVO> result = new ArrayList<>();
		
		FormItemPCVO vo = new FormItemPCVO();
		vo.setFitem_code(getCode());
		vo.setFitem_name(getName());
		vo.setFitem_data_type(getDataType());
		vo.setFitem_length(getLength());
		vo.setFitem_input_element(getInputElement());
		vo.setFitem_input_element_query(getInputElement());
		vo.setFitem_input_type(getInputType());
		vo.setFitem_input_type_query(getInputType());
		vo.setFitem_input_config(getInputConfig());
		vo.setFitem_type(getType());
		vo.setFitem_card_default(getCardDefault());
		vo.setProduct_flag(getProductFlag());
		vo.setFitem_isenable(getIsenable());
		vo.setFitem_readonly(getReadonly());
		vo.setFitem_notnull(getNotnull());
		vo.setFitem_show_list(getShowList());
		vo.setFitem_quick_list(getQuickList());
		vo.setFitem_batch(getBatch());
		vo.setFitem_com_query(getComQuery());
		vo.setFitem_quick_query(getQuickQuery());
		vo.setFitem_sortable(getSortable());
		vo.setFitem_value_regexp(getValueRegexp());
		vo.setFitem_value_regexp_msg(getValueRegexpMsg());
		vo.setFitem_value_scope(getValueScope());
		vo.setFitem_value_scope_msg(getValueScopeMsg());
		vo.setFitem_unique_group(getUniqueGroup());
		vo.setFitem_unique_info(getUniqueInfo());
		vo.setFitem_query_one(getQueryOne());
		vo.setFitem_query_two(getQueryTwo());
		vo.setFitem_card_row(getCardRow());
		vo.setFitem_card_column(getCardColumn());
		vo.setFitem_column_order(getColumnOrder());
		vo.setFitem_query_order(getQueryOrder());
		vo.setFitem_order(getOrder());
		vo.setFitem_query_default(getQueryDefault());
		vo.setFitem_list_width(getListWidth());
		vo.setFitem_card_format(getCardFormat());
		vo.setFitem_list_format(getListFormat());
		vo.setFitem_query_notnull(getQueryNotnull());
		vo.setFitem_list_col_wrap(getListColWrap());
		
		vo.setFormid(formVO.getFormid());
		vo.setForm_code(formVO.getForm_code());
		vo.setTenantid(formVO.getTenantid());
		
		result.add(vo);
		return result;
	}

}
