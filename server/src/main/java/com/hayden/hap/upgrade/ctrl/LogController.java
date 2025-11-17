package com.hayden.hap.upgrade.ctrl;

import com.hayden.hap.common.entity.ParamVO;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.formmgr.message.Status;
import com.hayden.hap.upgrade.itf.ILogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/12 14:57
 */
@RestController
@RequestMapping("/metadata/UPGRADE")
public class LogController {
    @Autowired
    private ILogsService logsService;

    @GetMapping("/getLogs")
    public ReturnResult upgrade(@RequestParam String project, @RequestParam String env, @RequestParam Long datetime){
        ReturnResult returnResult=new ReturnResult();
        try {
            ParamVO paramVO =new ParamVO();
            paramVO.setProject(project);
            paramVO.setEnv(env);
            paramVO.setDatetime(datetime);

            returnResult=new ReturnResult(logsService.getLogs(paramVO));
        } catch (Exception e) {
            e.printStackTrace();
            returnResult.setStatus(Status.FAIL);
            returnResult.setMessage(e.getMessage());
        }
        return returnResult;
    }
}
