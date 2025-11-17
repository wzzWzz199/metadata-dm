package com.hayden.hap.upgrade.service;

import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.dbop.exception.HDRuntimeException;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.entity.ParamVO;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.db.dataSource.DataSourceCreator;
import com.hayden.hap.db.dataSource.itf.IDataSourceGeneratorService;
import com.hayden.hap.upgrade.entity.PatchFileVO;
import com.hayden.hap.upgrade.entity.ProgressModuleVO;
import com.hayden.hap.upgrade.itf.*;
import com.hayden.hap.upgrade.utils.MetaDataFileUtils;
import com.hayden.hap.upgrade.utils.PatchFileFilter;
import com.hayden.hap.upgrade.utils.RunnableUtils;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/8 13:30
 */
@Service("metaDataHandleService")
public class MetaDataHandleServiceImpl implements IMetaDataHandleService {

    @Value("${META.UPGRADE.ROOTPATH}")
    private String PATCHROOT;

    @Value("${META.GIT.ROOTPATH}")
    private String GITROOT;

    @Autowired
    private IDataHandleService commonDataHandleService;
    @Autowired
    private IProgressService progressService;
    @Autowired
    private DataSourceCreator dataSourceCreator;
    @Autowired
    private IDbHandleService dbHandleService;
    @Autowired
    private ILogsService logsService;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private IDataSourceGeneratorService dataSourceGeneratorService;

    private HashMap<String,Boolean> lockMap=new HashMap<>();



    /**
     * 元数据升级方法
     *
     * @param paramVO
     * @return
     * @throws IOException
     * @throws HDException
     * @throws ClassNotFoundException
     */
    @Override
    public List<File> upgradeMetaData(ParamVO paramVO) throws HDException {
        //到这一步redis必然存在过程实体，除非redis服务宕机，目前先不考虑
        ProgressModuleVO progressModuleVO = progressService.getProgressModuleVO(paramVO);
        logsService.setLogs(paramVO, "正在创建数据库连接", progressModuleVO);
        String dataSourceId = dataSourceCreator.getDataSourceId(paramVO.getProject(), paramVO.getEnv());
        dataSourceCreator.getDataSource(dataSourceId);
        paramVO.setDataSourceId(dataSourceId);

        boolean islock=false;
        logsService.setLogs(paramVO, "创建数据库连接完成，等待清除主键缓存", progressModuleVO);
        synchronized(dataSourceId){
            if(!lockMap.containsKey(dataSourceId)||(lockMap.containsKey(dataSourceId)&&!lockMap.get(dataSourceId))){
                lockMap.put(dataSourceId,true);
                dataSourceGeneratorService.cleanCache(dataSourceId);
                islock=true;
            }
        }
        logsService.setLogs(paramVO, "清除主键缓存完成，准备开始升级", progressModuleVO);
        try {
            this.beforeUpgrade(paramVO,progressModuleVO);
            //判断线程是否终止，终止则向上抛异常
            RunnableUtils.isInterrupt(paramVO);
            this.doUpgrade(paramVO, progressModuleVO);
            progressModuleVO.setProgress(100f);
            progressService.setProgressModuleVO(paramVO, progressModuleVO);
            progressService.upgradeFinish(paramVO, progressModuleVO);
            logsService.setLogs(paramVO, "升级完成", progressModuleVO);
            this.clearAllCache();
        } catch (Exception e) {
            progressService.changeStatus2UpgradeError(paramVO, progressModuleVO);
            logsService.setLogs(paramVO, "升级异常 " + e.getMessage(), progressModuleVO);
            e.printStackTrace();
        }finally {
            if(islock){
                lockMap.put(dataSourceId,false);
            }
        }
        return null;
    }

    @Override
    public void doUpgrade(ParamVO paramVO, ProgressModuleVO progressModuleVO) throws Exception {
        String ver = progressModuleVO.getCurrentver();

        logsService.setLogs(paramVO, "版本:" + ver + " 开始升级,正在预处理数据", progressModuleVO);
        //初始化预处理导出实体
        File[] patchFiles = init(paramVO, progressModuleVO);
        if (progressModuleVO.getPatchNum() > 0) {
            //元数据导入前处理，根据时间戳排序
            List<File> fileSortList = MetaDataFileUtils.patchPreHandle(patchFiles, 1);

            logsService.setLogs(paramVO, "版本:" + ver + " 预处理数据完成 共计 " + progressModuleVO.getPatchNum() + " 个升级文件", progressModuleVO);

            IDataHandleService dataHandleService = null;

            logsService.setLogs(paramVO, "版本:" + ver + " 连接数据源", progressModuleVO);
            //取得数据源


            logsService.setLogs(paramVO, "版本:" + ver + " 开始处理元数据", progressModuleVO);
            if (ObjectUtil.isNotNull(fileSortList)) {
                for (File patchFile : fileSortList) {
                    progressModuleVO.setFilename(patchFile.getName());
                    //判断线程是否终止，终止则向上抛异常
                    RunnableUtils.isInterrupt(paramVO);

                    PatchFileVO patchFileVO = new PatchFileVO(patchFile);
                    String metaDataType = patchFileVO.getMetaDataType();
                    try {
                        dataHandleService = (IDataHandleService) AppServiceHelper.findBean(metaDataType + "DataHandleService");
                    } catch (HDRuntimeException e) {
                        dataHandleService = commonDataHandleService;
                    }
                    //逐文件升级
                    dataHandleService.metaDataUpgradeHandle(patchFile, paramVO, progressModuleVO);
                }
            }

            logsService.setLogs(paramVO, "版本:" + ver + " 元数据升级完成", progressModuleVO);
            this.clearAllCache();
            dbHandleService.createTable(paramVO, paramVO.getDataSourceId(), progressModuleVO);

            logsService.setLogs(paramVO, "版本:" + ver + " 升级完成", progressModuleVO);
        } else {
            logsService.setLogs(paramVO, "版本:" + ver + " 不存在升级文件，升级结束", progressModuleVO);
        }
    }


    @Override
    public void clearAllCache(){
        Collection<String> names=  cacheManager.getCacheNames();
        for(String name:names){
            cacheManager.getCache(name).clear();
        }
    }

    private File[] init(ParamVO paramVO, ProgressModuleVO progressModuleVO) throws IOException {
        HashSet<String> tableSet = new HashSet<>();

        File[] patchFiles = null;
        File rootDir = copyGitRoot2UpgradeRoot(paramVO);

        String version = paramVO.getVersion();
        String currentVer = progressModuleVO.getCurrentver();
        Long lastts = progressModuleVO.getLastfilets() == null ? 1546272000000L : progressModuleVO.getLastfilets();

        patchFiles = rootDir.listFiles(new PatchFileFilter(currentVer, version, lastts, tableSet));

        Integer tableName = tableSet.size();

        Integer patchNum = patchFiles.length + tableName;
        Float pec = 100f / patchNum;

        //初始化模块过程实体
        progressModuleVO.setPatchNum(patchNum);
        progressModuleVO.setPct(pec);
        progressService.setProgressModuleVO(paramVO, progressModuleVO);

        return patchFiles;
    }


    private File copyGitRoot2UpgradeRoot(ParamVO paramVO) throws IOException {

        String project = paramVO.getProject();
        String module = paramVO.getModule();
        String version = paramVO.getVersion();

        File gitDir = new File(GITROOT + File.separator + project + File.separator + module + File.separator + version);
        File upgradeDir = new File(PATCHROOT + File.separator + project + File.separator + module + File.separator + version);

        FileUtils.deleteDirectory(upgradeDir);
        FileUtils.copyDirectory(gitDir, upgradeDir);
        return upgradeDir;
    }

    @Override
    public void beforeUpgrade(ParamVO paramVO, ProgressModuleVO progressModuleVO) throws IOException, GitAPIException{

    }
}
