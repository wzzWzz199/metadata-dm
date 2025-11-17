package com.hayden.hap.upgrade.service;

import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.tableDef.entity.TableDefVO;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.entity.ParamVO;
import com.hayden.hap.common.formmgr.message.Message;
import com.hayden.hap.common.git.itf.IGitService;
import com.hayden.hap.db.dataSource.DataSourceCreator;
import com.hayden.hap.db.dataSource.itf.IDataSourceGeneratorService;
import com.hayden.hap.db.dataSource.itf.ISimpleJdbcTemplateSupportDao;
import com.hayden.hap.serial.JsonUtils;
import com.hayden.hap.upgrade.entity.*;
import com.hayden.hap.upgrade.enums.UpgradeEnum;
import com.hayden.hap.upgrade.itf.IDbHandleService;
import com.hayden.hap.upgrade.itf.IProgressService;
import com.hayden.hap.upgrade.itf.IUpgradeService;
import com.hayden.hap.upgrade.run.UpgradeRun;
import com.hayden.hap.upgrade.utils.MetaDataFileUtils;
import com.hayden.hap.upgrade.utils.RunnableUtils;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/4 18:14
 */
@Service("upgradeService")
public class UpgradeServiceImpl implements IUpgradeService {
    @Autowired
    private ISimpleJdbcTemplateSupportDao simpleJdbcTemplateSupportDao;
    @Autowired
    private IGitService gitService;
    @Autowired
    private DataSourceCreator dataSourceCreator;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTsakExecutor;
    @Autowired
    private IProgressService progressService;
    @Autowired
    private IDataSourceGeneratorService dataSourceGeneratorService;
    @Autowired
    private IDbHandleService dbHandleService;

    //补丁服务器根目录
    @Value("${META.GIT.ROOTPATH}")
    private String GITROOT;
    //升级目录
    @Value("${META.UPGRADE.ROOTPATH}")
    private String PATCHROOT;
    //模块名称字典配置数据
    @Value("${META.UPGRADE.MODULE}")
    private String MODULE;

    //模块名称字典map
    private Map<String, String> moduleMap = new HashMap<>();
    //升级模块列表缓存
    private Map<String, List<ShowModuleVO>> showVOsCache = new HashMap<>();


    @Override
    public List<ShowModuleVO> getModuListWithSync(ParamVO paramVO) throws Exception {
        String project = paramVO.getProject();
        String env = paramVO.getEnv();

        PullResult pullResult = null;
        try{
        if (!paramVO.getEnv().equals(UpgradeEnum.ENV_TYPE_PRO.getCode())) {
            pullResult = gitService.cloneOrPull();
        }}catch (JGitInternalException e){
           if(e.getCause().getClass().getName().equals("org.eclipse.jgit.errors.LockFailedException")){
               throw new HDException("其他用户正在同步git补丁目录,请稍等");
           }else {
               throw new HDException(e);
           }
        }

        //if (!showVOsCache.containsKey(project + env) || pullResult == null || !pullResult.getMergeResult().getMergeStatus().equals(MergeResult.MergeStatus.ALREADY_UP_TO_DATE)) {
            this.getModuleList(paramVO);
        //}
        return showVOsCache.get(project + env);
    }

    @Override
    public List<ShowModuleVO> getModuleList(ParamVO paramVO) throws Exception {

        String project = paramVO.getProject();
        String env = paramVO.getEnv();

        //从数据库取得当前项目环境已存储的模块信息
        Map<String, UpgradeModuleVO> moduleVOMap = getModuleMapfromDataBaseCheck(project, env);

        List<ShowModuleVO> showVOs = new ArrayList<>();
        List<UpgradeModuleVO> insertVOs = new ArrayList<>();

        //取得当前项目补丁目录
        File projectDir = new File(GITROOT + File.separator + project);
        if (projectDir.exists() && projectDir.isDirectory()) {
            //遍历模块目录
            for (File moduleDir : projectDir.listFiles()) {
                if (!moduleDir.isDirectory())
                    continue;

                String moduleCode = moduleDir.getName();
                UpgradeModuleVO moduleVO;
                //数据库中不存在此模块，则初始化模块vo
                if (!moduleVOMap.containsKey(moduleCode)) {
                    String moduleName = getModule(moduleCode);

                    moduleVO = new UpgradeModuleVO();

                    String dataSourceId = dataSourceCreator.getDataSourceId(paramVO.getProject(), paramVO.getEnv());
                    TableDefVO tableDefVO = simpleJdbcTemplateSupportDao.queryDetailedTableByTbname(moduleVO.getTableName(), dataSourceId);
                    Long pkValue = Long.parseLong(dataSourceGeneratorService.generate(dataSourceGeneratorService.getPkColGencode(tableDefVO), dataSourceId));

                    moduleVO.setId(pkValue);
                    moduleVO.setCode(moduleCode);
                    moduleVO.setName(moduleName != null ? moduleName : moduleCode);
                    moduleVO.setStatus(UpgradeEnum.MODULE_STATUS_UPGRADEABLE.getCode());
                    //将模块放入待插入列表
                    insertVOs.add(moduleVO);
                } else {
                    String key = project + env + moduleCode;
                    if (progressService.hasProgressModuleVO(paramVO)) {
                        moduleVO = exchange2UpgradeModuleVO(progressService.getProgressModuleVO(paramVO));
                    } else {
                        moduleVO = moduleVOMap.get(moduleCode);
                    }
                }
                showVOs.add(exchange2ShowModuleVO(moduleVO));
            }
            insertModuleVOs(insertVOs, project, env);
        }
        //放入缓存
        showVOsCache.put(project + env, showVOs);
        return showVOs;
    }

    /**
     * 获取数据库中储存的升级模块实体
     *
     * @param project
     * @param env
     * @return
     * @throws HDException
     */
    @Override
    public Map<String, UpgradeModuleVO> getModuleMapfromDataBase(String project, String env) throws HDException {

        Map<String, UpgradeModuleVO> moduleVOMap = new HashMap<>();

        String dataSourceId = dataSourceCreator.getDataSourceId(project, env);
        dataSourceCreator.getDataSource(dataSourceId);

        VOSet<UpgradeModuleVO> vOSet = simpleJdbcTemplateSupportDao.query(new UpgradeModuleVO(), new DynaSqlVO(), dataSourceId);
        if (ObjectUtil.isNotNull(vOSet) && ObjectUtil.isNotNull(vOSet.getVoList())) {
            for (UpgradeModuleVO upgradeModuleVO : vOSet.getVoList()) {
                moduleVOMap.put(upgradeModuleVO.getCode(), upgradeModuleVO);
            }
        }

        return moduleVOMap;
    }


    @Override
    public Map<String, UpgradeModuleVO> getModuleMapfromDataBaseCheck(String project, String env) throws Exception {

        Map<String, UpgradeModuleVO> moduleVOMap = new HashMap<>();

        String dataSourceId = dataSourceCreator.getDataSourceId(project, env);
        dataSourceCreator.getDataSource(dataSourceId);

        createTableisNotExist(dataSourceId,project,env);

        VOSet<UpgradeModuleVO> vOSet = simpleJdbcTemplateSupportDao.query(new UpgradeModuleVO(), new DynaSqlVO(), dataSourceId);
        if (ObjectUtil.isNotNull(vOSet) && ObjectUtil.isNotNull(vOSet.getVoList())) {
            for (UpgradeModuleVO upgradeModuleVO : vOSet.getVoList()) {
                moduleVOMap.put(upgradeModuleVO.getCode(), upgradeModuleVO);
            }
        }

        return moduleVOMap;
    }


    public void createTableisNotExist(String dataSourceId,String project, String env) throws Exception {
        ParamVO paramVO=new ParamVO();
        paramVO.setProject(project);
        paramVO.setEnv(env);

        DynaSqlVO dynaSqlVO=new DynaSqlVO();
        dynaSqlVO.setWhereClause("table_code='mgr_upgrade_module'");

        VOSet<TableDefVO> vOSet =simpleJdbcTemplateSupportDao.query(new TableDefVO(), dynaSqlVO, dataSourceId);
        if(vOSet==null||vOSet.getVoList()==null||vOSet.getVoList().size()==0){
            dbHandleService.initDataBase(paramVO);
        }
    }

    /**
     * 插入数据库中不存在的模块实体
     *
     * @param volist
     * @param project
     * @param env
     * @throws HDException
     */
    private void insertModuleVOs(List<UpgradeModuleVO> volist, String project, String env) throws HDException {
        String dataSourceId = dataSourceCreator.getDataSourceId(project, env);
        dataSourceCreator.getDataSource(dataSourceId);

        simpleJdbcTemplateSupportDao.insertBatch(volist, new DynaSqlVO(), dataSourceId);
    }

    /**
     * 数据库实体转展示实体
     *
     * @param upgradeModuleVO
     * @return
     */
    private ShowModuleVO exchange2ShowModuleVO(UpgradeModuleVO upgradeModuleVO) {
        ShowModuleVO showModuleVO = new ShowModuleVO();
        showModuleVO.setCode(upgradeModuleVO.getCode());
        showModuleVO.setName(upgradeModuleVO.getName());
        showModuleVO.setCurrentver(upgradeModuleVO.getCurrentver());
        showModuleVO.setDatetime(upgradeModuleVO.getDatetime());
        showModuleVO.setPerson(upgradeModuleVO.getPerson());
        showModuleVO.setStatus(upgradeModuleVO.getStatus());
        return showModuleVO;
    }

    /**
     * 过程实体转数据库实体
     *
     * @param progressModuleVO
     * @return
     */
    private UpgradeModuleVO exchange2UpgradeModuleVO(ProgressModuleVO progressModuleVO) {
        UpgradeModuleVO upgradeModuleVO = new UpgradeModuleVO();
        upgradeModuleVO.setCode(progressModuleVO.getCode());
        upgradeModuleVO.setName(progressModuleVO.getName());
        upgradeModuleVO.setCurrentver(progressModuleVO.getCurrentver());
        upgradeModuleVO.setProductver(progressModuleVO.getProductver());
        upgradeModuleVO.setProlastfilets(progressModuleVO.getProlastfilets());
        upgradeModuleVO.setStatus(progressModuleVO.getStatus());
        upgradeModuleVO.setProgress(String.valueOf(progressModuleVO.getProgress()));
        upgradeModuleVO.setPerson(progressModuleVO.getPerson());
        upgradeModuleVO.setDatetime(progressModuleVO.getDatetime());
        upgradeModuleVO.setLastfilets(progressModuleVO.getLastfilets());
        return upgradeModuleVO;
    }

    /**
     * 获取模块中文
     *
     * @param key
     * @return
     * @throws HDException
     */
    private String getModule(String key) throws HDException {
        if (!moduleMap.containsKey(key)) {
            moduleMap = (Map<String, String>) JsonUtils.parse(MODULE, HashMap.class);
        }
        return moduleMap.get(key);
    }

    /**
     * 获取版本及补丁详情
     *
     * @param paramVO
     * @return
     * @throws HDException
     */
    @Override
    public void getVersionList(ParamVO paramVO, String project, String currentVer, Long lastts, List<ShowVersionVO> showVersionVOS) {
        String module = paramVO.getModule();

        //取得当前模块补丁目录
        File moduleDir = new File(GITROOT + File.separator + project + File.separator + module);
        if (moduleDir.exists() && moduleDir.isDirectory()) {

            for (File versionDir : MetaDataFileUtils.versionPreHandle(moduleDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return Pattern.matches("^\\d+(\\.\\d+)+(\\.\\d+)+(\\.\\d+)", pathname.getName());
                }
            }))) {
                //得到模块下的版本号
                String version = versionDir.getName();
                //如果当前版本为空，或者版本大于等于当前版本
                if (currentVer == null || version.compareTo(currentVer) >= 0) {
                    ShowVersionVO showVersionVO = new ShowVersionVO();
                    List<String> patchList = new ArrayList<>();
                    List<String> hispatchList = new ArrayList<>();
                    //只有目录才会继续过滤杂项文件
                    if (!versionDir.isDirectory())
                        continue;

                    File[] files = versionDir.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File pathname) {
                            PatchFileVO patchFileVO = new PatchFileVO(pathname);
                            return patchFileVO.isCheck();
                        }
                    });
                    for (File patchFile : MetaDataFileUtils.patchPreHandle(files, -1)) {
                        PatchFileVO patchFileVO = new PatchFileVO(patchFile);
                        if (patchFileVO.isCheck()) {
                            if (currentVer == null || version.compareTo(currentVer) > 0 || patchFileVO.getTimestamp().compareTo(lastts) > 0) {
                                patchList.add(patchFile.getName());
                            } else {
                                hispatchList.add(patchFile.getName());
                            }
                        }
                    }

                    showVersionVO.setVer(version);
                    if (currentVer != null && currentVer.equals(version) && patchList.size() > 0) {
                        showVersionVO.setStatus(UpgradeEnum.VERSION_STATUS_CHANGED.getCode());
                    } else if (currentVer != null && currentVer.equals(version)) {
                        showVersionVO.setStatus(UpgradeEnum.VERSION_STATUS_UPDATED.getCode());
                    } else {
                        showVersionVO.setStatus(UpgradeEnum.VERSION_STATUS_UNUPDATE.getCode());
                    }
                    showVersionVO.setPatchs(patchList);
                    showVersionVO.setHistorypatchs(hispatchList);
                    showVersionVOS.add(showVersionVO);
                }
            }
        }
    }

    @Override
    public Object getVersionList(ParamVO paramVO) throws Exception {
        String project = paramVO.getProject();
        String env = paramVO.getEnv();
        String module = paramVO.getModule();

        List<ShowVersionVO> projectVersionVOS = new ArrayList<>();
        List<ShowVersionVO> productVersionVOS = null;

        Map<String, List> versionMap = new HashMap<>();

        //取得数据库中模块实体map
        Map<String, UpgradeModuleVO> upgradeModuleVOMap = getModuleMapfromDataBase(project, env);
        //数据库包含当前模块，一般不存在不包含情况
        if (upgradeModuleVOMap.containsKey(module)) {
            //得到数据库中保存的当前版本及当前版本升级时间戳(最后一个补丁的时间戳)
            UpgradeModuleVO upgradeModuleVO = upgradeModuleVOMap.get(module);

            String currentVer = upgradeModuleVO.getCurrentver();
            Long lastts = upgradeModuleVO.getLastfilets() == null ? 1546272000000L : upgradeModuleVO.getLastfilets();
            this.getVersionList(paramVO, project, currentVer, lastts, projectVersionVOS);
            versionMap.put("proj", projectVersionVOS);

            if (env.equals(UpgradeEnum.ENV_TYPE_DEV.getCode())) {
                productVersionVOS=new ArrayList<>();
                String productver = upgradeModuleVO.getProductver();
                Long prolastfilets = upgradeModuleVO.getProlastfilets() == null ? 1546272000000L : upgradeModuleVO.getProlastfilets();
                this.getVersionList(paramVO, UpgradeEnum.PRODUCT_CODE.getCode(), productver, prolastfilets, productVersionVOS);
            }
            versionMap.put("prod", productVersionVOS);
        }
        return versionMap;
    }

    @Override
    public Message upgrade(ParamVO paramVO) throws Exception {
        ProgressModuleVO progressModuleVO = null;
        String project = paramVO.getProject();
        String env = paramVO.getEnv();
        String module = paramVO.getModule();
        synchronized (paramVO.getKey()) {
            if (progressService.hasProgressModuleVO(paramVO) && progressService.getProgressModuleVO(paramVO).getStatus().equals(UpgradeEnum.MODULE_STATUS_UPGRADING.getCode())) {
                return new Message("此模块正在升级请等待上次升级结束后再操作");
            }
            //修改状态
            Map<String, UpgradeModuleVO> upgradeModuleVOMap = getModuleMapfromDataBase(project, env);
            progressModuleVO = exchange2ProgressModuleVO(upgradeModuleVOMap.get(module));
            progressService.changeStatus2Upgrading(paramVO, progressModuleVO);
        }

        UpgradeRun upgradeRun = new UpgradeRun(threadPoolTsakExecutor, paramVO);
        RunnableUtils.setRunnable(paramVO, upgradeRun);
        //执行升级
        threadPoolTsakExecutor.execute(upgradeRun);
        return new Message("升级中");
    }


    /**
     * 展示实体转过程实体
     *
     * @param upgradeModuleVO
     * @return
     */
    @Override
    public ProgressModuleVO exchange2ProgressModuleVO(UpgradeModuleVO upgradeModuleVO) {
        ProgressModuleVO progressModuleVO = new ProgressModuleVO();
        progressModuleVO.setCode(upgradeModuleVO.getCode());
        progressModuleVO.setName(upgradeModuleVO.getName());
        progressModuleVO.setCurrentver(upgradeModuleVO.getCurrentver());
        progressModuleVO.setProductver(upgradeModuleVO.getProductver());
        progressModuleVO.setProlastfilets(upgradeModuleVO.getProlastfilets());
        progressModuleVO.setStatus(upgradeModuleVO.getStatus());
        progressModuleVO.setProgress(0f);
        progressModuleVO.setPerson(upgradeModuleVO.getPerson());
        progressModuleVO.setDatetime(upgradeModuleVO.getDatetime());
        progressModuleVO.setLastfilets(upgradeModuleVO.getLastfilets());
        return progressModuleVO;
    }

    public Map<String, List<ShowModuleVO>> getShowVOsCache() {
        return showVOsCache;
    }

    @Override
    public Message stopUpgrade(ParamVO paramVO) {
        return RunnableUtils.interrupt(paramVO);
    }
}
