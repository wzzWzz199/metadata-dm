package com.hayden.hap.common.menu.entity;

import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.enumerate.FuncTypeEnum;
import com.hayden.hap.common.enumerate.MenuTypeEnum;
import lombok.Data;

/**
 * 用于构建菜单树
 * 
 * @author lianghua
 * @date 2015年11月25日
 */
@Data
public class MenuTreeVO implements Comparable<MenuTreeVO> {

	private static final String HTTPSTR = "http";

	private Long menuid;
	private String menuname;
	private String moduleCode;
	private String menucode;
	private Long parentid;
	private String urlStr;
	private String clientType;
	private Integer showOrder;
	private Integer is_buy;
	private String tartget;
	private String menuicon;
	private String menutype;

	public MenuTreeVO() {
	}

	public MenuTreeVO(MenuVO menuVO) {
		this.menuid = menuVO.getMenuid();
		this.menuname = menuVO.getMenuname();
		this.moduleCode = menuVO.getString("modulecode");
		this.menucode = menuVO.getMenucode();
		this.parentid = menuVO.getParentid();
		// this.urlStr = menuVO.getMenuinfo();
		this.clientType = menuVO.getMenuclienttype();
		this.showOrder = menuVO.getMenuorder();
		this.is_buy = menuVO.getIs_buy();
		this.setMenuicon(menuVO.getMenuicon());
		this.menutype = menuVO.getMenutype();

		if (ObjectUtil.isTrue(menuVO.getIsleaf())) {
			String menutype = menuVO.getMenutype();
			String functype = menuVO.getString("functype");
			String funcinfo = menuVO.getString("func_info");
			String funccode = menuVO.getFunc_code();
			String menuinfo = menuVO.getMenuinfo();
			String contextPath = menuVO.getString("contextPath");
			if (contextPath==null) {
				contextPath="";
			}
			if (MenuTypeEnum.FUNC.getId().equals(menutype) || MenuTypeEnum.REPORT.getId().equals(menuVO.getMenutype())) {
				if (FuncTypeEnum.FORM.getId().equals(functype) || FuncTypeEnum.CUSTOM.getId().equals(functype)
						|| FuncTypeEnum.REPORT.getId().equals(functype)) {

					this.setUrlStr(funccode);
				} else if (FuncTypeEnum.URL.getId().equals(functype)) {
					if (funcinfo != null && !funcinfo.startsWith(HTTPSTR)) {
						this.setUrlStr(funccode);
					} else {
						this.setUrlStr(funcinfo);
					}
				}
			} else if (MenuTypeEnum.URL.getName().equals(menutype)) {
				if (menuinfo != null && !menuinfo.startsWith(HTTPSTR)) {
					this.setUrlStr(contextPath + menuinfo);
				} else {
					this.setUrlStr(menuinfo);
				}
			}
		}
	}

	public MenuTreeVO(MenuMVO menuVO) {
		this.menuid = menuVO.getMenuid();
		this.menuname = menuVO.getMenuname();
		this.moduleCode = menuVO.getString("modulecode");
		this.menucode = menuVO.getMenucode();
		this.parentid = menuVO.getParentid();
		this.showOrder = menuVO.getMenuorder();
		this.is_buy = menuVO.getIs_buy();
		this.menutype = menuVO.getMenutype();
		if (ObjectUtil.isTrue(menuVO.getIsleaf())) {
			String menutype = menuVO.getMenutype();
			String functype = menuVO.getString("functype");
			String funcinfo = menuVO.getString("func_info");
			String funccode = menuVO.getFunc_code();
			String menuinfo = menuVO.getMenuinfo();
			String contextPath = menuVO.getString("contextPath");

			if (MenuTypeEnum.FUNC.getId().equals(menutype) || MenuTypeEnum.REPORT.getId().equals(menuVO.getMenutype())) {
				if (FuncTypeEnum.FORM.getId().equals(functype) || FuncTypeEnum.CUSTOM.getId().equals(functype)
						|| FuncTypeEnum.REPORT.getId().equals(functype)) {

					this.setUrlStr(funccode);
				} else if (FuncTypeEnum.URL.getId().equals(functype)) {
					if (funcinfo != null && !funcinfo.startsWith(HTTPSTR)) {
						this.setUrlStr(funccode);
					} else {
						this.setUrlStr(funcinfo);
					}
				}
			} else if (MenuTypeEnum.URL.getName().equals(menutype)) {
				if (menuinfo != null && !menuinfo.startsWith(HTTPSTR)) {
					this.setUrlStr(contextPath + menuinfo);
				} else {
					this.setUrlStr(menuinfo);
				}
			}
		}
	}

    @Override
	public int compareTo(MenuTreeVO o) {
		return this.getShowOrder() - o.getShowOrder();
	}

}
