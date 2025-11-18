package com.hayden.hap.service.upgrade.itf;

import com.hayden.hap.common.entity.ParamVO;
import com.hayden.hap.vo.upgrade.ModuleLogsVO;
import com.hayden.hap.vo.upgrade.PatchFileVO;
import com.hayden.hap.vo.upgrade.ProgressModuleVO;

import java.util.List;

public interface ILogsService {


    void setLogs(ParamVO paramVO, String msg, ProgressModuleVO progressModuleVO);

    void setLogs(ParamVO paramVO, PatchFileVO patchFileVO, String msg, ProgressModuleVO progressModuleVO);

    void setLogs(ParamVO paramVO, String name, Long date, String msg, ProgressModuleVO progressModuleVO);


    List<ModuleLogsVO> getLogs(ParamVO paramVO);
}
