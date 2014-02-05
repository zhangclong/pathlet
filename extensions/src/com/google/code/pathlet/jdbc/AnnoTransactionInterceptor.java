package com.google.code.pathlet.jdbc;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;

import com.google.code.pathlet.config.anno.ContainerIn;
import com.google.code.pathlet.core.PathletContainer;
import com.google.code.pathlet.core.ProceedingJoinPoint;
import com.google.code.pathlet.util.ValueUtils;

/**
 * Pathlet AOP interceptor for declarative transaction
 * management using the common Spring transaction infrastructure.
 * ({@link org.springframework.transaction.PlatformTransactionManager}).
 * Supporting the {@link org.springframework.transaction.annotation.Transactional} using in the class.
 * <p>The default transaction manager is set by property "transactionManager" in this class. 
 * The <code>value</code> of <code>@Transactional</code> is the path of the transaction manager. For instance:<br/>
 *  <code>@Transactional(value="/myTransactionManager")</code> the instance of the path "/myTransactionManager" will be used.
 *
 * @author Charlie Zhang
 * @see com.google.code.pathlet.jdbc.ConfTransactionInterceptor
 */
public class AnnoTransactionInterceptor {
	
	@ContainerIn
	private PathletContainer container;

	private PlatformTransactionManager transactionManager;
	
	private TransactionAttributeSource annoAttributeSource = new AnnotationTransactionAttributeSource();;
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	/**
	 * get the PlatformTransactionManager instance.
	 * If the qualifier attribute has been defined in the TransactionAttribute, 
	 * the PlatformTransactionManager instance will be got from the container by the path specified in the qualifier.
	 * If the qualifier is empty, the default  this.transactionManager will be used. 
	 * 
	 * @param txAttr
	 * @return
	 */
	private PlatformTransactionManager retrieveTxManager(TransactionAttribute txAttr) {
		if(txAttr != null && ValueUtils.notEmpty(txAttr.getQualifier())) {
			String txManagerPath = txAttr.getQualifier();
			return (PlatformTransactionManager)this.container.getInstance(txManagerPath);
		}
		else {
			return this.transactionManager;
		}
	}
	
	
	
	public Object around(ProceedingJoinPoint aj) throws Throwable {
		boolean rollbacked = false;
		TransactionAttribute txAttr = this.annoAttributeSource.getTransactionAttribute(aj.getMethod(), aj.getTarget().getClass());
		if(txAttr != null) {
			//if the @Trasactional has not been found, wrap the method process by transactionManager.
			
			TransactionStatus status = transactionManager.getTransaction(txAttr);
			PlatformTransactionManager txManager = retrieveTxManager(txAttr);
			try {
				return aj.proceed();
			}
			catch (Throwable ex) {
				if (txAttr.rollbackOn(ex)) {
					txManager.rollback(status);
					rollbacked = true;
				}
	
				throw ex;
			}finally {
				if(rollbacked == false) {
					txManager.commit(status);
				}
			}
		}
		else {
			//if the @Trasactional has not been found in class on method, process directly
			return aj.proceed();
		}
	}

}
