package com.GcChatServer.trans;

public interface Transation {
	public void begin();

	public void commit();

	public void rollback();
}
