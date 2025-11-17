package com.hayden.hap.common.utils.action;

import com.hayden.hap.dbop.exception.HDRuntimeException;
import com.hayden.hap.common.utils.ClassUtils;
import com.hayden.hap.common.utils.action.annotation.BusinessAction;
import com.hayden.hap.common.utils.action.annotation.BusinessActionItem;
import com.hayden.hap.common.utils.action.entity.ActionResult;
import com.hayden.hap.common.utils.properties.ProjectIdentifyUtil;
import com.hayden.hap.common.utils.tuple.TupleUtils;
import com.hayden.hap.common.utils.tuple.TwoTuple;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName ActionObserverRegister
 * @Description 业务扩展入口工具类
 * @Author zhangfeng
 * @Date 2020-04-13 10:50
 **/
public class BusinessActionUtil {

    private static final Logger logger = LoggerFactory.getLogger(BusinessActionUtil.class);

    private Map<String, TwoTuple<Method,Class<?>>> map = new HashMap<>();

    public BusinessActionUtil() {
        Set<Class<?>> classes = ClassUtils.getClassByAnnotation(BusinessAction.class);

        String currentProjectCode = ProjectIdentifyUtil.getProjectIdentify();

        for(Class<?> x : classes){
            BusinessAction businessAction = x.getAnnotation(BusinessAction.class);
            if(StringUtils.isEmpty(businessAction.supportProject())) {
                continue;
            }

            //只加载属于当前项目的扩展类
            if(!businessAction.supportProject().equalsIgnoreCase(currentProjectCode)) {
                continue;
            }

            Class<?> parentClass = businessAction.extendClass();
            Method[] methods = x.getMethods();
            for (Method method : methods) {
                BusinessActionItem item = method.getAnnotation(BusinessActionItem.class);
                if (item == null)
                    continue;
                String actionCode = item.actionCode();
                String key = parentClass.getName()+"#"+actionCode;
                map.put(key,TupleUtils.tuple(method,x));
            }
        }
    }

    public static BusinessActionUtil getInstance() {
        return A.B;
    }

    private static class A {
        private static final BusinessActionUtil B = new BusinessActionUtil();
    }

    /**
     * 执行业务操作
     * @param actionCode 操作编码
     * @param clazz
     * @param param
     */
    public ActionResult doAction(String actionCode, Class<?> clazz, Object... param) {
        String key = clazz.getName()+"#"+actionCode;
        return doAction(key,param);
    }

    private ActionResult doAction(String key, Object... param) {

        TwoTuple<Method,Class<?>> tuple = map.get(key);

        if(tuple==null) {
            logger.info(" there is no business action for :"+key);
            return new ActionResult(null);
        }

        try {
            Object action = tuple._2().newInstance();
            Object result = tuple._1().invoke(action,param);
            return new ActionResult(result);
        }catch (InvocationTargetException e) {
            logger.error(e.getMessage(), e);
            throw new HDRuntimeException(e.getTargetException().getMessage());
        } catch (Exception e){
            logger.error(e.getMessage(), e);
            throw new HDRuntimeException(e);
        }
    }
}
