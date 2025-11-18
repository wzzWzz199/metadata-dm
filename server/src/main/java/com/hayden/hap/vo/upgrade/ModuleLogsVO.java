package com.hayden.hap.vo.upgrade;

import java.util.List;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/12 13:38
 */
public class ModuleLogsVO {
    private String code;
    private List<LogsVO> logs;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<LogsVO> getLogs() {
        return logs;
    }

    public void setLogs(List<LogsVO> logs) {
        this.logs = logs;
    }
}
