package com.hayden.hap.common.formmgr.service;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.formmgr.entity.Id2NameVO;
import com.hayden.hap.common.formmgr.inputconfig.QueryselectorInputConfigVO;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.user.constant.UserConstant;
import com.hayden.hap.common.user.entity.UserVO;
import com.hayden.hap.common.user.itf.IUserService;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 用户查询选择，id返名称策略
 * @author zhangfeng
 * @date 2016年9月7日
 */
public class UserId2NameStrategy extends AbstractId2NameStrategy{
	
	private static final String NICK_SUFFIX = "_nick";

	@Override
	public void assignName4single(AbstractVO abstractVO, Id2NameVO id2NameVO) {	
		addNickMap(id2NameVO.getInputConfigVO());
		super.assignName4single(abstractVO, id2NameVO);
	}

	@Override
	public void assignName4single(List<? extends AbstractVO> abstractVOs, Id2NameVO id2NameVO) {
		addNickMap(id2NameVO.getInputConfigVO());
		super.assignName4single(abstractVOs, id2NameVO);
	}
	
	@Override
	public void assignName4multiple(AbstractVO abstractVO, Id2NameVO id2NameVO) {
		addNickMap(id2NameVO.getInputConfigVO());
		super.assignName4multiple(abstractVO, id2NameVO);
	}
	
	@Override
	public void assignName4multiple(List<? extends AbstractVO> abstractVOs, Id2NameVO id2NameVO) {
		addNickMap(id2NameVO.getInputConfigVO());
		super.assignName4multiple(abstractVOs, id2NameVO);
	}
	
	@Override
	protected List<? extends AbstractVO> getVOList(String tableName,String uniqueColName,
			Collection<String> uniqueValues,Long tenantid) {
		IUserService userService = AppServiceHelper.findBean(IUserService.class);
		List<UserVO> userList = userService.findUser(tenantid);
		
		List<UserVO> result = new ArrayList<>();
		for(UserVO userVO : userList) {
			String colValue = userVO.getString(uniqueColName);
			if(isSuperMgrUser(uniqueColName, colValue)) {
				result.add(UserConstant.SYSTEM_USERVO);
				continue;
			}
			if(uniqueValues.contains(colValue)) {
				result.add(userVO);
			}
		}
		
		return result;
	}

	@Override
	protected AbstractVO getVO(String tableName,String uniqueColName,String uniqueValue,Long tenantid) {
		IUserService userService = AppServiceHelper.findBean(IUserService.class);
		
		List<UserVO> list=userService.findUser(tenantid);
		for(UserVO vo : list) {
			String colValue = vo.getString(uniqueColName);
			if(isSuperMgrUser(uniqueColName, colValue)) {
				return UserConstant.SYSTEM_USERVO;
			}
			if(uniqueValue.equals(vo.getString(uniqueColName))) {
				return vo;
			}
		}
		return null;
	}
	
	/**
	 * 是否虚拟超级管理员
	 * @param colName
	 * @param colValue
	 * @return 
	 * @author zhangfeng
	 * @date 2018年1月26日
	 */
	private boolean isSuperMgrUser(String colName, String colValue) {
		//如果用户编码是超级管理员编码，则是超级管理员
		if(UserConstant.USERCODE_COL.equals(colName) 
				&& UserConstant.SYSTEM_USER_CODE.equals(colValue))
			return true;
		
		//如果用户id是超级管理员id，则是超级管理员
		if(UserConstant.USERID_COL.equals(colName) 
				&& UserConstant.SYSTEM_USER_ID.toString().equals(colValue))
			return true;
		
		//否则不是
		return false;
	}
	
	/**
	 * 增加昵称的映射
	 * @param inputConfigVO 
	 * @author zhangfeng
	 * @date 2018年8月21日
	 */
	private void addNickMap(QueryselectorInputConfigVO inputConfigVO) {
		Map<String,String> map = inputConfigVO.getMap();
		if(map==null)
			return;
		String usernameFiled = map.get("username");
		if(StringUtils.isBlank(usernameFiled))
			return;
		if(map.containsKey("cu_nick"))
			return;
		map.put("cu_nick", usernameFiled+NICK_SUFFIX);
	}
}
