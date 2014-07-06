package com.google.code.pathlet.jdbc;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.NoRollbackRuleAttribute;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;

/**
 * Pathlet AOP interceptor for declarative transaction
 * management using the common Spring transaction infrastructure.
 * ({@link org.springframework.transaction.PlatformTransactionManager}).
 * Supporting the Pathlet property configuration to control the transaction options.
 * 
 * @author Charlie Zhang
 *
 */
public class ConfTransactionInterceptor extends BaseTransactionInterceptor {
	
	private static final long serialVersionUID = 380535429510069680L;
	
	private int propagationBehavior;
	
	private boolean readOnly;
	
	private List<String> rollbackFor;
	
	private List<String> noRollbackFor;
	
	private int timeout = TransactionDefinition.TIMEOUT_DEFAULT;
	
	private int isolationLevel = TransactionDefinition.ISOLATION_DEFAULT;
	
	private TransactionAttribute txAttribute = null; //Cache after the first parse the properties in this class.
	
	public void setPropagationBehavior(int propagationBehavior) {
		this.propagationBehavior = propagationBehavior;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public void setRollbackFor(List<String> rollbackFor) {
		this.rollbackFor = rollbackFor;
	}

	public void setNoRollbackFor(List<String> noRollbackFor) {
		this.noRollbackFor = noRollbackFor;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void setIsolationLevel(int isolationLevel) {
		this.isolationLevel = isolationLevel;
	}

	@Override
	public TransactionAttribute getTransactionAttribute(Method method,
			Class<?> targetClass) {
		if(this.txAttribute == null) {
			RuleBasedTransactionAttribute rbta = new RuleBasedTransactionAttribute();
			rbta.setPropagationBehavior(this.propagationBehavior);
			rbta.setIsolationLevel(this.isolationLevel);
			rbta.setTimeout(this.timeout);
			rbta.setReadOnly(this.readOnly);
			//rbta.setQualifier(ann.value()); //Skip by the transactionManager has been defined in this class.
			ArrayList<RollbackRuleAttribute> rollBackRules = new ArrayList<RollbackRuleAttribute>();
	
			if(this.rollbackFor != null && this.rollbackFor.size() > 0) {
				for (String rbRule : this.rollbackFor) {
					rollBackRules.add(new RollbackRuleAttribute(rbRule));
				}
			}
			
			if(this.noRollbackFor != null && this.noRollbackFor.size() > 0)
			for (String nrbRule : this.noRollbackFor) {
				rollBackRules.add(new NoRollbackRuleAttribute(nrbRule));
			}
			rbta.getRollbackRules().addAll(rollBackRules);
			return rbta;
		}
		else {
			return this.txAttribute;
		}
	}
	
	
}
