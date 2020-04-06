package com.GcChatServer.service.proxy;

import java.util.List;

import com.GcChatServer.enity.RecentChat;
import com.GcChatServer.factory.ObjectFactory;
import com.GcChatServer.service.RecentChatService;
import com.GcChatServer.trans.Transation;

public class RecentChatServiceProxy implements RecentChatService {
	Transation trans = (Transation) ObjectFactory.getObject("trans");
	RecentChatService recentChatService = (RecentChatService) ObjectFactory
			.getObject("recentChatService");

	// 添加记录
	@Override
	public void addRecentChat(RecentChat recentChat) {
		trans.begin();
		try {
			recentChatService.addRecentChat(recentChat);
			trans.commit();
		} catch (Exception e) {
			e.printStackTrace();
			trans.rollback();
		}
	}

	// 根据接收方id查询记录
	@Override
	public List<RecentChat> getRecentChatsByRecieverId(Integer recieverId) {
		trans.begin();
		List<RecentChat> list = null;
		try {
			list = recentChatService.getRecentChatsByRecieverId(recieverId);
			trans.commit();
		} catch (Exception e) {
			e.printStackTrace();
			trans.rollback();
		}
		return list;
	}

	// 获取所有记录
	@Override
	public List<RecentChat> getAllRecentChats() {
		trans.begin();
		List<RecentChat> list = null;
		try {
			list = recentChatService.getAllRecentChats();
			trans.commit();
		} catch (Exception e) {
			trans.rollback();
			e.printStackTrace();
		}
		return list;
	}

	// 删除好友时，删除最近联系人
	@Override
	public void removeRecentChatByTwoSidesId(Integer selfId, Integer friendId) {
		trans.begin();
		try {
			recentChatService.removeRecentChatByTwoSidesId(selfId, friendId);
			trans.commit();
		} catch (Exception e) {
			trans.rollback();
			e.printStackTrace();
		}
	}
}
