package com.hayden.hap.menu.entity;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/2 11:44
 */
@Data
public class ProjectMenuVO {
    private String code;
    private String name;
    private List<EnvMenuVO> envs;

}
