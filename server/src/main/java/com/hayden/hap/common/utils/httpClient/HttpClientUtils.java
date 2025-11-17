/**
 * Project Name:hap-sy
 * File Name:HttpClientUtils.java
 * Package Name:com.hayden.hap.sy.utils.httpClient
 * Date:2016年2月15日
 * Copyright (c) 2016, zhangjie@ushayden.com All Rights Reserved.
 *
 */

package com.hayden.hap.common.utils.httpClient;

import com.hayden.hap.dbop.exception.HDException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * ClassName:HttpClientUtils ().<br/>
 * Date: 2016年2月15日 <br/>
 * 
 * @author ZhangJie
 * @version
 * @see
 */
public class HttpClientUtils {
	private static Log log = LogFactory.getLog(HttpClientUtils.class);

	/**
	 * doGet:(访问get服务). <br/>
	 * date: 2016年2月15日 <br/>
	 *
	 * @author ZhangJie
	 * @param url
	 * @param queryString
	 *            parameter参数，可以为json格式
	 * @return
	 * @throws HDException 
	 */
	public static String doGet(String url, String queryString) throws HDException {
		return doGet(url, queryString, "UTF-8", true);
	}

	/**
	 * 执行一个HTTP GET请求，返回请求响应的HTML
	 *
	 * @param url
	 *            请求的URL地址
	 * @param queryString
	 *            请求的查询参数,可以为null
	 * @param charset
	 *            字符集
	 * @param pretty
	 *            是否美化
	 * @return 返回请求响应的HTML
	 * @throws HDException 
	 */

	public static String doGet(String url, String queryString, String charset,
			boolean pretty) throws HDException {
		StringBuffer response = new StringBuffer();
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(url);
		Header header = new Header();
		header.setName("accept");
		header.setValue("application/json");
		method.setRequestHeader(header);
		try {
			if (queryString != null)
				// 对get请求参数做了http请求默认编码，好像没有任何问题，汉字编码后，就成为%式样的字符串
				method.setQueryString(URIUtil.encodeQuery(queryString));
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(method.getResponseBodyAsStream(),
								charset));
				String line;
				while ((line = reader.readLine()) != null) {
					if (pretty)
						response.append(line).append(
								System.getProperty("line.separator"));
					else
						response.append(line);
				}
				reader.close();
			}
		} catch (URIException e) {
			log.error("执行HTTP Get请求时，编码查询字符串“" + queryString + "”发生异常！", e);
			throw new HDException(e);
		} catch (IOException e) {
			log.error("执行HTTP Get请求" + url + "时，发生异常！", e);
			throw new HDException(e);
		} finally {
			method.releaseConnection();
		}
		return response.toString();
	}

	/**
	 * doPost:(提交到服务器). <br/>
	 * date: 2016年2月15日 <br/>
	 *
	 * @author ZhangJie
	 * @param url
	 * @param params 参数
	 * @param json 提交的数据
	 * @return
	 * @throws HDException 
	 */
	public static String doPost(String url, Map<String, String> params,
			String json) throws HDException {
		return doPost(url, params, json, "UTF-8", true);
	}

	/**
	 * 执行一个HTTP POST请求，返回请求响应的HTML
	 *
	 * @param url
	 *            请求的URL地址
	 * @param params
	 *            请求的查询参数,可以为null
	 * @param json
	 *            回传的字符串
	 * @param charset
	 *            字符集
	 * @param pretty
	 *            是否美化
	 * @return 返回请求响应的HTML
	 * @throws HDException 
	 */
	public static String doPost(String url, Map<String, String> params,
			String json, String charset, boolean pretty) throws HDException {
		StringBuffer response = new StringBuffer();
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(url);
		Header header = new Header();
		header.setName("accept");
		header.setValue("application/json");
		method.setRequestHeader(header);
		// 设置Http Post数据
		if (params != null) {
			HttpMethodParams p = new HttpMethodParams();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				p.setParameter(entry.getKey(), entry.getValue());
			}
			method.setParams(p);
		}
		try {
			RequestEntity requestEntity = new StringRequestEntity(json);
			method.setRequestEntity(requestEntity);
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(method.getResponseBodyAsStream(),
								charset));
				String line;
				while ((line = reader.readLine()) != null) {
					if (pretty)
						response.append(line).append(
								System.getProperty("line.separator"));
					else
						response.append(line);
				}
				reader.close();
			}
		} catch (IOException e) {
			log.error("执行HTTP Post请求" + url + "时，发生异常！", e);
			throw new HDException(e);
		} finally {
			method.releaseConnection();
		}
		return response.toString();
	}

	public static String doPostJson(String url, Map<String, String> params,
								String json, String charset, boolean pretty) throws HDException {
		StringBuffer response = new StringBuffer();
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(url);
		Header header = new Header();
		header.setName("accept");
		header.setValue("application/json");
		header.setName("Content-Type");
		header.setValue("application/json");
		method.setRequestHeader(header);
		// 设置Http Post数据
		if (params != null) {
			HttpMethodParams p = new HttpMethodParams();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				p.setParameter(entry.getKey(), entry.getValue());
			}
			method.setParams(p);
		}
		try {
			RequestEntity requestEntity = new StringRequestEntity(json);
			method.setRequestEntity(requestEntity);
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(method.getResponseBodyAsStream(),
								charset));
				String line;
				while ((line = reader.readLine()) != null) {
					if (pretty)
						response.append(line).append(
								System.getProperty("line.separator"));
					else
						response.append(line);
				}
				reader.close();
			}
		} catch (IOException e) {
			log.error("执行HTTP Post请求" + url + "时，发生异常！", e);
			throw new HDException(e);
		} finally {
			method.releaseConnection();
		}
		return response.toString();
	}

	public static void main(String[] args) throws HDException {
		String contextPath = "http://192.168.6.154:8080/hap-eam-app/m/sy";
		String url = contextPath + "/EAM_FAULT_M/listQuery.json?act=listQuery";
		String getStr = doGet(url, "act1={\"a\":\"a1\",\"b\":\"b1\"}", "UTF-8",
				true);
		System.out.println(getStr);
		//
		/*
		 * url = contextPath+"/EAM_FAULT_M/cardSave.json?act=cardSave";
		 * Map<String,String> params = new HashMap<String,String>();
		 * params.put("abc", "123"); String postStr = doPost(url,
		 * params,"我是JSON字符串", "UTF-8", false); System.out.println(postStr);
		 */
	}

}
