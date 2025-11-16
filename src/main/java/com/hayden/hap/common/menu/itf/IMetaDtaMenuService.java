package com.hayden.hap.common.menu.itf;

import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.menu.entity.ProjectMenuVO;
import com.hayden.hap.common.spring.service.IService;

import java.net.UnknownHostException;
import java.util.List;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/2 18:00
 */
@IService("metaDataMenuService")
public interface IMetaDtaMenuService {
    /**
     * 获取菜单
     * @return
     * @throws UnknownHostException
     */
    List<ProjectMenuVO> getProjectsMenu() throws UnknownHostException, HDException;
}
