package com.hayden.hap.backgroud;


import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.entity.ParamVO;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.formmgr.message.Status;
import com.hayden.hap.common.git.itf.IGitService;
import com.hayden.hap.db.dataSource.DataSourceCreator;
import com.hayden.hap.db.dataSource.itf.ISimpleJdbcTemplateSupportDao;
import com.hayden.hap.upgrade.entity.UpgradeModuleVO;
import com.hayden.hap.upgrade.itf.IDbHandleService;
import com.hayden.hap.upgrade.itf.IMetaDataHandleService;
import com.hayden.hap.upgrade.itf.IUpgradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/2 13:15
 */
@Controller
public class BackGroudController {
    @Autowired
    private DataSourceCreator dataSourceCreator;
    @Autowired
    private IGitService gitService;
    @Autowired
    private IUpgradeService upgradeService;
    @Resource(name = "redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ISimpleJdbcTemplateSupportDao simpleJdbcTemplateSupportDao;
    @Autowired
    @Qualifier("metaDataHandleService")
    private IMetaDataHandleService metaDataHandleService;

    @Autowired
    private IDbHandleService dbHandleService;

    private String password = "6ty7yu";

    @RequestMapping(method = RequestMethod.GET, value = "/cleanCache")
    @ResponseBody
    public ReturnResult cleanCache(@RequestParam String project, @RequestParam String env, @RequestParam String module, @RequestParam String key) throws HDException {
        ReturnResult returnResult = new ReturnResult();
        try {
            if (key.equals(password)) {
                redisTemplate.opsForHash().delete("changedtablemap", project + env + module);
                redisTemplate.opsForHash().delete("logskey" + project + env, module);
                redisTemplate.opsForHash().delete("upgradeprogress", project + env + module);
                upgradeService.getShowVOsCache().remove(project + env);
                returnResult.setMessage("成功清除缓存");
            }
        } catch (Exception e) {
            returnResult.setStatus(Status.FAIL);
            returnResult.setMessage(e.getMessage());
        }
        return returnResult;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/cleanProgress")
    @ResponseBody
    public ReturnResult cleanProgress(@RequestParam String project, @RequestParam String env, @RequestParam String module, @RequestParam String key) throws HDException {
        ReturnResult returnResult = new ReturnResult();
        try {
            if (key.equals(password)) {
                redisTemplate.opsForHash().delete("logskey" + project + env, module);
                redisTemplate.opsForHash().delete("upgradeprogress", project + env + module);
                upgradeService.getShowVOsCache().remove(project + env);
                returnResult.setMessage("成功清除缓存");
            }
        } catch (Exception e) {
            returnResult.setStatus(Status.FAIL);
            returnResult.setMessage(e.getMessage());
        }
        return returnResult;
    }


    @RequestMapping(method = RequestMethod.GET, value = "/rest")
    @ResponseBody
    public ReturnResult rest(@RequestParam String project, @RequestParam String env, @RequestParam String key) throws HDException {
        ReturnResult returnResult = new ReturnResult();
        try {
            if (key.equals(password)) {

                String dataSourceId = dataSourceCreator.getDataSourceId(project, env);

                Map<String, UpgradeModuleVO> upgradeModuleVOMap = upgradeService.getModuleMapfromDataBase(project, env);

                redisTemplate.delete("logskey" + project + env);
                if (upgradeModuleVOMap != null) {
                    for (String module : upgradeModuleVOMap.keySet()) {
                        UpgradeModuleVO upgradeModuleVO = upgradeModuleVOMap.get(module);
                        redisTemplate.opsForHash().delete("changedtablemap", project + env + module);
                        redisTemplate.opsForHash().delete("upgradeprogress", project + env + module);
                        upgradeModuleVO.setLastfilets(null);
                        upgradeModuleVO.setCurrentver(null);
                        upgradeModuleVO.setProgress(null);
                        upgradeModuleVO.setStatus("0");
                        upgradeModuleVO.setDatetime(null);
                        upgradeModuleVO.setProductver(null);
                        upgradeModuleVO.setProlastfilets(null);
                        simpleJdbcTemplateSupportDao.update(upgradeModuleVO, new DynaSqlVO(), dataSourceId);
                    }
                }
                upgradeService.getShowVOsCache().clear();
                returnResult.setMessage("成功重置");
            }
        } catch (Exception e) {
            returnResult.setStatus(Status.FAIL);
            returnResult.setMessage(e.getMessage());
        }
        return returnResult;
    }


    @RequestMapping(method = RequestMethod.GET, value = "/initdatabase")
    @ResponseBody
    public ReturnResult initdatabase(@RequestParam String project, @RequestParam String env, @RequestParam String key) throws HDException {
        ReturnResult returnResult = new ReturnResult();
        try {
            if (key.equals(password)) {
                ParamVO paramVO = new ParamVO();
                paramVO.setProject(project);
                paramVO.setEnv(env);
                dbHandleService.initDataBase(paramVO);
                returnResult.setMessage("成功初始化");
            }
        } catch (Exception e) {
            returnResult.setStatus(Status.FAIL);
            returnResult.setMessage(e.getMessage());
        }
        return returnResult;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/cleanAllPlatCache")
    @ResponseBody
    public ReturnResult cleanAllPlatCache(@RequestParam String key) throws HDException {
        ReturnResult returnResult = new ReturnResult();
        try {
            if (key.equals(password)) {
                metaDataHandleService.clearAllCache();
                returnResult.setMessage("成功清除缓存");
            }
        } catch (Exception e) {
            returnResult.setStatus(Status.FAIL);
            returnResult.setMessage(e.getMessage());
        }
        return returnResult;
    }
}
