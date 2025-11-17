package com.hayden.hap.common.excel.utils;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.db.orm.sql.JdbcType;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.enumerate.MessageLevel;
import com.hayden.hap.common.excel.entity.ExcelTemplateItemVO;
import com.hayden.hap.common.formmgr.message.Message;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.formmgr.message.Status;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
/**
 * @ClassName: ExcelValidations
 * @Description: excel数据验证类
 * @author liyan
 *
 */
public class ExcelValidations {

	public static ReturnResult<AbstractVO> validate(ExcelTemplateItemVO excelTemplateItemVO,AbstractVO uncheckVO
			,Object uncheckedValue){
		ReturnResult<AbstractVO> returnResult = new ReturnResult<AbstractVO>();
		List<Message> msgList = new ArrayList<>();
		String item_code = excelTemplateItemVO.getItem_code().toLowerCase();
		if(!ObjectUtil.isNotNull(uncheckedValue)){//非空校验
			returnResult = validateNULL(excelTemplateItemVO,uncheckedValue,uncheckVO);
		}else{//校验长度和类型
			JdbcType jdbcType=JdbcType.forName(excelTemplateItemVO.getItem_type());
			switch (jdbcType.getTypeCode()){
			case Types.TINYINT:
				returnResult = validateTINYINT(excelTemplateItemVO,uncheckedValue,uncheckVO);
				if(null != returnResult.getMessages() && returnResult.getStatus()==Status.FAIL)
					break;
				uncheckedValue = uncheckVO.get(item_code);
				msgList = validateLENGTH(excelTemplateItemVO,uncheckedValue,uncheckVO);
				if(msgList.size()>0){
					returnResult.setMessages(msgList);
				}
				break;
			case Types.INTEGER:
				returnResult = validateINTEGER(excelTemplateItemVO,uncheckedValue,uncheckVO);
				if(null != returnResult.getMessages() && returnResult.getStatus()==Status.FAIL)
					break;
				uncheckedValue = uncheckVO.get(item_code);
				msgList = validateLENGTH(excelTemplateItemVO,uncheckedValue,uncheckVO);
				if(msgList.size()>0){
					returnResult.setMessages(msgList);
				}
				break;
			case Types.BIGINT:
				returnResult = validateBIGINT(excelTemplateItemVO,uncheckedValue,uncheckVO);
				if(null != returnResult.getMessages() && returnResult.getStatus()==Status.FAIL)
					break;
				uncheckedValue = uncheckVO.get(item_code);
				msgList = validateLENGTH(excelTemplateItemVO,uncheckedValue,uncheckVO);
				if(msgList.size()>0){
					returnResult.setMessages(msgList);
				}
				break;
			case Types.FLOAT:
				returnResult = validateFLOAT(excelTemplateItemVO,uncheckedValue,uncheckVO);
				if(null != returnResult.getMessages() && returnResult.getStatus()==Status.FAIL)
					break;
				uncheckedValue = uncheckVO.get(item_code);
				msgList = validatePRECISION(excelTemplateItemVO,uncheckedValue,uncheckVO);
				if(msgList.size()>0){
					returnResult.setMessages(msgList);
				}
				break;
			case Types.DECIMAL:
				returnResult = validateDECIMAL(excelTemplateItemVO,uncheckedValue,uncheckVO);
				if(null != returnResult.getMessages() && returnResult.getStatus()==Status.FAIL)
					break;
				uncheckedValue = uncheckVO.get(item_code);
				msgList  = validatePRECISION(excelTemplateItemVO,uncheckedValue,uncheckVO);
				if(msgList.size()>0){
					returnResult.setMessages(msgList);
				}
				break;
//			case Types.DATE:
//				returnResult = validateDATE(excelTemplateItemVO,uncheckedValue,uncheckVO);
//				break;
			case Types.CHAR:
				returnResult = validateVARCHAR(excelTemplateItemVO,uncheckedValue,uncheckVO);
				if(null != returnResult.getMessages() && returnResult.getStatus()==Status.FAIL)
					break;
				uncheckedValue = uncheckVO.get(item_code);
				msgList = validateLENGTH(excelTemplateItemVO,uncheckedValue,uncheckVO);
				if(msgList.size()>0){
					returnResult.setMessages(msgList);
				}
				break;
			case Types.VARCHAR:
				returnResult = validateVARCHAR(excelTemplateItemVO,uncheckedValue,uncheckVO);
				if(null != returnResult.getMessages() && returnResult.getStatus()==Status.FAIL)
					break;
				uncheckedValue = uncheckVO.get(item_code);
				msgList = validateLENGTH(excelTemplateItemVO,uncheckedValue,uncheckVO);
				if(msgList.size()>0){
					returnResult.setMessages(msgList);
				}
				break; 
			case Types.SMALLINT:
				returnResult = validateINTEGER(excelTemplateItemVO,uncheckedValue,uncheckVO);
				if(null != returnResult.getMessages() && returnResult.getStatus()==Status.FAIL)
					break;
				uncheckedValue = uncheckVO.get(item_code);
				msgList = validateLENGTH(excelTemplateItemVO,uncheckedValue,uncheckVO);
				if(msgList.size()>0){
					returnResult.setMessages(msgList);
				}
				break;
			default:
				returnResult.setData(uncheckVO);
			}
			
		}
		return returnResult;
		
	}

	/**
	 *
	 * @param excelTemplateItemVO
	 * @param uncheckValue
	 * @param uncheckVO
	 * @return ResultMessage
	 */
	public static ReturnResult<AbstractVO> validateNULL(
			ExcelTemplateItemVO excelTemplateItemVO,Object uncheckValue,AbstractVO uncheckVO){
		ReturnResult<AbstractVO> returnResult = null;
		//主键列也可以为空ObjectUtil.isTrue(excelTemplateItemVO.getIs_pk()) || 
		if(ObjectUtil.isTrue(excelTemplateItemVO.getIs_notnull())){
			if(ObjectUtil.isNotNull(uncheckValue)){
				returnResult = new ReturnResult<AbstractVO>(uncheckVO);
			}else{
				returnResult = new ReturnResult<AbstractVO>(excelTemplateItemVO.getColumn_title() + " 不能为空！",MessageLevel.ERROR);
			}
		}
		return returnResult;
	}
	
	
	/**
	 * TINYINT
	 * @param excelTemplateItemVO
	 * @param uncheckVO
	 * @return
	 */
	private static ReturnResult<AbstractVO> validateTINYINT(
			ExcelTemplateItemVO excelTemplateItemVO,Object uncheckValue,AbstractVO uncheckVO){
		ReturnResult<AbstractVO> returnResult = null;
		if(uncheckValue instanceof Integer){
			uncheckVO.setInt(excelTemplateItemVO.getItem_code(), ((Integer) uncheckValue).intValue());
			returnResult = new ReturnResult<AbstractVO>(uncheckVO);
		}else if(uncheckValue instanceof Double){
			uncheckVO.setInt(excelTemplateItemVO.getItem_code(), (Integer)((Double) uncheckValue).intValue());
			returnResult = new ReturnResult<AbstractVO>(uncheckVO);
		}else if(uncheckValue instanceof String){
			try {
				uncheckVO.setInt(excelTemplateItemVO.getItem_code(), 
						Integer.parseInt(uncheckValue.toString()));
				returnResult = new ReturnResult<AbstractVO>(uncheckVO);
			} catch (NumberFormatException e) {
				returnResult = new ReturnResult<AbstractVO>(excelTemplateItemVO.getColumn_title() + "类型不正确,需要整型，写入为"+uncheckValue,MessageLevel.ERROR);
			}
		}else if(uncheckValue instanceof Boolean){
			uncheckVO.setInt(excelTemplateItemVO.getItem_code(), 
					(boolean) uncheckValue?0:1);
			returnResult = new ReturnResult<AbstractVO>(uncheckVO);
		}else {
			returnResult = new ReturnResult<AbstractVO>(excelTemplateItemVO.getColumn_title() + "类型不正确,需要整型，写入为"+uncheckValue,MessageLevel.ERROR);
		}
		return returnResult;
	}
	
	private static ReturnResult<AbstractVO> validateINTEGER(
			ExcelTemplateItemVO excelTemplateItemVO,Object uncheckValue,AbstractVO uncheckVO){
		ReturnResult<AbstractVO> returnResult = null;
		if(uncheckValue instanceof Integer){
			uncheckVO.setInt(excelTemplateItemVO.getItem_code(), ((Integer) uncheckValue).intValue());
			returnResult = new ReturnResult<AbstractVO>(uncheckVO);
		}else if(uncheckValue instanceof Double){
			uncheckVO.setInt(excelTemplateItemVO.getItem_code(), (Integer)((Double) uncheckValue).intValue());
			returnResult = new ReturnResult<AbstractVO>(uncheckVO);
		}else if(uncheckValue instanceof String){
			try {
				uncheckVO.setInt(excelTemplateItemVO.getItem_code(), 
						Integer.parseInt(uncheckValue.toString()));
				returnResult = new ReturnResult<AbstractVO>(uncheckVO);
			} catch (NumberFormatException e) {
				returnResult = new ReturnResult<AbstractVO>(excelTemplateItemVO.getColumn_title() + "类型不正确,需要整型，写入为"+uncheckValue,MessageLevel.ERROR);
			}
		}else {
			returnResult = new ReturnResult<AbstractVO>(excelTemplateItemVO.getColumn_title() + "类型不正确,需要整型，写入为"+uncheckValue,MessageLevel.ERROR);
		}
		return returnResult;
	}
	
	private static ReturnResult<AbstractVO> validateBIGINT(
			ExcelTemplateItemVO excelTemplateItemVO,Object uncheckValue,AbstractVO uncheckVO){
		ReturnResult<AbstractVO> returnResult = null;
		if(uncheckValue instanceof Long){
			uncheckVO.setLong(excelTemplateItemVO.getItem_code(), 
					((Long) uncheckValue).longValue());
			returnResult = new ReturnResult<AbstractVO>(uncheckVO);
		}else if(uncheckValue instanceof Double){
			uncheckVO.setLong(excelTemplateItemVO.getItem_code(), 
					(Long)((Double) uncheckValue).longValue());
			returnResult = new ReturnResult<AbstractVO>(uncheckVO);
		}else if(uncheckValue instanceof String){
			try {
				uncheckVO.setLong(excelTemplateItemVO.getItem_code(), 
						Long.parseLong(uncheckValue.toString()));
				returnResult = new ReturnResult<AbstractVO>(uncheckVO);
			} catch (NumberFormatException e) {
				returnResult = new ReturnResult<AbstractVO>(excelTemplateItemVO.getColumn_title() + "类型不正确,需要整型，写入为"+uncheckValue,MessageLevel.ERROR);
			}
		}else {
			returnResult = new ReturnResult<AbstractVO>(excelTemplateItemVO.getColumn_title() + "类型不正确,需要整型，写入为"+uncheckValue,MessageLevel.ERROR);
		}
		return returnResult;
	}
	
	private static ReturnResult<AbstractVO> validateFLOAT(
			ExcelTemplateItemVO excelTemplateItemVO,Object uncheckValue,AbstractVO uncheckVO){
		ReturnResult<AbstractVO> returnResult = null;
		if(uncheckValue instanceof Float){
			uncheckVO.setFloat(excelTemplateItemVO.getItem_code(), 
					(((Float)uncheckValue)).floatValue());
			returnResult = new ReturnResult<AbstractVO>(uncheckVO);
		}else if(uncheckValue instanceof Double){
			uncheckVO.setFloat(excelTemplateItemVO.getItem_code(), 
					(((Double)uncheckValue)).floatValue());
			returnResult = new ReturnResult<AbstractVO>(uncheckVO);
		}else if(uncheckValue instanceof String){
			try {
				uncheckVO.setFloat(excelTemplateItemVO.getItem_code(), 
						Float.parseFloat(uncheckValue.toString()));
				returnResult = new ReturnResult<AbstractVO>(uncheckVO);
			} catch (NumberFormatException e) {
				returnResult = new ReturnResult<AbstractVO>(excelTemplateItemVO.getColumn_title() + "类型不正确,需要数值型，写入为"+uncheckValue,MessageLevel.ERROR);
			}
		}else {
			returnResult = new ReturnResult<AbstractVO>(excelTemplateItemVO.getColumn_title() + "类型不正确,需要数值型,写入为"+uncheckValue,MessageLevel.ERROR);
		}
		return returnResult;
	}

//	private static ReturnResult<AbstractVO> validateDATE(
//			ExcelTemplateItemVO excelTemplateItemVO,Object uncheckValue,AbstractVO uncheckVO){
//		ReturnResult<AbstractVO> returnResult = null;
//		if(uncheckValue instanceof Date){
//			uncheckVO.set(excelTemplateItemVO.getItem_code(),
//					uncheckValue);
//			returnResult = new ReturnResult<AbstractVO>(uncheckVO);
//		}else if(uncheckValue instanceof String){
//			SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
//			try {
//				Date date = sf.parse(uncheckValue.toString());
//				uncheckVO.set(excelTemplateItemVO.getItem_code(),
//						date);
//				returnResult = new ReturnResult<AbstractVO>(uncheckVO);
//			} catch (ParseException e) {
//				returnResult = new ReturnResult<AbstractVO>(excelTemplateItemVO.getColumn_title() + "类型不正确！",MessageLevel.ERROR);
//			}
//			//returnResult = new ReturnResult<AbstractVO>(excelTemplateItemVO.getColumn_title() + "类型不正确！",MessageLevel.ERROR);
//		}
//		return returnResult;
//	}
	
	private static ReturnResult<AbstractVO> validateDECIMAL(
			ExcelTemplateItemVO excelTemplateItemVO,Object uncheckValue,AbstractVO uncheckVO){
		ReturnResult<AbstractVO> returnResult = null;
		if(uncheckValue instanceof Double){
			uncheckVO.set(excelTemplateItemVO.getItem_code(),Double.parseDouble(uncheckValue.toString())); 
				//	new BigDecimal(String.valueOf(uncheckValue)));
			returnResult = new ReturnResult<AbstractVO>(uncheckVO);
		}else if(uncheckValue instanceof String){
			try {
				uncheckVO.set(excelTemplateItemVO.getItem_code(), 
						Double.parseDouble(uncheckValue.toString()));
				returnResult = new ReturnResult<AbstractVO>(uncheckVO);
			} catch (NumberFormatException e) {
				returnResult = new ReturnResult<AbstractVO>(excelTemplateItemVO.getColumn_title() + "类型不正确，需要数值型，写入值为字符串"+uncheckValue.toString(),MessageLevel.ERROR);
			}
		}else {
			returnResult = new ReturnResult<AbstractVO>(excelTemplateItemVO.getColumn_title() + "类型不正确,需要数值型，写入值为"+uncheckValue,MessageLevel.ERROR);
		}
		return returnResult;
	}
	
	private static ReturnResult<AbstractVO> validateVARCHAR(
			ExcelTemplateItemVO excelTemplateItemVO,Object uncheckValue,AbstractVO uncheckVO){
		ReturnResult<AbstractVO> returnResult = null;
		if(uncheckValue instanceof String){
			uncheckVO.setString(excelTemplateItemVO.getItem_code(), 
					uncheckValue.toString());
			returnResult = new ReturnResult<AbstractVO>(uncheckVO);
		}else if(uncheckValue instanceof Double){
			uncheckVO.setString(excelTemplateItemVO.getItem_code(), 
					Long.toString((((Double)uncheckValue).longValue())));
			returnResult = new ReturnResult<AbstractVO>(uncheckVO);
		}else {
			returnResult = new ReturnResult<AbstractVO>(excelTemplateItemVO.getColumn_title() + "类型不正确，需要字符串，写入值类型为"+uncheckValue.getClass(),MessageLevel.ERROR);
		}
		return returnResult;
	}
	
	private static List<Message> validateLENGTH(
			ExcelTemplateItemVO excelTemplateItemVO,Object uncheckValue,AbstractVO uncheckVO){
			List<Message> msgList = new ArrayList<>();
			if(null == uncheckValue){
				return msgList;
			}
			int length = uncheckValue.toString().length();
			Object lenLimit = excelTemplateItemVO.getItem_len();
			if(null == lenLimit){
				return msgList;
			}
			if(length > ObjectUtil.asInteger(lenLimit.toString())) {
				Message msg = new Message(excelTemplateItemVO.getColumn_title() + " 长度超出限制！",MessageLevel.ERROR);
				msgList.add(msg);
			}
		return msgList;
	}
	
	/**
	 * 结合长度限制校验精度
	 * @param excelTemplateItemVO
	 * @param uncheckValue
	 * @param uncheckVO
	 * @return 
	 * @author liyan
	 * @date 2017年6月6日
	 */
	private static List<Message> validatePRECISION(
			ExcelTemplateItemVO excelTemplateItemVO, Object uncheckValue,
			AbstractVO uncheckVO) {
		List<Message> msgList = new ArrayList<>();
		//if(uncheckValue instanceof String){
		int index = uncheckValue.toString().indexOf(".");
		if(index>0){
			int length = uncheckValue.toString().length()-1;//除去小数点
			int precision = ObjectUtil.asInteger(excelTemplateItemVO.getItem_precision());
			if(length > ObjectUtil.asInteger(excelTemplateItemVO.getItem_len())) {
				Message msg = new Message(excelTemplateItemVO.getColumn_title() + " 长度超出限制！",MessageLevel.ERROR);
				msgList.add(msg);
			}
			if(0!=precision){
				if(length - index >  precision){
					Message msg = new Message(excelTemplateItemVO.getColumn_title() + " 精度超出限制！",MessageLevel.ERROR);
					msgList.add(msg);
				}
			}			
		}
		return msgList;
	}
}
