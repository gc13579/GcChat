package com.example.gcchat222;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import com.example.Opreation.Opreation;
import com.example.enity.ChatRecord;
import com.example.enity.User;
import com.example.util.DateUtil;
import com.example.util.InteractiveBetweenRecieverAndActivity;
import com.example.util.MyReciever;
import com.example.util.SocketUtil;
import com.example.util.ToastUtil;

public class Chat extends Activity implements
		InteractiveBetweenRecieverAndActivity {
	private TextView showChatFriend;
	private TextView showAllMsgs;
	private EditText message;
	private ScrollView scrollview;
	private Button send;
	/**
	 * ���ѵ�id�����ڷ�������������������allSockets��ͨ������id ��ȷ�����ĸ�socket����Ϣ
	 */
	private Integer friendId;
	private String friendPetName;
	private PrintWriter pw;
	private Socket socket;
	private boolean isDisconnected;
	private User user;
	private MyReciever myReciever;
	private MyReciever myReciever2;
	private MyReciever myReciever3;
	private boolean isRegisteredReciever;
	private boolean isBreakDown;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (isRegisteredReciever) {
			unregisterReceiver(myReciever);
			unregisterReceiver(myReciever2);
			unregisterReceiver(myReciever3);
		}
		isRegisteredReciever = false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		// ��ֹ����
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		showChatFriend = (TextView) findViewById(R.id.showChatFriend);
		showAllMsgs = (TextView) findViewById(R.id.showAllMsgs);
		message = (EditText) findViewById(R.id.message);
		scrollview = (ScrollView) findViewById(R.id.scrollview);
		send = (Button) findViewById(R.id.send);
		Bundle bundle = Chat.this.getIntent().getExtras();
		if (bundle != null) {
			user = (User) bundle.getSerializable("user");
			friendPetName = bundle.getString("chatFriendPetName");
			showChatFriend.setText(friendPetName);
			friendId = bundle.getInt("chatFriendId");
		}

		// ��������ʾ�����·�
		scrollview.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						scrollview.post(new Runnable() {
							@Override
							public void run() {
								scrollview.fullScroll(ScrollView.FOCUS_DOWN);
							}
						});
					}
				});

		myReciever = new MyReciever();
		IntentFilter filter = new IntentFilter(
				"LOOK_UP_CHAT_RECORD_BY_TWO_SIDES_ID");
		registerReceiver(myReciever, filter);
		myReciever.setInteractive(this);

		myReciever2 = new MyReciever();
		IntentFilter filter2 = new IntentFilter("ACCEPT_MSG");
		registerReceiver(myReciever2, filter2);
		myReciever2.setInteractive(this);

		// ��ѯ�����������¼
		new MyAsyncTask2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
				friendId, user.getId());

		// ���Ͱ�ť
		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String msg = message.getText().toString();
				if (msg.length() > 30) {
					ToastUtil.toastInScreenMiddle("һ����෢��30����Ӵ~", Chat.this);
					return;
				}
				if (isBreakDown) {
					ToastUtil.toastInScreenMiddle("�Է��ѽ���ɾ�����޷�������Ϣ", Chat.this);
					return;
				}
				new MyAsyncTask().executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR, friendId.toString(),
						friendPetName, msg);
				// һ�η��ͺ��������򣬽�������Ϣ��ʾ��ҳ��
				message.setText("");
				showAllMsgs.append(DateUtil.getCurrentTime() + "\n"
						+ user.getPetName() + "˵:" + msg + "\n\n");

				if (!isRegisteredReciever) {
					myReciever3 = new MyReciever();
					IntentFilter filter3 = new IntentFilter(
							"FRIEND_RELATIONSHIP_BREAKDOWN");
					registerReceiver(myReciever3, filter3);
					myReciever3.setInteractive(Chat.this);
					isRegisteredReciever = true;
				}
			}
		});

		// Ϊ���������С��ʽ
		setIconForMessage(message);
	}

	public class MyAsyncTask extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... params) {
			// ��ȡ�׽���
			socket = SocketUtil.getSocket(isDisconnected);
			try {
				pw = new PrintWriter(new OutputStreamWriter(
						socket.getOutputStream(), "gbk"));
				// ����������ͣ�������Ϣ�ı�־λ
				pw.println(Opreation.SEND_MSG);
				// ����������ͣ�������Ϣ��socket���û�id,�ǳ�
				pw.println(params[0] + " " + params[1]);
				// ����������ͣ���������
				pw.println(params[2]);
				pw.println("stop the loop");
				pw.flush();

				cancel(true);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public class MyAsyncTask2 extends AsyncTask<Integer, Void, Void> {
		@Override
		protected Void doInBackground(Integer... params) {
			// ��ȡ�׽���
			socket = SocketUtil.getSocket(isDisconnected);
			try {
				pw = new PrintWriter(new OutputStreamWriter(
						socket.getOutputStream(), "gbk"));
				pw.println(Opreation.LOOK_UP_CHAT_RECORD_BY_TWO_SIDES_ID);
				pw.println(params[0] + "," + params[1]);
				pw.flush();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	// Ϊ���������С��ʽ
	public void setIconForMessage(EditText message) {
		Drawable d = getResources().getDrawable(R.drawable.send);
		d.setBounds(0, 0, 80, 80);
		message.setCompoundDrawables(d, null, null, null);
	}

	@Override
	public void setMsgFromServer(String msg) {
		// ����ǿգ���������յ������ѹ�ϵ���ѱ�־λ�����Է����Լ�ɾ��
		if ("breakdown".equals(msg)) {
			isBreakDown = true;
			return;
		}
		// ��ʾ���������������¼
		ArrayList<ChatRecord> list = new ArrayList<ChatRecord>();
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(msg);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject obj = jsonArray.getJSONObject(i);
				showAllMsgs.append(obj.getString("date") + "\n"
						+ obj.getString("senderPetName") + "˵:"
						+ obj.getString("message") + "\n\n");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setMsgFromServer2(Integer senderId, String senderName,
			String message, String currentTime) {
		showAllMsgs.append(currentTime + "\n" + senderName + "˵:" + message
				+ "\n\n");
	}

	@Override
	public void setMsgFromServer3(String newChatSenderIds,
			String allLastChatRecords) {
	}
}
