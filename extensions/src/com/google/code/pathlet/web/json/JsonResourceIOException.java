package com.google.code.pathlet.web.json;

import com.google.code.pathlet.core.exception.SysRuntimeException;


public class JsonResourceIOException extends SysRuntimeException {

	private static final long serialVersionUID = 4894215553603411833L;

	public JsonResourceIOException() {
		super();
	}


	public JsonResourceIOException(String message, Throwable cause) {
		super(message, cause);
	}

	public JsonResourceIOException(String message) {
		super(message);
	}

	public JsonResourceIOException(Throwable cause) {
		super(cause);
	}

}
