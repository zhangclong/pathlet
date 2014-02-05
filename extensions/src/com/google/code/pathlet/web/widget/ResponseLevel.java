package com.google.code.pathlet.web.widget;

/**
 * The Level for response message.
 * 
 * @author Charlie Zhang

 */
public enum ResponseLevel {
	INFO,   //Common information prompt
	WARNING,//Warning information. Some information prompt may cause some problem or potential problem.
	ERROR,  //Business logic error
	FATAL;  //System level error. This kind of error could not be recovered.
}
