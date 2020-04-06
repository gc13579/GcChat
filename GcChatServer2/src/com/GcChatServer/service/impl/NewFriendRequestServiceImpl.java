package com.GcChatServer.service.impl;

import java.sql.SQLException;
import java.util.List;

import org.omg.CORBA.FREE_MEM;

import com.GcChatServer.dao.FriendRelationshipsDao;
import com.GcChatServer.dao.NewFriendRequestDao;
import com.GcChatServer.enity.FriendRelationships;
import com.GcChatServer.enity.NewFriendRequest;
import com.GcChatServer.exception.RepeatAddFriendException;
import com.GcChatServer.factory.ObjectFactory;
import com.GcChatServer.service.NewFriendRequestService;

public class NewFriendRequestServiceImpl implements NewFriendRequestService {
	NewFriendRequestDao newFriendRequestDao = (NewFriendRequestDao) ObjectFactory
			.getObject("newFriendRequestDao");
	FriendRelationshipsDao friendRelationshipsDao = (FriendRelationshipsDao) ObjectFactory
			.getObject("friendRelationshipsDao");

	// 添加好友请求
	@Override
	public void addNewFriendRequest(Integer sourceUserId,
			String sourceUserAccount, String sourceUserPetName,
			Integer targetUserId, String message) throws SQLException,
			RepeatAddFriendException {
		System.out.println("NewFriendRequestServiceImpl:" + sourceUserId
				+ "   " + targetUserId);
		// 判断好友列表里，是否存在好友关系
		FriendRelationships friendRelationships = friendRelationshipsDao
				.selectFriendRelationshipBySelfAndFriendId(sourceUserId,
						targetUserId);
		// 若已经是好友
		if (friendRelationships != null) {
			throw new RepeatAddFriendException("您和此用户已经是好友了，无法重复添加");
		}
		List<NewFriendRequest> newFriendRequests = newFriendRequestDao
				.selectNewFriendRequestBySourceAndTargetId(sourceUserId,
						targetUserId);
		System.out.println("好友申请里 newFriendRequest=" + newFriendRequests);
		// 若不是好友，且好友申请状态是未同意
		if (newFriendRequests != null
				&& newFriendRequests.size() != 0
				&& newFriendRequests.get(newFriendRequests.size() - 1)
						.getStatus().equals(0)) {
			throw new RepeatAddFriendException("您已向此用户发送过好友请求，请等待对方回复");
		}
		newFriendRequestDao.insertNewFriendRequest(sourceUserId,
				sourceUserAccount, sourceUserPetName, targetUserId, message);
	}

	// 查看是否已经添加过好友请求
	@Override
	public List<NewFriendRequest> getNewFriendRequestBySourceAndTargetId(
			Integer soruceUserId, Integer targetUserId) {
		return newFriendRequestDao.selectNewFriendRequestBySourceAndTargetId(
				soruceUserId, targetUserId);
	}

	// 查看自己的好友请求
	@Override
	public List<NewFriendRequest> getNewFriendRequestsById(Integer id) {
		return newFriendRequestDao.selectNewFriendRequestsById(id);
	}

	// 修改好友请求的访问次数为1
	@Override
	public void modifyNewFriendRequestStatusById(Integer requestId) {
		newFriendRequestDao.updateNewFriendRequestStatusById(requestId);
	}

	// 修改好友请求的状态为同意或拒绝
	@Override
	public void modifyNewFriendRequestStatus(Integer requestId, Integer status) {
		newFriendRequestDao.updateNewFriendRequestStatus(requestId, status);
	}

}
