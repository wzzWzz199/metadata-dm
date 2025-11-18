package com.hayden.hap.service.upgrade.itf;

import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.entity.ParamVO;
import com.hayden.hap.common.formmgr.message.Message;

import com.hayden.hap.vo.upgrade.ProgressModuleVO;
import com.hayden.hap.vo.upgrade.ShowModuleVO;
import com.hayden.hap.vo.upgrade.ShowVersionVO;
import com.hayden.hap.vo.upgrade.UpgradeModuleVO;

import java.util.List;
import java.util.Map;

@IService("upgradeService")
public interface IUpgradeService {
    List<ShowModuleVO> getModuleList(ParamVO paramVO) throws Exception;

    List<ShowModuleVO> getModuListWithSync(ParamVO paramVO) throws Exception;

    Object getVersionList(ParamVO paramVO) throws Exception;

    Map<String, UpgradeModuleVO> getModuleMapfromDataBaseCheck(String project, String env) throws Exception;

    void getVersionList(ParamVO paramVO, String project, String currentVer, Long lastts, List<ShowVersionVO> showVersionVOS) throws HDException;

    Message upgrade(ParamVO paramVO) throws Exception;

    Map<String, UpgradeModuleVO> getModuleMapfromDataBase(String project, String env) throws HDException;

    ProgressModuleVO exchange2ProgressModuleVO(UpgradeModuleVO upgradeModuleVO);

    public Map<String, List<ShowModuleVO>> getShowVOsCache();

    Message stopUpgrade(ParamVO paramVO);
}
