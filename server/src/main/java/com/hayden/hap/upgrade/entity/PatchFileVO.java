package com.hayden.hap.upgrade.entity;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/8 15:59
 */
public class PatchFileVO {
    private String metaDataType;
    private String uniqueCode;
    private boolean isAll;
    private String fileName;
    private Long timestamp;
    private String type;
    private String project;
    private String env;
    private String username;
    private Long oldtimestap;
    private boolean isCheck;
    private String version;
    private Long tenantid;
    public String getVersion() {
        return version;
    }

    public PatchFileVO(File file) {
        setFileName(file.getName());
    }

    public Long getTenantid() { return tenantid; }

    public Long getOldtimestap() {
        return oldtimestap;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMetaDataType() {
        return metaDataType;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public boolean isAll() {
        return isAll;
    }

    public String getProject() {
        return project;
    }

    public String getEnv() {
        return env;
    }

    public String getUsername() {
        return username;
    }

    private void menu() {
        String[] filenamearr = fileName.split("-");

        if (this.type.equals("hdml") && filenamearr.length >= 6) {
            this.metaDataType = filenamearr[0];
            this.uniqueCode = filenamearr[1];
            this.timestamp = Long.valueOf(filenamearr[2]);
            this.project = filenamearr[3];
            this.env = filenamearr[4];
            this.username = filenamearr[5].replace(".hdml", "");

            if (filenamearr.length > 6) {
                this.oldtimestap = Long.valueOf(filenamearr[6]);
                this.version = filenamearr[7].replace(".hdml", "");
            }
            this.isCheck = true;
        } else {
            this.isCheck = false;
        }
    }

    private void other() {
        try {
            Pattern patchPattern = Pattern
                    .compile("^([a-z]+)-([\\u4e00-\\u9fa5\\w\\s-]+)-([0-9]+)-([a-zA-Z]+)-([a-zA-Z]+)-([\\u4e00-\\u9fa5\\w-.]+)",
                            Pattern.CASE_INSENSITIVE);
            Matcher matcher = patchPattern.matcher(fileName);
            if (this.type.equals("hdml") && matcher.find()) {
                this.metaDataType = matcher.group(1);
                this.uniqueCode = matcher.group(2);
                this.timestamp = Long.valueOf(matcher.group(3));
                this.project = matcher.group(4);
                this.env = matcher.group(5);
                String[] last = matcher.group(6).split("-");
                this.username = last[0].replace(".hdml", "");
                //以前的补丁命名默认租户为1
                this.tenantid = 1L;
                if (last.length > 2) {
                    //从产品升级到项目的补丁文件命名规则
                    this.oldtimestap = Long.valueOf(last[1]);
                    this.version = last[2].replace(".hdml", "");
                }
                if (last.length==2){
                    this.tenantid = Long.valueOf(last[1].replace(".hdml", ""));
                }
                if (this.metaDataType.equals("table")||this.metaDataType.equals("tablecolumn")){
                    this.tenantid = 1L;
                }
                this.isCheck = true;
            } else {
                this.isCheck = false;
            }
        } catch (Exception e) {
            this.isCheck = false;
        } finally {
            return;
        }
    }

    public void setFileName(String fileName) {

        this.fileName = fileName;
        this.type = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
//        if(fileName.startsWith("pcmenu")||fileName.startsWith("mobilemenu")){
//            menu();
//        }else{
        other();
//        }
    }
    public static void main(String[] args) {
    	 Pattern patchPattern = Pattern
                 .compile("^([a-z]+)-([\\u4e00-\\u9fa5\\w\\s-]+)-([0-9]+)-([a-zA-Z]+)-([a-zA-Z]+)-([\\u4e00-\\u9fa5\\w-.]+)",
                         Pattern.CASE_INSENSITIVE);
    	 Matcher matcher = patchPattern.matcher("pcfitem-PHD_HD_TJ_HDFST -1595555614566-hd-dev-郭志恒-2000000000000.hdml");
         if (matcher.find()) {
        	 System.out.println(matcher.group(1)+"***"+matcher.group(2)+"***"+matcher.group(3));
         }
	}
}
