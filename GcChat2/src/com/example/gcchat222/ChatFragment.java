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

	// ����������ص���������
	AssetFileDescriptor assetFileDescriptor;
	MediaPlayer mediaPlayer;

	private boolean isStartedMusic;
	// ��MainActivity���գ����Լ�������Ϣ�˵�id��������ʾ��������
	private String allSenderIds;
	private Map<Integer, String> allSenderIdMap = new HashMap<Integer, String>();

	private InteractiveBetweenFragmentAndActivity interactive;

	// ͳ�ƺ�����
	private int redCount = 0;
	// �Ƿ�ע����㲥
	private boolean isRegistered;
	// ��ǰ�����������id,���ݸ�MainActivity
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
		// ��ȡ�����˵�id
		allSenderIds = ((MainActivity) myActivity).getAllSenderIds();
		System.out.println("������Ƭ  onResume   ��ȡ����allSenderIds=" + allSenderIds);
		// ��֮ǰ�洢�����˵�map��գ����´洢
		allSenderIdMap.clear();
		String[] strs = allSenderIds.split(",");
		for (int i = 0; i < strs.length; i++) {
			if (!"".equals(strs[i])) {
				allSenderIdMap.put(Integer.valueOf(strs[i]), "0");
			}
		}
		System.out.println("������Ƭ  onResume  allSenderIdMap=" + allSenderIdMap);
		// ToastUtil.toastInScreenMiddle("allSendedIds:" + allSenderIds + "\n"
		// + "allSenderIdMap:" + allSenderIdMap, myActivity);
		new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		// ����ǰ�������id(null)���ݸ�MainActivity
		currentFriendId = null;
		((MainActivity) myActivity).setCurrentChatFriendId(currentFriendId);
		System.out.println("onResume   currentFriendId=" + currentFriendId);
		System.out.println("����һ������");
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
		// ���û��ע�������㲥����ע�ᣬ��ֹÿ�μ��ظ�ҳ�棬��ע��һ���㲥
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
			// �����׽���
			socket = SocketUtil.getSocket(isDisconnected);
			isDisconnected = false;
			try {
				pw = new PrintWriter(new OutputStreamWriter(
						socket.getOutputStream(), "gbk"));
				// ����������ͣ��鿴 �����¼��־λ
				pw.println(Opreation.LOOK_UP_ALL_CHAT_RECORD);
				// ����������ͣ��Լ���id
				pw.println(user.getId());
				pw.flush();
				System.out.println(user.getAccount()
						+ "����Ƭ�Σ�����������ͣ��鿴���������¼����־λ");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	// ���ڴ��������
	// 1.��ǰfragmentΪChatFragment�����ѷ�����Ϣ����MainActivity����ʱ��MainActivity���͹㲥��Ϣ
	// ���ѵ�ǰfragment�Զ�ˢ��
	// 2.���ڵ�ǰfragmentʱ������ɾ�����Լ�
	@Override
	public void setMsgFromServer(String msg) {
		// ��ȡ�����˵�id
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
		System.out.println("������Ƭ setMsgFromServer   ��ȡ����allSenderIds="
				+ allSenderIds);
		System.out.println("������Ƭ  setMsgFromServer   allSenderIdMap="
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
		System.out.println("�յ�һ������");
		System.out.println("newChatSenderIds=" + newChatSenderIds);
		System.out.println("allLastChatRecords=" + allLastChatRecords);
		Map<Integer, String> map = null;
		// newChatSenderIds��map����ǣ�����ʱ�����з����˵�id
		if (newChatSenderIds.length() > 0) {
			String[] allNewChatSenderIds = newChatSenderIds.split(",");
			map = new HashMap<Integer, String>();
			for (int i = 0; i < allNewChatSenderIds.length; i++) {
				map.put(Integer.valueOf(allNewChatSenderIds[i]), "");
			}
		}
		// ����list�����շ��������͹����ģ����У����һ�������¼
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
			// System.out.println("����list = " + list);
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
									+ "˵:"
									+ (chatRecord.getMessage().length() > 10 ? chatRecord
											.getMessage().substring(0, 10)
											+ "..." : chatRecord.getMessage()));
					rowData.put("time", chatRecord.getDate());
					rowData.put(
							"friendId",
							chatRecord.getSenderId().equals(user.getId()) ? chatRecord
									.getRecieverId().toString() : chatRecord
									.getSenderId().toString());
					// ���������¼�ķ�����id�����ڣ�����ʱ�������˵�id
					if (map != null
							&& map.get(chatRecord.getSenderId()) != null) {
						// ��ʾ���
						rowData.put("flag", R.drawable.red2);
						((MainActivity) myActivity).getTab().setIcon(
								R.drawable.red);
						// ���û�в��Ź���Ϣ��������
						if (!isStartedMusic) {
							mediaPlayer.prepare();
							mediaPlayer.start();
							mediaPlayer.seekTo(0);
							isStartedMusic = true;
						}
					}
					// ���������¼�� 1.���������Լ� 2.�������Ƿ��������ݹ����ģ�������id
					// ��ʾ��������
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
						System.out.println("��ʾ��ϣ�redCount = " + redCount);
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
					// ���ĳ����¼����ת����Ӧ����ҳ��
					final HashMap<String, Object> hashMap = (HashMap<String, Object>) parent
							.getItemAtPosition(position);
					// ����������ͣ�ɾ��newChat��־λ
					// ����ɾ�����߱��¼
					new Thread() {
						public void run() {
							pw.println(Opreation.DELETE_NEW_CHAT);
							pw.println(hashMap.get("friendId") + ","
									+ user.getId());
							pw.flush();
						};
					}.start();
					// "����"MainActivity�����һ�����߷�����id
					interactive.sendMessageToActivity(hashMap.get("friendId")
							.toString());
					if (redCount > 0) {
						redCount--;
					}
					if (redCount == 0) {
						((MainActivity) myActivity).getTab().setIcon(null);
					}
					// ����ǰ����ҳ��ĺ���id���͸�MainActivity
					// ��MainActivity�յ�������Ϣ�ķ�����id�͵�ǰ����ҳ��ĺ���id
					// ����ȣ�����ʾ��������
					currentFriendId = Integer.valueOf(hashMap.get("friendId")
							.toString());
					((MainActivity) myActivity)
							.setCurrentChatFriendId(currentFriendId);

					// ��ת������ҳ��
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
