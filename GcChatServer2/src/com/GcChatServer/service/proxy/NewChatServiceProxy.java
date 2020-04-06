package com.GcChatServer.service.proxy;

import java.util.List;

import com.GcChatServer.enity.NewChat;
import com.GcChatServer.factory.ObjectFactory;
import com.GcChatServer.service.NewChatService;
import com.GcChatServer.trans.Transation;

public class NewChatServiceProxy implements NewChatService {
	Transation trans = (Transation) ObjectFactory.getObject("trans");
	NewChatService newChatService = (NewChatService) ObjectFactory
			.getObject("newChatService");

	// 添加记录
	@Override
	public void addNewChat(NewChat newChat) {
		trans.begin();
		try {
			newChatService.addNewChat(newChat);
			trans.commit();
		} catch (Exception e) {
			e.printStackTrace();
			trans.rollback();
		}
	}

	// 查询所有记录
	@Override
	public List<NewChat> getAllNewChats() {
		trans.begin();
		List<NewChat> list = null;
		try {
			list = newChatService.getAllNewChats();
			trans.commit();
		} catch (Exception e) {
			trans.commit();
			e.printStackTrace();
		}
		return list;
	}

	// 根据id,删除记录
	@Override
	public void removeNewChatBySenderAndRecieverId(Integer senderId,
			Integer recieverId) {
		trans.begin();
		try {
			newChatService.removeNewChatBySenderAndRecieverId(senderId,
					recieverId);
			trans.commit();
		} catch (Exception e) {
			trans.rollback();
			e.printStackTrace();
		}
	}

	@Override
	public List<NewChat> getNewChatsByRecieverId(Integer recieverId) {
		trans.begin();
		List<NewChat> list = null;
		try {
			list = newChatService.getNewChatsByRecieverId(recieverId);
			trans.commit();
		} catch (Exception e) {
			e.printStackTrace();
			trans.rollback();
		}
		return list;
	}

	// 删除好友时，删除下线联系人
	@Override
	public void removeNewChatsByTwoSidesId(Integer selfId, Integer friendId) {
		trans.begin();
		try {
			newChatService.removeNewChatsByTwoSidesId(selfId, friendId);
			trans.commit();
		} catch (Exception e) {
			trans.rollback();
			e.printStackTrace();
		}
	}

}
