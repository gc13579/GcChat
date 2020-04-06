package com.example.gcchat222;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.Opreation.Opreation;
import com.example.enity.User;
import com.example.util.InteractiveBetweenRecieverAndActivity;
import com.example.util.JudgeNetUtil;
import com.example.util.MyReciever;
import com.example.util.SocketUtil;
import com.example.util.ToastUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class Search extends Activity implements
		InteractiveBetweenRecieverAndActivity {

	private EditText searchEditText;
	private Button searchButton;
	private ProgressDialog progressDialog;
	private ListView listView;
	private AlertDialog.Builder builder;
	private boolean isDisconnected = false;
	private Socket socket;
	private PrintWriter pw;
	private BufferedReader br;
	private User user;
	private MyReciever myReciever;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		// 禁止横屏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		searchEditText = (EditText) findViewById(R.id.searchEditText);
		searchButton = (Button) findViewById(R.id.searchButton);
		listView = (ListView) findViewById(R.id.listView);
		builder = new AlertDialog.Builder(this);
		Bundle bundle = Search.this.getIntent().getExtras();
		if (bundle != null) {
			user = (User) bundle.getSerializable("user");
		}
		progressDialog = new ProgressDialog(Search.this);
		progressDialog.setTitle("查询中");
		progressDialog.setMessage("正在查询，以下情况可能花费更长时间，请耐心等待\n1.断线重连\n2.网络不佳");
		progressDialog.setCancelable(false);

		myReciever = new MyReciever();
		IntentFilter filter = new IntentFilter("SEARCH_AND_ADD_NEW_FRIEND");
		registerReceiver(myReciever, filter);
		myReciever.setInteractive(this);

		// 给查询框添加小图标样式
		addIconToAccountAndPwd(searchEditText);
		// 查询
		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 如果没有联网
				if (JudgeNetUtil.judgeNet(Search.this) == false) {
					ToastUtil.toastInScreenMiddle("连接失败，请稍后再试，请检查网络是否连接",
							Search.this);
					isDisconnected = true;
					return;
				}
				String searchCondition = searchEditText.getText().toString();
				// 表单验证
				if ("".equals(searchCondition.trim())) {
					ToastUtil.toastInScreenMiddle("查询条件不能为空", Search.this);
					return;
				}
				new MyAsyncTask().executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR, searchCondition, user
								.getId().toString());
			}
		});
	}

	public class MyAsyncTask extends AsyncTask<String, Void, List<User>> {
		@Override
		protected List<User> doInBackground(String... params) {
			// 创立套接字
			socket = SocketUtil.getSocket(isDisconnected);
			isDisconnected = false;
			List<User> list = null;
			try {
				pw = new PrintWriter(new OutputStreamWriter(
						socket.getOutputStream(), "gbk"));
				br = new BufferedReader(new InputStreamReader(
						socket.getInputStream(), "gbk"));
				// 向服务器发送，查询标志位
				pw.println(Opreation.SEARCH_AND_ADD_NEW_FRIEND);
				// 向服务器发送，查询条件和自己的id
				pw.println(params[0] + "," + params[1]);
				pw.flush();
				// String allUsers = br.readLine();
				// list = new ArrayList<User>();
				// JSONArray jsonArray = new JSONArray(allUsers);
				// for (int i = 0; i < jsonArray.length(); i++) {
				// JSONObject obj = jsonArray.getJSONObject(i);
				// list.add(new User(Integer
				// .parseInt(obj.get("id").toString()), obj.get(
				// "account").toString(), obj.get("password")
				// .toString(), obj.get("petName").toString()));
				// }
			} catch (UnsupportedEncodingException e) {
				System.out.println("查询用户页面，doInBackground报JSONException");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("查询用户页面，doInBackground报IOException");
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println("查询用户页面，doInBackground报JSONException");
				e.printStackTrace();
			}
			return list;
		}

		// @Override
		// protected void onPostExecute(List<User> result) {
		// List<Map<String, String>> data = new ArrayList<Map<String,
		// String>>();
		// Map<String, String> title = new HashMap<String, String>();
		// title.put("petName", "昵称");
		// title.put("account", "账号");
		// data.add(title);
		// if (result != null && result.size() != 0) {
		// for (User user : result) {
		// Map<String, String> rowData = new HashMap<String, String>();
		// rowData.put("petName", user.getPetName());
		// rowData.put("account", user.getAccount());
		// rowData.put("id", user.getId().toString());
		// data.add(rowData);
		// }
		// }
		// listView.setAdapter(new SimpleAdapter(Search.this, data,
		// R.layout.listviewformat, new String[] { "petName",
		// "account" }, new int[] {
		// R.id.listviewformat_petName,
		// R.id.listviewformat_account }));
		// listView.setOnItemClickListener(new OnItemClickListener() {
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long lineNumber) {
		// if (position == 0) {
		// return;
		// }
		// final HashMap<String, String> hashMap = (HashMap<String, String>)
		// parent
		// .getItemAtPosition(position);
		// System.out.println(hashMap);
		// parent.getItemAtPosition(position);
		// builder.setPositiveButton("添加好友",
		// new AlertDialog.OnClickListener() {
		// public void onClick(DialogInterface dialog,
		// int which) {
		// Intent intent = new Intent(Search.this,
		// AddFriend.class);
		// Bundle bundle = new Bundle();
		// bundle.putSerializable("selfInfo", user);
		// bundle.putSerializable("hashMap", hashMap);
		// intent.putExtras(bundle);
		// startActivity(intent);
		// }
		// }).create().show();
		// }
		//
		// });
		// progressDialog.dismiss();
		// cancel(true);
		// }
	}

	// 给查询框添加小图标样式
	public void addIconToAccountAndPwd(EditText searchEditText) {
		Drawable d1 = getResources().getDrawable(R.drawable.search);
		d1.setBounds(0, 0, 80, 80);// 第一个 0 是距左边距离，第二个 0 是距上边距离，80 分别是长宽
		searchEditText.setCompoundDrawables(d1, null, null, null);
	}

	@Override
	public void setMsgFromServer(String msg) {
		ArrayList<User> list = new ArrayList<User>();
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(msg);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject obj = jsonArray.getJSONObject(i);
				list.add(new User(Integer.parseInt(obj.get("id").toString()),
						obj.get("account").toString(), obj.get("password")
								.toString(), obj.get("petName").toString(),
						Integer.valueOf(obj.get("status").toString())));
			}
			List<Map<String, String>> data = new ArrayList<Map<String, String>>();
			Map<String, String> title = new HashMap<String, String>();
			title.put("petName", "昵称");
			title.put("account", "账号");
			data.add(title);
			if (list != null && list.size() != 0) {
				for (User user : list) {
					Map<String, String> rowData = new HashMap<String, String>();
					rowData.put("petName", user.getPetName());
					rowData.put("account", user.getAccount());
					rowData.put("id", user.getId().toString());
					data.add(rowData);
				}
			}
			listView.setAdapter(new SimpleAdapter(Search.this, data,
					R.layout.listviewformat, new String[] { "petName",
							"account" }, new int[] {
							R.id.listviewformat_petName,
							R.id.listviewformat_account }));
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long lineNumber) {
					if (position == 0) {
						return;
					}
					final HashMap<String, String> hashMap = (HashMap<String, String>) parent
							.getItemAtPosition(position);
					System.out.println(hashMap);
					parent.getItemAtPosition(position);
					builder.setPositiveButton("添加好友",
							new AlertDialog.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent(Search.this,
											AddFriend.class);
									Bundle bundle = new Bundle();
									bundle.putSerializable("selfInfo", user);
									bundle.putSerializable("hashMap", hashMap);
									intent.putExtras(bundle);
									startActivity(intent);
								}
							}).create().show();
				}
			});
			progressDialog.dismiss();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void setMsgFromServer2(Integer senderId,String senderName, String message,
			String currentTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMsgFromServer3(String newChatSenderIds,
			String allLastChatRecords) {
		// TODO Auto-generated method stub
		
	}

}
