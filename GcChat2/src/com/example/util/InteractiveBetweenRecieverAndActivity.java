package com.example.util;

public interface InteractiveBetweenRecieverAndActivity {
	public void setMsgFromServer(String msg);

	public void setMsgFromServer2(Integer senderId, String senderName,
			String message, String currentTime);

	public void setMsgFromServer3(String newChatSenderIds,
			String allLastChatRecords);

}
