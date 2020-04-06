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
		System.out.println("�Զ���service û��");
		super.onDestroy();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		System.out.println("�Զ���service��ʼִ��");
		user = (User) intent.getSerializableExtra("user");
		// ��ȡ�׽���
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
				System.out.println("�ͻ���MyIntentService opreationFromServer="
						+ opreationFromServer);
				Integer opreation = Integer.parseInt(opreationFromServer);
				switch (opreation) {
				// ��¼�������˽��
				// case Opreation.LOGIN:
				// System.out.println("�Լ���service,������ login��־");
				// String msgFromServer = br.readLine();
				// String user = br.readLine();
				// Intent i1 = new Intent("LOGIN");
				// i1.putExtra("message", msgFromServer);
				// i1.putExtra("user", user);
				// sendBroadcast(i1);
				// break;
				// �鿴�����û��������˽��
				case Opreation.LOOK_UP_ALL_FRIENDS:
					String allFriends = br.readLine();
					System.out.println("MyIntentService �鿴���к��� allFriends="
							+ allFriends);
					Intent intent2 = new Intent("LOOK_UP_ALL_FRIENDS");
					intent2.putExtra("allFriends", allFriends);
					sendBroadcast(intent2);
					break;
				// ɾ�����ѣ������˽��
				case Opreation.DELETE_FRIEND:
					String message = br.readLine();
					System.out.println("MyIntentService ɾ������ message="
							+ message);
					Intent intent3 = new Intent("DELETE_FRIEND");
					intent3.putExtra("message", message);
					sendBroadcast(intent3);
					break;
				// ��ѯ��׼������º��ѣ������˽��
				case Opreation.SEARCH_AND_ADD_NEW_FRIEND:
					String allUsers = br.readLine();
					Intent intent4 = new Intent("SEARCH_AND_ADD_NEW_FRIEND");
					intent4.putExtra("allUsers", allUsers);
					sendBroadcast(intent4);
					break;
				// �鿴�Ƿ��У��µĺ������󣬷����˽��
				case Opreation.LOOK_UP_NEW_FRIEND_REQUEST:
					// String msg = "";
					String msg = br.readLine();
					Intent i = new Intent("LOOK_UP_NEW_FRIEND_REQUEST");
					i.putExtra("msg", msg);
					sendBroadcast(i);
					// while ((msg = br.readLine()) != null) {
					// if ("�𼱣���ɶ".equals(msg)) {
					// break;
					// }
					// if ("show notification".equals(msg)) {
					// // ��ȡ��������ķ�����
					// String sourceUserPetName = br.readLine();
					// // ��ȡ�������󣬷����˵ķ��͵���Ϣ
					// String msg1 = "";
					// String msg2 = "";
					// while ((msg1 = br.readLine()) != null) {
					// if ("�𼱣���ɶ".equals(msg1)) {
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
				// ���������ʱ�����ͺ�������
				case Opreation.SEND_HELLO_TO_USER:
					String message2 = br.readLine();
					Intent intent5 = new Intent("SEND_HELLO_TO_USER");
					intent5.putExtra("message", message2);
					sendBroadcast(intent5);
					break;
				// ��ʾ���е��º�������,�����˽��
				case Opreation.SHOW_ALL_NEW_FRIEND_REQUEST:
					String wholeNewFriendRequests = br.readLine();
					System.out.println("�ͻ���service �յ��ˣ���ʾ���к�������"
							+ wholeNewFriendRequests);
					Intent intent6 = new Intent("SHOW_ALL_NEW_FRIEND_REQUEST");
					intent6.putExtra("wholeNewFriendRequests",
							wholeNewFriendRequests);
					sendBroadcast(intent6);
					break;
				// ͬ����Ӻ��ѣ����ؽ��
				case Opreation.AGREE_REQUEST:
					String message3 = br.readLine();
					Intent intent7 = new Intent("AGREE_REQUEST");
					intent7.putExtra("message", message3);
					sendBroadcast(intent7);
					break;
				// �ܾ���Ӻ��ѣ����ؽ��
				case Opreation.DISAGREE_REQUEST:
					String message4 = br.readLine();
					Intent intent8 = new Intent("DISAGREE_REQUEST");
					intent8.putExtra("message", message4);
					sendBroadcast(intent8);
					break;
				// �˳������ؽ��
				case Opreation.QUIT:
					isQuit = true;
					break;
				// ������Ϣ
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
				// �鿴���������¼
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
				// �����ĳ�˵�����ҳ�棬��ʾ�����¼
				case Opreation.LOOK_UP_CHAT_RECORD_BY_TWO_SIDES_ID:
					String records = br.readLine();
					Intent intent11 = new Intent(
							"LOOK_UP_CHAT_RECORD_BY_TWO_SIDES_ID");
					intent11.putExtra("records", records);
					sendBroadcast(intent11);
					break;
				// ���ѽ��Լ�ɾ��
				case Opreation.FRIEND_RELATIONSHIP_BREAKDOWN:
					String friendId = br.readLine();
					pw.println(Opreation.DELETE_TWO_LIST_RECORD);
					pw.println(friendId);
					pw.flush();
					Intent intent12 = new Intent(
							"FRIEND_RELATIONSHIP_BREAKDOWN");
					sendBroadcast(intent12);
					break;
				// ����ͬ�����Լ��ĺ�������
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
