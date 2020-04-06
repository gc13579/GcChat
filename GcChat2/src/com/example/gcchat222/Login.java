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
		// ��ֹ����
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		loginAccount = (EditText) findViewById(R.id.loginAccount);
		loginPwd = (EditText) findViewById(R.id.loginPwd);
		login = (Button) findViewById(R.id.login);
		enroll = (Button) findViewById(R.id.enroll);
		progressDialog = new ProgressDialog(Login.this);
		progressDialog.setTitle("������");
		progressDialog
				.setMessage("�������ӷ�����������������ܻ��Ѹ���ʱ�䣬�����ĵȴ�\n1.�״ε�¼\n2.��������\n3.���粻��");
		progressDialog.setCancelable(false);

//		myReciever = new MyReciever();
//		IntentFilter filter = new IntentFilter("LOGIN");
//		registerReceiver(myReciever, filter);
//		myReciever.setInteractive(this);

		// ���Сͼ��
		addIconToAccountAndPwd(loginAccount, loginPwd);
		// ��¼
		login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ���û������
				if (JudgeNetUtil.judgeNet(Login.this) == false) {
					ToastUtil.toastInScreenMiddle("�޷���¼�����Ժ����ԣ����������Ƿ�����",
							Login.this);
					isDisconnected = true;
					return;
				}
				String account = loginAccount.getText().toString();
				String password = loginPwd.getText().toString();
				// ����֤
				String message = checkAccountAndPwd(account, password);
				if (!"".equals(message)) {
					// ��Ļ�����ӡ������Ϣ
					ToastUtil.toastInScreenMiddle(message, Login.this);
					return;
				}
				try {
					/**
					 * �����¼����,����ִ�С�
					 */
					new MyAsyncTask().executeOnExecutor(
							AsyncTask.THREAD_POOL_EXECUTOR, account, password);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		// ע��
		enroll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ���û������
				if (JudgeNetUtil.judgeNet(Login.this) == false) {
					ToastUtil.toastInScreenMiddle("�޷�ע�ᣬ���Ժ����ԣ����������Ƿ�����",
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
			// �����׽���
			socket = SocketUtil.getSocket(isDisconnected);
			isDisconnected = false;
			// ����list,���շ��������صĵ�¼״̬(�ɹ�/ʧ��)��user
			ArrayList<Object> list = new ArrayList<Object>();
			// ���շ������ķ���״̬(�ɹ�/ʧ��)
			String message = "";
			try {
				pw = new PrintWriter(new OutputStreamWriter(
						socket.getOutputStream(), "gbk"));
				br = new BufferedReader(new InputStreamReader(
						socket.getInputStream(), "gbk"));
				// ����������ͣ���¼��־λ
				pw.println(Opreation.LOGIN);
				// ����������ͣ���¼���˻�������
				pw.println(params[0] + "," + params[1]);
				pw.flush();
				
				// ��ȡ��¼��־λ(�ɹ�/ʧ��)
				message = br.readLine();
				list.add(message);
				// ��ȡuser
				String stringUser = br.readLine();
				/**
				 * ������������Է�������stringUser����Ϊnull,����¼ʱ��������Ϊnull
				 */
				if (stringUser == null) {
					throw new NetOutAageException("����ʧ�ܣ��������������");
				}
				/**
				 * ����д����Ȼ����ֻ���ڵ�¼ʧ��ʱ������һ��idΪ0��user���ж� if(stringUser!=null){
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
				// idΪ0��˵���Ǽ�user,��¼ʧ��
				if (user.getId() != 0) {
					list.add(user);
				}
			} catch (IOException e) {
				System.out.println("��¼ҳ�棬doInBackground��IOException");
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
			if ("��¼�ɹ�".equals(message)) {
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

	// ���˺ź��������Сͼ����ʽ
	public void addIconToAccountAndPwd(EditText loginAccount, EditText loginPwd) {
		Drawable d1 = getResources().getDrawable(R.drawable.user);
		d1.setBounds(0, 0, 80, 80);// ��һ�� 0 �Ǿ���߾��룬�ڶ��� 0 �Ǿ��ϱ߾��룬80 �ֱ��ǳ���
		loginAccount.setCompoundDrawables(d1, null, null, null);
		Drawable d2 = getResources().getDrawable(R.drawable.password);
		d2.setBounds(0, 0, 80, 80);// ��һ�� 0 �Ǿ���߾��룬�ڶ��� 0 �Ǿ��ϱ߾��룬80 �ֱ��ǳ���
		loginPwd.setCompoundDrawables(d2, null, null, null);
	}

	// ��¼����֤
	public String checkAccountAndPwd(String account, String password) {
		StringBuffer errorMessage = new StringBuffer();
		// ����˺Ż���������Ƿ��ַ�
		if (!Pattern.compile("^[a-zA-Z0-9]{4,10}$").matcher(account).find()) {
			errorMessage.append("�˺�ֻ����4~10λ���ֺ���ĸ���Ŷ-_-\r\n");
		}
		if (!Pattern.compile("^[a-zA-Z0-9]{1,}$").matcher(password).find()) {
			errorMessage.append("����ֻ�������ֺ���ĸ���Ŷ-_-");
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
//			if ("��¼�ɹ�".equals(message)) {
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
