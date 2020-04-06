package com.GcChatServer.service;

import java.util.ArrayList;
import java.util.List;

import com.GcChatServer.enity.ChatRecord;

public interface ChatRecordService {
	// 添加聊天记录
	public void addCharRecord(ChatRecord chatRecord);

	// 查询聊天记录
	public ChatRecord getLastChatRecordsByTwoSidesId(Integer senderId,
			Integer recieverId);

	// 根据双方id,查询聊天记录
	public List<ChatRecord> getChatRecordsByTwoSidesId(Integer friendId,
			Integer selfId);

	// 删除好友时，聊天记录删除
	public void removeChatRecordByTwoSidesId(Integer friendId, Integer selfId);
}
