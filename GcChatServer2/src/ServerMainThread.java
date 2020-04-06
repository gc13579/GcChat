import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.GcChatServer.Opreation.Opreation;
import com.GcChatServer.Util.DateUtil;
import com.GcChatServer.enity.ChatRecord;
import com.GcChatServer.enity.FriendRelationships;
import com.GcChatServer.enity.NewChat;
import com.GcChatServer.enity.NewFriendRequest;
import com.GcChatServer.enity.RecentChat;
import com.GcChatServer.enity.User;
import com.GcChatServer.exception.LoginFailException;
import com.GcChatServer.exception.RepeatAddFriendException;
import com.GcChatServer.exception.UserExistedException;
import com.GcChatServer.factory.ObjectFactory;
import com.GcChatServer.service.proxy.ChatRecordServiceProxy;
import com.GcChatServer.service.proxy.FriendRelationshipsServiceProxy;
import com.GcChatServer.service.proxy.NewChatServiceProxy;
import com.GcChatServer.service.proxy.NewFriendRequestServiceProxy;
import com.GcChatServer.service.proxy.RecentChatServiceProxy;
import com.GcChatServer.service.proxy.UserServiceProxy;

public class ServerMainThread extends Thread {
	private Socket socket;
	// private List<Socket> allSockets;
	private static HashMap<Integer, Socket> allSockets;
	private PrintWriter pw;
	private BufferedReader br;
	private UserServiceProxy userServiceProxy;
	private FriendRelationshipsServiceProxy friendRelationshipsServiceProxy;
	private NewFriendRequestServiceProxy newFriendRequestServiceProxy;
	private ChatRecordServiceProxy chatRecordServiceProxy;
	private NewChatServiceProxy newChatServiceProxy;
	private RecentChatServiceProxy recentChatServiceProxy;
	private User user;
	private List<RecentChat> recentChatList = new ArrayList<RecentChat>();
	private List<NewChat> newChatList = new ArrayList<NewChat>();

	public ServerMainThread(Socket socket, HashMap<Integer, Socket> allSockets) {
		this.socket = socket;
		this.allSockets = allSockets;
	}

	@Override
	public void run() {
		userServiceProxy = (UserServiceProxy) ObjectFactory
				.getObject("userServiceProxy");
		friendRelationshipsServiceProxy = (FriendRelationshipsServiceProxy) ObjectFactory
				.getObject("friendRelationshipsServiceProxy");
		newFriendRequestServiceProxy = (NewFriendRequestServiceProxy) ObjectFactory
				.getObject("newFriendRequestServiceProxy");
		chatRecordServiceProxy = (ChatRecordServiceProxy) ObjectFactory
				.getObject("chatRecordServiceProxy");
		newChatServiceProxy = (NewChatServiceProxy) ObjectFactory
				.getObject("newChatServiceProxy");
		recentChatServiceProxy = (RecentChatServiceProxy) ObjectFactory
				.getObject("recentChatServiceProxy");
		// 先将最近联系人和下线联系人的数据，存至两个list,避免每次上线，list为空，向数据库重复添加数据
		newChatList = newChatServiceProxy.getAllNewChats();
		recentChatList = recentChatServiceProxy.getAllRecentChats();

		try {
			br = new BufferedReader(new InputStreamReader(
					socket.getInputStream(), "gbk"));
			pw = new PrintWriter(new OutputStreamWriter(
					socket.getOutputStream(), "gbk"));
			while (true) {
				/**
				 * opreationFromClient确定作何操作
				 */
				String opreationFromClient = br.readLine();
				System.out.println("opreationFromClient = "
						+ opreationFromClient);
				Integer opreation = Integer.parseInt(opreationFromClient);
				// 新增好友的id
				String newFriendId = "";
				// 根据操作，细化服务器要执行的代码
				switch (opreation) {
				// 注册
				case Opreation.ENROLL:
					String AccountAndPwdAndPetName = br.readLine();
					String enrollAccount = AccountAndPwdAndPetName.substring(0,
							AccountAndPwdAndPetName.indexOf(","));
					String enrollPassword = AccountAndPwdAndPetName.substring(
							AccountAndPwdAndPetName.indexOf(",") + 1,
							AccountAndPwdAndPetName.lastIndexOf(","));
					String enrollPetName = AccountAndPwdAndPetName
							.substring(AccountAndPwdAndPetName.lastIndexOf(",") + 1);
					try {
						userServiceProxy.enroll(enrollAccount, enrollPassword,
								enrollPetName);
						pw.println("注册成功");
						pw.flush();
					} catch (UserExistedException e1) {
						e1.printStackTrace();
						pw.println(e1.getMessage());
						pw.flush();
					}

					break;
				// 登录
				case Opreation.LOGIN:
					String AccountAndPwd = br.readLine();
					String loginAccount = AccountAndPwd.substring(0,
							AccountAndPwd.indexOf(","));
					String loginPassword = AccountAndPwd
							.substring(AccountAndPwd.indexOf(",") + 1);
					try {
						user = userServiceProxy.login(loginAccount,
								loginPassword);
						/**
						 * 
						 * 添加套接字
						 * 
						 */
						allSockets.put(user.getId(), socket);
						// pw.println(Opreation.LOGIN);
						pw.println("登录成功");
						pw.println(JSONObject.fromObject(user).toString());
						pw.flush();
						// 登录成功后，修改用户状态为，已登录
						userServiceProxy.modifyUserStatus(user.getId(), 1);
					} catch (LoginFailException e) {
						// pw.println(Opreation.LOGIN);
						pw.println(e.getMessage());
						pw.println(JSONObject.fromObject(
								new User(0, "", "", "", 0)).toString());
						pw.flush();
						e.printStackTrace();
					}
					System.out.println("登录后，现在的套接字集合:" + allSockets);
					break;
				// 显示所有好友
				case Opreation.LOOK_UP_ALL_FRIENDS:
					synchronized (this) {
						String id = br.readLine();
						List<FriendRelationships> list = null;
						try {
							list = friendRelationshipsServiceProxy
									.getAllFriendRelationships(Integer
											.parseInt(id));
							pw.println(Opreation.LOOK_UP_ALL_FRIENDS);
							pw.flush();
							pw.println(JSONArray.fromObject(list).toString());
							pw.flush();
							System.out
									.println("服务器 LOOK_UP_ALL_FRIENDS 向客户端发送 ："
											+ list);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					break;
				// 删除好友
				case Opreation.DELETE_FRIEND:
					String selfAndFriendId = br.readLine();
					try {
						Integer selfId = Integer.parseInt(selfAndFriendId
								.substring(0, selfAndFriendId.indexOf(",")));
						Integer friendId = Integer.parseInt(selfAndFriendId
								.substring(selfAndFriendId.indexOf(",") + 1));
						System.out.println("删除好友的id =" + friendId);
						// 删除好友关系
						friendRelationshipsServiceProxy.removeFriendById(
								selfId, friendId);
						// 删除聊天记录
						chatRecordServiceProxy.removeChatRecordByTwoSidesId(
								friendId, selfId);
						// 删除最近联系人
						recentChatServiceProxy.removeRecentChatByTwoSidesId(
								selfId, friendId);
						// 删除下线联系人
						newChatServiceProxy.removeNewChatsByTwoSidesId(selfId,
								friendId);
						// 两个list，也要删除对应的记录
						recentChatList.remove(new RecentChat(friendId, selfId));
						recentChatList.remove(new RecentChat(selfId, friendId));

						newChatList.remove(new NewChat(friendId, selfId));
						newChatList.remove(new NewChat(selfId, friendId));
						System.out.println(user.getAccount()
								+ "删除了好友，recentChatList=" + recentChatList);
						pw.println(Opreation.DELETE_FRIEND);
						pw.println("删除成功");
						pw.flush();

						Socket socket = allSockets.get(friendId);
						// 如果删除对方时，对方在线
						if (socket != null) {
							PrintWriter pw2 = new PrintWriter(
									new OutputStreamWriter(
											socket.getOutputStream(), "gbk"));
							pw2.println(Opreation.FRIEND_RELATIONSHIP_BREAKDOWN);
							System.out.println(user.getAccount()
									+ "服务器，发送给好友，关系破裂标志位");
							// 将自己id发给对方，提醒对方删除2个list对应的值
							pw2.println(selfId);
							pw2.flush();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				// 查询，准备添加新好友
				case Opreation.SEARCH_AND_ADD_NEW_FRIEND:
					// 获取客户端发送的查询条件和id
					String message = br.readLine();
					List<User> allUsers = userServiceProxy.getUsersByCondition(
							"%" + message.substring(0, message.indexOf(","))
									+ "%", Integer.valueOf(message
									.substring(message.indexOf(",") + 1)));
					pw.println(Opreation.SEARCH_AND_ADD_NEW_FRIEND);
					pw.println(JSONArray.fromObject(allUsers).toString());
					pw.flush();
					System.out.println("服务器 SEARCH_AND_ADD_NEW_FRIEND 向客户端发送 ："
							+ allUsers);
					break;
				// 添加新朋友时，发送好友请求
				case Opreation.SEND_HELLO_TO_USER:
					String oneLineMsg = "";
					String allMsg = "";
					// 获取要加好友的id
					newFriendId = br.readLine();
					// 获取发送的内容
					while ((oneLineMsg = br.readLine()) != null) {
						if ("stop the loop".equals(oneLineMsg)) {
							break;
						}
						allMsg += oneLineMsg + "\n";
					}
					allMsg = allMsg.substring(0, allMsg.length() - 1);
					/**
					 * 把添加请求的数据，入库
					 */
					try {
						newFriendRequestServiceProxy.addNewFriendRequest(
								user.getId(), user.getAccount(),
								user.getPetName(),
								Integer.valueOf(newFriendId), allMsg);
						pw.println(Opreation.SEND_HELLO_TO_USER);
						pw.println("成功发送请求，请等待对方回复");
						pw.flush();
					} catch (SQLException e) {
						e.printStackTrace();
					} catch (RepeatAddFriendException e) {
						pw.println(Opreation.SEND_HELLO_TO_USER);
						pw.println(e.getMessage());
						pw.flush();
						System.out.println("服务器主线程 RepeatAddFriendException");
						e.printStackTrace();
						continue;
					}
					System.out.println("准备判断，要加的人是不是在线");
					Socket targetSocket = allSockets.get(Integer
							.valueOf(newFriendId));
					if (targetSocket == null) {
						continue;
					}
					/**
					 * 如果要添加的好友在线
					 */
					List<NewFriendRequest> newFriendRequests = newFriendRequestServiceProxy
							.getNewFriendRequestsById(Integer
									.valueOf(newFriendId));
					PrintWriter pw2 = null;
					int i = 0;
					for (; i < newFriendRequests.size(); i++) {
						NewFriendRequest request = newFriendRequests.get(i);
						// 如果记录的访问次数是0，则是刚才新加入的好友申请
						if (request.getVisitedTimes() == 0) {
							// "告诉"安卓service，向用户发一次有好友请求的通知
							pw2 = new PrintWriter(new OutputStreamWriter(
									targetSocket.getOutputStream(), "gbk"));
							pw2.println(Opreation.LOOK_UP_NEW_FRIEND_REQUEST);
							pw2.println("yes");
							// pw2.println("show notification");
							// pw2.println(request.getSourceUserPetName());
							// pw2.flush();
							// pw2.print(request.getMessage());
							// pw2.println();
							// pw2.flush();
							// pw2.println("别急，急啥");
							pw2.flush();
							// 然后将这条记录，访问次数改成1，以免重复向用户发送通知
							newFriendRequestServiceProxy
									.modifyNewFriendRequestStatusById(request
											.getId());
							break;
						}
					}
					if (i == newFriendRequests.size()) {
						pw2.println("no");
						pw2.flush();
					}
					// pw2.println("别急，急啥");
					// pw2.flush();
					break;
				// 查看是否有，新的好友请求
				case Opreation.LOOK_UP_NEW_FRIEND_REQUEST:
					String targetUserId = br.readLine();
					List<NewFriendRequest> allNewFriendRequests = newFriendRequestServiceProxy
							.getNewFriendRequestsById(Integer
									.valueOf(targetUserId));
					System.out.println("服务器主线程allNewFriendRequests= "
							+ allNewFriendRequests);
					if (allNewFriendRequests.size() == 0) {
						pw.println(Opreation.LOOK_UP_NEW_FRIEND_REQUEST);
						// pw.println("别急，急啥");
						pw.println("no");
						pw.flush();
						continue;
					}
					pw.println(Opreation.LOOK_UP_NEW_FRIEND_REQUEST);
					int i2 = 0;
					for (; i2 < allNewFriendRequests.size(); i2++) {
						NewFriendRequest request = allNewFriendRequests.get(i2);
						// 如果有记录的访问次数是0，则是一条，没有发过通知的新好友请求
						if (request.getVisitedTimes() == 0) {
							// "告诉"安卓service，向用户发一次有好友请求的通知
							// pw.println("show notification");
							// pw.println(request.getSourceUserPetName());
							// pw.flush();
							// pw.print(request.getMessage());
							// pw.println();
							// pw.flush();
							// pw.println("别急，急啥");
							pw.println("yes");
							pw.flush();
							// 然后将这条记录，访问次数改成1，以免重复向用户发送通知
							newFriendRequestServiceProxy
									.modifyNewFriendRequestStatusById(request
											.getId());
							break;
						}
					}
					if (i2 == allNewFriendRequests.size()) {
						pw.println("no");
						pw.flush();
					}
					// pw.println("别急，急啥");
					// pw.flush();
					break;
				// 显示所有的新好友请求
				case Opreation.SHOW_ALL_NEW_FRIEND_REQUEST:
					// 目标人id，也就是自己的所有好友请求
					String selfId = br.readLine();
					List<NewFriendRequest> wholeNewFriendRequests = newFriendRequestServiceProxy
							.getNewFriendRequestsById(Integer.valueOf(selfId));
					System.out.println("wholeNewFriendRequests="
							+ wholeNewFriendRequests);
					// 查到的结果发送给用户
					pw.println(Opreation.SHOW_ALL_NEW_FRIEND_REQUEST);
					pw.println(JSONArray.fromObject(wholeNewFriendRequests)
							.toString());
					pw.flush();

					break;
				// 同意好友请求
				case Opreation.AGREE_REQUEST:
					// 获取新增记录id,新增好友的id，账号，昵称和自己的id，账号，昵称
					String msg = br.readLine();
					String[] msgs = msg.split(" ");
					// 获取对请求状态的修改 0:未同意 1:已同意2:已拒绝
					String status = br.readLine();
					newFriendRequestServiceProxy.modifyNewFriendRequestStatus(
							Integer.valueOf(msgs[0]), Integer.valueOf(status));
					try {
						friendRelationshipsServiceProxy.addFriendRelationships(
								Integer.valueOf(msgs[1]), msgs[2], msgs[3],
								Integer.valueOf(msgs[4]), msgs[5], msgs[6]);
					} catch (Exception e) {
						e.printStackTrace();
					}
					pw.println(Opreation.AGREE_REQUEST);
					pw.println("您已同意该请求，快来和好友聊天吧！");
					pw.flush();

					Socket socket = allSockets.get(Integer.valueOf(msgs[4]));
					if (socket != null) {
						PrintWriter pw4 = new PrintWriter(
								new OutputStreamWriter(
										socket.getOutputStream(), "gbk"));
						pw4.println(Opreation.FRIEND_RELATIONSHIP_BUILD);
						pw4.flush();
					}
					break;
				// 拒绝好友请求
				case Opreation.DISAGREE_REQUEST:
					// 获取请求的id
					String requestId2 = br.readLine();
					// 获取对请求状态的修改 0:未同意 1:已同意2:已拒绝
					String status2 = br.readLine();
					newFriendRequestServiceProxy.modifyNewFriendRequestStatus(
							Integer.valueOf(requestId2),
							Integer.valueOf(status2));
					pw.println(Opreation.DISAGREE_REQUEST);
					pw.println("已拒绝该请求");
					pw.flush();
					break;
				// 退出
				case Opreation.QUIT:
					allSockets.remove(user.getId());
					userServiceProxy.modifyUserStatus(user.getId(), 0);
					System.out.println("退出后，现在的套接字集合:" + allSockets);
					pw.println(Opreation.QUIT);
					pw.flush();
					break;
				// 发送消息
				case Opreation.SEND_MSG:
					String senderIdAndName = br.readLine();
					Integer recieverId = Integer.valueOf(senderIdAndName
							.substring(0, senderIdAndName.indexOf(" ")));
					String recieverPetName = senderIdAndName
							.substring(senderIdAndName.indexOf(" ") + 1);

					String s = "";
					String msg2 = "";
					while ((s = br.readLine()) != null) {
						if ("stop the loop".equals(s)) {
							break;
						}
						msg2 += s + "\n";
					}
					msg2 = msg2.substring(0, msg2.length() - 1);
					// 生成要入库的聊天记录
					ChatRecord chatRecord = new ChatRecord();
					chatRecord.setSenderId(user.getId());
					chatRecord.setSenderPetName(user.getPetName());
					chatRecord.setRecieverId(recieverId);
					chatRecord.setRecieverPetName(recieverPetName);
					chatRecord.setMessage(msg2);
					chatRecord.setDate(DateUtil.getCurrentTime());

					// 获取接受信息人的socket
					Socket aimSocket = allSockets.get(Integer
							.valueOf(recieverId));
					// 若对方在线
					if (aimSocket != null) {
						PrintWriter pw3 = new PrintWriter(
								new OutputStreamWriter(
										aimSocket.getOutputStream(), "gbk"));
						pw3.println(Opreation.ACCEPT_MSG);
						pw3.println(user.getId());
						pw3.println(user.getPetName());
						pw3.println(msg2);
						pw3.println("stop the loop");
						pw3.println(chatRecord.getDate());
						pw3.flush();

					} else {
						// 对方下线时
						// 生成下线记录
						NewChat newChat = new NewChat();
						newChat.setSenderId(user.getId());
						newChat.setRecieverId(recieverId);
						// 将记录保存在newChatList，若A向B发送多条信息，只保存一条A和B的记录
						int i1 = 0;
						for (; i1 < newChatList.size(); i1++) {
							// 如果要入库的记录，已存在，则放弃入库
							if (newChatList.get(i1).getRecieverId() == newChat
									.getRecieverId()
									&& newChatList.get(i1).getSenderId() == newChat
											.getSenderId()) {
								break;
							}
						}
						// i和newChatList长度相等时，说明没有一条记录和，即将入库的记录一致，此时可入库
						if (i1 == newChatList.size()) {
							newChatList.add(newChat);
							newChatServiceProxy.addNewChat(newChat);
						}
					}
					// 生成最近聊天记录
					RecentChat recentChat = new RecentChat();
					recentChat.setSenderId(user.getId());
					recentChat.setRecieverId(recieverId);
					// 将记录保存在recentChatList，若A向B发送多条信息，只保存一条A和B的记录
					int i1 = 0;
					for (; i1 < recentChatList.size(); i1++) {
						// 如果要入库的记录，已存在，则放弃入库
						if (recentChatList.get(i1).getRecieverId() == recentChat
								.getRecieverId()
								&& recentChatList.get(i1).getSenderId() == recentChat
										.getSenderId()) {
							break;
						}
					}
					// i和recentChatList长度相等时，说明没有一条记录和，即将入库的记录一致，此时可入库
					System.out.println("i1=" + i1 + "  recentChatList.size()="
							+ recentChatList.size());
					if (i1 == recentChatList.size()) {
						recentChatList.add(recentChat);
						recentChatServiceProxy.addRecentChat(recentChat);
					}
					System.out.println(user.getAccount()
							+ "添加了 recentChatList=" + recentChatList);
					// 无论对方是否在线，聊天记录表，入库
					chatRecordServiceProxy.addCharRecord(chatRecord);
					break;
				case Opreation.LOOK_UP_ALL_CHAT_RECORD:
					// 获取接收方id
					Integer recieverId2 = Integer.valueOf(br.readLine());
					// 获取最近联系人
					List<RecentChat> recentChats = recentChatServiceProxy
							.getRecentChatsByRecieverId(recieverId2);
					// 获取下线联系人
					List<NewChat> newChats = newChatServiceProxy
							.getNewChatsByRecieverId(recieverId2);
					// set用于存储所有，给自己发送过信息的好友id
					Set<Integer> set = new HashSet<Integer>();

					// newChatSenderIds给下线时发送信息的好友id，做一个备份.
					// 发给客户端，用以显示小红点和音乐
					StringBuffer newChatSenderIds = new StringBuffer();
					for (RecentChat chat : recentChats) {
						set.add(chat.getSenderId());
					}
					for (NewChat chat : newChats) {
						set.add(chat.getSenderId());
						newChatSenderIds.append(chat.getSenderId() + ",");
					}
					// 所有的聊天记录
					List<ChatRecord> allLastChatRecords = new ArrayList<ChatRecord>();
					for (Integer integer : set) {
						ChatRecord record = chatRecordServiceProxy
								.getLastChatRecordsByTwoSidesId(integer,
										recieverId2);
						allLastChatRecords.add(record);
					}
					pw.println(Opreation.LOOK_UP_ALL_CHAT_RECORD);
					pw.println(newChatSenderIds);
					pw.println(JSONArray.fromObject(allLastChatRecords)
							.toString());
					pw.flush();
					System.out.println("服务器 LOOK_UP_ALL_CHAT_RECORD");
					System.out.println("newChatSenderIds=" + newChatSenderIds);
					System.out.println("------------");
					System.out.println("allLastChatRecords="
							+ allLastChatRecords);
					System.out.println("------------");
					break;
				// 查看双方聊天记录
				case Opreation.LOOK_UP_CHAT_RECORD_BY_TWO_SIDES_ID:
					String twoSidesId = br.readLine();
					List<ChatRecord> wholeChatRecords = chatRecordServiceProxy
							.getChatRecordsByTwoSidesId(Integer
									.valueOf(twoSidesId.substring(0,
											twoSidesId.indexOf(","))), Integer
									.valueOf(twoSidesId.substring(twoSidesId
											.indexOf(",") + 1)));
					pw.println(Opreation.LOOK_UP_CHAT_RECORD_BY_TWO_SIDES_ID);
					pw.println(JSONArray.fromObject(wholeChatRecords)
							.toString());
					pw.flush();
					break;
				// 删除下线联系人的记录
				case Opreation.DELETE_NEW_CHAT:
					String SenderAndRecieverId = br.readLine();
					newChatServiceProxy.removeNewChatBySenderAndRecieverId(
							Integer.valueOf(SenderAndRecieverId.substring(0,
									SenderAndRecieverId.indexOf(","))), Integer
									.valueOf(SenderAndRecieverId
											.substring(SenderAndRecieverId
													.indexOf(",") + 1)));
					break;
				// 好友将自己删除
				case Opreation.DELETE_TWO_LIST_RECORD:
					// 此时，删除两个List的相关数据
					String friendId = br.readLine();
					recentChatList.remove(new RecentChat(Integer
							.valueOf(friendId), user.getId()));
					recentChatList.remove(new RecentChat(user.getId(), Integer
							.valueOf(friendId)));

					newChatList.remove(new NewChat(Integer.valueOf(friendId),
							user.getId()));
					newChatList.remove(new NewChat(user.getId(), Integer
							.valueOf(friendId)));
					System.out.println("-------------");
					System.out.println(user.getAccount()
							+ "服务器收到了，要删除两个list的通知，删除后");
					System.out.println("recentChatList=" + recentChatList);
					System.out.println("newChatList=" + newChatList);
					System.out.println("-------------");
					break;
				default:
					break;
				}
			}
		} catch (UnsupportedEncodingException e) {
			System.out
					.println("ServerMainThread 报UnsupportedEncodingException");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("ServerMainThread 报IOException");
			e.printStackTrace();
		} catch (NumberFormatException e) {
			System.out.println("ServerMainThread 报NumberFormatException");
			// 强关app导致读到客户端的 操作指令 为null
			allSockets.remove(user.getId());
			System.out.println("强关后，现在的套接字集合:" + allSockets);
			userServiceProxy.modifyUserStatus(user.getId(), 0);
			e.printStackTrace();
		}

	}
}
