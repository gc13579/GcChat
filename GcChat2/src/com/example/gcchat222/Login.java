package com.example.gcchat222;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.Opreation.Opreation;
import com.example.enity.User;
import com.example.exception.NetOutAageException;
import com.example.util.InteractiveBetweenRecieverAndActivity;
import com.example.util.MyReciever;
import com.example.util.SocketUtil;
import com.example.util.JudgeNetUtil;
import com.example.util.ToastUtil;

import android.R.menu;
import android.os.AsyncTask;
import android.os.Bundle;
import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Login extends Activity  {
	private EditText loginAccount;
	private EditText loginPwd;
	private Button login;
	private Button enroll;
	private ProgressDialog progressDialog;
	private boolean isDisconnected;
	private Socket socket;
	private PrintWriter pw;
	private BufferedReader br;
//	private MyReciever myReciever;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// 禁止横屏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		loginAccount = (EditText) findViewById(R.id.loginAccount);
		loginPwd = (EditText) findViewById(R.id.loginPwd);
		login = (Button) findViewById(R.id.login);
		enroll = (Button) findViewById(R.id.enroll);
		progressDialog = new ProgressDialog(Login.this);
		progressDialog.setTitle("连接中");
		progressDialog
				.setMessage("正在连接服务器，以下情况可能花费更长时间，请耐心等待\n1.首次登录\n2.断线重连\n3.网络不佳");
		progressDialog.setCancelable(false);

//		myReciever = new MyReciever();
//		IntentFilter filter = new IntentFilter("LOGIN");
//		registerReceiver(myReciever, filter);
//		myReciever.setInteractive(this);

		// 添加小图标
		addIconToAccountAndPwd(loginAccount, loginPwd);
		// 登录
		login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 如果没有联网
				if (JudgeNetUtil.judgeNet(Login.this) == false) {
					ToastUtil.toastInScreenMiddle("无法登录，请稍后再试，请检查网络是否连接",
							Login.this);
					isDisconnected = true;
					return;
				}
				String account = loginAccount.getText().toString();
				String password = loginPwd.getText().toString();
				// 表单验证
				String message = checkAccountAndPwd(account, password);
				if (!"".equals(message)) {
					// 屏幕中央打印错误消息
					ToastUtil.toastInScreenMiddle(message, Login.this);
					return;
				}
				try {
					/**
					 * 处理登录事务,并行执行。
					 */
					new MyAsyncTask().executeOnExecutor(
							AsyncTask.THREAD_POOL_EXECUTOR, account, password);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		// 注册
		enroll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 如果没有联网
				if (JudgeNetUtil.judgeNet(Login.this) == false) {
					ToastUtil.toastInScreenMiddle("无法注册，请稍后再试，请检查网络是否连接",
							Login.this);
					isDisconnected = true;
					return;
				}
				Intent intent = new Intent(Login.this, Enroll.class);
				startActivity(intent);
				Login.this.finish();
			}
		});
	}

	public class MyAsyncTask extends AsyncTask<String, Void, ArrayList<Object>> {
		@Override
		protected void onPreExecute() {
			progressDialog.show();
		}

		@Override
		protected ArrayList<Object> doInBackground(String... params) {
			// 创立套接字
			socket = SocketUtil.getSocket(isDisconnected);
			isDisconnected = false;
			// 创建list,接收服务器返回的登录状态(成功/失败)和user
			ArrayList<Object> list = new ArrayList<Object>();
			// 接收服务器的返回状态(成功/失败)
			String message = "";
			try {
				pw = new PrintWriter(new OutputStreamWriter(
						socket.getOutputStream(), "gbk"));
				br = new BufferedReader(new InputStreamReader(
						socket.getInputStream(), "gbk"));
				// 向服务器发送，登录标志位
				pw.println(Opreation.LOGIN);
				// 向服务器发送，登录的账户和密码
				pw.println(params[0] + "," + params[1]);
				pw.flush();
				
				// 读取登录标志位(成功/失败)
				message = br.readLine();
				list.add(message);
				// 读取user
				String stringUser = br.readLine();
				/**
				 * 正常情况，来自服务器的stringUser不会为null,当登录时断网，会为null
				 */
				if (stringUser == null) {
					throw new NetOutAageException("连接失败，请检查网络后再试");
				}
				/**
				 * 这种写法仍然报错，只好在登录失败时，返回一个id为0的user来判断 if(stringUser!=null){
				 * JSONObject obj; try { obj = new JSONObject(stringUser); }
				 * catch (JSONException e) { e.printStackTrace(); } }
				 */
				JSONObject obj;
				obj = new JSONObject(stringUser);
				User user = new User(
						Integer.parseInt(obj.get("id").toString()), obj.get(
								"account").toString(), obj.get("password")
								.toString(), obj.get("petName").toString(),
						Integer.valueOf(obj.get("status").toString()));
				// id为0，说明是假user,登录失败
				if (user.getId() != 0) {
					list.add(user);
				}
			} catch (IOException e) {
				System.out.println("登录页面，doInBackground报IOException");
				e.printStackTrace();
			} catch (NetOutAageException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return list;
		}

		@Override
		protected void onPostExecute(ArrayList<Object> result) {
			String message = result.get(0).toString();
			ToastUtil.toastInScreenMiddle(message, Login.this);
			cancel(true);
			progressDialog.dismiss();
			if ("登录成功".equals(message)) {
				User user = (User) result.get(1);
				Intent intent = new Intent(Login.this, MainActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("user", user);
				intent.putExtras(bundle);
				startActivity(intent);
				Login.this.finish();
			}
		}
	}

	// 给账号和密码添加小图标样式
	public void addIconToAccountAndPwd(EditText loginAccount, EditText loginPwd) {
		Drawable d1 = getResources().getDrawable(R.drawable.user);
		d1.setBounds(0, 0, 80, 80);// 第一个 0 是距左边距离，第二个 0 是距上边距离，80 分别是长宽
		loginAccount.setCompoundDrawables(d1, null, null, null);
		Drawable d2 = getResources().getDrawable(R.drawable.password);
		d2.setBounds(0, 0, 80, 80);// 第一个 0 是距左边距离，第二个 0 是距上边距离，80 分别是长宽
		loginPwd.setCompoundDrawables(d2, null, null, null);
	}

	// 登录表单验证
	public String checkAccountAndPwd(String account, String password) {
		StringBuffer errorMessage = new StringBuffer();
		// 如果账号或密码包含非法字符
		if (!Pattern.compile("^[a-zA-Z0-9]{4,10}$").matcher(account).find()) {
			errorMessage.append("账号只能由4~10位数字和字母组成哦-_-\r\n");
		}
		if (!Pattern.compile("^[a-zA-Z0-9]{1,}$").matcher(password).find()) {
			errorMessage.append("密码只能由数字和字母组成哦-_-");
		}
		return errorMessage.toString();
	}

//	@Override
//	public void setMsgFromServer(String msg) {
//		String message = msg.substring(0, msg.indexOf(" "));
//		String userString = msg.substring(msg.indexOf(" ") + 1);
//		JSONObject obj;
//		try {
//			obj = new JSONObject(userString);
//			User user = new User(Integer.parseInt(obj.get("id").toString()),
//					obj.get("account").toString(), obj.get("password")
//							.toString(), obj.get("petName").toString(),
//					Integer.valueOf(obj.get("status").toString()));
//			
//			ToastUtil.toastInScreenMiddle(message, Login.this);
//			progressDialog.dismiss();
//			if ("登录成功".equals(message)) {
//				Intent intent2 = new Intent(Login.this, MainActivity.class);
//				Bundle bundle = new Bundle();
//				bundle.putSerializable("user", user);
//				intent2.putExtras(bundle);
//				startActivity(intent2);
//				Login.this.finish();
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}
}
