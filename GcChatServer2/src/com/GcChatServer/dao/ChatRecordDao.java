package com.GcChatServer.dao;

import java.util.ArrayList;
import java.util.List;

import com.GcChatServer.enity.ChatRecord;

public interface ChatRecordDao {

	// 添加聊天记录
	public void insertCharRecord(ChatRecord chatRecord);

	// 查询聊天记录
	public ChatRecord selectLastChatRecordsByTwoSidesId(Integer senderId,
			Integer recieverId);

	// 根据双方id,查询聊天记录
	public List<ChatRecord> selectChatRecordsByTwoSidesId(Integer friendId,
			Integer selfId);

	// 删除好友时，聊天记录删除
	public void deleteChatRecordByTwoSidesId(Integer friendId, Integer selfId);
}
