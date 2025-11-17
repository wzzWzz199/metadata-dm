package com.hayden.hap.serial;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

public class VOObjectMapper extends ObjectMapper {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(VOObjectMapper.class);

	private static final long serialVersionUID = 1L;

	public VOObjectMapper() {
		super();
		// 设置null转换""
		// getSerializerProvider().setNullValueSerializer(new NullSerializer()); //暂时不用
		// 设置日期转换yyyy-MM-dd HH:mm:ss
		setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

		SimpleModule module = new SimpleModule("custom", Version.unknownVersion());
		module.addDeserializer(Date.class, new DateJsonDeSerializer());
		module.addDeserializer(Time.class, new TimeJsonDeSerializer());
		module.addDeserializer(String.class, new StringJsonDeSerializer());
		module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeSerializer());
		module.addDeserializer(LocalDate.class, new LocalDateDeSerializer());

		module.addSerializer(BigDecimal.class, new BigDecimalSerializer());
		module.addSerializer(String.class, new StringSerializer());
		module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
		module.addSerializer(LocalDate.class, new LocalDateSerializer());

		registerModule(module);
		super.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true)
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	// null的JSON序列
	private class NullSerializer extends JsonSerializer<Object> {
		@Override
		public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider)
				throws IOException, JsonProcessingException {
			jgen.writeString("");
		}
	}

	private class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

		private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		@Override
		public void serialize(LocalDateTime dateTime, JsonGenerator jgen, SerializerProvider provider)
				throws IOException, JsonProcessingException {

			jgen.writeString(dateTime.format(dateTimeFormatter));
		}
	}

	private class LocalDateSerializer extends JsonSerializer<LocalDate> {

		private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		@Override
		public void serialize(LocalDate date, JsonGenerator jgen, SerializerProvider provider)
				throws IOException, JsonProcessingException {

			jgen.writeString(date.format(dateFormatter));
		}
	}

	private class LocalDateTimeDeSerializer extends JsonDeserializer<LocalDateTime> {

		private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		@Override
		public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			String dateTimeString = p.getValueAsString();
			LocalDateTime dateTime = null;
			if (Objects.isNull(dateTimeString)) {
				return dateTime;
			}
			try {
				dateTime = LocalDateTime.parse(dateTimeString, dateTimeFormatter);
			} catch (Throwable e) {
				logger.error("反json序列化日期时间错误：" + dateTimeString);
			}
			return dateTime;
		}
	}

	private class LocalDateDeSerializer extends JsonDeserializer<LocalDate> {

		private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		@Override
		public LocalDate deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			String dateString = p.getValueAsString();
			LocalDate date = null;
			if (Objects.isNull(dateString)) {
				return date;
			}
			try {
				date = LocalDate.parse(dateString, dateFormatter);
			} catch (Throwable e) {
				logger.error("反json序列化日期错误：" + dateString);
			}
			return date;
		}
	}

	private class StringJsonDeSerializer extends JsonDeserializer<String> {

		@Override
		public String deserialize(JsonParser jp, DeserializationContext ctx)
				throws IOException, JsonProcessingException {
			String value = jp.getValueAsString();
			if ("".equals(value)) {
				return null;
			}
			return value;
		}

	}

	private class DateJsonDeSerializer extends JsonDeserializer<Date> {

		@Override
		public Date deserialize(JsonParser jp, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			String dateStr = jp.getValueAsString();
			if (dateStr != null && dateStr.length() == 10) {
				dateStr += " 00:00:00";
			}

			if (dateStr != null && dateStr.length() > 10) {
				Date date = null;
				try {
					date = DateUtils.parseDate(dateStr, "yyyy-MM-dd HH:mm:ss");
				} catch (ParseException e) {
					logger.error("反json序列化日期错误：" + dateStr);
				}
				return date;
			}

			return null;
		}
	}

	private class TimeJsonDeSerializer extends JsonDeserializer<Time> {

		@Override
		public Time deserialize(JsonParser jp, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			String dateStr = jp.getValueAsString();

			if (StringUtils.isNotEmpty(dateStr)) {
				Date date = null;
				try {
					date = DateUtils.parseDate(dateStr, "HH:mm:ss");
				} catch (ParseException e) {
					logger.error("反json序列化时间错误：" + dateStr);
				}
				return new Time(date.getTime());
			}

			return null;
		}

	}

	private class BigDecimalSerializer extends JsonSerializer<BigDecimal> {

		@Override
		public void serialize(BigDecimal bigDecimal, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
				throws IOException, JsonProcessingException {
//			jsonGenerator.writeString(bigDecimal != null ? bigDecimal.toPlainString() : "");
			jsonGenerator.writeNumber(bigDecimal!=null?bigDecimal.toPlainString():"");
		}

	}

	private class StringSerializer extends JsonSerializer<String> {

		@Override
		public void serialize(String value, JsonGenerator gen, SerializerProvider serializers)
				throws IOException, JsonProcessingException {
			if (StringUtils.isEmpty(value)) {
				gen.writeString(value);
				return;
			}
			
			gen.writeString(value);

		}

	}
}
