package com.wanda.ccs.sqlasm;



public class CriteriaParseException extends RuntimeException {

	private static final long serialVersionUID = -8432742145681286324L;

	public CriteriaParseException(){
		super();
	}

	public CriteriaParseException(String message){
		super(message);
	}
	
	public CriteriaParseException(Throwable cause){
		super(cause);
	}
	
	public CriteriaParseException(String message,Throwable cause){
		super(message,cause);
	}
	
}
