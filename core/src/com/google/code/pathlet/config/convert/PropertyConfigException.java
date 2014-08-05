package com.google.code.pathlet.config.convert;

import com.google.code.pathlet.config.ConfigException;

/**
 * 
 * Property configuration Exception
 * 
 * @author Charlie Zhang
 *
 */
public class PropertyConfigException extends ConfigException {

	private static final long serialVersionUID = -3879418135289629309L;

	public PropertyConfigException() {
		super();
	}

	public PropertyConfigException(String message, Throwable cause) {
		super(message, cause);
	}

	public PropertyConfigException(String message) {
		super(message);
	}

	public PropertyConfigException(Throwable cause) {
		super(cause);
	}

}