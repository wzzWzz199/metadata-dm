package com.hayden.hap.dbop.entity;

import com.hayden.hap.dbop.db.util.ObjectUtil;
import com.hayden.hap.dbop.reflect.ClassInfo;
import com.hayden.hap.dbop.reflect.Invoker;
import org.apache.commons.beanutils.ConvertUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/** 
 * @ClassName: AbstractVO 
 * @Description: 值对象，所有VO类必须定义默认的构造函数，并且必须在默认构造函数设置对应的表名
 * @author LUYANYING
 * @date 2015年3月20日 下午2:29:42 
 * @version V1.0   
 *  
 */

public abstract class AbstractVO implements Serializable {
	private String tableName = null;
	
	private Map<String, Object> columnValues = null;
	
	public AbstractVO(String tableName){
		setTableName(tableName.toLowerCase());
	}
	public AbstractVO(){
		
	}
	
	/**
	 * 
	 * @Title: setFloat 
	 * @Description: 设置属性float值
	 * @param name 属性名称 
	 * @param value float值
	 * @return void
	 * @throws
	 */
	public void setFloat(String name, float value){
		set(name, value);
	}
	/**
	 * 
	 * @Title: setLong 
	 * @Description: 设置属性long值
	 * @param name 属性名称 
	 * @param value long值
	 * @return void
	 * @throws
	 */
	public void setLong(String name, long value){
		set(name, value);
	}
	/**
	 * 
	 * @Title: setInt 
	 * @Description: 设置属性int值
	 * @param name 属性名称 
	 * @param value int值
	 * @return void
	 * @throws
	 */
	public void setInt(String name, int value){
		set(name, value);
	}
	/**
	 * 
	 * @Title: setString 
	 * @Description: 设置属性String值
	 * @param name 属性名称 
	 * @param value String值
	 * @return void
	 * @throws
	 */
	public void setString(String name, String value){
		set(name, value);
	}
    
    /**
     * 直接得到字符串类型的返回值
     * @param name   属性名称
     * @return      字符串类型的返回值
     */
    public String getString(String name) {
        return getString(name, null);
    }
    
    /**
     * 直接得到字符串类型的返回值
     * @param name   属性名称
     * @param defaultValue  如果取不到就返回缺省值
     * @return      字符串类型的返回值
     */
    public String getString(String name, String defaultValue) {
        Object value = get(name);
        if (value == null) {
        	if(!ObjectUtil.isNotNull(defaultValue))
        		return null;
            return defaultValue;
        } else {
            return value.toString();
        }
    }
    
    /**
     * 当值为null时，返回空字符串
     * @param name
     * @return
     */
    public String getStringDefaultEmpty(String name) {
        Object value = get(name);
        if (value == null) {
            return "";
        } else {
            return value.toString();
        }
    }
    
    /**
     * 得到整型返回值
     * @param name   属性名称
     * @return      整型返回值
     */
    public Integer getInt(String name) {
        return getInt(name, null);
    }
    
    /**
     * 得到整型返回值
     * @param name       属性名称
     * @param defaultValue  如果为空则返回缺省值
     * @return          整型返回值
     */
    public Integer getInt(String name, Integer defaultValue) {
        Object value = get(name);
        if (!ObjectUtil.isNotNull(value)) {
            return defaultValue;
        } else {
            return Integer.parseInt((String.valueOf(value)));
        }
    }
    
    /**
     * getBigDecimal:(获取数据用于计算). <br/>
     * date: 2016年1月21日 <br/>
     *
     * @author ZhangJie
     * @param name
     * @param defaultValue
     * @return
     * 
     */
    public BigDecimal getBigDecimal(String name,BigDecimal defaultValue)
    {
        Object value = get(name);
        if (!ObjectUtil.isNotNull(value)) {
            return defaultValue;
        }else if(value instanceof BigDecimal) {
        	return (BigDecimal)value;
        }else {
        	return new BigDecimal(value.toString());
        }
    }
    
    public void setBigDecimal(String name,BigDecimal value)
    {
    	set(name, value);
    }

    /**
     * 得到长整型返回值
     * @param name   属性名称
     * @return      整型返回值
     */
    public Long getLong(String name) {
        return getLong(name , null);
    }
    
    /**
     * 得到长整型返回值
     * @param name       属性名称
     * @param defaultValue  如果为空则返回缺省值
     * @return          整型返回值
     */
    public Long getLong(String name, Long defaultValue) {
        Object value = get(name);
        if (!ObjectUtil.isNotNull(value)) {
            return defaultValue;
        } else {
            return Long.parseLong(String.valueOf(value));
        }
    }
    
    /**
     * 得到double型返回值
     * @param name   属性名称
     * @return      整型返回值
     */
    public Double getDouble(String name) {
        return getDouble(name, null);
    }
    
    /**
     * 得到double型返回值
     * @param name       属性名称
     * @param defaultValue  如果为空则返回缺省值
     * @return          整型返回值
     */
    public Double getDouble(String name, Double defaultValue) {
        Object value = get(name);
        if (!ObjectUtil.isNotNull(value)) {
            return defaultValue;
        } else {
            return Double.parseDouble(String.valueOf(value));
        }
    }

    /**
     * 得到float型返回值
     * @param name   属性名称
     * @return      float返回值
     */
    public Float getFloat(String name) {
        return getFloat(name, null);
    }
    
    /**
     * float
     * @param name       属性名称
     * @param defaultValue  如果为空则返回缺省值
     * @return          float返回值
     */
    public Float getFloat(String name, Float defaultValue) {
        Object value = get(name);
        if (!ObjectUtil.isNotNull(value)) {
            return defaultValue;
        } else {
            return Float.parseFloat(String.valueOf(value));
        }
    }
    /**
     * 
     * @Title: clear 
     * @Description: 清除数据
     * @return void
     * @throws
     */
	public void clear(){
		if(columnValues != null)
			columnValues.clear();
	}
	/**
	 * 
	 * @Title: set 
	 * @Description: 设置属性值
	 * @param name 属性名称
	 * @param value 值
	 * @return void
	 * @throws
	 */
	public void set(String name, Object value){
		if(name == null)
			return ;
		try {
			ClassInfo classInfo = ClassInfo.forClass(this.getClass());
			String propertyName = classInfo.findPropertyName(name.toLowerCase());
			if (classInfo.hasSetter(propertyName)) {
				Invoker invoker = classInfo.getSetInvoker(propertyName);
				Object convertValue = null;
				if(ObjectUtil.isNotNull(value))
					convertValue = ConvertUtils.convert(value, invoker.getType());
				invoker.invoke(this, new Object[] { convertValue });
			}else{
				if(columnValues == null)
					columnValues = new HashMap<String, Object>(){
						public Object put(String key, Object value) {
							if(!ObjectUtil.isNotNull(value))
								value = null;
							return super.put(key, value);
						}
					};
				columnValues.put(name.toLowerCase(), value);
			}
		} catch (Exception e) {
			if(columnValues == null)
				columnValues = new HashMap<String, Object>(){
					public Object put(String key, Object value) {
						if(!ObjectUtil.isNotNull(value))
							value = null;
						return super.put(key, value);
					}
				};
			columnValues.put(name.toLowerCase(), value);
		}
	}
	/**
	 * 
	 * @Title: get 
	 * @Description: 得到属性对应的值
	 * @param name 属性名称
	 * @return Object 对应的值
	 * @throws 
	 */
	public Object get(String name){
		if(name == null)
			return null;
		try {
			ClassInfo classInfo = ClassInfo.forClass(this.getClass());
			String propertyName = classInfo.findPropertyName(name.toLowerCase());
			if (classInfo.hasGetter(propertyName)) {
				Object value = classInfo.getGetInvoker(propertyName).invoke(this, null);
				if(!ObjectUtil.isNotNull(value))
					return null;
				return value;
			}else{
				if(columnValues != null)
					return columnValues.get(name.toLowerCase());
			}
		} catch (Exception e) {
			if(columnValues != null)
				return columnValues.get(name.toLowerCase());
		}
		return null;
	}
	
	/**
	 * 指定从map中获取结构
	 * @param name
	 * @return 
	 * @author zhangfeng
	 * @date 2018年2月2日
	 */
	public Object getFromColumnValues(String name) {
		return columnValues.get(name.toLowerCase());
	}
	
	/**
	 * 
	 * @Title: get 
	 * @Description: 得到属性对应的值
	 * @param name 属性名称
	 * @param defaultValue 缺省值，如果没有就返回缺省值
	 * @return Object
	 * @throws
	 */
    public Object get(String name, Object defaultValue) {
        Object value = get(name);
        if (value == null) {
        	if(!ObjectUtil.isNotNull(defaultValue))
        		return null;
            return defaultValue;
        } else {
            return value;
        }
    }
	
    /**
     * 指定从map里获取值
     * @param name
     * @return 
     * @author zhangfeng
     * @date 2015年11月10日
     */
    public Object getFromMap(String name) {
    	if(columnValues != null)
			return columnValues.get(name.toLowerCase());
    	return null;
	}
	
    /**
     * 是否拥有某属性
     * @param name
     * @return 
     * @author zhangfeng
     * @date 2017年7月17日
     */
    public boolean hasProperty(String name) {
    	ClassInfo classInfo = ClassInfo.forClass(this.getClass());
		String propertyName = classInfo.findPropertyName(name.toLowerCase());
		if (classInfo.hasGetter(propertyName)) {
			return true;
		}else{
			if(columnValues != null)
				return columnValues.containsKey(name);
			return false;
		}
    }
    
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public Map<String, Object> getColumnValues() {
		return columnValues;
	}
	public void setColumnValues(Map<String, Object> columnValues) {
		this.columnValues = columnValues;
	}
	
}
