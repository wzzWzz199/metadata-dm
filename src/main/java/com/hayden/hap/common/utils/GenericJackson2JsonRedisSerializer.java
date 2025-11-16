package com.hayden.hap.common.utils;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.hayden.hap.common.serial.VOObjectMapper;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;

/**
 * @Description: redis，map结构的value使用jackson序列化
 * @author: wangyi
 * @date: 2018年8月17日
 */
public class GenericJackson2JsonRedisSerializer implements RedisSerializer<Object> {

    private final VOObjectMapper mapper;

    /**
     * Creates {@link GenericJackson2JsonRedisSerializer} and configures {@link ObjectMapper} for default typing.
     */
    public GenericJackson2JsonRedisSerializer() {
        this((String) null);
    }

    /**
     * Creates {@link GenericJackson2JsonRedisSerializer} and configures {@link ObjectMapper} for default typing using the
     * given {@literal name}. In case of an {@literal empty} or {@literal null} String the default
     * {@link JsonTypeInfo.Id#CLASS} will be used.
     * 
     * @param classPropertyTypeName Name of the JSON property holding type information. Can be {@literal null}.
     */
    public GenericJackson2JsonRedisSerializer(String classPropertyTypeName) {
        this(new VOObjectMapper());

        if (StringUtils.isNotEmpty(classPropertyTypeName)) {
            mapper.enableDefaultTypingAsProperty(DefaultTyping.NON_FINAL, classPropertyTypeName);
        } /*else {
            mapper.enableDefaultTyping(DefaultTyping.NON_FINAL, As.PROPERTY);
        }*/
    }

    /**
     * Setting a custom-configured {@link ObjectMapper} is one way to take further control of the JSON serialization
     * process. For example, an extended {@link SerializerFactory} can be configured that provides custom serializers for
     * specific types.
     * 
     * @param mapper must not be {@literal null}.
     */
    public GenericJackson2JsonRedisSerializer(ObjectMapper mapper) {
        Assert.notNull(mapper, "ObjectMapper must not be null!");
        this.mapper = (VOObjectMapper) mapper;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.serializer.RedisSerializer#serialize(java.lang.Object)
     */
    @Override
    public byte[] serialize(Object source) throws SerializationException {
        if (source == null) {
            return new byte[0];
        }
        try {
            return mapper.writeValueAsBytes(source);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Could not write JSON: " + e.getMessage(), e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.serializer.RedisSerializer#deserialize(byte[])
     */
    @Override
    public Object deserialize(byte[] source) throws SerializationException {
        return deserialize(source, Object.class);
    }

    /**
     * @param source can be {@literal null}.
     * @param type must not be {@literal null}.
     * @return {@literal null} for empty source.
     * @throws SerializationException
     */
    public <T> T deserialize(byte[] source, Class<T> type) throws SerializationException {
        Assert.notNull(type,
                "Deserialization type must not be null! Pleaes provide Object.class to make use of Jackson2 default typing.");
        if (source == null || source.length == 0) {
            return null;
        }
        try {
            return mapper.readValue(source, type);
        } catch (Exception ex) {
            throw new SerializationException("Could not read JSON: " + ex.getMessage(), ex);
        }
    }
}