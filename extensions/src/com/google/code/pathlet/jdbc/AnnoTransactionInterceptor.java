package com.google.code.pathlet.jdbc;

import java.lang.reflect.Method;

import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;

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
public class AnnoTransactionInterceptor extends BaseTransactionInterceptor {

	private static final long serialVersionUID = 3543959677450028383L;
	
	private TransactionAttributeSource attributeSource = new AnnotationTransactionAttributeSource();
	
	@Override
	public TransactionAttributeSource getTransactionAttributeSource() {
		return this.attributeSource;
	}
	
	@Override
	public TransactionAttribute getTransactionAttribute(Method method,
			Class<?> targetClass) {
		
		return getTransactionAttributeSource().getTransactionAttribute(method, targetClass);
	}
	
}
