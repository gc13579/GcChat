package com.GcChatServer.dao.impl;

import java.sql.SQLException;
import java.util.List;
import com.GcChatServer.Mapper.FriendsRelationshipsMapper;
import com.GcChatServer.Util.JDBCTemplate;
import com.GcChatServer.dao.FriendRelationshipsDao;
import com.GcChatServer.enity.FriendRelationships;

public class FriendRelationshipsDaoImpl implements FriendRelationshipsDao {
	JDBCTemplate<FriendRelationships> temp = new JDBCTemplate<FriendRelationships>();

	// 查看所有好友
	@Override
	public List<FriendRelationships> selectAllFriendRelationships(Integer id) {
		List<FriendRelationships> list = null;
		StringBuffer sql = new StringBuffer()
				.append(" select ")
				.append(" 	id,self_id,friend_id,friend_account,friend_pet_name ")
				.append(" from ").append(" 	t_friends ").append(" where ")
				.append(" 	self_id = ? ");
		list = temp.selectAll(new FriendsRelationshipsMapper(), sql.toString(),
				id);
		return list;
	}

	// 删除好友
	@Override
	public void deleteFriendByFriendId(Integer selfId, Integer friendId) {
		StringBuffer sql = new StringBuffer().append(" delete from ")
				.append(" 	t_friends ").append(" where ").append(" ( ")
				.append(" 	( self_id = ? and friend_id = ? ) ").append(" 	or ")
				.append(" 	( friend_id = ? and self_id = ? ) ").append(")");
		try {
			temp.delete(sql.toString(), selfId, friendId, selfId, friendId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 添加好友
	@Override
	public void insertFriendRelationships(Integer selfId, String selfAccount,
			String selfPetName, Integer friendId, String friendAccount,
			String friendPetName) {
		StringBuffer sql = new StringBuffer()
				.append(" insert into ")
				.append(" 	t_friends ")
				.append(" 	(id,self_id,friend_id,friend_account,friend_pet_name) ")
				.append(" values ").append(" 	(null,?,?,?,?),(null,?,?,?,?) ");
		try {
			temp.insert(sql.toString(), selfId, friendId, friendAccount,
					friendPetName, friendId, selfId, selfAccount, selfPetName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 根据自己和对方id查询是否为好友，用于添加好友时，判断是否已经是好友
	@Override
	public FriendRelationships selectFriendRelationshipBySelfAndFriendId(
			Integer selfId, Integer friendId) {
		StringBuffer sql = new StringBuffer()
				.append(" select ")
				.append(" 	id,self_id,friend_id,friend_account,friend_pet_name ")
				.append(" from ").append(" 	t_friends ").append(" where ")
				.append(" self_id = ? and friend_id = ? ");
		FriendRelationships friendRelationships = temp.selectOne(
				new FriendsRelationshipsMapper(), sql.toString(), selfId,
				friendId);
		return friendRelationships;
	}

}
