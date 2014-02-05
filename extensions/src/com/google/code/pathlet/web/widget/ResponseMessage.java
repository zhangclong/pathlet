package com.google.code.pathlet.web.widget;

/**
 * 
 * @author Charlie Zhang
 *
 */
public class ResponseMessage {
	
	private ResponseLevel level;
	
	private String message;
	
	public ResponseMessage(ResponseLevel level, String message) {
		this.level = level;
		this.message = message;
	}

	public ResponseLevel getLevel() {
		return level;
	}

	public String getMessage() {
		return message;
	}

}
