package com.GcChatServer.dao;

import java.util.List;

import com.GcChatServer.enity.FriendRelationships;

public interface FriendRelationshipsDao {
	// 查看所有好友
	public List<FriendRelationships> selectAllFriendRelationships(Integer id);

	// 删除好友
	public void deleteFriendByFriendId(Integer selfId, Integer friendId);

	// 添加好友
	public void insertFriendRelationships(Integer selfId, String selfAccount,
			String selfPetName, Integer friendId, String friendAccount,
			String friendPetName);

	// 根据自己和对方id查询是否为好友，用于添加好友时，判断是否已经是好友
	public FriendRelationships selectFriendRelationshipBySelfAndFriendId(
			Integer selfId, Integer friendId);

}
