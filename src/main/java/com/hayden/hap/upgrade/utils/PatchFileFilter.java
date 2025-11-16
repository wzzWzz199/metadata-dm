package com.hayden.hap.upgrade.utils;

import com.hayden.hap.upgrade.entity.PatchFileVO;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/8 13:56
 */
public class PatchFileFilter implements FileFilter {
    private String currentVer;
    private String version;
    private Long lastts;
    private HashSet<String> tableSet;


    public PatchFileFilter(String currentVer, String version, Long lastts, HashSet<String> tableSet) {
        this.currentVer = currentVer;
        this.version = version;
        this.lastts = lastts;
        this.tableSet = tableSet;
    }

    /**
     * 过滤补丁文件
     * 格式必须满足 type-code-timstamp-patchtype 这种格式，第三段必须为时间戳
     * 文件类型必须为hdml
     * 当前遍历目录等于数据库存储版本时需要按照时间戳过滤，已升级补丁不展示，如版本不等于当前版本则全展示
     *
     * @param pathname
     * @return
     */
    @Override
    public boolean accept(File pathname) {
        PatchFileVO patchFileVO = new PatchFileVO(pathname);
        if (patchFileVO.isCheck()) {
            if (tableSet != null && (patchFileVO.getMetaDataType().equals("table") || patchFileVO.getMetaDataType().equals("tablecolumn"))) {
                tableSet.add(patchFileVO.getUniqueCode());
            }

            if (currentVer != null && currentVer.equals(version))
                return patchFileVO.getTimestamp().compareTo(lastts) > 0 ? true : false;
            else
                return true;
        }
        return false;
    }
}
