package com.GcChatServer.Mapper;

import java.sql.ResultSet;

public interface RowMapper<T> {
	public T mapperObject(ResultSet rs) throws Exception;
}