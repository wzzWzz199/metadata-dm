package com.hayden.hap.vo.upgrade;

import java.io.File;
import java.util.List;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/7/8 9:44
 */
public class VersionContext {

    private String ver;
    private List<String> versions;
    private List<File> fileSortList;
    private File upgradeFile;

    public File getUpgradeFile() {
        return upgradeFile;
    }

    public void setUpgradeFile(File upgradeFile) {
        this.upgradeFile = upgradeFile;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public List<String> getVersions() {
        return versions;
    }

    public void setVersions(List<String> versions) {
        this.versions = versions;
    }

    public List<File> getFileSortList() {
        return fileSortList;
    }

    public void setFileSortList(List<File> fileSortList) {
        this.fileSortList = fileSortList;
    }
}
