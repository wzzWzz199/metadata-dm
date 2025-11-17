package com.hayden.hap.dbop.db.util;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.stat.TableStat;
import com.ctrip.framework.apollo.ConfigService;
import com.hayden.hap.dbop.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.dbop.db.tableDef.entity.TableDefVO;
import com.hayden.hap.dbop.db.tableDef.itf.ITableDefService;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * mysql To dm
 *
 * @author xudongcheng
 * @date 2023/10/19 14:00
 */
public class MysqlToDMSqlConvert implements SqlConvert {
    private static final Logger logger = LoggerFactory.getLogger(MysqlToDMSqlConvert.class);


    public String convert(String sql) {
        //判断传入sql是否为空
        if (StringUtils.isEmpty(sql))
            return sql;
        String dmSql = sql;
        dmSql = convertMysqlConsts(dmSql);
        dmSql = convertDateFormatFunction(dmSql);
        dmSql = convertStr2DateFunction(dmSql);
        dmSql = convertDateOpeFunction(dmSql);
        dmSql = convertDateCompare(dmSql);
        return dmSql;
    }


    /**
     * 处理mysql的特定常量函数
     *
     * @param sql
     * @return
     * @author wangyi
     * @date 2017年7月3日
     */
    public String convertMysqlConsts(String sql) {
        String returnSql = null;
        //now()替换为sysdate
        Pattern nowPattern = Pattern.compile("now\\(\\)", Pattern.CASE_INSENSITIVE);
        returnSql = nowPattern.matcher(sql).replaceAll("sysdate");
        //ifnull(a,b)替换为nvl(a,b)
        Pattern ifNullPattern = Pattern.compile("ifnull\\(([A-Za-z0-9_\\.,\\s]*)\\)", Pattern.CASE_INSENSITIVE);
        returnSql = ifNullPattern.matcher(returnSql).replaceAll("nvl($1)");
        //curdate()替换为trunc(sysdate,'dd')
        Pattern curDatePattern = Pattern.compile("curdate\\(\\)", Pattern.CASE_INSENSITIVE);
        returnSql = curDatePattern.matcher(returnSql).replaceAll("trunc(sysdate,'dd')");
        //处理类似interval 1 day的时间加减，需要将1加上单引号
        Pattern opeDatePattern = Pattern.compile("interval(\\s*)(-?\\d*)(\\s*)(year|month|day|hour|minute|second)", Pattern.CASE_INSENSITIVE);
        returnSql = opeDatePattern.matcher(returnSql).replaceAll("interval$1'$2'$3$4");
        //处理\n换行符
        returnSql = returnSql.replaceAll("\\\\n", "'||chr(10)||'");
        //移除末尾分号
        if (returnSql.endsWith(";"))
            returnSql = returnSql.substring(0, returnSql.length() - 1);
        //处理GROUP_CONCAT行列转换函数
        Pattern groupConcatPattern = Pattern.compile("group_concat\\s*\\(", Pattern.CASE_INSENSITIVE);
        returnSql = groupConcatPattern.matcher(returnSql).replaceAll("wm_concat\\(");
        //处理cast函数
        returnSql = convertCastFunction(returnSql);
        //处理TIMESTAMPDIFF函数转换
        returnSql = convertTimestampDiffFunction(returnSql);
        //处理 str_to_date 函数
        returnSql=convertStrToDateFunction(returnSql);
        //替换库名
        String dbSuffix = ConfigService.getConfig("module-conf").getProperty("db_suffix", "");
        if (StringUtils.isNotBlank(dbSuffix)) {
            Pattern dataBaePase = Pattern.compile("hap_[^\\s]*_" + dbSuffix + "\\.", Pattern.CASE_INSENSITIVE);
            returnSql = dataBaePase.matcher(returnSql).replaceAll(" ");
        }
        return returnSql;
    }

    /**
     * 处理interval关键字的解析,处理类似interval * day的时间加减
     *
     * @param sql
     * @return
     * @author wangyi
     * @date 2017年7月18日
     */
    public String convertIntervalUsing(String sql) {
        Pattern opeDatePattern = Pattern.compile("interval\\s*([A-Za-z0-9_\\.\\-']*)\\s*(year|month|day|hour|minute|second)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = opeDatePattern.matcher(sql);
        //记录源和变更后的字符串映射
        Map<String, String> changeStrMap = new LinkedHashMap<String, String>();
        while (matcher.find()) {
            //当interval使用了field时，dm不能继续使用interval做日期操作。
            //使用numtodsinterval和numtoyminterval函数
            matcher.start();
            matcher.end();
            int groupCount = matcher.groupCount();
            //为2时表示正确解析，0表示全局字符串，1表示field或数字，2表示日期操作
            if (groupCount == 2) {
                String matchStr = matcher.group(0);
                String interVal = matcher.group(1);
                String dateOpeFunc = matcher.group(2);
                //解析dateOpeFunc值，
                //特殊处理第一个参数，因为有可能存在这种格式：DATE_FORMAT(date_add(CURDATE(), interval 1 DAY),'%Y-%m-%d')
                //去掉前后的单引号，interVal
                if (interVal.startsWith("'") && interVal.endsWith("'")) {
                    interVal = interVal.substring(1, interVal.length() - 1);
                }
                if (!StringUtils.isNumeric(interVal)) {
                    if (dateOpeFunc.equalsIgnoreCase("month") || dateOpeFunc.equalsIgnoreCase("year")) {
                        changeStrMap.put(matchStr, "numtoyminterval(" + interVal + ",'" + dateOpeFunc + "')");
                    } else {
                        changeStrMap.put(matchStr, "numtodsinterval(" + interVal + ",'" + dateOpeFunc + "')");
                    }
                }
            }
        }
        //替换字符串
        for (Map.Entry<String, String> entry : changeStrMap.entrySet()) {
            sql = sql.replace(entry.getKey(), entry.getValue());
        }

        return sql;
    }

    /**
     * 处理DateFormat日期格式转换函数
     *
     * @param sql
     * @return
     * @author wangyi
     * @date 2017年7月3日
     */
    public String convertDateFormatFunction(String sql) {
        Pattern dateFormatPattern = Pattern
                .compile(
                        "date_format\\s*\\(([A-Za-z0-9_\\.,\\s\\(\\)\\-']*)\\s*,\\s*'(%[YmcdHis%:\\-\\s]+)'\\s*\\)",
                        Pattern.CASE_INSENSITIVE);
        Matcher matcher = dateFormatPattern.matcher(sql);
        //记录源和变更后的字符串映射
        Map<String, String> changeStrMap = new LinkedHashMap<String, String>();
        while (matcher.find()) {
            matcher.start();
            matcher.end();
            int groupCount = matcher.groupCount();
            //为2时表示正确解析，0表示全局字符串，1表示第一个参数，2表示第二个参数
            if (groupCount == 2) {
                String matchStr = matcher.group(0);
                String firstParam = matcher.group(1);
                String secondParam = matcher.group(2);
                //解析第二个参数，生成dm可识别日期转换
                String dmDateFormat = getdmDateFormat(secondParam);
                //特殊处理第一个参数，因为有可能存在这种格式：DATE_FORMAT(date_add(CURDATE(), interval 1 DAY),'%Y-%m-%d')
                if (firstParam.toLowerCase().startsWith("date_add") ||
                        firstParam.toLowerCase().startsWith("date_sub")) {
                    firstParam = convertDateOpeFunction(firstParam);
                }
                changeStrMap.put(matchStr, "to_char(" + firstParam + "," + dmDateFormat + ")");
            }
        }
        //替换字符串
        for (Map.Entry<String, String> entry : changeStrMap.entrySet()) {
            sql = sql.replace(entry.getKey(), entry.getValue());
        }
        return sql;
    }

    /**
     * 处理str_to_date日期格式转换函数
     *
     * @param sql
     * @return
     * @author wangyi
     * @date 2017年7月3日
     */
    public String convertStr2DateFunction(String sql) {
        Pattern dateFormatPattern = Pattern
                .compile(
                        "str_to_date\\('([0-9:\\s\\-]*)'\\s*,\\s*'(%[YmdHis%\\-\\s:]+)'\\s*\\)",
                        Pattern.CASE_INSENSITIVE);
        Matcher matcher = dateFormatPattern.matcher(sql);
        //记录源和变更后的字符串映射
        Map<String, String> changeStrMap = new LinkedHashMap<String, String>();
        while (matcher.find()) {
            matcher.start();
            matcher.end();
            int groupCount = matcher.groupCount();
            //为2时表示正确解析，0表示全局字符串，1表示第一个参数，2表示第二个参数
            if (groupCount == 2) {
                String matchStr = matcher.group(0);
                String firstParam = matcher.group(1);
                String secondParam = matcher.group(2);
                //解析第二个参数，生成dm可识别日期转换
                String dmDateFormat = getdmDateFormat(secondParam);
                changeStrMap.put(matchStr, "to_date('" + firstParam + "','" + dmDateFormat + "')");
            }
        }
        //替换字符串
        for (Map.Entry<String, String> entry : changeStrMap.entrySet()) {
            sql = sql.replace(entry.getKey(), entry.getValue());
        }
        return sql;
    }

    /**
     * 转换为dm可识别日期格式
     *
     * @param dateFormat
     * @return
     * @author wangyi
     * @date 2017年7月3日
     */
    private String getdmDateFormat(String dateFormat) {
        //支持年-月-日 时-分-秒格式或年-月-日 时:分:秒格式，还支持仅日期格式
        String[] splitContents = dateFormat.split(" ");
        String dateContent = splitContents[0];
        String timeContent = null;
        //判断是否包含时间
        if (splitContents.length == 2) {
            timeContent = splitContents[1];
        }
        StringBuilder dmFormat = new StringBuilder();
        for (String format : dateContent.split("-")) {
            if (format.equals("%Y"))
                dmFormat.append("yyyy-");
            else if (format.equals("%y"))
                dmFormat.append("yy-");
            else if (format.equals("%c"))
                dmFormat.append("FMMM-");
            else if (format.equals("%m"))
                dmFormat.append("MM-");
            else if (format.equals("%d"))
                dmFormat.append("dd");
        }
        //dmFormat是否为空，为空时，表示传入的为时间
        if (StringUtils.isEmpty(dmFormat)) {
            timeContent = dateContent;
        }
        if (StringUtils.isNotEmpty(timeContent)) {
            String[] timeFormats = null;

            if (timeContent.contains(":"))
                timeFormats = timeContent.split(":");
            else
                timeFormats = timeContent.split("-");

            if (timeFormats != null) {
                //dmFormat值是否为空
                if (dmFormat.length() != 0) {
                    String lastStr = dmFormat.substring(dmFormat.length() - 1);
                    //如果最后字符串为-，则删除
                    if (lastStr.equals("-")) {
                        dmFormat.deleteCharAt(dmFormat.length() - 1);
                    }
                    //添加空格
                    dmFormat.append(" ");
                }
                for (String timeFormat : timeFormats) {
                    if (timeFormat.equals("%H"))
                        dmFormat.append("HH24:");
                    else if (timeFormat.equals("%i"))
                        dmFormat.append("mi:");
                    else if (timeFormat.equals("%s"))
                        dmFormat.append("ss");
                }
            }

        }

        //如果为年月，不包含时间内容，需要删除最后-字符
        if (dmFormat.length() != 0) {
            String lastStr = dmFormat.substring(dmFormat.length() - 1);
            //如果最后字符串为-或:(时间不全)，则删除
            if (lastStr.equals("-") || lastStr.equals(":")) {
                dmFormat.deleteCharAt(dmFormat.length() - 1);
            }
        }
        //前尾加上字符'
        dmFormat.insert(0, "'");
        dmFormat.append("'");
        return dmFormat.toString();
    }

    /**
     * 处理DATE_ADD/DATE_SUB日期操作函数
     *
     * @param sql
     * @return
     * @author wangyi
     * @date 2017年7月3日
     */
    public String convertDateOpeFunction(String sql) {
        Pattern dateOpePattern = Pattern
                .compile(
                        "(date_add|date_sub)\\(([A-Za-z0-9_\\.,'\\s\\(\\)]*)\\s*,\\s*([A-Za-z0-9_\\.'\\s\\-]*)\\s*\\)",
                        Pattern.CASE_INSENSITIVE);
        Matcher matcher = dateOpePattern.matcher(sql);
        //记录源和变更后的字符串映射
        Map<String, String> changeStrMap = new LinkedHashMap<String, String>();
        while (matcher.find()) {
            matcher.start();
            matcher.end();
            int groupCount = matcher.groupCount();
            //为3时表示正确解析，0表示全局字符串，1表示date_add|date_sub，2表示第一个参数，3表示第二个参数
            if (groupCount == 3) {
                String matchStr = matcher.group(0);
                String dateOpeFunc = matcher.group(1);
                String firstParam = matcher.group(2);
                String secondParam = matcher.group(3);
                secondParam = convertIntervalUsing(secondParam);
                //解析dateOpeFunc值，
                //特殊处理第一个参数，因为有可能存在这种格式：DATE_FORMAT(date_add(CURDATE(), interval 1 DAY),'%Y-%m-%d')
                if (dateOpeFunc.toLowerCase().startsWith("date_add")) {
                    changeStrMap.put(matchStr, firstParam + " + " + secondParam);
                } else if (dateOpeFunc.toLowerCase().startsWith("date_sub")) {
                    changeStrMap.put(matchStr, firstParam + " - " + secondParam);
                }
            }
        }
        //替换字符串
        for (Map.Entry<String, String> entry : changeStrMap.entrySet()) {
            sql = sql.replace(entry.getKey(), entry.getValue());
        }
        return sql;
    }

    /**
     * 处理日期间的比较，dm不能进行日期和字符串的比较
     *
     * @param sql
     * @return
     * @author wangyi
     * @date 2017年7月3日
     */
    public String convertDateCompare(String sql) {
        List<TableStat.Condition> conditions = null;
        Collection<TableStat.Column> columns = null;
        Map<String, String> aliasMap = null;
        try {
            //解析sql
            SQLStatementParser parser = new MySqlStatementParser(sql);
            SQLStatement statement = parser.parseStatement();
            MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
            statement.accept(visitor);
            //获取条件
            conditions = visitor.getConditions();
            columns = visitor.getColumns();
            //获取表别名
            aliasMap = visitor.getAliasMap();
        } catch (Exception e) {
            logger.debug(sql + "解析错误！" + e.getMessage());
        }

        String replacedSql = matchSingleDateCompare(sql, conditions, columns, aliasMap);
        //判断是否含有in多个日期操作
        replacedSql = matchMultiDateCompare(replacedSql, conditions, columns, aliasMap);

        return replacedSql;
    }

    /**
     * 解析日期的比较
     *
     * @param sql
     * @param conditions
     * @param aliasMap
     * @return
     * @author wangyi
     * @date 2017年8月16日
     */
    private String matchSingleDateCompare(String sql,
                                          List<TableStat.Condition> conditions, Collection<TableStat.Column> columns, Map<String, String> aliasMap) {
        //两种格式
        String[] regexs = {
                "\\s*([A-Za-z0-9_.]+)\\s*(=|>|>=|<|<=|!=|<>|like)\\s*'((\\d{4}-\\d{1,2}%?)|" +
                        "(\\d{4}-\\d{1,2}-\\d{1,2}%?)|" +
                        "(\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}))'\\s*",
                "\\s*'((\\d{4}-\\d{1,2}%?)|" +
                        "(\\d{4}-\\d{1,2}-\\d{1,2}%?)|" +
                        "(\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}))'\\s*" +
                        "(=|>|>=|<|<=|!=|<>|like)\\s*([A-Za-z0-9_.]+)\\s*"
        };
        //记录源和变更后的字符串映射
        Map<String, String> changeStrMap = new LinkedHashMap<String, String>();
        for (int i = 0; i < regexs.length; i++) {
            //减小解析次数，暂时支持年月、年月日和年月日时间的字符串格式
            Pattern dateOpePattern = Pattern
                    .compile(
                            regexs[i],
                            Pattern.CASE_INSENSITIVE);
            Matcher matcher = dateOpePattern.matcher(sql);
            while (matcher.find()) {
                matcher.start();
                matcher.end();
                int groupCount = matcher.groupCount();
                //为3时表示正确解析，0表示全局字符串，1表示date_add|date_sub，2表示第一个参数，3表示第二个参数
                if (groupCount == 6) {
                    String matchStr = matcher.group(0);
                    String column = null;
                    String compareVal = null;
                    String dateVal = null;
                    if (i == 0) {
                        column = matcher.group(1);
                        compareVal = matcher.group(2);
                        dateVal = matcher.group(3);
                    } else {
                        column = matcher.group(6);
                        compareVal = matcher.group(5);
                        dateVal = matcher.group(1);
                    }
                    //解析dateVal值，临时使用长度解析。
                    //当长度小于等于7时，为年月。yyyy-mm
                    //解析是否是日期，有可能确实是字符串，存储数据为日期格式
                    boolean isDateField = isDateField(column, compareVal, dateVal,
                            conditions, columns, aliasMap);
                    if (!isDateField)
                        continue;
                    //字符串日期
                    String changedDate = null;
                    if (dateVal.endsWith("%")) {
                        dateVal = dateVal.substring(0, dateVal.length() - 1);
                    }
                    if (dateVal.length() <= 7)
                        changedDate = " to_date('" + dateVal + "','yyyy-MM')";
                    else if (dateVal.length() <= 10) //yyyy-mm-dd 长度小于等于10
                        changedDate = " to_date('" + dateVal + "','yyyy-MM-dd')";
                    else
                        changedDate = " to_date('" + dateVal + "','yyyy-MM-dd HH24:mi:ss')";
                    //日期写在前面，区分处理下
                    if (i == 0) {
                        changeStrMap.put(matchStr, " " + column + " " + compareVal + changedDate + " ");
                    } else {
                        changeStrMap.put(matchStr, " " + changedDate + " " + compareVal + " " + column + " ");
                    }

                }
            }
        }
        //替换字符串
        for (Map.Entry<String, String> entry : changeStrMap.entrySet()) {
            sql = sql.replace(entry.getKey(), entry.getValue());
        }
        return sql;
    }

    /**
     * 解析in多个日期
     *
     * @param sql
     * @param conditions
     * @param aliasMap
     * @author wangyi
     * @date 2017年8月16日
     */
    private String matchMultiDateCompare(String sql,
                                         List<TableStat.Condition> conditions, Collection<TableStat.Column> columns, Map<String, String> aliasMap) {
        Pattern dateOpePattern = Pattern
                .compile(
                        "\\s*([A-Za-z0-9_\\.]+)\\s*in\\s*\\((\\s*'((\\d{4}-\\d{1,2})|" +
                                "(\\d{4}-\\d{1,2}-\\d{1,2})|" +
                                "(\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}))'\\s*,?)+\\)",
                        Pattern.CASE_INSENSITIVE);
        Matcher matcher = dateOpePattern.matcher(sql);
        //记录源和变更后的字符串映射
        Map<String, String> changeStrMap = new LinkedHashMap<String, String>();
        while (matcher.find()) {
            matcher.start();
            matcher.end();
            String matchStr = matcher.group(0);
            String column = matcher.group(1);
            //解析in括号里面的数据
            String dateStr = matchStr.substring(matchStr.indexOf("(") + 1, matchStr.indexOf(")"));
            String[] dateStrs = dateStr.split(",");
            //建立日期数组
            List<String> dateList = new ArrayList<String>();
            //这里为了后续的比较，需要移除下单引号字符
            for (String tmpDate : dateStrs) {
                //移除单引号
                String innerDate = tmpDate.substring(1, tmpDate.length() - 1);
                dateList.add(innerDate);
            }
            //这里需要写个分支，适配上面的逻辑。等于1时直接拿元素进行比较
            boolean isDateField = false;
            if (dateList.size() == 1)
                isDateField = isDateField(column, "in", dateList.get(0),
                        conditions, columns, aliasMap);
            else
                isDateField = isDateField(column, "in", dateList.toString(),
                        conditions, columns, aliasMap);
            //不是日期，直接跳出，执行下个满足数据
            if (!isDateField)
                continue;
            //解析后日期字符串
            StringBuilder replaceStr = new StringBuilder();
            replaceStr.append(" ").append(column).append(" in (");
            for (String tmpDate : dateStrs) {
                //移除单引号
                String innerDate = tmpDate.substring(1, tmpDate.length() - 1);
                if (innerDate.trim().length() <= 7)
                    replaceStr.append("to_date('" + innerDate.trim() + "','yyyy-MM')");
                else if (innerDate.trim().length() <= 10) //yyyy-mm-dd 长度小于等于10
                    replaceStr.append("to_date('" + innerDate.trim() + "','yyyy-MM-dd')");
                else
                    replaceStr.append("to_date('" + innerDate.trim() + "','yyyy-MM-dd HH24:mi:ss')");
                replaceStr.append(",");
            }
            replaceStr.deleteCharAt(replaceStr.length() - 1).append(")");
            changeStrMap.put(matchStr, replaceStr.toString());
        }
        //替换字符串
        for (Map.Entry<String, String> entry : changeStrMap.entrySet()) {
            sql = sql.replace(entry.getKey(), entry.getValue());
        }
        return sql;
    }

    private boolean isDateField(String column, String compareVal, String dateVal,
                                List<TableStat.Condition> conditions, Collection<TableStat.Column> columns, Map<String, String> aliasMap) {
        //当解析数据出现错误时，默认为true。表示属于日期。这个没法解析
        if (conditions == null || columns == null || aliasMap == null || StringUtils.isEmpty(column) ||
                StringUtils.isEmpty(compareVal) || StringUtils.isEmpty(dateVal))
            return true;
        String tableName = null;
        //使用了别名
        if (column.contains(".")) {
            String alias = column.substring(0, column.indexOf("."));
            tableName = aliasMap.get(alias);
            //选择.后面的column值
            column = column.substring(column.indexOf(".") + 1);
        } else {
            //遍历条件数组，解析属于哪个表
            for (TableStat.Condition condition : conditions) {
                //列名称
                TableStat.Column conditionCol = condition.getColumn();
                if (!conditionCol.getName().equals(column))
                    continue;
				/*
				不比较比较符和值，直接取一个字段值相等的表
				//比较符
				String operator = condition.getOperator();
				//日期值
				String columnVal = null;
				List<Object> values = condition.getValues();
				if(values!=null && values.size()!=0){
					if(values.size()==1)
						columnVal = (String) values.get(0);
					else
						columnVal = values.toString();
				}
				if(operator.equalsIgnoreCase(compareVal)&&
						(StringUtils.isNotEmpty(columnVal)&&columnVal.equals(dateVal))){
					tableName = conditionCol.getTable();
					break;
				}*/
                tableName = conditionCol.getTable();
            }
            //解析columns拿到表名称
            if (StringUtils.isEmpty(tableName)) {
                for (TableStat.Column tmpColumn : columns) {
                    if (tmpColumn.getName().equals(column)) {
                        tableName = tmpColumn.getTable();
                        break;
                    }
                }
            }
        }
        //表名为空时，返回属于日期
        if (StringUtils.isEmpty(tableName))
            return false;
        //获取到表名后，判断表定义中对该列的定义是否为日期类型
        ITableDefService tableDefService = AppServiceHelper.findBean(ITableDefService.class, "tableDefService");
        TableDefVO tableDefVO = tableDefService.queryDetailedTableByTbname(tableName.toLowerCase());
        List<TableColumnVO> columnList = tableDefVO.getColumnList();
        for (TableColumnVO tableColumnVO : columnList) {
            if (tableColumnVO.getColcode().equalsIgnoreCase(column)) {
                if (tableColumnVO.getColtype().equals("DATE") ||
                        tableColumnVO.getColtype().equals("DATETIME")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 处理cast函数，只支持了整型值的比较
     *
     * @param sql
     * @return
     * @author wangyi
     * @date 2017年12月20日
     */
    public String convertCastFunction(String sql) {
        Pattern castFormatPattern = Pattern
                .compile(
                        "cast\\s*\\(\\s*([A-Za-z0-9_.]+)\\s+[A-Za-z0-9\\s(),]*\\s*\\)\\s*(asc|desc)?",
                        Pattern.CASE_INSENSITIVE);
        Matcher matcher = castFormatPattern.matcher(sql);
        //记录源和变更后的字符串映射
        Map<String, String> changeStrMap = new LinkedHashMap<String, String>();
        while (matcher.find()) {
            matcher.start();
            matcher.end();
            int groupCount = matcher.groupCount();
            //为2时表示正确解析，0表示全局字符串，1表示第一个参数，2表示第二个参数
            if (groupCount == 2) {
                String matchStr = matcher.group(0);
                String firstParam = matcher.group(1);
                String secondParam = matcher.group(2);
                String replaceStr = " to_number(" + firstParam + ")";
                if (!(StringUtils.isEmpty(secondParam) || secondParam.equals("null"))) {
                    replaceStr = replaceStr + " " + secondParam + " ";
                }
                changeStrMap.put(matchStr, replaceStr);
            }
        }
        //替换字符串
        for (Map.Entry<String, String> entry : changeStrMap.entrySet()) {
            sql = sql.replace(entry.getKey(), entry.getValue());
        }
        return sql;
    }

    /**
     * 处理TimestampDiff函数
     *
     * @param sql
     * @return
     * @author wangyi
     * @date 2018年1月9日
     */
    public String convertTimestampDiffFunction(String sql) {
        Pattern timestampDiffFormatPattern = Pattern
                .compile(
                        "ABS\\(TIMESTAMPDIFF\\(MINUTE,\\s*([A-Za-z0-9_.]+)\\s*,\\s*([A-Za-z0-9_.]+)\\s*\\)\\)",
                        Pattern.CASE_INSENSITIVE);
        Matcher matcher = timestampDiffFormatPattern.matcher(sql);
        //记录源和变更后的字符串映射
        Map<String, String> changeStrMap = new LinkedHashMap<String, String>();
        while (matcher.find()) {
            matcher.start();
            matcher.end();
            int groupCount = matcher.groupCount();
            //为2时表示正确解析，0表示全局字符串，1表示第一个参数，2表示第二个参数
            if (groupCount == 2) {
                String matchStr = matcher.group(0);
                String firstParam = matcher.group(1);
                String secondParam = matcher.group(2);
                String replaceStr = MessageFormat.format("ABS(CEIL(({0} - {1})*24*60))", firstParam, secondParam);
                changeStrMap.put(matchStr, replaceStr);
            }
        }
        //替换字符串
        for (Map.Entry<String, String> entry : changeStrMap.entrySet()) {
            sql = sql.replace(entry.getKey(), entry.getValue());
        }
        return sql;
    }

    private  String convertStrToDateFunction(String sql) {
        //STR_TO_DATE 函数替换 TO_DATE 函数
        Pattern strToDateConcatPattern = Pattern.compile("STR_TO_DATE\\(", Pattern.CASE_INSENSITIVE);
        return strToDateConcatPattern.matcher(sql).replaceAll("TO_DATE\\(");
    }

}
