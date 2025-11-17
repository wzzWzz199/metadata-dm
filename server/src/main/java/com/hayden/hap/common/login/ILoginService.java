package com.hayden.hap.common.login;

import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.entity.UserVO;
import com.hayden.hap.common.formmgr.message.ReturnResult;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/16 14:17
 */
public interface ILoginService {

    ReturnResult login(HttpServletRequest request, HttpServletResponse response, UserVO userVO) throws IOException, HDException;

    ReturnResult logout(HttpServletRequest request, HttpServletResponse response) throws IOException, HDException;
}
