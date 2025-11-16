package com.hayden.hap.common.menu.ctrl;

import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.formmgr.message.Status;
import com.hayden.hap.common.menu.itf.IMetaDtaMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/2 17:56
 */
@Controller
public class MetaDataMenuController {

    @Autowired
    IMetaDtaMenuService metaDtaMenuService;

    @RequestMapping(method = RequestMethod.GET,value="/metadata/COMMON/getProjects")
    @ResponseBody
    public ReturnResult getProjects(){
        ReturnResult returnResult=new ReturnResult();
        try {
            returnResult.setData(metaDtaMenuService.getProjectsMenu());
        } catch (Exception e) {
            e.printStackTrace();
            returnResult.setStatus(Status.FAIL);
            returnResult.setMessage(e.getMessage());
        }
        return returnResult;
    }


    @RequestMapping(method = RequestMethod.GET,value="/metadata/COMMON/getServerDate")
    @ResponseBody
    public ReturnResult getServerDate(){
        ReturnResult returnResult=new ReturnResult();
        try {
            returnResult.setData(System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
            returnResult.setStatus(Status.FAIL);
            returnResult.setMessage(e.getMessage());
        }
        return returnResult;
    }
}
