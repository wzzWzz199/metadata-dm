package com.hayden.hap.common.menu.ctrl;

import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.formmgr.message.Status;
import com.hayden.hap.common.menu.itf.IMetaDtaMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/2 17:56
 */
@RestController
@RequestMapping("/metadata/COMMON")
public class MetaDataMenuController {

    @Autowired
    IMetaDtaMenuService metaDtaMenuService;

    @GetMapping("/getProjects")
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


    @GetMapping("/getServerDate")
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
