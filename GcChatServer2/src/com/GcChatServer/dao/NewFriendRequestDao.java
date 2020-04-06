package com.GcChatServer.dao;

import java.sql.SQLException;
import java.util.List;

import com.GcChatServer.enity.NewFriendRequest;

public interface NewFriendRequestDao {
	// 添加好友请求
	public void insertNewFriendRequest(Integer sourceUserId,
			String sourceUserAccount, String sourceUserPetName,
			Integer targetUserId, String message) throws SQLException;

	// 查看是否已经添加过好友请求
	public List<NewFriendRequest> selectNewFriendRequestBySourceAndTargetId(
			Integer soruceUserId, Integer targetUserId);

	// 查看自己的好友请求
	public List<NewFriendRequest> selectNewFriendRequestsById(Integer id);

	// 修改好友请求的访问次数为1
	public void updateNewFriendRequestStatusById(Integer requestId);

	// 修改好友请求的状态为同意或拒绝
	public void updateNewFriendRequestStatus(Integer requestId, Integer status);
}
