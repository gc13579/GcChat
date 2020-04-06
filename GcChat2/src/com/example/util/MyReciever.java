package com.example.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReciever extends BroadcastReceiver {
	private InteractiveBetweenRecieverAndActivity interactive;

	@Override
	public void onReceive(Context context, Intent intent) {
		// 登录，的广播监听
		// System.out.println("自己的recierve,接受了 login标志");
		// if (intent.getAction().equals("LOGIN")) {
		// String message = intent.getStringExtra("message");
		// String user = intent.getStringExtra("user");
		// interactive.setMsgFromServer(message + " " + user);
		// }
		// 查看所有用户，的广播监听
		if (intent.getAction().equals("LOOK_UP_ALL_FRIENDS")) {
			String allFriends = intent.getStringExtra("allFriends");
			interactive.setMsgFromServer(allFriends);
		}
		// 删除好友，的广播监听
		if (intent.getAction().equals("DELETE_FRIEND")) {
			String message = intent.getStringExtra("message");
			interactive.setMsgFromServer(message);
		}
		// 查看是否有，新的好友请求，的广播监听
		if (intent.getAction().equals("LOOK_UP_NEW_FRIEND_REQUEST")) {
			String message = intent.getStringExtra("msg");
			interactive.setMsgFromServer(message);
			// String sourceUserPetName = intent
			// .getStringExtra("sourceUserPetName");
			// String message = intent.getStringExtra("message");
			// String petNameAndMsg = sourceUserPetName + " " + message;
			// interactive.setMsgFromServer(petNameAndMsg);
		}
		// 查询，准备添加新好友,的广播监听
		if (intent.getAction().equals("SEARCH_AND_ADD_NEW_FRIEND")) {
			String allUsers = intent.getStringExtra("allUsers");
			interactive.setMsgFromServer(allUsers);
		}

		// 添加新朋友时，发送好友请求，的广播监听
		if (intent.getAction().equals("SEND_HELLO_TO_USER")) {
			String message = intent.getStringExtra("message");
			interactive.setMsgFromServer(message);
		}
		// 显示所有好友请求，的广播监听
		if (intent.getAction().equals("SHOW_ALL_NEW_FRIEND_REQUEST")) {
			String wholeNewFriendRequests = intent
					.getStringExtra("wholeNewFriendRequests");
			interactive.setMsgFromServer(wholeNewFriendRequests);
		}
		// 同意添加好友，的广播监听
		if (intent.getAction().equals("AGREE_REQUEST")) {
			String message = intent.getStringExtra("message");
			interactive.setMsgFromServer(message);
		}
		// 拒绝添加好友，的广播监听
		if (intent.getAction().equals("DISAGREE_REQUEST")) {
			String message = intent.getStringExtra("message");
			interactive.setMsgFromServer(message);
		}
		// 接收消息，的广播监听
		if (intent.getAction().equals("ACCEPT_MSG")) {
			Integer senderId = intent.getIntExtra("senderId", 0);
			String senderName = intent.getStringExtra("senderName");
			String message = intent.getStringExtra("message");
			String currentTime = intent.getStringExtra("currentTime");
			interactive.setMsgFromServer2(senderId, senderName, message,
					currentTime);
		}
		// 查看聊天记录，的广播监听
		if (intent.getAction().equals("LOOK_UP_ALL_CHAT_RECORD")) {
			String newChatSenderIds = intent.getStringExtra("newChatSenderIds");
			String allLastChatRecords = intent
					.getStringExtra("allLastChatRecords");
			interactive.setMsgFromServer3(newChatSenderIds, allLastChatRecords);
		}
		// 查看和某人聊天记录，的广播监听
		if (intent.getAction().equals("LOOK_UP_CHAT_RECORD_BY_TWO_SIDES_ID")) {
			String records = intent.getStringExtra("records");
			interactive.setMsgFromServer(records);
		}
		// MainActivity发送给ChatFragment，刷新当前fragment，的广播监听
		if (intent.getAction().equals("FLUSH_CHAT_TAB")) {
			interactive.setMsgFromServer("");
		}
		// MainActivity发送给AllFriendsFragment，刷新当前fragment，的广播监听
		if (intent.getAction().equals("FLUSH_ALL_FRIENDS_TAB")) {
			interactive.setMsgFromServer("");
		}
		// 如果对方删除了自己
		if (intent.getAction().equals("FRIEND_RELATIONSHIP_BREAKDOWN")) {
			interactive.setMsgFromServer("breakdown");
		}
		// 如果有人同意了好友请求
		if (intent.getAction().equals("FRIEND_RELATIONSHIP_BUILD")) {
			interactive.setMsgFromServer("build");
		}

	}

	public void setInteractive(InteractiveBetweenRecieverAndActivity interactive) {
		this.interactive = interactive;
	}

}
