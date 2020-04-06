package com.GcChatServer.service.proxy;

import java.util.ArrayList;
import java.util.List;

import com.GcChatServer.enity.ChatRecord;
import com.GcChatServer.factory.ObjectFactory;
import com.GcChatServer.service.ChatRecordService;
import com.GcChatServer.trans.Transation;

public class ChatRecordServiceProxy implements ChatRecordService {
	Transation trans = (Transation) ObjectFactory.getObject("trans");
	ChatRecordService chatRecordService = (ChatRecordService) ObjectFactory
			.getObject("chatRecordServiceImpl");

	// 添加聊天记录
	@Override
	public void addCharRecord(ChatRecord chatRecord) {
		trans.begin();
		try {
			chatRecordService.addCharRecord(chatRecord);
			trans.commit();
		} catch (Exception e) {
			e.printStackTrace();
			trans.rollback();
		}
	}

	// 查询聊天记录
	@Override
	public ChatRecord getLastChatRecordsByTwoSidesId(Integer senderId,
			Integer recieverId) {
		trans.begin();
		ChatRecord chatRecord = null;
		;
		try {
			trans.commit();
			chatRecord = chatRecordService.getLastChatRecordsByTwoSidesId(
					senderId, recieverId);
		} catch (Exception e) {
			e.printStackTrace();
			trans.rollback();
		}
		return chatRecord;
	}

	// 根据双方id,查询聊天记录
	@Override
	public List<ChatRecord> getChatRecordsByTwoSidesId(Integer friendId,
			Integer selfId) {
		trans.begin();
		List<ChatRecord> list = null;
		try {
			list = chatRecordService.getChatRecordsByTwoSidesId(friendId,
					selfId);
			trans.commit();
		} catch (Exception e) {
			trans.rollback();
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public void removeChatRecordByTwoSidesId(Integer friendId, Integer selfId) {
		trans.begin();
		try {
			chatRecordService.removeChatRecordByTwoSidesId(friendId, selfId);
			trans.commit();
		} catch (Exception e) {
			trans.rollback();
			e.printStackTrace();
		}
	}
}
