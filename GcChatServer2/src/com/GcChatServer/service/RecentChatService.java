package com.GcChatServer.service;

import java.util.List;

import com.GcChatServer.enity.RecentChat;

public interface RecentChatService {
	// 添加记录
	public void addRecentChat(RecentChat recentChat);

	// 获取所有记录
	public List<RecentChat> getAllRecentChats();

	// 根据接收方id查询记录
	public List<RecentChat> getRecentChatsByRecieverId(Integer recieverId);

	// 删除好友时，删除最近联系人
	public void removeRecentChatByTwoSidesId(Integer selfId, Integer friendId);
}
