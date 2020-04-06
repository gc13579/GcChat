package com.GcChatServer.service;

import java.util.List;

import com.GcChatServer.enity.NewChat;
import com.GcChatServer.enity.RecentChat;

public interface NewChatService {
	// 添加记录
	public void addNewChat(NewChat newChat);

	// 查询所有记录
	public List<NewChat> getAllNewChats();

	// 根据id,删除记录
	public void removeNewChatBySenderAndRecieverId(Integer senderId,
			Integer recieverId);

	// 根据接收方id查询记录
	public List<NewChat> getNewChatsByRecieverId(Integer recieverId);

	// 删除好友时，删除下线联系人
	public void removeNewChatsByTwoSidesId(Integer selfId, Integer friendId);
}
