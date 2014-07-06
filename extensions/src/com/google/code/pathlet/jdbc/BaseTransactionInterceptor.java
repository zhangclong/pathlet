package com.google.code.pathlet.jdbc;

import java.lang.reflect.Method;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.support.CallbackPreferringPlatformTransactionManager;
import org.springframework.transaction.support.TransactionCallback;

import com.google.code.pathlet.config.anno.ContainerIn;
import com.google.code.pathlet.core.PathletContainer;
import com.google.code.pathlet.core.ProceedingJoinPoint;
import com.google.code.pathlet.util.ValueUtils;

/**
 * 
 * extends the {@link org.springframework.transaction.interceptor.TransactionInterceptor} class, 
 * To use some protected the spring TransactionInterceptor method into Pathlet. 
 * 
 * @author Charlie Zhang
 * @see com.google.code.pathlet.jdbc.AnnoTransactionInterceptor
 * @see com.google.code.pathlet.jdbc.ConfTransactionInterceptor
 * 
 *
 */
public abstract class BaseTransactionInterceptor extends TransactionInterceptor {

	private static final long serialVersionUID = -3476266809913371712L;
	
	@ContainerIn
	private PathletContainer container;
		
	private PlatformTransactionManager transactionManager;
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	public PlatformTransactionManager getTransactionManager() {
		return this.transactionManager;
	}
	
	public abstract TransactionAttribute getTransactionAttribute(Method method, Class<?> targetClass);
	
	public Object around(final ProceedingJoinPoint jp) throws Throwable {
		Class targetClass = (jp.getTarget() != null ? jp.getTarget().getClass() : null);
		
		// If the transaction attribute is null, the method is non-transactional.
		//final TransactionAttribute txAttr = getTransactionAttributeSource().getTransactionAttribute(aj.getMethod(), targetClass);
		final TransactionAttribute txAttr = getTransactionAttribute(jp.getMethod(), targetClass);
		retrieveTxManager(txAttr);
		
		final String joinpointIdentification = jp.getMethod().getDeclaringClass().getName() + "." + jp.getMethod().getName();
		
		if (txAttr == null || !(getTransactionManager() instanceof CallbackPreferringPlatformTransactionManager)) {
			// Standard transaction demarcation with getTransaction and commit/rollback calls.
			TransactionInfo txInfo = createTransactionIfNecessary(getTransactionManager(), txAttr, joinpointIdentification);
			Object retVal = null;
			try {
				// This is an around advice: Invoke the next interceptor in the chain.
				// This will normally result in a target object being invoked.
				retVal = jp.proceed();
			}
			catch (Throwable ex) {
				// target invocation exception
				completeTransactionAfterThrowing(txInfo, ex);
				throw ex;
			}
			finally {
				cleanupTransactionInfo(txInfo);
			}
			commitTransactionAfterReturning(txInfo);
			
			return retVal;
		} else {
			// It's a CallbackPreferringPlatformTransactionManager: pass a TransactionCallback in.
			try {
				Object result = ((CallbackPreferringPlatformTransactionManager) getTransactionManager()).execute(txAttr,
					new TransactionCallback() {
						public Object doInTransaction(TransactionStatus status) {
							TransactionInfo txInfo = prepareTransactionInfo(getTransactionManager(),txAttr, joinpointIdentification, status);
							try {
								return jp.proceed();
							} catch (Throwable ex) {
								if (txAttr.rollbackOn(ex)) {
									// A RuntimeException: will lead to a rollback.
									if (ex instanceof RuntimeException) {
										throw (RuntimeException) ex;
									} else {
										throw new ThrowableHolderException(ex);
									}
								} else {
									// A normal return value: will lead to a commit.
									return new ThrowableHolder(ex);
								}
							} finally {
								cleanupTransactionInfo(txInfo);
							}
						}
					});
	
				// Check result: It might indicate a Throwable to rethrow.
				if (result instanceof ThrowableHolder) {
					throw ((ThrowableHolder) result).getThrowable();
				} else {
					return result;
				}
			} catch (ThrowableHolderException ex) {
				throw ex.getCause();
			}
		}
	}
	
	private void retrieveTxManager(TransactionAttribute txAttr) {
		if(txAttr != null && ValueUtils.notEmpty(txAttr.getQualifier())) {
			String txManagerPath = txAttr.getQualifier();
			setTransactionManager((PlatformTransactionManager) this.container.getInstance(txManagerPath));
		}
	}

	/**
	 * Internal holder class for a Throwable, used as a return value
	 * from a TransactionCallback (to be subsequently unwrapped again).
	 */
	private static class ThrowableHolder {

		private final Throwable throwable;

		public ThrowableHolder(Throwable throwable) {
			this.throwable = throwable;
		}

		public final Throwable getThrowable() {
			return this.throwable;
		}
	}

	/**
	 * Internal holder class for a Throwable, used as a RuntimeException to be
	 * thrown from a TransactionCallback (and subsequently unwrapped again).
	 */
	private static class ThrowableHolderException extends RuntimeException {

		public ThrowableHolderException(Throwable throwable) {
			super(throwable);
		}

		public String toString() {
			return getCause().toString();
		}
	}
	
}
