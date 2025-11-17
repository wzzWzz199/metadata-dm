package com.hayden.hap.upgrade.ctrl;

import com.hayden.hap.common.entity.ParamVO;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.formmgr.message.Status;
import com.hayden.hap.upgrade.itf.IUpgradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/2 17:56
 */
@Controller
public class UpgradeController {

    @Autowired
    IUpgradeService upgradeService;


    @RequestMapping(method = RequestMethod.GET, value = "/metadata/UPGRADE/getModuleListCache")
    @ResponseBody
    public ReturnResult getModuleListCache(@RequestParam String project, @RequestParam String env) {
        ReturnResult returnResult = new ReturnResult();
        try {
            if (upgradeService.getShowVOsCache().containsKey(project + env)) {
                returnResult.setData(upgradeService.getShowVOsCache().get(project + env));
            }
        } catch (Exception e) {
            returnResult.setStatus(Status.FAIL);

            e.printStackTrace();
            returnResult.setMessage(e.getMessage());
        }
        return returnResult;
    }


    @RequestMapping(method = RequestMethod.GET, value = "/metadata/UPGRADE/getModuleList")
    @ResponseBody
    public ReturnResult getProjects(@RequestParam String project, @RequestParam String env) {
        ReturnResult returnResult = new ReturnResult();
        try {
            ParamVO paramVO = new ParamVO();
            paramVO.setProject(project);
            paramVO.setEnv(env);

            returnResult.setData(upgradeService.getModuListWithSync(paramVO));
        } catch (Exception e) {
            returnResult.setStatus(Status.FAIL);

            e.printStackTrace();

//            Writer result = new StringWriter();
//            PrintWriter printWriter = new PrintWriter(result);
//            e.fillInStackTrace().printStackTrace(printWriter);
            returnResult.setMessage(e.getMessage());
        }
        return returnResult;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/metadata/UPGRADE/getVersionList")
    @ResponseBody
    public ReturnResult getVersions(@RequestParam String project, @RequestParam String env, @RequestParam String modulecode) {
        ReturnResult returnResult = new ReturnResult();
        try {
            ParamVO paramVO = new ParamVO();
            paramVO.setProject(project);
            paramVO.setEnv(env);
            paramVO.setModule(modulecode);

            returnResult.setData(upgradeService.getVersionList(paramVO));
        } catch (Exception e) {
            e.printStackTrace();
            returnResult.setStatus(Status.FAIL);
            returnResult.setMessage(e.getMessage());
        }
        return returnResult;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/metadata/UPGRADE/upgrade")
    @ResponseBody
    public ReturnResult upgrade(@RequestParam String project, @RequestParam String env, @RequestParam String modulecode, @RequestParam String version,@RequestParam(required = false, defaultValue = "") String prover) {
        ReturnResult returnResult = new ReturnResult();
        try {
            ParamVO paramVO = new ParamVO();
            paramVO.setProject(project);
            paramVO.setEnv(env);
            paramVO.setModule(modulecode);
            paramVO.setVersion(version);
            paramVO.setProVer(prover);

            returnResult = new ReturnResult(null, upgradeService.upgrade(paramVO));
        } catch (Exception e) {
            e.printStackTrace();
            returnResult.setStatus(Status.FAIL);
            returnResult.setMessage(e.getMessage());
        }
        return returnResult;
    }


    @RequestMapping(method = RequestMethod.GET, value = "/metadata/UPGRADE/stop")
    @ResponseBody
    public ReturnResult stop(@RequestParam String project, @RequestParam String env, @RequestParam String modulecode) {
        ReturnResult returnResult = new ReturnResult();
        try {
            ParamVO paramVO = new ParamVO();
            paramVO.setProject(project);
            paramVO.setEnv(env);
            paramVO.setModule(modulecode);

            returnResult = new ReturnResult(null, upgradeService.stopUpgrade(paramVO));
        } catch (Exception e) {
            e.printStackTrace();
            returnResult.setStatus(Status.FAIL);
            returnResult.setMessage(e.getMessage());
        }
        return returnResult;
    }
}
