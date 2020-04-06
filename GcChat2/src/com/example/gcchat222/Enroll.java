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
		// ��ֹ����
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		enrollAccount = (EditText) findViewById(R.id.enrollAccount);
		enrollPwd = (EditText) findViewById(R.id.enrollPwd);
		sureEnrollPwd = (EditText) findViewById(R.id.sureEnrollPwd);
		petName = (EditText) findViewById(R.id.petName);
		enroll = (Button) findViewById(R.id.enroll);
		back = (Button) findViewById(R.id.back);

		progressDialog = new ProgressDialog(Enroll.this);
		progressDialog.setTitle("ע����");
		progressDialog
				.setMessage("����Ϊ��ע�ᣬ����������ܻ��Ѹ���ʱ�䣬�����ĵȴ�\n1.�״�ע��\n2.��������\n3.���粻��");
		progressDialog.setCancelable(false);
		// ���˺ź��������Сͼ����ʽ
		addIconToAccountAndPwd(enrollAccount, enrollPwd, sureEnrollPwd, petName);
		// ע��
		enroll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ���û������
				if (JudgeNetUtil.judgeNet(Enroll.this) == false) {
					ToastUtil.toastInScreenMiddle("����ʧ�ܣ����Ժ����ԣ����������Ƿ�����",
							Enroll.this);
					isDisconnected = true;
					return;
				}
				String account = enrollAccount.getText().toString();
				String password = enrollPwd.getText().toString();
				String surePwd = sureEnrollPwd.getText().toString();
				String nickName = petName.getText().toString();
				// ����֤
				String message = checkAccountAndPwd(account, password, surePwd,
						nickName);
				if (!"".equals(message)) {
					// ��Ļ�����ӡ������Ϣ
					ToastUtil.toastInScreenMiddle(message, Enroll.this);
					return;
				}
				new MyAsyncTask().executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR, account, password,
						nickName);
			}
		});
		// ����
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
		// ׼������
		@Override
		protected void onPreExecute() {
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// �����׽���
			socket = SocketUtil.getSocket(isDisconnected);
			isDisconnected = false;
			// ���շ��������ص�ע��״̬(�ɹ�/ʧ��)
			String message = "";
			try {
				pw = new PrintWriter(new OutputStreamWriter(
						socket.getOutputStream(), "gbk"));
				br = new BufferedReader(new InputStreamReader(
						socket.getInputStream(), "gbk"));
				// ����������ͣ�ע���־λ
				pw.println(Opreation.ENROLL);
				// ����������ͣ�ע����˻���������ǳ�
				pw.println(params[0] + "," + params[1] + "," + params[2]);
				try {
					Thread.sleep(8000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				pw.flush();
				// ��ȡע���־λ(�ɹ�/ʧ��)
				message = br.readLine();
				/**
				 * ������������������ص�message��Ϊnull,�������ж�ʱΪnull
				 */
				if (message == null) {
					throw new NetOutAageException("����ʧ�ܣ��������������");
				}
			} catch (UnsupportedEncodingException e) {
				System.out
						.println("ע��ҳ�棬doInBackground��UnsupportedEncodingException");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("ע��ҳ�棬doInBackground��IOException");
				e.printStackTrace();
			} catch (NetOutAageException e) {
				System.out.println("ע��ҳ�棬doInBackground��NetOutAageException");
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
			if ("ע��ɹ�".equals(result)) {
				Intent intent = new Intent(Enroll.this, Login.class);
				startActivity(intent);
				Enroll.this.finish();
			}
		}
	}

	// ���˺ź��������Сͼ����ʽ
	public void addIconToAccountAndPwd(EditText enrollAccount,
			EditText enrollPwd, EditText sureEnrollPwd, EditText petName) {
		Drawable d1 = getResources().getDrawable(R.drawable.user);
		d1.setBounds(0, 0, 80, 80);// ��һ�� 0 �Ǿ���߾��룬�ڶ��� 0 �Ǿ��ϱ߾��룬80 �ֱ��ǳ���
		enrollAccount.setCompoundDrawables(d1, null, null, null);
		petName.setCompoundDrawables(d1, null, null, null);
		Drawable d2 = getResources().getDrawable(R.drawable.password);
		d2.setBounds(0, 0, 80, 80);// ��һ�� 0 �Ǿ���߾��룬�ڶ��� 0 �Ǿ��ϱ߾��룬80 �ֱ��ǳ���
		enrollPwd.setCompoundDrawables(d2, null, null, null);
		sureEnrollPwd.setCompoundDrawables(d2, null, null, null);
	}

	// ע�����֤
	public String checkAccountAndPwd(String account, String password,
			String surePwd, String nickName) {
		StringBuffer errorMessage = new StringBuffer();
		// ����˺Ż���������Ƿ��ַ�
		if (!Pattern.compile("^[a-zA-Z0-9]{4,10}$").matcher(account).find()) {
			errorMessage.append("�˺�ֻ����4~10λ���ֺ���ĸ���Ŷ-_-\n");
		}
		if (!Pattern.compile("^[a-zA-Z0-9]{1,}$").matcher(password).find()
				|| !Pattern.compile("^[a-zA-Z0-9]{1,}$").matcher(surePwd)
						.find()) {
			errorMessage.append("����ֻ�������ֺ���ĸ���Ŷ-_-\n");
		}
		if (!password.equals(surePwd)) {
			errorMessage.append("������������������ͬŶ-_-\n");
		}
		if (!Pattern.compile("^[a-zA-Z0-9\\u4E00-\\u9FA5]{1,10}$")
				.matcher(nickName).find()) {
			errorMessage.append("�ǳ�ֻ����1~10λ���֡���ĸ���������Ŷ-_-\n");
		}
		return errorMessage.toString();
	}
}
