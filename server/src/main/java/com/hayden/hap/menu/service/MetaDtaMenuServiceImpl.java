package com.hayden.hap.menu.service;


import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.menu.entity.EnvMenuVO;
import com.hayden.hap.menu.entity.ProjectMenuVO;
import com.hayden.hap.menu.itf.IMetaDtaMenuService;
import com.hayden.hap.meta.dataSource.DataSourceCreator;
import com.hayden.hap.meta.dataSource.entity.EnvConfVO;
import com.hayden.hap.meta.dataSource.entity.ProjectConfigVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/2 18:01
 */
@Service("metaDtaMenuServiceImpl")
public class MetaDtaMenuServiceImpl implements IMetaDtaMenuService {
    @Autowired
    DataSourceCreator dataSourceCreator;


    @Override
    public List<ProjectMenuVO> getProjectsMenu() throws UnknownHostException, HDException {
        List<ProjectConfigVO> projectConfigVOS = dataSourceCreator.getProjectList();
        List<ProjectMenuVO> projectMenuVOS = new ArrayList<>();
        InetAddress address = InetAddress.getLocalHost();

        for (ProjectConfigVO projectConfigVO : projectConfigVOS) {
            List<EnvMenuVO> envMenuVOS = new ArrayList<>();

            ProjectMenuVO projectMenuVO = new ProjectMenuVO();
            projectMenuVO.setCode(projectConfigVO.getCode());
            projectMenuVO.setName(projectConfigVO.getName());
            projectMenuVO.setEnvs(envMenuVOS);

            if (projectConfigVO.getEnvs() != null) {
                for (EnvConfVO envConfVO : projectConfigVO.getEnvs()) {
                    EnvMenuVO envMenuVO = new EnvMenuVO();
                    envMenuVO.setCode(envConfVO.getCode());
                    envMenuVO.setName(envConfVO.getName());
                    if (envConfVO.getUrl().indexOf("?")>-1){
                        envMenuVO.setDataserver(envConfVO.getUrl().substring(0, envConfVO.getUrl().indexOf("?")));
                    }else{
                        envMenuVO.setDataserver(envConfVO.getUrl());
                    }
                    envMenuVO.setAppserver(address.getHostAddress());
                    envMenuVOS.add(envMenuVO);
                }
            }
            projectMenuVOS.add(projectMenuVO);
        }
        return projectMenuVOS;
    }
}
