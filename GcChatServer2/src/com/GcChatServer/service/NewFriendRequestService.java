package com.GcChatServer.service;

import java.sql.SQLException;
import java.util.List;

import com.GcChatServer.enity.NewFriendRequest;
import com.GcChatServer.exception.RepeatAddFriendException;

public interface NewFriendRequestService {
	// 添加好友请求
	public void addNewFriendRequest(Integer sourceUserId,
			String sourceUserAccount, String sourceUserPetName,
			Integer targetUserId, String message) throws SQLException,
			RepeatAddFriendException;

	// 查看是否已经添加过好友请求
	public List<NewFriendRequest> getNewFriendRequestBySourceAndTargetId(
			Integer soruceUserId, Integer targetUserId);

	// 查看自己的好友请求
	public List<NewFriendRequest> getNewFriendRequestsById(Integer id);

	// 修改好友请求的访问次数为1
	public void modifyNewFriendRequestStatusById(Integer requestId);

	// 修改好友请求的状态为同意或拒绝
	public void modifyNewFriendRequestStatus(Integer requestId, Integer status);
}
