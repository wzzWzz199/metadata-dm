package com.hayden.hap.dbop.transaction.annotation;

import com.hayden.hap.dbop.exception.HDCapturedException;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.dbop.db.util.ObjectUtil;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.NoRollbackRuleAttribute;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;

import java.lang.reflect.AnnotatedElement;
import java.util.List;

/**
 * 添加默认的回滚和不回滚异常
 * 对默认行为的事务，设置回滚HDException，不回滚HDCapturedExcepton
 * 对其它行为的事务，只设置回滚HDException
 * @author wangyi
 * @date 2018年3月20日
 */
@SuppressWarnings("serial")
public class CustomAnnotationTransactionAttributeSource extends AnnotationTransactionAttributeSource{
	protected TransactionAttribute determineTransactionAttribute(AnnotatedElement ae) {
        if (ae.getAnnotations().length > 0) {
        	TransactionAttribute attr = super.determineTransactionAttribute(ae);
            if (attr != null) {
            	int propagationBehavior = attr.getPropagationBehavior();
            	// 强制转成RuleBasedTransactionAttribute ，并设置
                RuleBasedTransactionAttribute ruleBaseAttr = (RuleBasedTransactionAttribute)attr;
                //只处理Propagation.REQUIRED和Propagation.REQUIRES_NEW
            	if(propagationBehavior == Propagation.REQUIRED.value() || 
            			propagationBehavior == Propagation.REQUIRES_NEW.value()){
            		//添加回滚HDException，不回滚HDCapturedException
            		List<RollbackRuleAttribute> rollbackRules = ruleBaseAttr.getRollbackRules();
            		//没有定义异常处理
                    if(ObjectUtil.isEmpty(rollbackRules)){
                    	//当不包含自定义的Exception时，添加默认HDException：
                        rollbackRules.add(new RollbackRuleAttribute(HDException.class));
                        rollbackRules.add(new NoRollbackRuleAttribute(HDCapturedException.class));
                    }else{
                    	//检查rollback和norollback规则是否都存在
                    	boolean isHaveRollbackRule = false;
                    	boolean isHaveNoRollbackRule = false;
                    	for(RollbackRuleAttribute rollbackRuleAttribute:rollbackRules){
                    		//当属于NoRollbackRuleAttribute时表示配置了norollback规则
                    		if(rollbackRuleAttribute instanceof NoRollbackRuleAttribute){
                    			isHaveNoRollbackRule = true;
                    		}else{
                    			isHaveRollbackRule = true;
                    		}
                    	}
                    	//没有配置时分别进行添加
                    	if(!isHaveRollbackRule){
                    		rollbackRules.add(new RollbackRuleAttribute(HDException.class));
                    	}
                    	if(!isHaveNoRollbackRule){
                    		rollbackRules.add(new NoRollbackRuleAttribute(HDCapturedException.class));
                    	}
                    }
            	}
                return attr;
            }
        }
        return null;
}
}
