package com.wanda.ccs.sqlasm;



public class CriteriaDefineException extends RuntimeException {

	private static final long serialVersionUID = -8432742145681286324L;

	public CriteriaDefineException(){
		super();
	}

	public CriteriaDefineException(String message){
		super(message);
	}
	
	public CriteriaDefineException(Throwable cause){
		super(cause);
	}
	
	public CriteriaDefineException(String message,Throwable cause){
		super(message,cause);
	}
	
}
