package com.hayden.hap.common.login;

import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.entity.UserVO;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.formmgr.message.Status;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/16 14:24
 */
@RestController
@RequestMapping
public class LoginController {
    @Autowired
    private ILoginService loginService;

    @GetMapping("/")
    public ResponseEntity<Void> index() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/static/login.html"))
                .build();
    }

    @PostMapping(value = "/login")
    public ReturnResult login(HttpServletRequest request, HttpServletResponse response, @RequestBody UserVO userVO){
        ReturnResult returnResult=new ReturnResult();
        try {
            returnResult=loginService.login(request,response,userVO);
        } catch (HDException | IOException e) {
            e.printStackTrace();
            returnResult.setStatus(Status.FAIL);
            returnResult.setMessage(e.getMessage());
        }
        return returnResult;
    }

    @GetMapping(value = "/logout")
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
