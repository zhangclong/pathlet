package com.google.code.pathlet.jdbc;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.google.code.pathlet.core.ProceedingJoinPoint;

public class TransactionInterceptor {
	
	private PlatformTransactionManager transactionManager;
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	
	public Object aroundReadonly(ProceedingJoinPoint aj) throws Throwable {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		def.setReadOnly(true);
		TransactionStatus status = transactionManager.getTransaction(def);
		boolean rollbacked = false;
		try {
			//TODO for debug trace
			//System.out.println("~~~~TransactionInterceptor-readonly transaction for path=" + aj.getResource().getPath()
			//		+ ", method=" + aj.getMethod().getName());
			return aj.proceed();
		}
		catch (Exception ex) {
			transactionManager.rollback(status);
			rollbacked = true;
			throw ex;
		}finally {
			if(rollbacked == false) {
				transactionManager.commit(status);
			}
		}
	}
	

	public Object aroundWritable(ProceedingJoinPoint aj) throws Throwable {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		def.setReadOnly(false);
		TransactionStatus status = transactionManager.getTransaction(def);
		boolean rollbacked = false;
		try {
			//TODO for debug trace
			//System.out.println("~~~~TransactionInterceptor-writeable transaction for path=" + aj.getResource().getPath()
			//		+ ", method=" + aj.getMethod().getName());
			return aj.proceed();
		}
		catch (Exception ex) {
			//TODO for debug trace
			transactionManager.rollback(status);
			rollbacked = true;
			throw ex;
		}
		finally {
			if(rollbacked == false) {
				transactionManager.commit(status);
			}
		}
	}
	
}
