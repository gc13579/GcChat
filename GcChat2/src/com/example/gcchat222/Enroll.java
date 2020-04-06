package com.example.gcchat222;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.sql.Connection;
import java.util.regex.Pattern;

import com.example.Opreation.Opreation;
import com.example.exception.NetOutAageException;
import com.example.util.JudgeNetUtil;
import com.example.util.SocketUtil;
import com.example.util.ToastUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Enroll extends Activity {
	private EditText enrollAccount;
	private EditText enrollPwd;
	private EditText sureEnrollPwd;
	private EditText petName;
	private Button enroll;
	private Button back;
	private ProgressDialog progressDialog;
	private boolean isDisconnected = false;
	private Socket socket;
	private PrintWriter pw;
	private BufferedReader br;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enroll);
		// 禁止横屏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		enrollAccount = (EditText) findViewById(R.id.enrollAccount);
		enrollPwd = (EditText) findViewById(R.id.enrollPwd);
		sureEnrollPwd = (EditText) findViewById(R.id.sureEnrollPwd);
		petName = (EditText) findViewById(R.id.petName);
		enroll = (Button) findViewById(R.id.enroll);
		back = (Button) findViewById(R.id.back);

		progressDialog = new ProgressDialog(Enroll.this);
		progressDialog.setTitle("注册中");
		progressDialog
				.setMessage("正在为您注册，以下情况可能花费更长时间，请耐心等待\n1.首次注册\n2.断线重连\n3.网络不佳");
		progressDialog.setCancelable(false);
		// 给账号和密码添加小图标样式
		addIconToAccountAndPwd(enrollAccount, enrollPwd, sureEnrollPwd, petName);
		// 注册
		enroll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 如果没有联网
				if (JudgeNetUtil.judgeNet(Enroll.this) == false) {
					ToastUtil.toastInScreenMiddle("连接失败，请稍后再试，请检查网络是否连接",
							Enroll.this);
					isDisconnected = true;
					return;
				}
				String account = enrollAccount.getText().toString();
				String password = enrollPwd.getText().toString();
				String surePwd = sureEnrollPwd.getText().toString();
				String nickName = petName.getText().toString();
				// 表单验证
				String message = checkAccountAndPwd(account, password, surePwd,
						nickName);
				if (!"".equals(message)) {
					// 屏幕中央打印错误消息
					ToastUtil.toastInScreenMiddle(message, Enroll.this);
					return;
				}
				new MyAsyncTask().executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR, account, password,
						nickName);
			}
		});
		// 返回
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Enroll.this, Login.class);
				startActivity(intent);
				Enroll.this.finish();
			}
		});
	}

	public class MyAsyncTask extends AsyncTask<String, Integer, String> {
		// 准备工作
		@Override
		protected void onPreExecute() {
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// 创立套接字
			socket = SocketUtil.getSocket(isDisconnected);
			isDisconnected = false;
			// 接收服务器返回的注册状态(成功/失败)
			String message = "";
			try {
				pw = new PrintWriter(new OutputStreamWriter(
						socket.getOutputStream(), "gbk"));
				br = new BufferedReader(new InputStreamReader(
						socket.getInputStream(), "gbk"));
				// 向服务器发送，注册标志位
				pw.println(Opreation.ENROLL);
				// 向服务器发送，注册的账户和密码和昵称
				pw.println(params[0] + "," + params[1] + "," + params[2]);
				try {
					Thread.sleep(8000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				pw.flush();
				// 读取注册标志位(成功/失败)
				message = br.readLine();
				/**
				 * 正常情况，服务器返回的message不为null,在网络中断时为null
				 */
				if (message == null) {
					throw new NetOutAageException("连接失败，请检查网络后再试");
				}
			} catch (UnsupportedEncodingException e) {
				System.out
						.println("注册页面，doInBackground报UnsupportedEncodingException");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("注册页面，doInBackground报IOException");
				e.printStackTrace();
			} catch (NetOutAageException e) {
				System.out.println("注册页面，doInBackground报NetOutAageException");
				isDisconnected = true;
				message = e.getMessage();
				e.printStackTrace();
			}
			return message;
		}

		@Override
		protected void onPostExecute(String result) {
			ToastUtil.toastInScreenMiddle(result, Enroll.this);
			cancel(true);
			progressDialog.dismiss();
			if ("注册成功".equals(result)) {
				Intent intent = new Intent(Enroll.this, Login.class);
				startActivity(intent);
				Enroll.this.finish();
			}
		}
	}

	// 给账号和密码添加小图标样式
	public void addIconToAccountAndPwd(EditText enrollAccount,
			EditText enrollPwd, EditText sureEnrollPwd, EditText petName) {
		Drawable d1 = getResources().getDrawable(R.drawable.user);
		d1.setBounds(0, 0, 80, 80);// 第一个 0 是距左边距离，第二个 0 是距上边距离，80 分别是长宽
		enrollAccount.setCompoundDrawables(d1, null, null, null);
		petName.setCompoundDrawables(d1, null, null, null);
		Drawable d2 = getResources().getDrawable(R.drawable.password);
		d2.setBounds(0, 0, 80, 80);// 第一个 0 是距左边距离，第二个 0 是距上边距离，80 分别是长宽
		enrollPwd.setCompoundDrawables(d2, null, null, null);
		sureEnrollPwd.setCompoundDrawables(d2, null, null, null);
	}

	// 注册表单验证
	public String checkAccountAndPwd(String account, String password,
			String surePwd, String nickName) {
		StringBuffer errorMessage = new StringBuffer();
		// 如果账号或密码包含非法字符
		if (!Pattern.compile("^[a-zA-Z0-9]{4,10}$").matcher(account).find()) {
			errorMessage.append("账号只能由4~10位数字和字母组成哦-_-\n");
		}
		if (!Pattern.compile("^[a-zA-Z0-9]{1,}$").matcher(password).find()
				|| !Pattern.compile("^[a-zA-Z0-9]{1,}$").matcher(surePwd)
						.find()) {
			errorMessage.append("密码只能由数字和字母组成哦-_-\n");
		}
		if (!password.equals(surePwd)) {
			errorMessage.append("两次输入的密码必须相同哦-_-\n");
		}
		if (!Pattern.compile("^[a-zA-Z0-9\\u4E00-\\u9FA5]{1,10}$")
				.matcher(nickName).find()) {
			errorMessage.append("昵称只能由1~10位数字、字母、中文组成哦-_-\n");
		}
		return errorMessage.toString();
	}
}
