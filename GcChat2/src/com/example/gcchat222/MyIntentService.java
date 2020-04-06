package com.example.gcchat222;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import com.example.Opreation.Opreation;
import com.example.enity.User;
import com.example.util.SocketUtil;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class MyIntentService extends IntentService {
	private Socket socket;
	private PrintWriter pw;
	private BufferedReader br;
	private User user;
	private boolean isQuit;

	public MyIntentService() {
		super("MyIntentService");
	}

	public MyIntentService(String name) {
		super(name);
	}

	@Override
	public void onDestroy() {
		System.out.println("自定义service 没了");
		super.onDestroy();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		System.out.println("自定义service开始执行");
		user = (User) intent.getSerializableExtra("user");
		// 获取套接字
		socket = SocketUtil.getSocket(false);
		try {
			pw = new PrintWriter(new OutputStreamWriter(
					socket.getOutputStream(), "gbk"));
			br = new BufferedReader(new InputStreamReader(
					socket.getInputStream(), "gbk"));
			pw.println(Opreation.LOOK_UP_NEW_FRIEND_REQUEST);
			pw.println(user.getId());
			pw.flush();
			while (true) {
				String opreationFromServer = br.readLine();
				System.out.println("客户端MyIntentService opreationFromServer="
						+ opreationFromServer);
				Integer opreation = Integer.parseInt(opreationFromServer);
				switch (opreation) {
				// 登录，返回了结果
				// case Opreation.LOGIN:
				// System.out.println("自己的service,接受了 login标志");
				// String msgFromServer = br.readLine();
				// String user = br.readLine();
				// Intent i1 = new Intent("LOGIN");
				// i1.putExtra("message", msgFromServer);
				// i1.putExtra("user", user);
				// sendBroadcast(i1);
				// break;
				// 查看所有用户，返回了结果
				case Opreation.LOOK_UP_ALL_FRIENDS:
					String allFriends = br.readLine();
					System.out.println("MyIntentService 查看所有好友 allFriends="
							+ allFriends);
					Intent intent2 = new Intent("LOOK_UP_ALL_FRIENDS");
					intent2.putExtra("allFriends", allFriends);
					sendBroadcast(intent2);
					break;
				// 删除好友，返回了结果
				case Opreation.DELETE_FRIEND:
					String message = br.readLine();
					System.out.println("MyIntentService 删除好友 message="
							+ message);
					Intent intent3 = new Intent("DELETE_FRIEND");
					intent3.putExtra("message", message);
					sendBroadcast(intent3);
					break;
				// 查询，准备添加新好友，返回了结果
				case Opreation.SEARCH_AND_ADD_NEW_FRIEND:
					String allUsers = br.readLine();
					Intent intent4 = new Intent("SEARCH_AND_ADD_NEW_FRIEND");
					intent4.putExtra("allUsers", allUsers);
					sendBroadcast(intent4);
					break;
				// 查看是否有，新的好友请求，返回了结果
				case Opreation.LOOK_UP_NEW_FRIEND_REQUEST:
					// String msg = "";
					String msg = br.readLine();
					Intent i = new Intent("LOOK_UP_NEW_FRIEND_REQUEST");
					i.putExtra("msg", msg);
					sendBroadcast(i);
					// while ((msg = br.readLine()) != null) {
					// if ("别急，急啥".equals(msg)) {
					// break;
					// }
					// if ("show notification".equals(msg)) {
					// // 获取好友请求的发起人
					// String sourceUserPetName = br.readLine();
					// // 获取好友请求，发起人的发送的消息
					// String msg1 = "";
					// String msg2 = "";
					// while ((msg1 = br.readLine()) != null) {
					// if ("别急，急啥".equals(msg1)) {
					// break;
					// }
					// msg2 = msg2 + msg1 + "\r\n";
					// }
					// Intent i = new Intent("LOOK_UP_NEW_FRIEND_REQUEST");
					// i.putExtra("sourceUserPetName", sourceUserPetName);
					// i.putExtra("message", msg2);
					// sendBroadcast(i);
					// }
					// }
					break;
				// 添加新朋友时，发送好友请求
				case Opreation.SEND_HELLO_TO_USER:
					String message2 = br.readLine();
					Intent intent5 = new Intent("SEND_HELLO_TO_USER");
					intent5.putExtra("message", message2);
					sendBroadcast(intent5);
					break;
				// 显示所有的新好友请求,返回了结果
				case Opreation.SHOW_ALL_NEW_FRIEND_REQUEST:
					String wholeNewFriendRequests = br.readLine();
					System.out.println("客户端service 收到了，显示所有好友请求"
							+ wholeNewFriendRequests);
					Intent intent6 = new Intent("SHOW_ALL_NEW_FRIEND_REQUEST");
					intent6.putExtra("wholeNewFriendRequests",
							wholeNewFriendRequests);
					sendBroadcast(intent6);
					break;
				// 同意添加好友，返回结果
				case Opreation.AGREE_REQUEST:
					String message3 = br.readLine();
					Intent intent7 = new Intent("AGREE_REQUEST");
					intent7.putExtra("message", message3);
					sendBroadcast(intent7);
					break;
				// 拒绝添加好友，返回结果
				case Opreation.DISAGREE_REQUEST:
					String message4 = br.readLine();
					Intent intent8 = new Intent("DISAGREE_REQUEST");
					intent8.putExtra("message", message4);
					sendBroadcast(intent8);
					break;
				// 退出，返回结果
				case Opreation.QUIT:
					isQuit = true;
					break;
				// 接收消息
				case Opreation.ACCEPT_MSG:
					String senderId = br.readLine();
					String senderName = br.readLine();
					String s = "";
					String msg2 = "";
					while ((s = br.readLine()) != null) {
						if ("stop the loop".equals(s)) {
							break;
						}
						msg2 += s + "\n";
					}
					msg2 = msg2.substring(0, msg2.length() - 1);
					String currentTime = br.readLine();
					Intent intent9 = new Intent("ACCEPT_MSG");
					intent9.putExtra("senderId", Integer.valueOf(senderId));
					intent9.putExtra("senderName", senderName);
					intent9.putExtra("message", msg2);
					intent9.putExtra("currentTime", currentTime);
					sendBroadcast(intent9);
					break;
				// 查看所有聊天记录
				case Opreation.LOOK_UP_ALL_CHAT_RECORD:
					String newChatSenderIds = br.readLine();
					String allLastChatRecords = br.readLine();
					Intent intent10 = new Intent("LOOK_UP_ALL_CHAT_RECORD");
					intent10.putExtra("newChatSenderIds", newChatSenderIds);
					intent10.putExtra("allLastChatRecords", allLastChatRecords);
					// LocalBroadcastManager.getInstance(getApplicationContext())
					// .sendBroadcast(intent);
					sendBroadcast(intent10);
					break;
				// 进入和某人的聊天页面，显示聊天记录
				case Opreation.LOOK_UP_CHAT_RECORD_BY_TWO_SIDES_ID:
					String records = br.readLine();
					Intent intent11 = new Intent(
							"LOOK_UP_CHAT_RECORD_BY_TWO_SIDES_ID");
					intent11.putExtra("records", records);
					sendBroadcast(intent11);
					break;
				// 好友将自己删除
				case Opreation.FRIEND_RELATIONSHIP_BREAKDOWN:
					String friendId = br.readLine();
					pw.println(Opreation.DELETE_TWO_LIST_RECORD);
					pw.println(friendId);
					pw.flush();
					Intent intent12 = new Intent(
							"FRIEND_RELATIONSHIP_BREAKDOWN");
					sendBroadcast(intent12);
					break;
				// 有人同意了自己的好友请求
				case Opreation.FRIEND_RELATIONSHIP_BUILD:
					Intent intent13 = new Intent("FRIEND_RELATIONSHIP_BUILD");
					sendBroadcast(intent13);
					break;
				default:
					break;
				}
				if (isQuit) {
					isQuit = false;
					break;
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
