package com.GcChatServer.exception;

public class LoginFailException extends Exception {

	public LoginFailException() {
		super();
	}

	public LoginFailException(String message, Throwable cause) {
		super(message, cause);
	}

	public LoginFailException(String message) {
		super(message);
	}

	public LoginFailException(Throwable cause) {
		super(cause);
	}

}
