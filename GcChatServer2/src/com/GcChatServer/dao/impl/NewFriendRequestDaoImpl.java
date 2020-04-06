package com.GcChatServer.dao.impl;

import java.sql.SQLException;
import java.util.List;

import com.GcChatServer.Mapper.FriendsRelationshipsMapper;
import com.GcChatServer.Mapper.NewFriendRequestMapper;
import com.GcChatServer.Util.JDBCTemplate;
import com.GcChatServer.dao.NewFriendRequestDao;
import com.GcChatServer.enity.NewFriendRequest;

public class NewFriendRequestDaoImpl implements NewFriendRequestDao {
	JDBCTemplate<NewFriendRequest> temp = new JDBCTemplate<NewFriendRequest>();

	// 添加好友请求
	@Override
	public void insertNewFriendRequest(Integer sourceUserId,
			String sourceUserAccount, String sourceUserPetName,
			Integer targetUserId, String message) throws SQLException {
		StringBuffer sql = new StringBuffer()
				.append(" insert into ")
				.append(" 	t_new_friend_request ")
				.append(" 	(id,source_user_id,source_user_pet_name,target_user_id,message,status,visited_times,source_user_account) ")
				.append(" values ").append(" ( null,?,?,?,?,0,0,? ) ");
		temp.insert(sql.toString(), sourceUserId, sourceUserPetName,
				targetUserId, message, sourceUserAccount);
	}

	// 查看是否已经添加过好友请求
	@Override
	public List<NewFriendRequest> selectNewFriendRequestBySourceAndTargetId(
			Integer soruceUserId, Integer targetUserId) {
		StringBuffer sql = new StringBuffer()
				.append(" select ")
				.append(" 	id,source_user_id,source_user_pet_name,target_user_id,message,status,visited_times,source_user_account ")
				.append(" from ").append(" 	t_new_friend_request ")
				.append(" where ")
				.append(" 	source_user_id = ? and target_user_id = ? ");
		List<NewFriendRequest> newFriendRequests = null;
		newFriendRequests = temp.selectAll(new NewFriendRequestMapper(),
				sql.toString(), soruceUserId, targetUserId);
		return newFriendRequests;
	}

	// 查看自己的好友请求
	@Override
	public List<NewFriendRequest> selectNewFriendRequestsById(Integer id) {
		StringBuffer sql = new StringBuffer()
				.append(" select ")
				.append(" 	id,source_user_id,source_user_pet_name,target_user_id,message,status,visited_times,source_user_account ")
				.append(" from ").append(" 	t_new_friend_request ")
				.append(" where ").append(" 	target_user_id = ? ");
		List<NewFriendRequest> list = null;
		list = temp.selectAll(new NewFriendRequestMapper(), sql.toString(), id);
		return list;
	}

	// 修改好友请求的访问次数为1
	@Override
	public void updateNewFriendRequestStatusById(Integer requestId) {
		StringBuffer sql = new StringBuffer().append(" update ")
				.append(" 	t_new_friend_request ").append(" set ")
				.append(" 	visited_times = 1 ").append(" where ")
				.append(" id = ? ");
		try {
			temp.update(sql.toString(), requestId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 修改好友请求的状态为同意或拒绝
	@Override
	public void updateNewFriendRequestStatus(Integer requestId, Integer status) {
		StringBuffer sql = new StringBuffer().append(" update ")
				.append(" 	t_new_friend_request ").append(" set ")
				.append(" 	status = ? ").append(" where ").append(" 	id = ? ");
		try {
			temp.update(sql.toString(), status, requestId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
