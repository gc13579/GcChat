package com.example.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReciever extends BroadcastReceiver {
	private InteractiveBetweenRecieverAndActivity interactive;

	@Override
	public void onReceive(Context context, Intent intent) {
		// ��¼���Ĺ㲥����
		// System.out.println("�Լ���recierve,������ login��־");
		// if (intent.getAction().equals("LOGIN")) {
		// String message = intent.getStringExtra("message");
		// String user = intent.getStringExtra("user");
		// interactive.setMsgFromServer(message + " " + user);
		// }
		// �鿴�����û����Ĺ㲥����
		if (intent.getAction().equals("LOOK_UP_ALL_FRIENDS")) {
			String allFriends = intent.getStringExtra("allFriends");
			interactive.setMsgFromServer(allFriends);
		}
		// ɾ�����ѣ��Ĺ㲥����
		if (intent.getAction().equals("DELETE_FRIEND")) {
			String message = intent.getStringExtra("message");
			interactive.setMsgFromServer(message);
		}
		// �鿴�Ƿ��У��µĺ������󣬵Ĺ㲥����
		if (intent.getAction().equals("LOOK_UP_NEW_FRIEND_REQUEST")) {
			String message = intent.getStringExtra("msg");
			interactive.setMsgFromServer(message);
			// String sourceUserPetName = intent
			// .getStringExtra("sourceUserPetName");
			// String message = intent.getStringExtra("message");
			// String petNameAndMsg = sourceUserPetName + " " + message;
			// interactive.setMsgFromServer(petNameAndMsg);
		}
		// ��ѯ��׼������º���,�Ĺ㲥����
		if (intent.getAction().equals("SEARCH_AND_ADD_NEW_FRIEND")) {
			String allUsers = intent.getStringExtra("allUsers");
			interactive.setMsgFromServer(allUsers);
		}

		// ���������ʱ�����ͺ������󣬵Ĺ㲥����
		if (intent.getAction().equals("SEND_HELLO_TO_USER")) {
			String message = intent.getStringExtra("message");
			interactive.setMsgFromServer(message);
		}
		// ��ʾ���к������󣬵Ĺ㲥����
		if (intent.getAction().equals("SHOW_ALL_NEW_FRIEND_REQUEST")) {
			String wholeNewFriendRequests = intent
					.getStringExtra("wholeNewFriendRequests");
			interactive.setMsgFromServer(wholeNewFriendRequests);
		}
		// ͬ����Ӻ��ѣ��Ĺ㲥����
		if (intent.getAction().equals("AGREE_REQUEST")) {
			String message = intent.getStringExtra("message");
			interactive.setMsgFromServer(message);
		}
		// �ܾ���Ӻ��ѣ��Ĺ㲥����
		if (intent.getAction().equals("DISAGREE_REQUEST")) {
			String message = intent.getStringExtra("message");
			interactive.setMsgFromServer(message);
		}
		// ������Ϣ���Ĺ㲥����
		if (intent.getAction().equals("ACCEPT_MSG")) {
			Integer senderId = intent.getIntExtra("senderId", 0);
			String senderName = intent.getStringExtra("senderName");
			String message = intent.getStringExtra("message");
			String currentTime = intent.getStringExtra("currentTime");
			interactive.setMsgFromServer2(senderId, senderName, message,
					currentTime);
		}
		// �鿴�����¼���Ĺ㲥����
		if (intent.getAction().equals("LOOK_UP_ALL_CHAT_RECORD")) {
			String newChatSenderIds = intent.getStringExtra("newChatSenderIds");
			String allLastChatRecords = intent
					.getStringExtra("allLastChatRecords");
			interactive.setMsgFromServer3(newChatSenderIds, allLastChatRecords);
		}
		// �鿴��ĳ�������¼���Ĺ㲥����
		if (intent.getAction().equals("LOOK_UP_CHAT_RECORD_BY_TWO_SIDES_ID")) {
			String records = intent.getStringExtra("records");
			interactive.setMsgFromServer(records);
		}
		// MainActivity���͸�ChatFragment��ˢ�µ�ǰfragment���Ĺ㲥����
		if (intent.getAction().equals("FLUSH_CHAT_TAB")) {
			interactive.setMsgFromServer("");
		}
		// MainActivity���͸�AllFriendsFragment��ˢ�µ�ǰfragment���Ĺ㲥����
		if (intent.getAction().equals("FLUSH_ALL_FRIENDS_TAB")) {
			interactive.setMsgFromServer("");
		}
		// ����Է�ɾ�����Լ�
		if (intent.getAction().equals("FRIEND_RELATIONSHIP_BREAKDOWN")) {
			interactive.setMsgFromServer("breakdown");
		}
		// �������ͬ���˺�������
		if (intent.getAction().equals("FRIEND_RELATIONSHIP_BUILD")) {
			interactive.setMsgFromServer("build");
		}

	}

	public void setInteractive(InteractiveBetweenRecieverAndActivity interactive) {
		this.interactive = interactive;
	}

}
