package com.GcChatServer.dao;

import java.util.List;

import com.GcChatServer.enity.NewChat;

public interface NewChatDao {
	// 添加记录
	public void insertNewChat(NewChat newChat);

	// 查询所有记录
	public List<NewChat> selectAllNewChats();

	// 根据id,删除记录
	public void deleteNewChatBySenderAndRecieverId(Integer senderId,
			Integer recieverId);

	// 根据接收方id查询记录
	public List<NewChat> selectNewChatsByRecieverId(Integer recieverId);

	// 删除好友时，删除下线联系人
	public void deleteNewChatsByTwoSidesId(Integer selfId, Integer friendId);
}
