package com.google.code.newpath.jdbc.example;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.google.code.pathlet.config.anno.Advisor;
import com.google.code.pathlet.config.anno.InstanceIn;
import com.google.code.pathlet.config.anno.Pointcut;
import com.google.code.pathlet.core.ProceedingJoinPoint;


/**
 * 
 * FIXME TODO By now I don't know the mechanism of the propagation in spring Transaction Manager? It's must be improved and tested!
 * 
 * @author Charlie Zhang
 *
 */
@Advisor
public class DbTransactionInterceptor {

//	@Around(includePaths = "/**/*Service", methods = "save*,set*,update*")
//	public Object around(ProceedingJoinPoint aj) throws Throwable {
//		System.out.println("~~~~OperationInterceptor~~~~ begin");
//		Object[] args = aj.getArguments();
//		args[0] = args[0] + "Zhang";  //Change direct from array reference.
//		
//		Object obj =  aj.proceed();
//		
//		System.out.println("~~~~OperationInterceptor~~~~ end argumentsSize=" + args.length + ", argument1=" + args[0] + " return=" + obj);
//		
//		//Object obj =  aj.proceed();
//		//Object[] o = aj.getArguments();
//		//System.out.println("record operation log: [object:"+aj.getTarget().getClass().getSimpleName()+"]  [function:"+aj.getMethod().getName()+"]  [info:"+o[0]+"]");
//		return obj + ",OperationInterceptorChanged!";
//	}
	
	
	@InstanceIn(path = "/transactionManager")
	private PlatformTransactionManager transactionManager;
	
	@Pointcut(includes = "/**/*Service", methods = "get*,query*,find*")
	public Object around(ProceedingJoinPoint aj) throws Throwable {
		
		
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		def.setReadOnly(true);
		TransactionStatus status = transactionManager.getTransaction(def);
		boolean rollbacked = false;
		try {
			return aj.proceed();
		}catch (RuntimeException ex) {
			transactionManager.rollback(status);
			rollbacked = true;
			throw ex;
		}finally {
			if(rollbacked == false) {
				transactionManager.commit(status);
			}
		}
	}
	
	
	@Pointcut(includes = "/**/*Service", methods = "save*,set*,update*")
	public Object aroundWritable(ProceedingJoinPoint aj) throws Throwable {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		def.setReadOnly(false);
		TransactionStatus status = transactionManager.getTransaction(def);
		boolean rollbacked = false;
		try {
			return aj.proceed();
		}catch (RuntimeException ex) {
			transactionManager.rollback(status);
			rollbacked = true;
			throw ex;
		}finally {
			if(rollbacked == false) {
				transactionManager.commit(status);
			}
		}
	}
	
	
}
