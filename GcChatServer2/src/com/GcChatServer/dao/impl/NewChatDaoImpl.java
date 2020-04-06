package com.GcChatServer.dao.impl;

import java.sql.SQLException;
import java.util.List;

import com.GcChatServer.Mapper.NewChatMapper;
import com.GcChatServer.Util.JDBCTemplate;
import com.GcChatServer.dao.NewChatDao;
import com.GcChatServer.enity.NewChat;

public class NewChatDaoImpl implements NewChatDao {
	JDBCTemplate<NewChat> temp = new JDBCTemplate<NewChat>();

	// 添加记录
	@Override
	public void insertNewChat(NewChat newChat) {
		StringBuffer sql = new StringBuffer().append(" insert into ")
				.append(" 	t_new_chat ")
				.append(" 	(id,sender_id,reciever_id ) ").append(" values ")
				.append(" 	(null,?,? ) ");
		try {
			temp.insert(sql.toString(), newChat.getSenderId(),
					newChat.getRecieverId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 查询所有记录
	@Override
	public List<NewChat> selectAllNewChats() {
		StringBuffer sql = new StringBuffer().append(" select ")
				.append(" 	id,sender_id,reciever_id ").append(" from ")
				.append(" 	t_new_chat ");
		return temp.selectAll(new NewChatMapper(), sql.toString());
	}

	// 根据id,删除记录
	@Override
	public void deleteNewChatBySenderAndRecieverId(Integer senderId,
			Integer recieverId) {
		StringBuffer sql = new StringBuffer().append(" delete from ")
				.append(" 	t_new_chat ").append(" where ")
				.append(" 	sender_id = ? ").append(" and ")
				.append(" 	reciever_id = ? ");
		try {
			temp.delete(sql.toString(), senderId, recieverId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 根据接收方id查询记录
	@Override
	public List<NewChat> selectNewChatsByRecieverId(Integer recieverId) {
		StringBuffer sql = new StringBuffer().append(" select ")
				.append(" 	id,sender_id,reciever_id  ").append(" from ")
				.append(" 	t_new_chat ").append(" where ")
				.append(" 	reciever_id = ? ");
		List<NewChat> list = temp.selectAll(new NewChatMapper(),
				sql.toString(), recieverId);
		return list;
	}

	// 删除好友时，删除下线联系人
	@Override
	public void deleteNewChatsByTwoSidesId(Integer selfId, Integer friendId) {
		StringBuffer sql = new StringBuffer().append(" delete from ")
				.append(" 	t_new_chat ").append(" where ")
				.append(" 	(reciever_id = ? ").append(" 	and sender_id = ?) ")
				.append(" or ").append(" 	(sender_id = ? ")
				.append(" 	and reciever_id = ?) ");
		try {
			temp.delete(sql.toString(), selfId, friendId, selfId, friendId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
