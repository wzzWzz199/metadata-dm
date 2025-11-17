package com.hayden.hap.common.formmgr.filter;

import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.config.entity.ConfigVO;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.tenant.utils.TenantUtil;
import com.hayden.hap.common.user.entity.UserVO;
import com.hayden.hap.common.utils.ConfigUtils;
import com.hayden.hap.msg.common.entity.MsgTypeEnum;
import com.hayden.hap.msg.mq.utils.MsgSenderUtil;
import com.hayden.hap.msg.push.entity.PushMsgDataVO;
import com.hayden.hap.msg.push.entity.PushSrcTypeEnum;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 清理线程变量过滤器
 * @author zhangfeng
 * @date 2017年2月24日
 */
public class ClearThreadDataFilter implements Filter{

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		TenantUtil.setCurrentDataTenantid(null);
		chain.doFilter(request, response);
		String uri = ((HttpServletRequest)request).getRequestURI();
		String userPositionUrls = ConfigUtils.getValueOfAdmin("userPositionUrls");
		String pub_currentPosition_time_interval = null;
		RedisTemplate template = (RedisTemplate) AppServiceHelper.findBean(RedisTemplate.class, "redisTemplate");
		IBaseService baseService = (IBaseService)AppServiceHelper.findBean(IBaseService.class, "baseService");
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam("conf_code", "pub_currentPosition_time_interval");
		dynaSqlVO.addWhereParam("tenantid", Integer.valueOf(1));
		dynaSqlVO.addWhereParam("conf_isenable", Integer.valueOf(1));
		VOSet<ConfigVO> voset = baseService.query(ConfigVO.class, dynaSqlVO);
		if (ObjectUtil.isNotEmpty(voset.getVoList()))
			pub_currentPosition_time_interval = ((ConfigVO)voset.getVoList().get(0)).getConf_value();
		String userSql = "select * from sy_user";
		if (userPositionUrls != null && userPositionUrls.contains("\""+uri+"\"")) {
			template.opsForValue().set("openUserGps", Integer.valueOf(1), 5L, TimeUnit.MINUTES);
			if (pub_currentPosition_time_interval == null || pub_currentPosition_time_interval.equals("0")) {
				String sql = "update sy_config set conf_value='10',conf_isenable=1 where conf_code='pub_currentPosition_time_interval' and tenantid=1";
				baseService.executeUpate(sql, "sy_config");
				PushMsgDataVO pushMsgDataVO = new PushMsgDataVO();
				pushMsgDataVO.setMessageTitle("开启收集GPS通知");
				pushMsgDataVO.setMessageType(MsgTypeEnum.PUSH.getCode());
				pushMsgDataVO.setSourceType(PushSrcTypeEnum.NOTICE.getCode());
				pushMsgDataVO.setMessage("pub_currentPosition_time_interval=10");
				pushMsgDataVO.setSendUserid(Long.valueOf(1000L));
				pushMsgDataVO.setSendTenantid(1L);
				pushMsgDataVO.setRecvUsers((List)baseService.executeQuery(UserVO.class, userSql, null).getVoList().stream()
						.map(x -> x.getUserid()).collect(Collectors.toList()));
				MsgSenderUtil.sendMsg(pushMsgDataVO);
			}
		} else {
			Object openUserGps = template.opsForValue().get("openUserGps");
			if (openUserGps == null && pub_currentPosition_time_interval != null && !pub_currentPosition_time_interval.equals("0")) {
				String sql = "update sy_config set conf_value='0' where conf_code='pub_currentPosition_time_interval' and tenantid=1";
				baseService.executeUpate(sql, "sy_config");
				PushMsgDataVO pushMsgDataVO = new PushMsgDataVO();
				pushMsgDataVO.setMessageTitle("关闭收集GPS通知");
				pushMsgDataVO.setMessageType(MsgTypeEnum.PUSH.getCode());
				pushMsgDataVO.setSourceType(PushSrcTypeEnum.NOTICE.getCode());
				pushMsgDataVO.setMessage("pub_currentPosition_time_interval=0");
				pushMsgDataVO.setSendUserid(Long.valueOf(1000L));
				pushMsgDataVO.setSendTenantid(1L);
				pushMsgDataVO.setRecvUsers((List)baseService.executeQuery(UserVO.class, userSql, null).getVoList().stream()
						.map(x -> x.getUserid()).collect(Collectors.toList()));
				MsgSenderUtil.sendMsg(pushMsgDataVO);
			}
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
