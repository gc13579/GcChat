package com.GcChatServer.dao.impl;

import java.sql.SQLException;
import java.util.List;

import com.GcChatServer.Mapper.NewChatMapper;
import com.GcChatServer.Mapper.RecentChatMapper;
import com.GcChatServer.Util.JDBCTemplate;
import com.GcChatServer.dao.RecentChatDao;
import com.GcChatServer.enity.NewChat;
import com.GcChatServer.enity.RecentChat;

public class RecentChatDaoImpl implements RecentChatDao {
	JDBCTemplate<RecentChat> temp = new JDBCTemplate<RecentChat>();

	// 添加记录
	@Override
	public void insertRecentChat(RecentChat recentChat) {
		StringBuffer sql = new StringBuffer().append(" insert into ")
				.append(" 	t_recent_chat ")
				.append(" 	(id,sender_id,reciever_id) ").append(" values ")
				.append(" 	(null,?,?),(null,?,?) ");
		try {
			temp.insert(sql.toString(), recentChat.getSenderId(),
					recentChat.getRecieverId(), recentChat.getRecieverId(),
					recentChat.getSenderId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 获取所有记录
	@Override
	public List<RecentChat> selectAllRecentChats() {
		StringBuffer sql = new StringBuffer().append(" select ")
				.append(" 	id,sender_id,reciever_id ").append(" from ")
				.append(" 	t_recent_chat ");

		return temp.selectAll(new RecentChatMapper(), sql.toString());
	}

	// 根据接收方id查询记录
	@Override
	public List<RecentChat> selectRecentChatsByRecieverId(Integer recieverId) {
		StringBuffer sql = new StringBuffer().append(" select ")
				.append(" 	id,sender_id,reciever_id ").append(" from ")
				.append(" 	t_recent_chat ").append(" where ")
				.append(" 	reciever_id = ? ");
		List<RecentChat> list = temp.selectAll(new RecentChatMapper(),
				sql.toString(), recieverId);
		return list;
	}

	// 删除好友时，删除最近联系人
	@Override
	public void deleteRecentChatByTwoSidesId(Integer selfId, Integer friendId) {
		StringBuffer sql = new StringBuffer().append(" delete from ")
				.append(" 	t_recent_chat ").append(" where ")
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
