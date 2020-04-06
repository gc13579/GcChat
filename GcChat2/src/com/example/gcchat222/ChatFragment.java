package com.example.gcchat222;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.database.DataSetObserver;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.Opreation.Opreation;
import com.example.enity.ChatRecord;
import com.example.enity.User;
import com.example.util.InteractiveBetweenFragmentAndActivity;
import com.example.util.InteractiveBetweenRecieverAndActivity;
import com.example.util.MyReciever;
import com.example.util.SocketUtil;
import com.example.util.ToastUtil;

public class ChatFragment extends Fragment implements
		InteractiveBetweenRecieverAndActivity {
	private Socket socket;
	private PrintWriter pw;
	private User user;
	private boolean isDisconnected;
	private ListView listView;
	private Activity myActivity;

	IntentFilter intentFilter;
	private MyReciever myReciever;
	private MyReciever myReciever2;
	private MyReciever myReciever3;

	// 播放音乐相关的两个对象
	AssetFileDescriptor assetFileDescriptor;
	MediaPlayer mediaPlayer;

	private boolean isStartedMusic;
	// 从MainActivity接收，向自己发送信息人的id，用以显示红点和音乐
	private String allSenderIds;
	private Map<Integer, String> allSenderIdMap = new HashMap<Integer, String>();

	private InteractiveBetweenFragmentAndActivity interactive;

	// 统计红点个数
	private int redCount = 0;
	// 是否注册过广播
	private boolean isRegistered;
	// 当前正在聊天好友id,传递给MainActivity
	private Integer currentFriendId;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.myActivity = activity;
		interactive = (InteractiveBetweenFragmentAndActivity) myActivity;
	}

	@Override
	public void onResume() {
		super.onResume();
		redCount = 0;
		Bundle bundle = this.getArguments();
		if (bundle != null) {
			user = (User) bundle.getSerializable("user");
		}
		// 获取发送人的id
		allSenderIds = ((MainActivity) myActivity).getAllSenderIds();
		System.out.println("聊天碎片  onResume   获取到的allSenderIds=" + allSenderIds);
		// 将之前存储发送人的map清空，重新存储
		allSenderIdMap.clear();
		String[] strs = allSenderIds.split(",");
		for (int i = 0; i < strs.length; i++) {
			if (!"".equals(strs[i])) {
				allSenderIdMap.put(Integer.valueOf(strs[i]), "0");
			}
		}
		System.out.println("聊天碎片  onResume  allSenderIdMap=" + allSenderIdMap);
		// ToastUtil.toastInScreenMiddle("allSendedIds:" + allSenderIds + "\n"
		// + "allSenderIdMap:" + allSenderIdMap, myActivity);
		new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		// 将当前聊天好友id(null)传递给MainActivity
		currentFriendId = null;
		((MainActivity) myActivity).setCurrentChatFriendId(currentFriendId);
		System.out.println("onResume   currentFriendId=" + currentFriendId);
		System.out.println("发出一次请求");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		redCount = 0;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_chat, container, false);
		listView = (ListView) view.findViewById(R.id.listView);
		// 如果没有注册过这个广播，则注册，防止每次加载该页面，都注册一个广播
		if (!isRegistered) {
			myReciever = new MyReciever();
			intentFilter = new IntentFilter("LOOK_UP_ALL_CHAT_RECORD");
			myActivity.getApplicationContext().registerReceiver(myReciever,
					intentFilter);
			myReciever.setInteractive(this);

			myReciever2 = new MyReciever();
			IntentFilter intentFilter = new IntentFilter("FLUSH_CHAT_TAB");
			myActivity.getApplicationContext().registerReceiver(myReciever2,
					intentFilter);
			myReciever2.setInteractive(this);

			myReciever3 = new MyReciever();
			IntentFilter filter3 = new IntentFilter(
					"FRIEND_RELATIONSHIP_BREAKDOWN");
			myActivity.getApplicationContext().registerReceiver(myReciever3,
					filter3);
			myReciever3.setInteractive(this);
			isRegistered = true;
		}
		try {
			assetFileDescriptor = myActivity.getAssets().openFd("dingdong.wav");
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),
					assetFileDescriptor.getStartOffset(),
					assetFileDescriptor.getLength());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return view;
	}

	public class MyAsyncTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			// 创立套接字
			socket = SocketUtil.getSocket(isDisconnected);
			isDisconnected = false;
			try {
				pw = new PrintWriter(new OutputStreamWriter(
						socket.getOutputStream(), "gbk"));
				// 向服务器发送，查看 聊天记录标志位
				pw.println(Opreation.LOOK_UP_ALL_CHAT_RECORD);
				// 向服务器发送，自己的id
				pw.println(user.getId());
				pw.flush();
				System.out.println(user.getAccount()
						+ "聊天片段，向服务器发送，查看所有聊天记录，标志位");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	// 用于此种情况：
	// 1.当前fragment为ChatFragment，好友发送消息，被MainActivity接收时，MainActivity发送广播消息
	// 提醒当前fragment自动刷新
	// 2.处在当前fragment时，好友删除了自己
	@Override
	public void setMsgFromServer(String msg) {
		// 获取发送人的id
		allSenderIds = ((MainActivity) myActivity).getAllSenderIds();
		allSenderIdMap.clear();
		String[] strs = allSenderIds.split(",");
		for (int i = 0; i < strs.length; i++) {
			if (!"".equals(strs[i])) {
				allSenderIdMap.put(Integer.valueOf(strs[i]), "0");
			}
		}
		if ("breakdown".equals(msg)) {
			redCount = 0;
			new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			return;
		}
		System.out.println("聊天碎片 setMsgFromServer   获取到的allSenderIds="
				+ allSenderIds);
		System.out.println("聊天碎片  setMsgFromServer   allSenderIdMap="
				+ allSenderIdMap);
		redCount = 0;
		new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	public void setMsgFromServer2(Integer senderId, String senderName,
			String message, String currentTime) {
	}

	@Override
	public void setMsgFromServer3(String newChatSenderIds,
			String allLastChatRecords) {
		System.out.println("收到一次请求");
		System.out.println("newChatSenderIds=" + newChatSenderIds);
		System.out.println("allLastChatRecords=" + allLastChatRecords);
		Map<Integer, String> map = null;
		// newChatSenderIds和map存的是，下线时，所有发送人的id
		if (newChatSenderIds.length() > 0) {
			String[] allNewChatSenderIds = newChatSenderIds.split(",");
			map = new HashMap<Integer, String>();
			for (int i = 0; i < allNewChatSenderIds.length; i++) {
				map.put(Integer.valueOf(allNewChatSenderIds[i]), "");
			}
		}
		// 定义list，接收服务器发送过来的，所有，最后一条聊天记录
		ArrayList<ChatRecord> list = new ArrayList<ChatRecord>();
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(allLastChatRecords);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject obj;
				obj = jsonArray.getJSONObject(i);
				ChatRecord chatRecord = new ChatRecord(obj.getInt("id"),
						obj.getInt("senderId"), obj.getString("senderPetName"),
						obj.getInt("recieverId"),
						obj.getString("recieverPetName"),
						obj.getString("message"), obj.getString("date"));
				list.add(chatRecord);
			}
			// System.out.println("遍历list = " + list);
			List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
			if (list != null && list.size() != 0) {
				for (ChatRecord chatRecord : list) {
					Map<String, Object> rowData = new HashMap<String, Object>();
					rowData.put(
							"petName",
							chatRecord.getSenderPetName().equals(
									user.getPetName()) ? chatRecord
									.getRecieverPetName() : chatRecord
									.getSenderPetName());
					rowData.put(
							"message",
							chatRecord.getSenderPetName()
									+ "说:"
									+ (chatRecord.getMessage().length() > 10 ? chatRecord
											.getMessage().substring(0, 10)
											+ "..." : chatRecord.getMessage()));
					rowData.put("time", chatRecord.getDate());
					rowData.put(
							"friendId",
							chatRecord.getSenderId().equals(user.getId()) ? chatRecord
									.getRecieverId().toString() : chatRecord
									.getSenderId().toString());
					// 如果这条记录的发送人id存在于，下线时，发送人的id
					if (map != null
							&& map.get(chatRecord.getSenderId()) != null) {
						// 显示红点
						rowData.put("flag", R.drawable.red2);
						((MainActivity) myActivity).getTab().setIcon(
								R.drawable.red);
						// 如果没有播放过消息提醒音乐
						if (!isStartedMusic) {
							mediaPlayer.prepare();
							mediaPlayer.start();
							mediaPlayer.seekTo(0);
							isStartedMusic = true;
						}
					}
					// 如果该条记录的 1.接收人是自己 2.发送人是服务器传递过来的，发送人id
					// 显示红点和音乐
					if (allSenderIds != null
							&& allSenderIdMap.get(chatRecord.getSenderId()) != null
							&& allSenderIdMap.get(chatRecord.getSenderId())
									.equals("0")
							&& chatRecord.getRecieverId().equals(user.getId())) {
						rowData.put("flag", R.drawable.red2);
						mediaPlayer.start();
						mediaPlayer.seekTo(0);
						redCount++;
						allSenderIdMap.put(chatRecord.getSenderId(), "1");
						System.out.println("显示完毕，redCount = " + redCount);
					}
					data.add(rowData);
				}
			}
			listView.setAdapter(new SimpleAdapter(myActivity, data,
					R.layout.listviewformat3, new String[] { "petName",
							"message", "time", "flag" }, new int[] {
							R.id.listviewformat3_name,
							R.id.listviewformat3_msg,
							R.id.listviewformat3_time,
							R.id.listviewformat3_flag }));
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, final View view,
						int position, long lineNumber) {
					// 点击某条记录后，跳转到相应聊天页面
					final HashMap<String, Object> hashMap = (HashMap<String, Object>) parent
							.getItemAtPosition(position);
					// 向服务器发送，删除newChat标志位
					// 即，删除下线表记录
					new Thread() {
						public void run() {
							pw.println(Opreation.DELETE_NEW_CHAT);
							pw.println(hashMap.get("friendId") + ","
									+ user.getId());
							pw.flush();
						};
					}.start();
					// "告诉"MainActivity，清除一个在线发送人id
					interactive.sendMessageToActivity(hashMap.get("friendId")
							.toString());
					if (redCount > 0) {
						redCount--;
					}
					if (redCount == 0) {
						((MainActivity) myActivity).getTab().setIcon(null);
					}
					// 将当前聊天页面的好友id发送给MainActivity
					// 若MainActivity收到聊天信息的发送人id和当前聊天页面的好友id
					// 不相等，才显示红点和音乐
					currentFriendId = Integer.valueOf(hashMap.get("friendId")
							.toString());
					((MainActivity) myActivity)
							.setCurrentChatFriendId(currentFriendId);

					// 跳转至聊天页面
					Intent intent = new Intent(myActivity, Chat.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("user", user);
					bundle.putString("chatFriendPetName", hashMap
							.get("petName").toString());
					bundle.putInt("chatFriendId",
							Integer.valueOf(hashMap.get("friendId").toString()));
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
