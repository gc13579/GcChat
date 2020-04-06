package com.GcChatServer.service.impl;

import java.util.List;

import com.GcChatServer.dao.FriendRelationshipsDao;
import com.GcChatServer.enity.FriendRelationships;
import com.GcChatServer.factory.ObjectFactory;
import com.GcChatServer.service.FriendRelationshipsService;

public class FriendRelationshipsServiceImpl implements
		FriendRelationshipsService {
	FriendRelationshipsDao friendRelationshipsDao = (FriendRelationshipsDao) ObjectFactory
			.getObject("friendRelationshipsDao");

	// 查看所有好友
	@Override
	public List<FriendRelationships> getAllFriendRelationships(Integer id) {
		List<FriendRelationships> list = friendRelationshipsDao
				.selectAllFriendRelationships(id);
		return list;
	}

	// 删除好友
	@Override
	public void removeFriendById(Integer selfId, Integer friendId) {
		friendRelationshipsDao.deleteFriendByFriendId(selfId, friendId);
	}

	// 添加好友
	@Override
	public void addFriendRelationships(Integer selfId, String selfAccount,
			String selfPetName, Integer friendId, String friendAccount,
			String friendPetName) {
		friendRelationshipsDao.insertFriendRelationships(selfId, selfAccount,
				selfPetName, friendId, friendAccount, friendPetName);
	}

}
