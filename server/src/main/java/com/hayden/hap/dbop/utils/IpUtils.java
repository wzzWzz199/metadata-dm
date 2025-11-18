package com.hayden.hap.dbop.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 获取服务器及本机IP信息
 * 
 * @author liyan
 * @date 2016年8月18日
 */
public class IpUtils {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(IpUtils.class);
	/**
	 * 获取本机IP地址
	 * @return 本机IP
	 * @author liyan
	 * @date 2016年8月11日
	 */
	public static String getIP() {
		Enumeration<NetworkInterface> allNetInterfaces;
		InetAddress ip = null;
		InetAddress myip = null;
		try {
			allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			while (allNetInterfaces.hasMoreElements())
			{
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				//System.out.println(netInterface.getName());
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements())
				{
					ip = (InetAddress) addresses.nextElement();
					if (ip != null && ip instanceof Inet4Address && !ip.isLoopbackAddress() )
					{
					//	System.out.println("本机的IP = " + ip.getHostAddress());
						myip = ip;
						break;
					} 
				}
				if(myip!=null){
					break;
				}
			}
		} catch (SocketException e) {
			logger.error("socket异常"+e);
		}
		if(null!=myip){
			if(null!=myip.getHostAddress()){
				return myip.getHostAddress();		
			}
			
		}
		return null;
	}
	
	/**
	 * 获取当前服务器信息，路径D:\\apache-tomcat-7.0.50
	 * @return 
	 * @author liyan
	 * @date 2016年8月11日
	 */
	public static String getCatalinaHome() {
		return System.getProperty("catalina.home");
	}
}
