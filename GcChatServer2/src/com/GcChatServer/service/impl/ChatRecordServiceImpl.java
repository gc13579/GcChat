package com.GcChatServer.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.GcChatServer.dao.ChatRecordDao;
import com.GcChatServer.enity.ChatRecord;
import com.GcChatServer.factory.ObjectFactory;
import com.GcChatServer.service.ChatRecordService;

public class ChatRecordServiceImpl implements ChatRecordService {
	ChatRecordDao chatRecordDao = (ChatRecordDao) ObjectFactory
			.getObject("chatRecordDao");

	// 添加聊天记录
	@Override
	public void addCharRecord(ChatRecord chatRecord) {
		chatRecordDao.insertCharRecord(chatRecord);
	}

	// 查询聊天记录
	@Override
	public ChatRecord getLastChatRecordsByTwoSidesId(Integer senderId,
			Integer recieverId) {
		return chatRecordDao.selectLastChatRecordsByTwoSidesId(senderId,
				recieverId);
	}

	// 根据双方id,查询聊天记录
	@Override
	public List<ChatRecord> getChatRecordsByTwoSidesId(Integer friendId,
			Integer selfId) {
		return chatRecordDao.selectChatRecordsByTwoSidesId(friendId, selfId);
	}

	// 删除好友时，聊天记录删除
	@Override
	public void removeChatRecordByTwoSidesId(Integer friendId, Integer selfId) {
		chatRecordDao.deleteChatRecordByTwoSidesId(friendId, selfId);
	}
}
