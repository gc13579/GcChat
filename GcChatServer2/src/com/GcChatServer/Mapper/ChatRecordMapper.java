package com.GcChatServer.Mapper;

import java.sql.ResultSet;

import com.GcChatServer.enity.ChatRecord;

public class ChatRecordMapper implements RowMapper<ChatRecord> {
	@Override
	public ChatRecord mapperObject(ResultSet rs) throws Exception {
		ChatRecord chatRecord = new ChatRecord();
		chatRecord.setId(rs.getInt("id"));
		chatRecord.setSenderId(rs.getInt("sender_id"));
		chatRecord.setSenderPetName(rs.getString("sender_name"));
		chatRecord.setRecieverId(rs.getInt("reciever_id"));
		chatRecord.setRecieverPetName(rs.getString("reciever_name"));
		chatRecord.setMessage(rs.getString("message"));
		chatRecord.setDate(rs.getString("date"));
		return chatRecord;
	}

}
