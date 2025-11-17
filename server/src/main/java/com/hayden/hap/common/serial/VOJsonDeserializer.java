package com.hayden.hap.common.serial;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.hayden.hap.dbop.entity.BaseVO;
import com.hayden.hap.dbop.reflect.ClassInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/** 
 * @ClassName: VOJsonDeserializer 
 * @Description: 
 * @author LUYANYING
 * @date 2015年6月24日 下午1:24:44 
 * @version V1.0   
 *   
 */
public class VOJsonDeserializer extends JsonDeserializer<BaseVO> {
	private static final Logger logger = LoggerFactory.getLogger(VOJsonDeserializer.class);

	@Override
	public BaseVO deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            jp.nextToken();
            BaseVO bean = ClassInfo.newInstance(BaseVO.class);
            for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
                String propName = jp.getCurrentName();
                // Skip field name:
                jp.nextToken();
                String propValue = jp.getValueAsString();
                bean.set(propName, propValue);
            }
            return bean;
        }
		return null;
	}

}
