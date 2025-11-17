package com.hayden.hap.common.login;

import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.entity.UserVO;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.formmgr.message.Status;
import com.hayden.hap.serial.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/16 14:24
 */
@Controller
public class LoginController {
    @Autowired
    private ILoginService loginService;

    @RequestMapping(method=RequestMethod.GET, value="/")
    public Object index() throws Exception {
        return "redirect:/static/login.html";
    }

//    @RequestMapping(method=RequestMethod.GET, value="{page}")
//    public Object htmlpage(@PathVariable String page,HttpServletRequest request) throws Exception {
//            return "redirect:/static/"+page+".html";
//    }



    @RequestMapping(method = RequestMethod.POST,value = "/login")
    @ResponseBody
    public ReturnResult login(HttpServletRequest request, HttpServletResponse response, @RequestBody String requestBody){
        ReturnResult returnResult=new ReturnResult();
        try {
            UserVO userVO=JsonUtils.parse(requestBody,UserVO.class);
            returnResult=loginService.login(request,response,userVO);
        } catch (HDException | IOException e) {
            e.printStackTrace();
            returnResult.setStatus(Status.FAIL);
            returnResult.setMessage(e.getMessage());
        }
        return returnResult;
    }

    @RequestMapping(method = RequestMethod.GET,value = "/logout")
    @ResponseBody
    public ReturnResult logout(HttpServletRequest request, HttpServletResponse response){
        ReturnResult returnResult=new ReturnResult();
        try {
            returnResult=loginService.logout(request,response);
        } catch (HDException | IOException e) {
            e.printStackTrace();
            returnResult.setStatus(Status.FAIL);
            returnResult.setMessage(e.getMessage());
        }
        return returnResult;
    }


}
