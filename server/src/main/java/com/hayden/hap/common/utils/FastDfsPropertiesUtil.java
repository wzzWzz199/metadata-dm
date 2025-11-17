package com.hayden.hap.common.utils;

import com.hayden.hap.common.attach.itf.IAttachConstants;
import org.csource.fastdfs.TrackerGroup;

import java.net.InetSocketAddress;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2019/7/26 13:25
 */
public class FastDfsPropertiesUtil {

    //连接超时时间
    private static int g_connect_timeout; //millisecond
    //网络超时时间
    private static int g_network_timeout; //millisecond
    //字符集
    private static String g_charset;
    // nginux 代理访问地址
    private static String g_nginux_http_address;
    //tracker服务端口
    private static int g_tracker_http_port;
    //是否启用防盗链token
    private static boolean g_anti_steal_token;  //if anti-steal token
    //密钥
    private static String g_secret_key;   //generage token secret key
    //tracker服务器地址组
    private static String g_tracker_group;

    private static TrackerGroup tracker_group;

    public static TrackerGroup getTracker_group() {
        return tracker_group;
    }

    
    public static String getG_nginux_http_address() {
		return g_nginux_http_address;
	}


	public static void setG_nginux_http_address(String g_nginux_http_address) {
		FastDfsPropertiesUtil.g_nginux_http_address = g_nginux_http_address;
	}


	public static int getG_connect_timeout() {
        return g_connect_timeout;
    }

    public static void setG_connect_timeout(int g_connect_timeout) {
        FastDfsPropertiesUtil.g_connect_timeout = g_connect_timeout;
    }

    public static int getG_network_timeout() {
        return g_network_timeout;
    }

    public static void setG_network_timeout(int g_network_timeout) {
        FastDfsPropertiesUtil.g_network_timeout = g_network_timeout;
    }

    public static String getG_charset() {
        return g_charset;
    }

    public static void setG_charset(String g_charset) {
        FastDfsPropertiesUtil.g_charset = g_charset;
    }

    public static int getG_tracker_http_port() {
        return g_tracker_http_port;
    }

    public static void setG_tracker_http_port(int g_tracker_http_port) {
        FastDfsPropertiesUtil.g_tracker_http_port = g_tracker_http_port;
    }

    public static boolean isG_anti_steal_token() {
        return g_anti_steal_token;
    }

    public static void setG_anti_steal_token(boolean g_anti_steal_token) {
        FastDfsPropertiesUtil.g_anti_steal_token = g_anti_steal_token;
    }

    public static String getG_secret_key() {
        return g_secret_key;
    }

    public static void setG_secret_key(String g_secret_key) {
        FastDfsPropertiesUtil.g_secret_key = g_secret_key.equals("")?null:g_secret_key;
    }

    public static String getG_tracker_group() {
        return g_tracker_group;
    }

    public static void setG_tracker_group(String g_tracker_group) {
        FastDfsPropertiesUtil.g_tracker_group = g_tracker_group;
        String[] trackerArr=g_tracker_group.split(IAttachConstants.COMMA);
        String trackerServer;

        InetSocketAddress[] tracker_servers=new InetSocketAddress[trackerArr.length];
        for(int i=0;i<trackerArr.length;i++){
            trackerServer=trackerArr[i];
            tracker_servers[i]=new InetSocketAddress(trackerServer.split(IAttachConstants.COLON)[0],Integer.valueOf(trackerServer.split(IAttachConstants.COLON)[1]));
        }
        tracker_group=new TrackerGroup(tracker_servers);
    }
}
