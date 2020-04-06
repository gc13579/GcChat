package com.GcChatServer.service;

import com.GcChatServer.enity.FriendRelationships;

import java.util.List;

public interface FriendRelationshipsService {
	// 查询所有好友
	public List<FriendRelationships> getAllFriendRelationships(Integer id)
			throws Exception;

	// 删除好友
	public void removeFriendById(Integer selfId, Integer friendId)
			throws Exception;

	// 添加好友
	public void addFriendRelationships(Integer selfId, String selfAccount,
			String selfPetName, Integer friendId, String friendAccount,
			String friendPetName) throws Exception;
}
