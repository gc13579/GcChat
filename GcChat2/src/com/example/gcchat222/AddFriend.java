package com.example.gcchat222;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.HashMap;
import com.example.Opreation.Opreation;
import com.example.enity.User;
import com.example.util.InteractiveBetweenRecieverAndActivity;
import com.example.util.MyReciever;
import com.example.util.SocketUtil;
import com.example.util.ToastUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddFriend extends Activity implements
		InteractiveBetweenRecieverAndActivity {
	private TextView textView;
	private EditText editText;
	private Button addFriend;
	private Button back;
	private boolean isDisconnected = false;
	private Socket socket;
	private PrintWriter pw;
	private BufferedReader br;
	private String newFriendId;
	private MyReciever myReciever;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(myReciever);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_friend);
		// 禁止横屏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		textView = (TextView) findViewById(R.id.textView);
		editText = (EditText) findViewById(R.id.editText);
		addFriend = (Button) findViewById(R.id.addFriend);
		back = (Button) findViewById(R.id.back);

		HashMap<String, String> hashMap = null;
		Bundle bundle = AddFriend.this.getIntent().getExtras();
		if (bundle != null) {
			hashMap = (HashMap) bundle.getSerializable("hashMap");
			newFriendId = hashMap.get("id");
			textView.setText("要添加的好友:" + hashMap.get("petName").toString());
			editText.setText("我是 "
					+ ((User) bundle.getSerializable("selfInfo")).getPetName()
					+ " ,来和我做朋友吧");
		}

		myReciever = new MyReciever();
		IntentFilter filter = new IntentFilter("SEND_HELLO_TO_USER");
		registerReceiver(myReciever, filter);
		myReciever.setInteractive(this);

		// 添加朋友
		addFriend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new MyAsyncTask().executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR, newFriendId, editText
								.getText().toString());
			}
		});
		// 返回
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AddFriend.this, Search.class);
				startActivity(intent);
				AddFriend.this.finish();
			}
		});
	}

	public class MyAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			// 创立套接字
			socket = SocketUtil.getSocket(isDisconnected);
			// 接收服务器返回消息（成功发送/不能重复添加）
			String message = "";
			isDisconnected = false;
			try {
				pw = new PrintWriter(new OutputStreamWriter(
						socket.getOutputStream(), "gbk"));
				br = new BufferedReader(new InputStreamReader(
						socket.getInputStream(), "gbk"));
				pw.println(Opreation.SEND_HELLO_TO_USER);
				// 要加的好友id
				pw.println(params[0]);
				// 发送的内容
				pw.println(params[1]);
				pw.println("stop the loop");
				pw.flush();

				// message = br.readLine();
			} catch (UnsupportedEncodingException e) {
				System.out.println("添加好友页面，报UnsupportedEncodingException");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("添加好友页面，报IOException");
				e.printStackTrace();
			}
			return message;
		}

		// @Override
		// protected void onPostExecute(String result) {
		// ToastUtil.toastInScreenMiddle(result, AddFriend.this);
		// }
	}

	@Override
	public void setMsgFromServer(String msg) {
		ToastUtil.toastInScreenMiddle(msg, AddFriend.this);
	}

	@Override
	public void setMsgFromServer2(Integer senderId, String senderName,
			String message, String currentTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMsgFromServer3(String newChatSenderIds,
			String allLastChatRecords) {
		// TODO Auto-generated method stub

	}

}
