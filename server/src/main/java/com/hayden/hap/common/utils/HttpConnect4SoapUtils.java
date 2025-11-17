package com.hayden.hap.common.utils;

import org.apache.xmlbeans.impl.util.Base64;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Copyright: Copyright (c) 2019 北京海顿中科技术有限公司
 * 
 * @ClassName: HttpConnect4SoapUtils.java
 * @Description: webservice调用工具类，支持soap1.1和soap1.2。注意：需要类路劲下有soap请求体的模板文件。<br>
 * 
 *     如：mobileCodeWS.template
 * <soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:web="http://WebXml.com.cn/">
 *   <soap:Header/>
 *   <soap:Body>
 *     <web:getMobileCodeInfo>
 *         <!--Optional:-->
 *        <web:mobileCode>{mobileCode}</web:mobileCode>
 *         <!--Optional:-->
 *         <web:userID>{userID}</web:userID>
 *      </web:getMobileCodeInfo>
 *   </soap:Body>
 * </soap:Envelope>
 * 
 * @version: v3
 * @author: 王振军
 * @date: 2019年7月23日 上午10:05:39
 *
 */
public class HttpConnect4SoapUtils {

	/**
	 * 基于SOAP1.1协议的调用
	 * 
	 * @param address  接口地址
	 * @param template 参数模板
	 * @param params   参数,可以为空
	 * @return 调用结果
	 * @throws Exception 调用失败抛出此异常
	 */
	public static String submitBySoap11(String address, String template, Map<String, String> params) throws Exception {
		return submit(address, template, params, null, null, SOAPConstants.SOAP_1_1_PROTOCOL);
	}

	/**
	 * 基于SOAP1.2协议的调用
	 * 
	 * @param address  接口地址
	 * @param template 参数模板
	 * @param params   参数,可以为空
	 * @return 调用结果
	 * @throws Exception 调用失败抛出此异常
	 */
	public static String submitBySoap12(String address, String template, Map<String, String> params) throws Exception {
		return submit(address, template, params, null, null, SOAPConstants.SOAP_1_2_PROTOCOL);
	}

	/**
	 * 基于SOAP1.1协议的调用
	 * 
	 * @param address  接口地址
	 * @param template 参数模板
	 * @param params   参数,可以为空
	 * @param username 用户名
	 * @param password 密码
	 * @return 调用结果
	 * @throws Exception 调用失败抛出此异常
	 */
	public static String submitBySoap11(String address, String template, Map<String, String> params, String username,
			String password) throws Exception {
		return submit(address, template, params, username, password, SOAPConstants.SOAP_1_1_PROTOCOL);
	}

	/**
	 * 基于SOAP1.2协议的调用
	 * 
	 * @param address  接口地址
	 * @param template 参数模板
	 * @param params   参数,可以为空
	 * @param username 用户名
	 * @param password 密码
	 * @return 调用结果
	 * @throws Exception 调用失败抛出此异常
	 */
	public static String submitBySoap12(String address, String template, Map<String, String> params, String username,
			String password) throws Exception {
		return submit(address, template, params, username, password, SOAPConstants.SOAP_1_2_PROTOCOL);
	}

	private static String submit(String address, String template, Map<String, String> params, String username,
			String password, String protocol) throws Exception {

		HttpURLConnection conn = null;
		String result = null;
		try {
			// 载入请求参数模板
			String requestBody = read(ClassLoader.getSystemResource(template).openStream());
			// 替换参数
			if (params != null) {
				for (Map.Entry<String, String> entry : params.entrySet()) {
					requestBody = requestBody.replaceAll("\\{" + entry.getKey() + "\\}", entry.getValue());
				}
			}

			URL url = new URL(address);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST"); // 请求方式
			conn.setDoOutput(true); // 向服务器发送数据
			conn.setDoInput(true); // 获取服务端的响应
			conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
			conn.setReadTimeout(5000); // 读取超时
			conn.setConnectTimeout(5000); // 服务器响应超时
			conn.setUseCaches(false); // 不使用缓存

			if (username != null && password != null) {
				String author = "Basic " + Base64.encode((username + ":" + password).getBytes());
				conn.setRequestProperty("Authorization", author);
			}

			conn.getOutputStream().write(requestBody.getBytes("UTF-8"));
			if (conn.getResponseCode() == 200) {
				result = parseXml(conn.getInputStream(), protocol);
			} else {
				throw new RuntimeException(conn.getResponseCode() + ":" + conn.getResponseMessage());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return result;
	}

	private static String read(InputStream in) {
		StringBuilder builder = new StringBuilder();
		String str = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			while ((str = reader.readLine()) != null) {
				builder.append(str);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return builder.toString();
	}

	private static String parseXml(InputStream in, String protocol) {
		String content = null;
		try {
			content = MessageFactory.newInstance(protocol).createMessage(new MimeHeaders(), in).getSOAPBody()
					.getTextContent();
		} catch (SOAPException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return content;
	}

//	mobileCodeWS.template
//	<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:web="http://WebXml.com.cn/">
//	   <soap:Header/>
//	   <soap:Body>
//	      <web:getMobileCodeInfo>
//	         <!--Optional:-->
//	         <web:mobileCode>{mobileCode}</web:mobileCode>
//	         <!--Optional:-->
//	         <web:userID>{userID}</web:userID>
//	      </web:getMobileCodeInfo>
//	   </soap:Body>
//	</soap:Envelope>
	public static void main(String[] args) {

		String template = "mobileCodeWS.template";
		String address = "http://ws.webxml.com.cn/WebServices/MobileCodeWS.asmx";
		Map<String, String> params = new HashMap<>();
		params.put("mobileCode", "1342641");
		params.put("userID", "");

		try {
			String result = submitBySoap12(address, template, params);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
