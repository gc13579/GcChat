package com.GcChatServer.exception;

public class UserExistedException extends Exception {

	public UserExistedException() {
		super();
	}

	public UserExistedException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserExistedException(String message) {
		super(message);
	}

	public UserExistedException(Throwable cause) {
		super(cause);
	}

}
