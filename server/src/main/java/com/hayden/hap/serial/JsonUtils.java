package com.hayden.hap.serial;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangfeng
 * @date 2015年11月9日
 */
public class JsonUtils {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    /**
     * json反序列化属性异常消息前缀
     */
    public static final String PROPTIES_EXCETTION_PREFIX = "[...]";

    public static <T> List<T> parseArrayInit(String json, Class<T> clazz) throws HDException {
        VOObjectMapper mapper = new VOObjectMapper();
        JavaType javaType = mapper.getTypeFactory().constructParametrizedType(ArrayList.class, ArrayList.class, clazz);
        try {
            return mapper.readValue(json, javaType);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if (e.getMessage() != null && e.getMessage().startsWith(PROPTIES_EXCETTION_PREFIX)) {
                String msg = e.getMessage();
                int index = e.getMessage().indexOf(" (through reference chain:");
                if (index > -1) {
                    msg = e.getMessage().substring(0, index);
                }
                throw new HDException(msg);
            }
            logger.error(e.getMessage(), e);
            throw new HDException("解析json数据异常...");
        }
    }


    public static <T> List<T> parseArray(String json, Class<T> clazz) throws HDException {
        VOObjectMapper mapper = (VOObjectMapper) AppServiceHelper.findBean("voObjectMapper");
        JavaType javaType = mapper.getTypeFactory().constructParametrizedType(ArrayList.class, ArrayList.class, clazz);
        try {
            return mapper.readValue(json, javaType);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if (e.getMessage() != null && e.getMessage().startsWith(PROPTIES_EXCETTION_PREFIX)) {
                String msg = e.getMessage();
                int index = e.getMessage().indexOf(" (through reference chain:");
                if (index > -1) {
                    msg = e.getMessage().substring(0, index);
                }
                throw new HDException(msg);
            }
            logger.error(e.getMessage(), e);
            throw new HDException("解析json数据异常...");
        }
    }

    public static <T> T parse(String json, Class<T> clazz) throws HDException {
        VOObjectMapper mapper = (VOObjectMapper) AppServiceHelper.findBean("voObjectMapper");
        T t;
        try {
            t = mapper.readValue(json, clazz);
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().startsWith(PROPTIES_EXCETTION_PREFIX)) {
                throw new HDException(e.getMessage());
            }
            logger.error(e.getMessage(), e);
            throw new HDException("解析json数据异常...");
        }
        return t;
    }

    public static String writeValueAsString(Object obj) throws HDException {
        VOObjectMapper mapper = (VOObjectMapper) AppServiceHelper.findBean("voObjectMapper");
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
            throw new HDException("对象转json异常...");
        }
    }

}
