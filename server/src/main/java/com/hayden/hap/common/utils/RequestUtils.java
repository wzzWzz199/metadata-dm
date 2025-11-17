
package com.hayden.hap.common.utils;

import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.common.exception.HDRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;



/**
 * <p>
 * 处理对于Request的一些得到客户提交参数和设置数据的方法
 * </p>
 *
 * @author Jerry Li
 */
public class RequestUtils {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(RequestUtils.class);
	
	

    /**
     * 得到request中对应parameter的参数的值，如果没有，返回null
     *
     * @param request http request
     * @param name param 参数名
     *
     * @return request中对应parameter的值
     */
	public static String getStringParameter(HttpServletRequest request,
			String name) {
		if (name == null) {
			return null;
		} else {

			return request.getParameter(name.trim());
		}
	}
	
    /**
     * 得到request中对应parameter的参数的值，如果没有，返回null
     * @param request http request
     * @param name param 参数名
     * @return request中对应parameter的值
     */
	public static String[] getStringParameters(HttpServletRequest request,
			String name) {
		if (name == null) {
			return null;
		} else {
			return request.getParameterValues(name.trim());
		}
	}

    /**
     * 得到request中对应parameter的参数的值，如果没有，返回指定的缺省值
     *
     * @param request http request
     * @param name param 参数名
     * @param def 指定的缺省值
     *
     * @return request中对应parameter的值
     */
    public static String getStringParameter(HttpServletRequest request, String name, String def) {
        String value = getStringParameter(request, name);
        if (value == null) {
            return def;
        } else {
            return value.trim();
        }
    }

    /**
     * 得到request中对应parameter的参数的值，如果没有，抛错
     *
     * @param request http request
     * @param name param 参数名
     *
     * @return request中对应parameter的值
     */
    public static int getIntegerParameter(HttpServletRequest request, String name) {
        return Integer.parseInt(request.getParameter(name));
    }

    /**
     * 得到request中对应parameter的参数的值，如果没有或出错，返回指定的缺省值
     *
     * @param request http request
     * @param name param 参数名
     * @param def 指定的缺省值
     *
     * @return request中对应parameter的值
     */
    public static int getIntegerParameter(HttpServletRequest request, String name, int def) {
        try {
            return getIntegerParameter(request, name);
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 对应字符串的简短显示格式，方便页面显示
     * @param strview 要显示的字符串
     * @param len 短显示格式字符串的长度
     *
     * @return 指定字符串的短显示格式
     */
    public static String shortView(String strview, int len) {
        int strlen = strview.length();
        String strshortview = "";

        if (strlen > len) {
            strshortview = strview.substring(0, len) + "...";
        } else {
            strshortview = strview;
        }

        return strshortview;
    }

    /**
     * 得到request中对应parameter的参数的值，如果没有，抛错
     *
     * @param request http request
     * @param name param 参数名
     *
     * @return request中对应parameter的值
     */
    public static long getLongParameter(HttpServletRequest request, String name) {
        return Long.parseLong(request.getParameter(name));
    }

    /**
     * 得到request中对应parameter的参数的值，如果没有或出错，返回指定的缺省值
     *
     * @param request http request
     * @param name param 参数名
     * @param def 指定的缺省值
     *
     * @return request中对应parameter的值
     */
    public static long getLongParameter(HttpServletRequest request, String name, long def) {
        try {
            return getLongParameter(request, name);
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 得到request中对应parameter的参数的值，如果没有，抛错
     *
     * @param request http request
     * @param name param 参数名
     *
     * @return request中对应parameter的值
     */
    public static float getFloatParameter(HttpServletRequest request, String name) {
        return Float.parseFloat(request.getParameter(name));
    }

    /**
     * 得到request中对应parameter的参数的值，如果没有或出错，返回指定的缺省值
     *
     * @param request http request
     * @param name param 参数名
     * @param def 指定的缺省值
     *
     * @return request中对应parameter的值
     */
    public static float getFloatParameter(HttpServletRequest request, String name, float def) {
        try {
            return getFloatParameter(request, name);
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 得到request中对应parameter的参数的值，如果没有，抛错
     *
     * @param request http request
     * @param name param 参数名
     *
     * @return request中对应parameter的值
     */
    public static double getDoubleParameter(HttpServletRequest request, String name) {
        return Double.parseDouble(request.getParameter(name));
    }

    /**
     * 得到request中对应parameter的参数的值，如果没有或出错，返回指定的缺省值
     *
     * @param request http request
     * @param name param 参数名
     * @param def 指定的缺省值
     *
     * @return request中对应parameter的值
     */
    public static double getDoubleParameter(HttpServletRequest request, String name, double def) {
        try {
            return getDoubleParameter(request, name);
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 得到request中对应Attribute的参数的值，如果没有，返回null
     *
     * @param request http request
     * @param name param 参数名
     *
     * @return request中对应Attribute的值
     */
    public static String getStringAttribute(HttpServletRequest request, String name) {
        if (name == null) {
            return null;
        } else {
            return (String) request.getAttribute(name.trim());
        }
    }

    /**
     * 得到request中对应Attribute的参数的值，如果没有，返回指定的缺省值
     *
     * @param request http request
     * @param name param 参数名
     * @param def 指定的缺省值
     *
     * @return request中对应Attribute的值
     */
    public static String getStringAttribute(HttpServletRequest request, String name, String def) {
        String value = getStringAttribute(request, name);

        if (value == null) {
            return def;
        } else {
            return value;
        }
    }

    /**
     * 得到request中对应Attribute的参数的值，如果没有，返回null
     *
     * @param request http request
     * @param name param 参数名
     *
     * @return request中对应Attribute的值
     */
    public static Object getAttribute(HttpServletRequest request, String name) {
        if (name == null) {
            return null;
        } else {
            return request.getAttribute(name.trim());
        }
    }

    /**
     * 得到request中对应Attribute的参数的值，如果没有，返回指定的缺省值
     *
     * @param request http request
     * @param name param 参数名
     * @param def 指定的缺省值
     *
     * @return request中对应Attribute的值
     */
    public static Object getAttribute(HttpServletRequest request, String name, Object def) {
        Object value = getAttribute(request, name);

        if (value == null) {
            return def;
        } else {
            return value;
        }
    }

    /**
     * 得到Session中的指定对象。
     *
     * @param req   reqeust对象
     * @param id    Session中对象名称
     *
     * @return Session中存放的对象
     */
    public static Object getSessionValue(HttpServletRequest req, String id) {
        HttpSession session = req.getSession(true);
        return session.getAttribute(id);
    }

    /**
     * 得到Session中的指定对象,如果为null，则返回缺省对象。
     *
     * @param req   reqeust对象
     * @param id    Session中对象名称
     * @param defaultObj    缺省对象
     *
     * @return Session中存放的对象
     */
    public static Object getSessionValue(HttpServletRequest req, String id, Object defaultObj) {
        if (getSessionValue(req, id) != null) {
            defaultObj = getSessionValue(req, id);
        }

        return defaultObj;
    }

    /**
     * 向session中存放对象。
     *
     * @param req   request对象
     * @param id    对象对应键值
     * @param value 要放到Session中的对象
     */
    public static void setSessionValue(HttpServletRequest req, String id, Object value) {
        HttpSession session = req.getSession(true);
        session.setAttribute(id, value);
    }

    /**
     * 移除Session中指定的对象。
     *
     * @param req   request对象
     * @param id    对象键值
     */
    public static void removeSessionValue(HttpServletRequest req, String id) {
        HttpSession session = req.getSession(true);
        Object value = session.getAttribute(id);

        if (value != null) {
            session.removeAttribute(id);
        }
    }

    /**
     * 得到request中对应parameter的参数的值,这个值是经过解码的值
     *
     * @param request http request
     * @param name param 参数名
     *
     * @return request中对应parameter的解码的值
     */
    public static String getUnEscapeParameter(HttpServletRequest request, String name) {
        String value = getStringParameter(request, name);

        if (value == null) {
            return "";
        } else {
            return unEscapeStr(value);
        }
    }

    /**
     * 对应解开客户端进行简单加密的字符串，进一步提高系统的安全性
     * 原理：对应客户端加密的字符串进行拆解，转为Unicode对应的数字，对每一个数字进行恢复的反向调整。
     * @param src   原加密字符串
     * @return String   解密后的字符串
     */
    public static String unEscapeStr(String src) {
        String ret = "";

        if (src == null) {
            return ret;
        }

        for (int i = src.length() - 1; i >= 0; i--) {
            int iCh = src.substring(i, i + 1).hashCode();

            if (iCh == 15) {
                iCh = 10;
            } else if (iCh == 16) {
                iCh = 13;
            } else if (iCh == 17) {
                iCh = 32;
            } else if (iCh == 18) {
                iCh = 9;
            } else {
                iCh = iCh - 5;
            }

            ret += (char) iCh;
        }

        //        logger.debug("unEscape: input=" + src + "   output=" + ret);
        return ret;
    }

    /**
     * 加密字符串，进一步提高系统的安全性
     * @param src   未加密字符串
     * @return String   加密后的字符串
     */
    public static String escapeStr(String src) {
        String ret = "";

        if (src == null) {
            return ret;
        }

        for (int i = src.length() - 1; i >= 0; i--) {
            int iCh = src.substring(i, i + 1).hashCode();

            if (iCh == 10) {
                iCh = 15;
            } else if (iCh == 13) {
                iCh = 16;
            } else if (iCh == 32) {
                iCh = 17;
            } else if (iCh == 9) {
                iCh = 18;
            } else {
                iCh = iCh + 5;
            }

            ret += (char) iCh;
        }

        //        logger.debug("unEscape: input=" + src + "   output=" + ret);
        return ret;
    }

    /**
     * 将request的头信息和参数信息输出到log中
     * @param request   http Request
     */
    public static void logRequest(HttpServletRequest request) {
        logger.debug("contentLength=" + request.getContentLength());
        logger.debug("ContentType=" + request.getContentType());
        Enumeration<String> headers = request.getHeaderNames();
        String reqName;
        String reqValue;

        while (headers.hasMoreElements()) {
            reqName = (String) headers.nextElement();
            reqValue = request.getParameter(reqName);
            logger.debug("!!!!!!Header!!!!!!!!!!" + reqName + "=" + reqValue);
        }

        Enumeration<String> names = request.getParameterNames();

        while (names.hasMoreElements()) {
            reqName = (String) names.nextElement();
            reqValue = request.getParameter(reqName);
            logger.debug("!!!!!!Request!!!!!!!!!!" + reqName + "=" + reqValue);
        }
    }

    /**
     * 将request的Parameter信息放置到Attribute中。
     * @param request   http Request
     */
    public static void paramToAttribute(HttpServletRequest request) {
        String reqName;
        String reqValue;
        Enumeration<String> names = request.getParameterNames();

        while (names.hasMoreElements()) {
            reqName = (String) names.nextElement();
            reqValue = request.getParameter(reqName);
            request.setAttribute(reqName, reqValue);
        }
    }

    /**
     * 根据request中的信息判断是否是文件上传模式，因为此模式取不到任何的Parameter信息，
     * 所以才如此进行是否上传的判断。
     * @param request   request 对象
     * @return  true 是文件上传， false 非文件上传
     */
    public static boolean isFileUpload(HttpServletRequest request) {
        boolean isUpload;

        if (
            (request.getContentType() != null)
                && (request.getContentType().indexOf("multipart/form-data;") >= 0)) {
            isUpload = true;
        } else {
            isUpload = false;
        }

        return isUpload;
    }

    /**
     * 将html标记转化为规定标示符
     *
     * @param input 要转换的HTML
     * @return   HTML对应的文本
     */
    public static String escapeHTMLTags(String input) {
        if ((input == null) || (input.length() == 0)) {
            return input;
        }

        StringBuffer stringbuffer = new StringBuffer(input.length() + 6);

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            switch (c) {
                case 60: // '<'
                    stringbuffer.append("&lt;");
                    break;
                case 62: // '>'
                    stringbuffer.append("&gt;");
                    break;
                case 10: // '\n'
                    stringbuffer.append("<br>");
                    break;
                case 13: // '\r'
                    stringbuffer.append("<br>");
                    i++;
                    break;
                case 32: // ' '
                    stringbuffer.append("&nbsp;");
                    break;
                case 39: // '\''
                    stringbuffer.append("&acute;");
                    break;
                case 34: // '"'
                    stringbuffer.append("&quot;");
                    break;
                /*case 63: // '?'
                   stringbuffer.append("~`~");
                    break;*/
                default:
                    stringbuffer.append(c);
                    break;
            }
        }
//        logger.debug("source=[" + input + "] result=[" + stringbuffer + "]");
        return stringbuffer.toString();
    }

    /**
     * 将文档内容中不符合Js变量要求的字符替换处理，使可以被Js使用
     *
     * @param input    要转换的doc文档内容
     * @return         Js可以使用的文本
     */
    public static String escapeDocToJs(String input) {
        if ((input == null) || (input.length() == 0)) {
            return input;
        }

        //得到回车的字符串
        byte[] btEnter = new byte[2];
        btEnter[0] = 13;
        btEnter[1] = 10;
        String strEnter = new String(btEnter);

        //首先成对替换掉"
        int iCount = 0;

        while (input.indexOf("\"") >= 0) {
            if ((iCount % 2) == 0) { //引号起始
                input = input.replaceFirst("\"", "“");
            } else { //引号结束
                input = input.replaceFirst("\"", "”");
            }

            iCount++;
        }

        //logger.debug(input);
        //然后替换掉换行符
        input = input.replaceAll(strEnter, "\" + String.fromCharCode(11) + \""); //JS可以解析的回车符
        return input;
    }



    /**判断一字符串是否在某数组中
     * @param arr   数组对象
     * @param str   字符串对象
     * @return  是否包含
     */
    public static boolean isContain(String[] arr, String str) {
        boolean flag = false;

        for (int j = 0; j < arr.length; j++) {
            if (arr[j].equals(str)) {
                flag = true;
                break;
            } else {
                flag = false;
            }
        }

        return flag;
    }
    

    /**
     * 通过Dispatcher方式向JSP或Servlet跳转.
     *
     * @param req   reqeust对象
     * @param res   response对象
     * @param url   目标的URL相对路径
     * @throws  HDException 当跳转到JSP页面出错时
     */
    public static void sendDisp(HttpServletRequest req, HttpServletResponse res, String url)
        throws HDRuntimeException {
        try {
            RequestDispatcher rd = null;

            if (!url.substring(0, 1).equals("/")) {
                url = "/" + url;
            }

            rd = req.getRequestDispatcher(url);
            rd.forward(req, res);
        } catch (Exception e) {
            e.printStackTrace();
//            throw new HDException(e.getMessage(), e);
        }
    }

    /**
     * 通过Direct方式向JSP或Servlet跳转.
     *
     * @param res   response对象
     * @param url   目标的URL相对路径
     */
    public static void sendDir(HttpServletResponse res, String url) {
        try {
            res.sendRedirect(url);
        } catch (java.io.IOException e) {
            logger.error("Unable to redirect to /" + url, e);
        }
    }

    /**
     * 通过JavaScript方式跳转页面.
     *
     * @param res   request对象
     * @param url   目标的URL绝对路径
     * @param holdhistory 是否在浏览器中保留历史纪录，true保留，false不保留
     */
    public static void sendReplace(HttpServletResponse res, String url , boolean holdhistory) {
        PrintWriter out = null;
        try {
            out = res.getWriter();
            out.println();
            out.println("<script language=\"javascript\">");
            if (holdhistory) {
                out.println("    window.location.href=\"" + url + "\";");
            } else {
                out.println("    window.location.replace(\"" + url + "\");");
            }

            out.println("</script>");
        } catch (IOException ioe) {
            logger.error("Unable to replace to /" + url, ioe);
        }
    }


    /**
     * http的URL前缀
     * @param req HttpServletRequest
     * @return http的URL前缀
     */
    public static String getHttpPrefix(HttpServletRequest req) {
        return "http://" + req.getHeader("Host") + "/";
    }
    /**
     * https的URL前缀
     * @param req HttpServletRequest
     * @return 全路径的首页httpsURL
     */
    public static String getHttpsPrefix(HttpServletRequest req) {
        return "https://" + req.getHeader("Host") + "/";
    }




    /**
     * 将Unicode转化为中文
     * @param s 需要转化的字符串
     * @param encoding  要转化的字符集
     * @return 首页URL前缀
     */
    public static String unicodeToCharset(String  s, String encoding) {
      try {
          if (s == null || s.equals("")) {
              return  "";
          }
          String newstring = null;
          newstring = new  String(s.getBytes("ISO8859_1"), encoding);
          return  newstring;
       } catch (Exception  e)  {
           return  s;
       }
    }

    /**
     * 将Unicode转化为中文
     * @param s 需要转化的字符串
     * @return 首页URL前缀
     */
    public static String unicodeToChinese(String  s) {
      try {
          if (s == null || s.equals("")) {
              return  "";
          }
          String newstring = null;
          newstring = new  String(s.getBytes("ISO8859_1"), "gb2312");
          return  newstring;
       } catch (Exception  e)  {
           return  s;
       }
    }
    /**
     * 获取strUrl响应结果文本
     * @param strUrl strUrl
     * @return  strUrl响应结果文本
     * @throws HDException 例外
     */
    public static String getResponseText(String strUrl) throws HDException {
        try {
            URL url = new URL(strUrl);
            URLConnection httpConn = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String str;
            while ((str = reader.readLine()) != null) {
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append(str);
            }
            reader.close();
            return sb.toString();
        } catch (Exception e) {
            throw new HDException(e.getMessage(), e);
        }
    }

    /**
     * 根据reuqest对象得到对应的URL的全名称信息，比如：http://www.sina.com.cn:81
     * @param request   request对象
     * @return          URL全名称字符串
     */
    public static String getHttpURL(HttpServletRequest request) {
        StringBuffer sbURL = new StringBuffer(request.getScheme());
//        String[] protocol = request.getProtocol().split("/"); //取得协议前面的字母，如HTTP/1.1,变为"HTTP","1.1"
//        sbURL.append(protocol[0]).append("://").append(request.getServerName());
        sbURL.append("://").append(request.getServerName());
        int port = request.getServerPort(); //取得端口值
        if (port != 80) { //查看端口是否为80，如果不是还需要在联接上加上端口
            sbURL.append(":").append(port);
        }
        return sbURL.toString();
    }
    
    
    /**
     * 获得虚拟机最大内存大小
     */
    public static Long getMaxMemory() {
        Long time = Runtime.getRuntime().maxMemory();
        return (time/1024)/1024;
    }
    /**
     * 获得虚拟机空闲内存大小
     */
    public static Long getFreeMemory() {
        Long time = Runtime.getRuntime().freeMemory();
        return (time/1024)/1024;
    }
    
    /**
     * 得到 ContextPath
     * 
     * @param request
     * @return 
     * @author lengzy
     * @date 2015年12月7日
     */
    public static String getContextPath(HttpServletRequest request) {
    	String contextPath = request.getContextPath();	
    	return contextPath == null ? "" : contextPath;
    }
    
    /**
	 * 等到域名，并且带 ContexPath 例如：http://www.hd.com/hap-sy-app
	 * 
	 * @param request
	 * @return
	 * @author lengzy
	 * @date 2015年12月19日
	 */
    public static String getHttpUrlAndContexPath(HttpServletRequest request) {
    	StringBuffer sbURL = new StringBuffer(request.getScheme());
    	sbURL.append("://");
    	sbURL.append(request.getServerName());
    	int port = request.getServerPort(); //取得端口值
        if (port != 80) { //查看端口是否为80，如果不是还需要在联接上加上端口
            sbURL.append(":").append(port);
        }
    	String contextPath = getContextPath(request);
    	if (!"".equals(contextPath)) {
    		sbURL.append(contextPath);
    	}
    	return sbURL.toString();
    }
    
    /**
     * 根据request得到requestbody
     * @param request
     * @return
     * @throws HDException 
     * @author lianghua
     * @date 2016年4月22日
     */
	public static String getRequestBody(ServletRequest request) throws HDException{
		String inputLine;
		String str = "";
		try {
			BufferedReader br= request.getReader();
			str = br.readLine();
			while ((br.readLine()) != null) {
				inputLine=br.readLine();
			str += inputLine;
		}
		br.close();
		} catch (IOException e) {
			throw new HDException(e.getMessage(), e);
		}
		return str;
	}
	
	/**
	 * 得到head区数据
	 * @param request
	 * @return 
	 * @author lianghua
	 * @date 2016年4月26日
	 */
	public static Map<String, String> getHeadersInfo(HttpServletRequest request) {
		    Map<String, String> map = new HashMap<String, String>();
		    Enumeration headerNames = request.getHeaderNames();
		    while (headerNames.hasMoreElements()) {
		        String key = (String) headerNames.nextElement();
		        String value = request.getHeader(key);
		        map.put(key, value);
		    }
		    return map;
		  }

}
