package com.hayden.hap.common.login;

import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.entity.UserVO;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.serial.JsonUtils;
import com.hayden.hap.upgrade.service.DbHandleServiceImpl;
import com.hayden.hap.upgrade.utils.MetaDataFileUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/16 14:17
 */
@Service("loginService")
public class LoginServiceImpl implements ILoginService {
    @Resource(name = "redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public ReturnResult login(HttpServletRequest request, HttpServletResponse response, UserVO userVO) throws IOException, HDException {

        ReturnResult returnResult=new ReturnResult();
        returnResult.setData(false);

        String usercode=userVO.getUsercode();
        String password=userVO.getPassword();

        String users = MetaDataFileUtils.readFile2Str(new File(DbHandleServiceImpl.class.getClassLoader().getResource("/user/user.txt").getPath()));
        HashMap<String,String> userMap=JsonUtils.parse(users, HashMap.class);
        if(userMap.containsKey(usercode)){
            if(password.equals(userMap.get(usercode))){
                String sessionid=request.getSession(true).getId();
                redisTemplate.opsForValue().set(sessionid,userVO,5, TimeUnit.HOURS);
                returnResult.setData(true);
                returnResult.setMessage("登录成功");
                return returnResult;
            }else{
                returnResult.setMessage("用户名密码错误");
                return returnResult;
            }
        }else {
            returnResult.setMessage("用户不存在");
            return returnResult;
        }
    }


    @Override
    public ReturnResult logout(HttpServletRequest request, HttpServletResponse response) throws IOException, HDException {
        ReturnResult returnResult=new ReturnResult();

        Cookie[] cookies=request.getCookies();
        for(Cookie cookie:cookies){
            if(cookie.getName().equals("JSESSIONID")){
                String value=cookie.getValue();
                redisTemplate.delete(value);
                returnResult.setData(true);
                returnResult.setMessage("登出成功");
                return returnResult;
            }
        }
        returnResult.setData(false);
        returnResult.setMessage("用户不存在");
        return returnResult;
    }
}
