package com.GcChatServer.service.impl;

import java.util.List;

import com.GcChatServer.dao.NewChatDao;
import com.GcChatServer.enity.NewChat;
import com.GcChatServer.factory.ObjectFactory;
import com.GcChatServer.service.NewChatService;

public class NewChatServiceImpl implements NewChatService {
	NewChatDao newChatDao = (NewChatDao) ObjectFactory.getObject("newChatDao");

	// 添加记录
	@Override
	public void addNewChat(NewChat newChat) {
		newChatDao.insertNewChat(newChat);
	}

	// 查询所有记录
	@Override
	public List<NewChat> getAllNewChats() {
		return newChatDao.selectAllNewChats();
	}

	// 根据id,删除记录
	public void removeNewChatBySenderAndRecieverId(Integer senderId,
			Integer recieverId) {
		newChatDao.deleteNewChatBySenderAndRecieverId(senderId, recieverId);
	}

	// 根据接收方id查询记录
	@Override
	public List<NewChat> getNewChatsByRecieverId(Integer recieverId) {
		return newChatDao.selectNewChatsByRecieverId(recieverId);
	}

	// 删除好友时，删除下线联系人
	@Override
	public void removeNewChatsByTwoSidesId(Integer selfId, Integer friendId) {
		newChatDao.deleteNewChatsByTwoSidesId(selfId, friendId);
	}
}
