package com.example.gcchat222;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.Opreation.Opreation;
import com.example.enity.NewFriendRequest;
import com.example.enity.User;
import com.example.util.InteractiveBetweenRecieverAndActivity;
import com.example.util.MyReciever;
import com.example.util.SocketUtil;
import com.example.util.ToastUtil;

public class HandleNewFriendRequest extends Activity implements
		InteractiveBetweenRecieverAndActivity {
	private ListView listView;
	private boolean isDisconnected = false;
	private Socket socket;
	private PrintWriter pw;
	private BufferedReader br;
	private User user;
	private AlertDialog.Builder builder;
	private ProgressDialog progressDialog;
	private MyReciever myReciever;
	private MyReciever myReciever2;
	private MyReciever myReciever3;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(myReciever);
		unregisterReceiver(myReciever2);
		unregisterReceiver(myReciever3);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_handle_new_friend_request);
		// 禁止横屏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		listView = (ListView) findViewById(R.id.listView);
		builder = new AlertDialog.Builder(HandleNewFriendRequest.this);
		progressDialog = new ProgressDialog(HandleNewFriendRequest.this);
		progressDialog.setTitle("查询中");
		progressDialog.setMessage("正在查询，以下情况可能花费更长时间，请耐心等待\n1.断线重连\n2.网络不佳");
		progressDialog.setCancelable(false);
		Bundle bundle = HandleNewFriendRequest.this.getIntent().getExtras();
		System.out.println("HandleNewFriendRequest bundle=" + bundle);
		if (bundle != null) {
			user = (User) bundle.getSerializable("user");
		} else {
			user = (User) getIntent().getSerializableExtra("user");
		}
		System.out.println("HandleNewFriendRequest user=" + user);
		// 监听，显示所有好友请求
		myReciever = new MyReciever();
		IntentFilter filter = new IntentFilter("SHOW_ALL_NEW_FRIEND_REQUEST");
		registerReceiver(myReciever, filter);
		myReciever.setInteractive(this);
		// 监听同意好友请求
		myReciever2 = new MyReciever();
		IntentFilter filter2 = new IntentFilter("AGREE_REQUEST");
		registerReceiver(myReciever2, filter2);
		myReciever2.setInteractive(this);
		// 监听拒绝好友请求
		myReciever3 = new MyReciever();
		IntentFilter filter3 = new IntentFilter("DISAGREE_REQUEST");
		registerReceiver(myReciever3, filter3);
		myReciever3.setInteractive(this);

		new LoginAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public class LoginAsyncTask extends
			AsyncTask<Void, Void, List<NewFriendRequest>> {

		@Override
		protected List<NewFriendRequest> doInBackground(Void... params) {
			// 创立套接字
			socket = SocketUtil.getSocket(isDisconnected);
			isDisconnected = false;
			List<NewFriendRequest> list = null;
			try {
				pw = new PrintWriter(new OutputStreamWriter(
						socket.getOutputStream(), "gbk"));
				br = new BufferedReader(new InputStreamReader(
						socket.getInputStream(), "gbk"));
				// 向服务器发送，显示所有新好友请求的标志物
				pw.println(Opreation.SHOW_ALL_NEW_FRIEND_REQUEST);
				// 发送自己的id
				pw.println(user.getId());
				pw.flush();
			} catch (UnsupportedEncodingException e) {
				System.out
						.println("HandleNewFriendRequest 报UnsupportedEncodingException");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("HandleNewFriendRequest 报IOException");
				e.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return list;
		}

	}

	@Override
	public void setMsgFromServer(String msg) {
		System.out.println("处理好友请求页面 请求有=" + msg);
		/**
		 * 同意或拒绝好友请求
		 */
		if ("您已同意该请求，快来和好友聊天吧！".equals(msg) || "已拒绝该请求".equals(msg)) {
			// Looper.prepare();
			showMessageAndJumpPage(msg);
			// Looper.loop();
			return;
		}
		ArrayList<NewFriendRequest> list = new ArrayList<NewFriendRequest>();
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(msg);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject obj = jsonArray.getJSONObject(i);
				list.add(new NewFriendRequest(Integer.valueOf(obj.get("id")
						.toString()), Integer.valueOf(obj.get("sourceUserId")
						.toString()), obj.get("sourceUserAccount").toString(),
						obj.get("sourceUserPetName").toString(), Integer
								.valueOf(obj.get("targetUserId").toString()),
						obj.get("message").toString(), Integer.valueOf(obj.get(
								"status").toString()), Integer.valueOf(obj.get(
								"visitedTimes").toString())));
			}
			List<Map<String, String>> data = new ArrayList<Map<String, String>>();
			if (list != null && list.size() != 0) {
				String status = "";
				for (NewFriendRequest newFriendRequest : list) {
					Map<String, String> rowData = new HashMap<String, String>();
					rowData.put("petName",
							newFriendRequest.getSourceUserPetName());
					rowData.put("message", newFriendRequest.getMessage());
					switch (newFriendRequest.getStatus()) {
					case 0:
						status = "未同意";
						break;
					case 1:
						status = "已同意";
						break;
					case 2:
						status = "已拒绝";
						break;
					}
					rowData.put("status", status);
					rowData.put("id", newFriendRequest.getId().toString());
					rowData.put("sourceUserId", newFriendRequest
							.getSourceUserId().toString());
					rowData.put("account",
							newFriendRequest.getSourceUserAccount());
					data.add(rowData);
				}
			}
			listView.setAdapter(new SimpleAdapter(HandleNewFriendRequest.this,
					data, R.layout.listviewformat2, new String[] { "petName",
							"message", "status" }, new int[] {
							R.id.listviewformat2_name,
							R.id.listviewformat2_msg,
							R.id.listviewformat2_status }));

			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, final View view,
						int position, long lineNumber) {
					final HashMap<String, String> hashMap = (HashMap<String, String>) parent
							.getItemAtPosition(position);
					System.out.println(hashMap);
					if (hashMap.get("status") == "已同意"
							|| hashMap.get("status") == "已拒绝") {
						return;
					}
					builder.setNegativeButton("同意", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							new Thread() {
								public void run() {
									pw.println(Opreation.AGREE_REQUEST);
									// 将新增记录的id，新增好友的id，账号，昵称和自己的id，账号，昵称发送给服务器
									pw.println(hashMap.get("id") + " "
											+ user.getId() + " "
											+ user.getAccount() + " "
											+ user.getPetName() + " "
											+ hashMap.get("sourceUserId") + " "
											+ hashMap.get("account") + " "
											+ hashMap.get("petName"));
									pw.println(1);
									pw.flush();
									// try {
									// message = br.readLine();
									// } catch (IOException e) {
									// e.printStackTrace();
									// }

								};
							}.start();
						}
					}).setPositiveButton("拒绝", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							new Thread() {
								public void run() {
									pw.println(Opreation.DISAGREE_REQUEST);
									pw.println(hashMap.get("id"));
									pw.println(2);
									pw.flush();
									// try {
									// message = br.readLine();
									// } catch (IOException e) {
									// e.printStackTrace();
									// }
								};
							}.start();
						}
					}).create().show();
				}
			});
			progressDialog.dismiss();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public void showMessageAndJumpPage(String message) {
		// Looper.prepare();
		ToastUtil.toastInScreenMiddle(message, HandleNewFriendRequest.this);
		Intent intent;
		intent = new Intent(HandleNewFriendRequest.this,
				HandleNewFriendRequest.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("user", user);
		intent.putExtras(bundle);

		startActivity(intent);
		HandleNewFriendRequest.this.finish();
		// Looper.loop();
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
