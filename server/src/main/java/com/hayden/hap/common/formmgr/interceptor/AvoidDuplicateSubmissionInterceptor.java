package com.hayden.hap.common.formmgr.interceptor;

import com.hayden.hap.common.formmgr.annotation.AvoidDuplicateSubmission;
import com.hayden.hap.common.utils.session.CurrentEnvUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 防重复提交拦截器
 * @author zhangfeng
 * @date 2016年7月8日
 */
public class AvoidDuplicateSubmissionInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(AvoidDuplicateSubmissionInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {

        Long userId = CurrentEnvUtils.getUserId();
        if (userId != null) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();

            AvoidDuplicateSubmission annotation = method.getAnnotation(AvoidDuplicateSubmission.class);
            if (annotation != null) {
                boolean needSaveSession = annotation.needSaveToken();
                if (needSaveSession) {
                    setTokenStr(request);
                }

                boolean needRemoveSession = annotation.needRemoveToken();
                if (needRemoveSession) {
                    if (isRepeatSubmit(request)) {
                        logger.warn("please don't repeat submit,[user:" + CurrentEnvUtils.getUserName() + ",url:"
                                + request.getServletPath() + "]");
                        
                        String rtnURL = request.getParameter("rtnURL");
                        logger.debug(rtnURL);
                        response.sendRedirect(rtnURL);
//                        request.setAttribute("tip", "重复提交错误，请重新打开页面。<br/>(为避免这个错误，请不要保存返回后右键刷新，不要同时打开多个相同功能的修改页面)");
//                        request.getRequestDispatcher("/WEB-INF/jsp/error/error-tip.jsp").forward(request, response);
                        return false;
                    }
                    removeTokenStr(request);
                }
            }
        }
        return true;
    }
    
    private void setTokenStr(HttpServletRequest request) {
    	Object obj = request.getSession(false).getAttribute("token");
    	String funcCode = request.getParameter("funcCode");
    	String tokenStr = UUID.randomUUID().toString();
    	if(obj!=null) {
    		@SuppressWarnings("unchecked")
			Map<String,String> map = (Map<String,String>)obj;    		
    		map.put(funcCode, tokenStr);
    	}else {
    		Map<String,String> map = new HashMap<>();
    		map.put(funcCode, tokenStr);
    		request.getSession(false).setAttribute("token", map);
    	} 
    }
    
    private String getTokenStr(HttpServletRequest request) {
    	Object obj = request.getSession(false).getAttribute("token");
    	String funcCode = request.getParameter("funcCode");
    	if(obj!=null) {
    		@SuppressWarnings("unchecked")
			Map<String,String> map = (Map<String,String>)obj; 
    		return map.get(funcCode);
    	}
    	return null;
    }
    
    private void removeTokenStr(HttpServletRequest request) {
    	Object obj = request.getSession(false).getAttribute("token");
    	String funcCode = request.getParameter("funcCode");
    	if(obj!=null) {
    		@SuppressWarnings("unchecked")
			Map<String,String> map = (Map<String,String>)obj; 
    		map.remove(funcCode);
    	}
    }

    private boolean isRepeatSubmit(HttpServletRequest request) {
        String serverToken = getTokenStr(request);
        if (serverToken == null) {
            return true;
        }
        String clinetToken = request.getParameter("token");
        if (clinetToken == null) {
            return true;
        }
        if (!serverToken.equals(clinetToken)) {
            return true;
        }
        return false;
    }

}
