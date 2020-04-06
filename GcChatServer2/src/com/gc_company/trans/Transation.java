package com.gc_company.trans;

public interface Transation {
	public void begin();

	public void commit();

	public void rollback();
}
