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
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.Opreation.Opreation;
import com.example.enity.FriendsRelationships;
import com.example.enity.User;
import com.example.util.InteractiveBetweenRecieverAndActivity;
import com.example.util.MyReciever;
import com.example.util.SocketUtil;
import com.example.util.ToastUtil;

public class AllFriendsFragment extends Fragment implements
		InteractiveBetweenRecieverAndActivity {
//	private Button search;
	private Button add;
	private LinearLayout ll;
	private ProgressDialog progressDialog;
	private Button newFriend;
	private ListView listView;
	private AlertDialog.Builder builder;
	private boolean isDisconnected = false;
	private Socket socket;
	private PrintWriter pw;
	private User user;
	private MyReciever myReciever;
	private MyReciever myReciever2;
	private MyReciever myReciever3;
	private MyReciever myReciever4;
	private MyReciever myReciever5;

	/**
	 * fragment������activity���ڣ�������������ͨ����activity������activity���ٺ���������
	 * ��myActivity����ָ���쳣�������onCreateView֮ǰ��onAttach�����������õ�activity����
	 */
	private Activity myActivity;

	// ��ǰ�����������id,���ݸ�MainActivity
	private Integer currentFriendId;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.myActivity = activity;
	}

	@Override
	public void onResume() {
		super.onResume();
		/**
		 * ֱ�Ӳ�ѯ���к���
		 */
		Bundle bundle = this.getArguments();
		if (bundle != null) {
			user = (User) bundle.getSerializable("user");
		}
		// ��գ�����������ѵ�id
		currentFriendId = null;
		((MainActivity) myActivity).setCurrentChatFriendId(currentFriendId);
		new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
				user.getId());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_allfriends, container,
				false);
//		search = (Button) view.findViewById(R.id.search);
		add = (Button) view.findViewById(R.id.add);
		ll = (LinearLayout) view.findViewById(R.id.ll);
		ll.setBackgroundColor(Color.WHITE);
		listView = (ListView) view.findViewById(R.id.listView);
		newFriend = (Button) view.findViewById(R.id.newFriend);
		builder = new AlertDialog.Builder(myActivity);
		progressDialog = new ProgressDialog(myActivity);
		progressDialog.setTitle("��ѯ��");
		progressDialog.setMessage("���ڲ�ѯ������������ܻ��Ѹ���ʱ�䣬�����ĵȴ�\n1.��������\n2.���粻��");
		progressDialog.setCancelable(false);

		myReciever = new MyReciever();
		IntentFilter filter = new IntentFilter("LOOK_UP_ALL_FRIENDS");
		myActivity.getApplicationContext().registerReceiver(myReciever, filter);
		myReciever.setInteractive(this);

		myReciever2 = new MyReciever();
		IntentFilter filter2 = new IntentFilter("DELETE_FRIEND");
		myActivity.getApplicationContext().registerReceiver(myReciever2,
				filter2);
		myReciever2.setInteractive(this);

		myReciever3 = new MyReciever();
		IntentFilter filter3 = new IntentFilter("FLUSH_ALL_FRIENDS_TAB");
		myActivity.getApplicationContext().registerReceiver(myReciever3,
				filter3);
		myReciever3.setInteractive(this);

		myReciever4 = new MyReciever();
		IntentFilter filter4 = new IntentFilter("FRIEND_RELATIONSHIP_BREAKDOWN");
		myActivity.getApplicationContext().registerReceiver(myReciever4,
				filter4);
		myReciever4.setInteractive(this);

		myReciever5 = new MyReciever();
		IntentFilter filter5 = new IntentFilter("FRIEND_RELATIONSHIP_BUILD");
		myActivity.getApplicationContext().registerReceiver(myReciever5,
				filter5);
		myReciever5.setInteractive(this);

		if (((MainActivity) myActivity).isShowRed()) {
			Drawable d1 = myActivity.getResources()
					.getDrawable(R.drawable.red2);
			d1.setBounds(0, 0, 40, 40);// ��һ�� 0 �Ǿ���߾��룬�ڶ��� 0 �Ǿ��ϱ߾��룬80 �ֱ��ǳ���
			newFriend.setCompoundDrawables(d1, null, null, null);
		}

		// �ı��ѯ����ӵ���ʽ
//		modifyButtonStyle(search, "search", "#ffffff");
		modifyButtonStyle(add, "add", "#ffffff");

		newFriend.setBackgroundColor(Color.parseColor("#e7e7e7"));

		// ��ѯ
//		search.setOnTouchListener(new OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				switch (event.getAction()) {
//				case MotionEvent.ACTION_DOWN:
//					modifyButtonStyle(search, "search2", "#595959");
//					Intent intent = new Intent(myActivity, Search.class);
//					Bundle bundle = new Bundle();
//					bundle.putSerializable("user", user);
//					intent.putExtras(bundle);
//					startActivity(intent);
//					break;
//				case MotionEvent.ACTION_UP:
//					modifyButtonStyle(search, "search", "#ffffff");
//					break;
//				}
//				return false;
//			}
//		});
		// ���
		add.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					modifyButtonStyle(add, "add2", "#595959");
					Intent intent = new Intent(myActivity, Search.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("user", user);
					intent.putExtras(bundle);
					startActivity(intent);
					break;
				case MotionEvent.ACTION_UP:
					modifyButtonStyle(add, "add", "#ffffff");
					break;
				}
				return false;
			}
		});

		// �鿴��������
		newFriend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				newFriend.setCompoundDrawables(null, null, null, null);
				((MainActivity) myActivity).getTab2().setIcon(null);
				((MainActivity) myActivity).setShowRed(false);
				Intent intent = new Intent(myActivity,
						HandleNewFriendRequest.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("user", user);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		return view;
	}

	// �ı��ѯ����ӵ���ʽ
	public void modifyButtonStyle(Button button, String nameInDrable,
			String color) {
		Drawable d1 = null;
		if ("search".equals(nameInDrable)) {
			d1 = getResources().getDrawable(R.drawable.search);
		}
		if ("search2".equals(nameInDrable)) {
			d1 = getResources().getDrawable(R.drawable.search2);
		}
		if ("add".equals(nameInDrable)) {
			d1 = getResources().getDrawable(R.drawable.add);
		}
		if ("add2".equals(nameInDrable)) {
			d1 = getResources().getDrawable(R.drawable.add2);
		}
		d1.setBounds(0, 0, 60, 60);// ��һ�� 0 �Ǿ���߾��룬�ڶ��� 0 �Ǿ��ϱ߾��룬80 �ֱ��ǳ���
		button.setCompoundDrawables(d1, null, null, null);
		button.setBackgroundColor(Color.parseColor(color));
	}

	public class MyAsyncTask extends
			AsyncTask<Integer, Void, List<FriendsRelationships>> {
		@Override
		protected List<FriendsRelationships> doInBackground(Integer... params) {
			// �����׽���
			socket = SocketUtil.getSocket(isDisconnected);
			isDisconnected = false;
			List<FriendsRelationships> list = null;
			try {
				pw = new PrintWriter(new OutputStreamWriter(
						socket.getOutputStream(), "gbk"));
				// ����������ͣ���ʾ���к��ѵı�־λ
				pw.println(Opreation.LOOK_UP_ALL_FRIENDS);
				pw.println(user.getId());
				pw.flush();
				System.out
						.println(user.getAccount() + "����Ƭ�Σ�����������ͣ��鿴���к��ѣ���־λ");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				System.out.println("��ҳ�棬doInBackground��JSONException");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("��ҳ�棬doInBackground��IOException");
			} catch (Exception e) {
				System.out.println("��ҳ�棬doInBackground��JSONException");
				e.printStackTrace();
			}
			return list;
		}

	}

	@Override
	public void setMsgFromServer(String msg) {
		/**
		 * 1.������ڴ�fragmentʱ������ɾ�����Լ� 2.������ڴ�fragmentʱ������ͬ�����Լ��ĺ�������
		 */
		if ("breakdown".equals(msg) || "build".equals(msg)) {
			// ��գ�����������ѵ�id
			currentFriendId = null;
			((MainActivity) myActivity).setCurrentChatFriendId(currentFriendId);
			new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
					user.getId());
			return;
		}
		/**
		 * ������º�������
		 */
		if ("".equals(msg)) {
			if (myActivity == null) {
				myActivity = getActivity();
			}
			Drawable d1 = myActivity.getResources()
					.getDrawable(R.drawable.red2);
			d1.setBounds(0, 0, 40, 40);// ��һ�� 0 �Ǿ���߾��룬�ڶ��� 0 �Ǿ��ϱ߾��룬80 �ֱ��ǳ���
			newFriend.setCompoundDrawables(d1, null, null, null);
			return;
		}
		/**
		 * �����ɾ�����ѳɹ�
		 */
		if ("ɾ���ɹ�".equals(msg)) {
			ToastUtil.toastInScreenMiddle(msg, myActivity);
			return;
		}
		/**
		 * �������ʾ���к���
		 */
		ArrayList<FriendsRelationships> list = new ArrayList<FriendsRelationships>();
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(msg);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject obj;
				obj = jsonArray.getJSONObject(i);
				list.add(new FriendsRelationships(Integer.parseInt(obj
						.get("id").toString()), Integer.parseInt(obj.get(
						"selfId").toString()), Integer.parseInt(obj.get(
						"friendId").toString()), obj.get("friendAccount")
						.toString(), obj.get("friendPetName").toString()));
			}
			List<Map<String, String>> data = new ArrayList<Map<String, String>>();
			Map<String, String> title = new HashMap<String, String>();
			title.put("petName", "�����ǳ�");
			title.put("account", "�����˺�");
			data.add(title);
			if (list != null && list.size() != 0) {
				for (FriendsRelationships relationships : list) {
					Map<String, String> rowData = new HashMap<String, String>();
					rowData.put("friendId", relationships.getFriendId()
							.toString());
					rowData.put("petName", relationships.getFriendPetName());
					rowData.put("account", relationships.getFriendAccount());
					data.add(rowData);
				}
			}
			listView.setAdapter(new SimpleAdapter(myActivity, data,
					R.layout.listviewformat, new String[] { "petName",
							"account" }, new int[] {
							R.id.listviewformat_petName,
							R.id.listviewformat_account }));
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, final View view,
						int position, long lineNumber) {
					if (position == 0) {
						return;
					}
					final HashMap<String, String> hashMap = (HashMap<String, String>) parent
							.getItemAtPosition(position);
					builder.setPositiveButton("������Ϣ",
							new AlertDialog.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// ����ǰ����ҳ��ĺ���id���͸�MainActivity
									// ��MainActivity�յ�������Ϣ�ķ�����id�͵�ǰ����ҳ��ĺ���id
									// ����ȣ�����ʾ��������
									currentFriendId = Integer.valueOf(hashMap
											.get("friendId").toString());
									((MainActivity) myActivity)
											.setCurrentChatFriendId(currentFriendId);

									Intent intent = new Intent(myActivity,
											Chat.class);
									Bundle bundle = new Bundle();
									bundle.putSerializable("user", user);
									bundle.putString("chatFriendPetName",
											hashMap.get("petName"));
									bundle.putInt("chatFriendId", Integer
											.valueOf(hashMap.get("friendId")));
									intent.putExtras(bundle);
									startActivity(intent);
								}
							})
							.setNegativeButton("ɾ������",
									new AlertDialog.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											new AlertDialog.Builder(myActivity)
													.setMessage("���Ҫɾ����")
													.setNegativeButton("ȡ��",
															null)
													.setPositiveButton(
															"ȷ��",
															new AlertDialog.OnClickListener() {
																public void onClick(
																		DialogInterface dialog,
																		int which) {
																	new Thread() {
																		public void run() {
																			/**
																			 * �߰汾android�������߳�����ʱ����
																			 * ֱ������
																			 */
																			pw.println(Opreation.DELETE_FRIEND);
																			pw.println(user
																					.getId()
																					+ ","
																					+ hashMap
																							.get("friendId"));
																			pw.flush();
																		};
																	}.start();
																	Fragment fragment = new AllFriendsFragment();
																	Bundle bundle = new Bundle();
																	bundle.putSerializable(
																			"user",
																			user);
																	fragment.setArguments(bundle);
																	FragmentManager fragmentManager = getFragmentManager();
																	FragmentTransaction ft = fragmentManager
																			.beginTransaction();
																	ft.replace(
																			R.id.ll,
																			fragment);
																	ft.commit();
																	// new
																	// MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
																	// user.getId());
																}
															}).create().show();
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
