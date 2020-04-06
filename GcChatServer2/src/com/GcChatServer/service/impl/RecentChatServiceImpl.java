package com.GcChatServer.service.impl;

import java.util.List;

import com.GcChatServer.dao.RecentChatDao;
import com.GcChatServer.enity.RecentChat;
import com.GcChatServer.factory.ObjectFactory;
import com.GcChatServer.service.RecentChatService;

public class RecentChatServiceImpl implements RecentChatService {
	RecentChatDao recentChatDao = (RecentChatDao) ObjectFactory
			.getObject("recentChatDao");

	// 添加记录
	@Override
	public void addRecentChat(RecentChat recentChat) {
		recentChatDao.insertRecentChat(recentChat);
	}

	// 获取所有记录
	@Override
	public List<RecentChat> getAllRecentChats() {
		return recentChatDao.selectAllRecentChats();
	}

	// 根据接收方id查询记录
	@Override
	public List<RecentChat> getRecentChatsByRecieverId(Integer recieverId) {
		return recentChatDao.selectRecentChatsByRecieverId(recieverId);
	}

	// 删除好友时，删除最近联系人
	@Override
	public void removeRecentChatByTwoSidesId(Integer selfId, Integer friendId) {
		recentChatDao.deleteRecentChatByTwoSidesId(selfId, friendId);
	}

}
