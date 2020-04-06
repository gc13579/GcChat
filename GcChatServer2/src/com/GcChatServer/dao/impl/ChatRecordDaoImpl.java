package com.GcChatServer.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.GcChatServer.Mapper.ChatRecordMapper;
import com.GcChatServer.Util.JDBCTemplate;
import com.GcChatServer.dao.ChatRecordDao;
import com.GcChatServer.enity.ChatRecord;

public class ChatRecordDaoImpl implements ChatRecordDao {
	JDBCTemplate<ChatRecord> temp = new JDBCTemplate<ChatRecord>();

	// 添加聊天记录
	@Override
	public void insertCharRecord(ChatRecord chatRecord) {
		StringBuffer sql = new StringBuffer()
				.append(" insert into ")
				.append(" 	t_chat_record ")
				.append(" 	(id,sender_id,sender_name,reciever_id,reciever_name,message,date) ")
				.append(" values ").append(" 	(null,?,?,?,?,?,?) ");
		try {
			temp.insert(sql.toString(), chatRecord.getSenderId(),
					chatRecord.getSenderPetName(), chatRecord.getRecieverId(),
					chatRecord.getRecieverPetName(), chatRecord.getMessage(),
					chatRecord.getDate());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 查询聊天记录
	@Override
	public ChatRecord selectLastChatRecordsByTwoSidesId(Integer senderId,
			Integer recieverId) {
		StringBuilder sql = new StringBuilder()
				.append(" select ")
				.append(" 	id,sender_id,sender_name,reciever_id,reciever_name,message,date ")
				.append(" from ").append(" 	t_chat_record ").append(" where ")
				.append(" 	id =  ").append(" 	(select ").append(" 		max(id) ")
				.append(" 	from ").append(" 		t_chat_record ")
				.append(" 	where ").append(" 		(reciever_id = ? ")
				.append(" 	and ").append(" 		sender_id = ?) ").append(" 	or ")
				.append(" 		(sender_id = ? ").append(" 	and ")
				.append(" 		reciever_id = ?)) ");
		return temp.selectOne(new ChatRecordMapper(), sql.toString(),
				recieverId, senderId, recieverId, senderId);
	}

	// 根据双方id,查询聊天记录
	@Override
	public List<ChatRecord> selectChatRecordsByTwoSidesId(Integer friendId,
			Integer selfId) {
		StringBuilder sql = new StringBuilder()
				.append(" select ")
				.append(" 	id,sender_id,sender_name,reciever_id,reciever_name,message,date ")
				.append(" from ").append(" 	t_chat_record ").append(" where ")
				.append(" 	(reciever_id = ? ").append(" 	and sender_id = ?) ")
				.append(" or ").append(" 	(reciever_id = ? ")
				.append(" 	and sender_id = ?) ");

		return temp.selectAll(new ChatRecordMapper(), sql.toString(), friendId,
				selfId, selfId, friendId);
	}

	// 删除好友时，聊天记录删除
	@Override
	public void deleteChatRecordByTwoSidesId(Integer friendId, Integer selfId) {
		StringBuffer sql = new StringBuffer().append(" delete from ")
				.append(" 	t_chat_record ").append(" where ")
				.append(" 	(reciever_id = ? ").append(" 	and sender_id = ?) ")
				.append(" or ").append(" 	(sender_id = ? ")
				.append(" 	and reciever_id = ?) ");
		try {
			temp.delete(sql.toString(), friendId, selfId, friendId, selfId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
