package com.GcChatServer.Mapper;

import java.sql.ResultSet;

import com.GcChatServer.enity.NewChat;

public class NewChatMapper implements RowMapper<NewChat> {

	@Override
	public NewChat mapperObject(ResultSet rs) throws Exception {
		NewChat newChat = new NewChat();
		newChat.setId(rs.getInt("id"));
		newChat.setSenderId(rs.getInt("sender_id"));
		newChat.setRecieverId(rs.getInt("reciever_id"));
		return newChat;
	}

}
