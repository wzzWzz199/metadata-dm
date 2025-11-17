package com.hayden.hap.common.utils;

import com.hayden.hap.common.common.exception.HDException;

import java.io.*;

public class SerializeUtil {
	public static byte[] serialize(Object object) {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			// 序列化
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (Exception e) {

		}

		return null;

	}



	/**
	 * 反
	 * @param bytes
	 * @return 
	 * @author lianghua
	 * @date 2016年4月21日
	 */
	public static Object unserialize( byte[] bytes) {

		ByteArrayInputStream bais = null;

		try {
			// 反序列化
			bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();

		} catch (Exception e) {
		}
		return null;
	}

	//序列化
	public static String serializeToString(Object obj) throws HDException{
		try {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();  
			ObjectOutputStream objOut = new ObjectOutputStream(byteOut);  
			objOut.writeObject(obj);  
			String str = byteOut.toString("ISO-8859-1");//此处只能是ISO-8859-1,但是不会影响中文使用
			return str;
		}catch(IOException e) {
			throw new HDException(e);
		}		
	}

	//反序列化
	public static <T> T deserializeToObject(String str, Class<T> clazz) throws HDException{
		try {
			ByteArrayInputStream byteIn = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));  
			ObjectInputStream objIn = new ObjectInputStream(byteIn);  
			@SuppressWarnings("unchecked")
			T t = (T) objIn.readObject();  
			return t;  
		}catch(IOException | ClassNotFoundException e) {
			throw new HDException(e);
		} 
	}
}
