package com.GcChatServer.service.proxy;

import java.util.List;

import com.GcChatServer.enity.FriendRelationships;
import com.GcChatServer.factory.ObjectFactory;
import com.GcChatServer.service.FriendRelationshipsService;
import com.GcChatServer.trans.Transation;

public class FriendRelationshipsServiceProxy implements
		FriendRelationshipsService {
	Transation trans = (Transation) ObjectFactory.getObject("trans");
	FriendRelationshipsService friendRelationshipsService = (FriendRelationshipsService) ObjectFactory
			.getObject("friendRelationshipsService");

	// 查看所有好友
	@Override
	public List<FriendRelationships> getAllFriendRelationships(Integer id)
			throws Exception {
		trans.begin();
		List<FriendRelationships> list = null;
		try {
			list = friendRelationshipsService.getAllFriendRelationships(id);
			trans.commit();
		} catch (Exception e) {
			e.printStackTrace();
			trans.rollback();
			throw e;
		}
		return list;
	}

	// 删除好友
	@Override
	public void removeFriendById(Integer selfId, Integer friendId)
			throws Exception {
		trans.begin();
		try {
			friendRelationshipsService.removeFriendById(selfId, friendId);
			trans.commit();
		} catch (Exception e) {
			e.printStackTrace();
			trans.rollback();
			throw e;
		}
	}

	// 添加好友
	@Override
	public void addFriendRelationships(Integer selfId, String selfAccount,
			String selfPetName, Integer friendId, String friendAccount,
			String friendPetName) throws Exception {
		trans.begin();
		try {
			friendRelationshipsService.addFriendRelationships(selfId,
					selfAccount, selfPetName, friendId, friendAccount,
					friendPetName);
			trans.commit();
		} catch (Exception e) {
			trans.rollback();
			e.printStackTrace();
			throw e;
		}
	}
}
