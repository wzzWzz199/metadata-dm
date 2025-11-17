package com.hayden.hap.upgrade.utils;

import com.hayden.hap.upgrade.entity.PatchFileVO;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/12 13:59
 */
public class MetaDataFileUtils {

    public static List<File> patchPreHandle(File[] patchFiles,int desc) {
        List<File> fileList = new ArrayList<>(Arrays.asList(patchFiles));
        fileList.sort(new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                PatchFileVO patchFileVO1 = new PatchFileVO(f1);
                PatchFileVO patchFileVO2 = new PatchFileVO(f2);

                if (patchFileVO1.isCheck() && patchFileVO2.isCheck()) {
                    return patchFileVO1.getTimestamp().compareTo(patchFileVO2.getTimestamp())*desc;
                }
                return 0;
            }
        });
        return fileList;
    }

    public static List<File> versionPreHandle(File[] patchFiles){
        List<File> fileList = new ArrayList<>(Arrays.asList(patchFiles));
        fileList.sort(new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                return f1.getName().compareTo(f2.getName());
            }
        });
        return fileList;
    }

    /**
     * 文件转字符串
     *
     * @param patchFile
     * @return
     * @throws IOException
     */
    public static String readFile2Str(File patchFile) throws IOException {

        FileInputStream inputStream = new FileInputStream(patchFile);
        int size = inputStream.available();
        byte[] buffer = new byte[size];
        inputStream.read(buffer);
        inputStream.close();

        return new String(buffer, "utf-8");
    }

    public static String copyFileNamebyNewTs(PatchFileVO patchFileVO,String version) {
        return patchFileVO.getMetaDataType() +"-"+ patchFileVO.getUniqueCode() +"-"+ System.currentTimeMillis()
                +"-"+ patchFileVO.getProject() +"-"+ patchFileVO.getEnv() +"-"+ patchFileVO.getUsername() +"-"+patchFileVO.getTimestamp()+"-"+version+ ".hdml";
    }
}
