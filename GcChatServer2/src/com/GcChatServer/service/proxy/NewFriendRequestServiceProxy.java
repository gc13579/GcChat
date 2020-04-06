package com.GcChatServer.service.proxy;

import java.sql.SQLException;
import java.util.List;

import com.GcChatServer.enity.NewFriendRequest;
import com.GcChatServer.exception.RepeatAddFriendException;
import com.GcChatServer.factory.ObjectFactory;
import com.GcChatServer.service.NewFriendRequestService;
import com.GcChatServer.trans.Transation;

public class NewFriendRequestServiceProxy implements NewFriendRequestService {
	Transation trans = (Transation) ObjectFactory.getObject("trans");
	NewFriendRequestService newFriendRequestService = (NewFriendRequestService) ObjectFactory
			.getObject("newFriendRequestService");

	// 添加一条好友请求
	@Override
	public void addNewFriendRequest(Integer sourceUserId,
			String sourceUserAccount, String sourceUserPetName,
			Integer targetUserId, String message) throws SQLException,
			RepeatAddFriendException {
		trans.begin();
		try {
			newFriendRequestService
					.addNewFriendRequest(sourceUserId, sourceUserAccount,
							sourceUserPetName, targetUserId, message);
			trans.commit();
		} catch (RepeatAddFriendException e) {
			trans.rollback();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			trans.rollback();
		}
	}

	// 查看是否已经添加过好友请求
	@Override
	public List<NewFriendRequest> getNewFriendRequestBySourceAndTargetId(
			Integer soruceUserId, Integer targetUserId) {
		trans.begin();
		List<NewFriendRequest> newFriendRequests = null;
		try {
			newFriendRequests = newFriendRequestService
					.getNewFriendRequestBySourceAndTargetId(soruceUserId,
							targetUserId);
			trans.commit();
		} catch (Exception e) {
			trans.rollback();
			e.printStackTrace();
		}
		return newFriendRequests;
	}

	// 查看自己的好友请求
	@Override
	public List<NewFriendRequest> getNewFriendRequestsById(Integer id) {
		trans.begin();
		List<NewFriendRequest> list = null;
		try {
			list = newFriendRequestService.getNewFriendRequestsById(id);
			trans.commit();
		} catch (Exception e) {
			trans.rollback();
			e.printStackTrace();
		}
		return list;
	}

	// 修改好友请求的访问次数为1
	@Override
	public void modifyNewFriendRequestStatusById(Integer requestId) {
		trans.begin();
		try {
			newFriendRequestService.modifyNewFriendRequestStatusById(requestId);
			trans.commit();
		} catch (Exception e) {
			e.printStackTrace();
			trans.rollback();
		}
	}

	// 修改好友请求的状态为同意或拒绝
	@Override
	public void modifyNewFriendRequestStatus(Integer requestId, Integer status) {
		trans.begin();
		try {
			newFriendRequestService.modifyNewFriendRequestStatus(requestId,
					status);
			trans.commit();
		} catch (Exception e) {
			e.printStackTrace();
			trans.rollback();
		}
	}

}
