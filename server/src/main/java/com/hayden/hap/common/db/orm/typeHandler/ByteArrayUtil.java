package com.hayden.hap.common.db.orm.typeHandler;

/**
 * 
 * @ClassName: ByteArrayUtil
 * @Description: byte数组工具类
 * @author LUYANYING
 * @date 2015年4月17日 下午4:53:24
 * @version V1.0
 * 
 */
public class ByteArrayUtil {
	public static byte[] convertToPrimitiveArray(Byte[] objects) {
		final byte[] bytes = new byte[objects.length];
		for (int i = 0; i < objects.length; i++) {
			Byte b = objects[i];
			bytes[i] = b;
		}
		return bytes;
	}

	public static Byte[] convertToObjectArray(byte[] bytes) {
		final Byte[] objects = new Byte[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			objects[i] = b;
		}
		return objects;
	}
}
