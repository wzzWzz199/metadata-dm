package com.hayden.hap.common.login;

import com.alibaba.fastjson.JSON;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.utils.CurrentEnvUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/16 15:45
 */

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String uri = httpServletRequest.getRequestURI();

        if (uri.indexOf("/login") >= 0||uri.equals("/")||uri.contains("/static")) {
            return true;
        }
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("JSESSIONID")) {
                    String value = cookie.getValue();
                    if (redisTemplate.opsForValue().get(value) != null) {
                        HashMap<String, Object> contextMap = new HashMap<>();
                        contextMap.put("uservo", redisTemplate.opsForValue().get(value));
                        CurrentEnvUtils.setContextMap(contextMap);
                        redisTemplate.opsForValue().set(value,redisTemplate.opsForValue().get(value),5, TimeUnit.HOURS);
                        return true;
                    }
                }
            }
        }


        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpServletResponse.setContentType("application/json;charset=utf-8");
        ReturnResult<?> result = new ReturnResult<>();
        result.setStatus(2401);
        result.setMessage("会话已失效，请重新登录！");
        PrintWriter p = httpServletResponse.getWriter();
        p.write(JSON.toJSONString(result));
        p.flush();
        p.close();
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
