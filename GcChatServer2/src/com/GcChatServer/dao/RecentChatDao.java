package com.GcChatServer.dao;

import java.util.List;

import com.GcChatServer.enity.NewChat;
import com.GcChatServer.enity.RecentChat;

public interface RecentChatDao {
	// 添加记录
	public void insertRecentChat(RecentChat recentChat);

	// 获取所有记录
	public List<RecentChat> selectAllRecentChats();

	// 根据接收方id查询记录
	public List<RecentChat> selectRecentChatsByRecieverId(Integer recieverId);

	// 删除好友时，删除最近联系人
	public void deleteRecentChatByTwoSidesId(Integer selfId, Integer friendId);
	
	//
}
