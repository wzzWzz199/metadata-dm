/**
 * Project Name:hap-sy
 * File Name:TableDefUtils.java
 * Package Name:com.hayden.hap.sy.utils.table
 * Date:2016年3月8日
 * Copyright (c) 2016, zhangjie@ushayden.com All Rights Reserved.
 *
*/

package com.hayden.hap.common.utils.table;

import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.db.tableDef.entity.DataTypeUtil;
import com.hayden.hap.common.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.common.db.tableDef.entity.TableDefVO;
import com.hayden.hap.common.utils.SyConstant;
import com.hayden.hap.common.utils.pdm.Column;
import com.hayden.hap.common.utils.pdm.Key;
import com.hayden.hap.common.utils.pdm.PDMReader;
import com.hayden.hap.common.utils.pdm.Table;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName:TableDefUtils ().<br/>
 * Date:     2016年3月8日  <br/>
 * @author   ZhangJie
 * @version  
 * @see 	 
 */
public class TableDefUtils {

	public static List<TableDefVO> getTableDefFromPDM(InputStream is) throws HDException
	{
		List<Table> tableList = PDMReader.getTables(is);
		return parseToVO(tableList);
	}
	
	public static List<TableDefVO> getTableDefFromPDM(String xmlPath) throws HDException
	{
		List<Table> tableList = PDMReader.getTables(xmlPath);
		return parseToVO(tableList);
	}
	
	private static List<TableDefVO> parseToVO(List<Table> tableList)
	{
		List<TableDefVO> tableDefList = new ArrayList<TableDefVO>();
		for (Table table : tableList) {
			TableDefVO tableDefVO = new TableDefVO();
			tableDefVO.setTable_code(table.Code.toLowerCase());//编码
			if(table.Name.equalsIgnoreCase(table.Code))
			{
				tableDefVO.setTable_name(table.Comment);//名称
			}else
			{
				tableDefVO.setTable_name(table.Name);//名称
			}
			tableDefVO.setTable_name(table.Name);//名称
			tableDefVO.setTable_desc(table.Comment);//描述
			tableDefVO.setTable_type(SyConstant.SY_TRUE);//表或视图
			tableDefVO.setIssqllog(SyConstant.SY_FALSE);//
			//解析是否包含包名等
			String table_code = table.Code;
			if(table_code.contains("/")){
				//获取真正的表名
				table_code = table_code.substring(table_code.lastIndexOf("/")+1);
			}
			tableDefVO.setModulecode(table_code.split("_")[0].toLowerCase());
			//设置实体类名称
			String[] tableInfos = table_code.split("_");
			//目前之支持表名中包含下划线的输入规则
			if(tableInfos.length>=2){
				//表名为大写，包名需要转换成小写
				String packageName="com.hayden.hap."+tableInfos[0]+"."+tableInfos[1]+".entity.";
				String tmpCode = table_code.substring(table_code.indexOf("_")+1);
				tmpCode = tmpCode.substring(0,1).toUpperCase()+tmpCode.substring(1);
				tmpCode = getCamelCaseString(tmpCode, true);
		        String voClassName = packageName.toLowerCase()+tmpCode+"VO";
		        tableDefVO.setClassname(voClassName);
			}
			List<TableColumnVO> columnList = new ArrayList<TableColumnVO>();
			int colorOrder = 1;
			if(table.Columns==null)
				continue;
			String col_table_code = table_code;
			if(table_code.contains("/")){
				//列这里写上具体的表名。表定义带上包名，方便查看，后续插入数据库时再更改表名。
				col_table_code = table_code.substring(table_code.lastIndexOf("/")+1);
			}
			for (Column column : table.Columns) {
				TableColumnVO columnVO = new TableColumnVO();
				columnVO.setColcode(column.Code.toLowerCase());
				columnVO.setTable_code(col_table_code.toLowerCase());
				column.DataType = column.DataType.toUpperCase();
				int index = column.DataType.indexOf("(");
				columnVO.setColtype(index>-1?column.DataType.substring(0,index):column.DataType);
				//处理oracle列类型
				String oraColType = DataTypeUtil.getOracleColType(columnVO.getColtype());
				if(org.apache.commons.lang3.StringUtils.isNotEmpty(oraColType))
					columnVO.setOra_coltype(oraColType);
				columnVO.setCollen(StringUtils.isEmpty(column.Length)?Integer.valueOf(0):Integer.parseInt(column.Length));
				columnVO.setColscale(StringUtils.isEmpty(column.Precision)?Integer.valueOf(0):Integer.parseInt(column.Precision));
				columnVO.setColname(column.Name);
				columnVO.setColdesc(StringUtils.isEmpty(column.Comment)?column.Name:column.Comment);
				boolean isPK = false;
				if(table.PrimaryKey!=null){
					for (Key key : table.PrimaryKey) {
						for(Key tableKey:table.Keys)
						{
							if(tableKey.Id.equalsIgnoreCase(key.Ref))
							{
								for(Column colKey:tableKey.Key_Columns)
								{
									if(colKey.Ref.equalsIgnoreCase(column.Id))
									{
										isPK = true;
									}
								}
							}
						}
					}
				}
				if(isPK)
				{
					columnVO.setIspk(SyConstant.SY_TRUE);
					columnVO.setIsautoinc(SyConstant.SY_TRUE);
				}else
				{
					columnVO.setIspk(SyConstant.SY_FALSE);
					columnVO.setIsautoinc(SyConstant.SY_FALSE);
				}
				columnVO.setColdefault(column.DefaultValue);
				if(!StringUtils.isEmpty(column.Mandatory_15))
				{
					//软件版本为15
					columnVO.setIsnotnull(Integer.parseInt(column.Mandatory_15));
				}else if(!StringUtils.isEmpty(column.Mandatory_16))
				{
					//软件版本为16
					columnVO.setIsnotnull(Integer.parseInt(column.Mandatory_16));
				}else
					columnVO.setIsnotnull(0);
				columnVO.setGencode(null);
				columnVO.setColorder(colorOrder++);
				columnList.add(columnVO);
			}
			tableDefVO.setColumnList(columnList);
			tableDefList.add(tableDefVO);
		}
		return tableDefList;
	}

	/**
     * Gets the camel case string.
     *
     * @param inputString
     *            the input string
     * @param firstCharacterUppercase
     *            the first character uppercase
     * @return the camel case string
     */
    public static String getCamelCaseString(String inputString,
            boolean firstCharacterUppercase) {
        StringBuilder sb = new StringBuilder();

        boolean nextUpperCase = false;
        for (int i = 0; i < inputString.length(); i++) {
            char c = inputString.charAt(i);

            switch (c) {
            case '_':
            case '-':
            case '@':
            case '$':
            case '#':
            case ' ':
            case '/':
            case '&':
                if (sb.length() > 0) {
                    nextUpperCase = true;
                }
                break;

            default:
                if (nextUpperCase) {
                    sb.append(Character.toUpperCase(c));
                    nextUpperCase = false;
                } else {
                    sb.append(Character.toLowerCase(c));
                }
                break;
            }
        }

        if (firstCharacterUppercase) {
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        }

        return sb.toString();
    }
}

