package com.hayden.hap.service.upgrade;

import com.hayden.hap.common.entity.ParamVO;
import com.hayden.hap.common.git.itf.IGitService;
import com.hayden.hap.vo.upgrade.PatchFileVO;
import com.hayden.hap.vo.upgrade.ProgressModuleVO;
import com.hayden.hap.enums.UpgradeEnum;
import com.hayden.hap.service.upgrade.itf.IProgressService;
import com.hayden.hap.utils.MetaDataFileUtils;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/7/9 17:49
 */
@Service("prodMetaDataHandleService")
public class ProdMetaDataHandleServiceImpl extends MetaDataHandleServiceImpl {

    @Value("${META.GIT.ROOTPATH}")
    private String GITROOT;

    @Autowired
    private IProgressService progressService;
    @Autowired
    private IGitService gitService;

    @Override
    public void beforeUpgrade(ParamVO paramVO,ProgressModuleVO progressModuleVO) throws IOException, GitAPIException {
        copyProductPatch(paramVO,progressModuleVO);
    }


    private void copyProductPatch(ParamVO paramVO,ProgressModuleVO progressModuleVO) throws IOException, GitAPIException {

        String project = paramVO.getProject();
        String module = paramVO.getModule();
        String targetVer = paramVO.getVersion();

        String currentPrdVer = progressModuleVO.getProductver();
        Long prdTs = progressModuleVO.getProlastfilets();
        String targetPath = project + File.separator + module + File.separator + targetVer + File.separator;

        List<File> versions = getPreVersions(paramVO, progressModuleVO);

        for (File versionfile : versions) {
            File[] patchFiles = versionfile.listFiles();

            List<File> fileList=MetaDataFileUtils.patchPreHandle(patchFiles, 1);

            String version = versionfile.getName();
            for (File patch : fileList) {
                PatchFileVO patchFileVO = new PatchFileVO(patch);
                if (patchFileVO.isCheck()) {
                    Long ts = patchFileVO.getTimestamp();

                    if (version.equals(currentPrdVer) && ts <= prdTs)
                        continue;

                    String newName = MetaDataFileUtils.copyFileNamebyNewTs(patchFileVO,version);

                    File targetFile = new File(GITROOT + File.separator + targetPath + newName);
                    FileUtils.copyFile(patch, targetFile);

                    progressModuleVO.setProductver(version);
                    progressModuleVO.setProlastfilets(ts);
                    progressService.setProgressModuleVO(paramVO, progressModuleVO);
                }
            }
            gitService.addAndPush("产品升级补丁");
        }
    }

    private List<File> getPreVersions(ParamVO paramVO, ProgressModuleVO progressModuleVO) {
        List<File> versions = new ArrayList<>();
        String module = paramVO.getModule();
        String selectVer = paramVO.getProVer();

        File moduleDir = new File(GITROOT + File.separator + UpgradeEnum.PRODUCT_CODE.getCode() + File.separator + module);

        File[] versionFiles = moduleDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return Pattern.matches("^\\d+(\\.\\d+)+(\\.\\d+)+(\\.\\d+)", pathname.getName());
            }
        });
        for (File versionFile : versionFiles) {
            String ver = versionFile.getName();
            String currentVer = progressModuleVO.getProductver();
            if (ver.compareTo(selectVer) <= 0 && (currentVer == null || ver.compareTo(currentVer) >= 0)) {
                versions.add(versionFile);
            }
        }
        Collections.sort(versions, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return versions;
    }
}
