package com.example.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.Opreation.Opreation;
import com.example.enity.User;
import com.example.gcchat222.HandleNewFriendRequest;
import com.example.gcchat222.R;

public class LookUpNewFriendRequestThread extends Thread {
	private TimeStore timeStore;
	private PrintWriter pw;
	private BufferedReader br;
	private String message;
	private Integer userId;
	private Context context;
	private NotificationManager nm;
	private Notification notification;
	private User user;

	public LookUpNewFriendRequestThread(TimeStore timeStore, PrintWriter pw,
			BufferedReader br, String message, Integer userId, Context context,
			NotificationManager nm, User user) {
		this.timeStore = timeStore;
		this.pw = pw;
		this.br = br;
		this.message = message;
		this.userId = userId;
		this.context = context;
		this.nm = nm;
		this.user = user;
	}

	@Override
	public void run() {
		while (true) {
			// 当上次发送时间是在7秒或之前，才发送
			if ((System.currentTimeMillis() - timeStore.getLastSendTime()) >= 7 * 1000) {
				// 记录时间
				timeStore.setLastSendTime(System.currentTimeMillis());
				pw.println(Opreation.LOOK_UP_NEW_FRIEND_REQUEST);
				pw.println(userId);
				pw.flush();
				try {
					while ((message = br.readLine()) != null) {
						System.out
								.println("LookUpNewFriendRequestThread  message="
										+ message);
						if ("别急，急啥".equals(message)) {
							break;
						}
						if ("show notification".equals(message)) {
							// 获取好友请求的发起人
							String sourceUserPetName = br.readLine();
							// 获取好友请求，发起人的发送的消息
							String message = "";
							String message2 = "";
							while ((message = br.readLine()) != null) {
								if ("别急，急啥".equals(message)) {
									break;
								}
								message2 = message2 + message + "\r\n";
							}
							Intent intent = new Intent(context,
									HandleNewFriendRequest.class);
							intent.putExtra("user", user);
							PendingIntent pi = PendingIntent.getActivity(
									context, 0, intent,
									PendingIntent.FLAG_CANCEL_CURRENT);
							Notification notification = new Notification.Builder(
									context).setAutoCancel(true)
									.setTicker("您有新的好友请求")
									.setSmallIcon(R.drawable.ic_launcher)
									.setContentTitle(sourceUserPetName)
									.setContentText(message2)
									.setDefaults(Notification.DEFAULT_ALL)
									.setWhen(System.currentTimeMillis())
									.setContentIntent(pi).setNumber(1).build();
							nm.notify(1, notification);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
