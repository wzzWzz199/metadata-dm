package com.hayden.hap.upgrade.service;

import com.hayden.hap.common.entity.ParamVO;
import com.hayden.hap.upgrade.entity.LogsVO;
import com.hayden.hap.upgrade.entity.ModuleLogsVO;
import com.hayden.hap.upgrade.entity.PatchFileVO;
import com.hayden.hap.upgrade.entity.ProgressModuleVO;
import com.hayden.hap.upgrade.itf.ILogsService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/12 9:40
 */
@Service("logsService")
public class LogsServiceImpl implements ILogsService {

    private final String LOGSKEY="logskey";

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void setLogs(ParamVO paramVO, String msg, ProgressModuleVO progressModuleVO) {
        if(progressModuleVO.getFilename()!=null)
            msg=progressModuleVO.getFilename()+":"+msg;
        setLogs(paramVO,"common",System.currentTimeMillis(),msg,progressModuleVO);
    }


    @Override
    public void setLogs(ParamVO paramVO, PatchFileVO patchFileVO, String msg, ProgressModuleVO progressModuleVO) {
        setLogs(paramVO,patchFileVO.getUniqueCode(),Long.valueOf(patchFileVO.getTimestamp()),msg,progressModuleVO);
    }


    @Override
    public void setLogs(ParamVO paramVO, String name, Long date, String msg, ProgressModuleVO progressModuleVO) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        LogsVO logsVO=new LogsVO();
        logsVO.setDate(sdf.format(new Date(date)));
        logsVO.setDatetime(System.currentTimeMillis());
        logsVO.setMsg(msg);
        logsVO.setName(name);
        logsVO.setProgress(String.valueOf(progressModuleVO.getProgress()));

        String status="1";
        if(progressModuleVO.getProgress()>=100&&!progressModuleVO.getStatus().equals("2")){
            status="3";
        }else if(progressModuleVO.getStatus().equals("2")){
            status="2";
        }
        logsVO.setStatus(status);

        String key=paramVO.getProject()+paramVO.getEnv();
        redisTemplate.opsForHash().put(LOGSKEY+key,paramVO.getModule(),logsVO);
    }

    @Override
    public List<ModuleLogsVO> getLogs(ParamVO paramVO) {
        List<ModuleLogsVO> moduleLogsVOS=new ArrayList<>();
        Long datetime=paramVO.getDatetime();
        String key=paramVO.getProject()+paramVO.getEnv();

        Map<Object,Object> logsMap=redisTemplate.opsForHash().entries(LOGSKEY+key);
        if(logsMap!=null){
            for(Object module:logsMap.keySet()){
                List<LogsVO> logsVOS=new ArrayList<>();
                LogsVO logsVO=(LogsVO)logsMap.get(module);

                if(datetime==null||datetime.compareTo(logsVO.getDatetime())<0){
                    logsVOS.add(logsVO);
                    ModuleLogsVO moduleLogsVO=new ModuleLogsVO();
                    moduleLogsVO.setCode(String.valueOf(module));
                    moduleLogsVO.setLogs(logsVOS);
                    moduleLogsVOS.add(moduleLogsVO);
                }
            }
        }
        return moduleLogsVOS;
    }
}
