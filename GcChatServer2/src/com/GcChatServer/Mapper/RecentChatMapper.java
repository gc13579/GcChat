package com.GcChatServer.Mapper;

import java.sql.ResultSet;

import com.GcChatServer.enity.RecentChat;

public class RecentChatMapper implements RowMapper<RecentChat> {

	@Override
	public RecentChat mapperObject(ResultSet rs) throws Exception {
		RecentChat recentChat = new RecentChat();
		recentChat.setId(rs.getInt("id"));
		recentChat.setSenderId(rs.getInt("sender_id"));
		recentChat.setRecieverId(rs.getInt("reciever_id"));
		return recentChat;
	}

}
