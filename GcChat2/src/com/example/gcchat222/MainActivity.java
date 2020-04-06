package com.example.gcchat222;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import com.example.Opreation.Opreation;
import com.example.enity.User;
import com.example.util.InteractiveBetweenFragmentAndActivity;
import com.example.util.InteractiveBetweenRecieverAndActivity;
import com.example.util.JudgeNetUtil;
import com.example.util.MyReciever;
import com.example.util.SocketUtil;
import com.example.util.ToastUtil;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActionBar.Tab;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;

public class MainActivity extends Activity implements
		InteractiveBetweenRecieverAndActivity,
		InteractiveBetweenFragmentAndActivity {
	private User user;
	private AlertDialog.Builder builder;
	private boolean isDisconnected;
	private NotificationManager nm;
	private Socket socket;
	private PrintWriter pw;
	private MyReciever myReciever;
	private MyReciever myReciever2;
	Intent intent;
	// 播放音乐相关的两个对象
	AssetFileDescriptor assetFileDescriptor;
	MediaPlayer mediaPlayer;

	// 从服务器接收到， 信息发送方id，用以显示红点和音乐
	// 将接收到的集合，发送给fragment
	private String allSendedIds = new String();
	private Tab tab;
	private Tab tab2;
	private Tab tab3;
	// 保留当前在chatActivity聊天对象的id，用于判断是否显示红点和音乐
	private Integer currentChatFriendId;

	// 获取当前所处的Tab
	private String tabText;

	// 新朋友是否显示红点
	private boolean showRed;

	@Override
	public void onBackPressed() {
		builder = new AlertDialog.Builder(this);
		builder.setTitle("确认退出吗?")
				.setNeutralButton("确定", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new Thread() {
							public void run() {
								try {
									socket = SocketUtil.getSocket(false);
									pw = new PrintWriter(
											new OutputStreamWriter(socket
													.getOutputStream(), "gbk"));
									pw.println(Opreation.QUIT);
									pw.flush();
									unregisterReceiver(myReciever);
									unregisterReceiver(myReciever2);
									stopService(intent);
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
							};
						}.start();
						MainActivity.this.finish();
					}
				}).setPositiveButton("让我再想想", null);
		builder.setCancelable(false).create().show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 禁止横屏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		myReciever = new MyReciever();
		IntentFilter filter = new IntentFilter("LOOK_UP_NEW_FRIEND_REQUEST");
		registerReceiver(myReciever, filter);
		myReciever.setInteractive(this);

		myReciever2 = new MyReciever();
		IntentFilter filter2 = new IntentFilter("ACCEPT_MSG");
		registerReceiver(myReciever2, filter2);
		myReciever2.setInteractive(this);

		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		Bundle bundle = MainActivity.this.getIntent().getExtras();
		if (bundle != null) {
			user = (User) bundle.getSerializable("user");
		}

		// Intent intent = new Intent(MainActivity.this, MyIntentService.class);
		intent = new Intent(MainActivity.this, MyIntentService.class);
		intent.putExtra("user", user);
		startService(intent);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(Gravity.TOP);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);// 设置导航模式
		tab = actionBar.newTab().setText("消息")
				.setTabListener(new MyListener(new ChatFragment()));
		actionBar.addTab(tab);
		tab2 = actionBar.newTab().setText("联系人")
				.setTabListener(new MyListener(new AllFriendsFragment()));
		actionBar.addTab(tab2);

		tab3 = actionBar.newTab().setText("个人中心")
				.setTabListener(new MyListener(new OwnerCenterFragment()));
		actionBar.addTab(tab3);

		try {
			assetFileDescriptor = getAssets().openFd("dingdong.wav");
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),
					assetFileDescriptor.getStartOffset(),
					assetFileDescriptor.getLength());
			mediaPlayer.prepare();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	class MyListener implements ActionBar.TabListener {
		Fragment fragment;

		public MyListener(Fragment fragment) {
			this.fragment = fragment;
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// ToastUtil.toastInScreenMiddle(tab.getPosition() + "",
			// MainActivity.this);
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// 如果没有联网
			if (JudgeNetUtil.judgeNet(MainActivity.this) == false) {
				ToastUtil.toastInScreenMiddle("您的网络质量不佳哟~请检查网络是否连接",
						MainActivity.this);
				isDisconnected = true;
				return;
			}

			tabText = tab.getText().toString();

			Bundle bundle = new Bundle();
			// bundle.putString("allSendedIds", allSendedIds);
			bundle.putSerializable("user", user);
			fragment.setArguments(bundle);
			FragmentTransaction ft1 = getFragmentManager().beginTransaction();
			ft1.replace(R.id.ll, fragment);
			ft1.commit();
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// ToastUtil.toastInScreenMiddle(tab.getPosition() + "!!!",
			// MainActivity.this);
		}

	}

	@Override
	public void setMsgFromServer(String msg) {
		if ("no".equals(msg)) {
			return;
		}
		if ("yes".equals(msg)) {
			mediaPlayer.start();
			mediaPlayer.seekTo(0);
			tab2.setIcon(R.drawable.red);
			setShowRed(true);
			Intent intent = new Intent("FLUSH_ALL_FRIENDS_TAB");
			sendBroadcast(intent);
			return;
		}
		// String sourceUserPetName = msg.substring(0, msg.indexOf(" "));
		// String msg2 = msg.substring(msg.indexOf(" ") + 1);
		// Intent intent = new Intent(MainActivity.this,
		// HandleNewFriendRequest.class);
		// intent.putExtra("user", user);
		// PendingIntent pi = PendingIntent.getActivity(MainActivity.this, 0,
		// intent, PendingIntent.FLAG_CANCEL_CURRENT);
		// Notification notification = new Notification.Builder(this)
		// .setAutoCancel(true).setTicker("您有新的好友请求")
		// .setSmallIcon(R.drawable.ic_launcher)
		// .setContentTitle(sourceUserPetName).setContentText(msg2)
		// .setDefaults(Notification.DEFAULT_ALL)
		// .setWhen(System.currentTimeMillis()).setContentIntent(pi)
		// .setNumber(1).build();
		// nm.notify(1, notification);
	}

	// 接收其他人发送的信息
	@Override
	public void setMsgFromServer2(Integer senderId, String senderName,
			String message, String currentTime) {
		// System.out.println("MainActivity收到，当前在聊天人的id=" +
		// currentChatFriendId);
		// 若当前正在聊天的好友id和发送消息的好友id 相同，不显示音乐和红点
		System.out.println("MainAactivity  参数senderId=" + senderId);
		System.out.println("MainAactivity  allSendedIds=" + allSendedIds
				+ "，准备加上senderId");
		if (!senderId.equals(currentChatFriendId)) {
			// 如果当前在消息导航栏
			if ("消息".equals(tabText)) {
				Intent intent = new Intent("FLUSH_CHAT_TAB");
				sendBroadcast(intent);
			}
			tab.setIcon(R.drawable.red);
			if (allSendedIds.indexOf(senderId.toString()) == -1) {
				allSendedIds += (senderId + ",");
			}
			mediaPlayer.start();
			mediaPlayer.seekTo(0);
		}
		System.out.println("MainAactivity  加上后 allSendedIds=" + allSendedIds);
	}

	@Override
	public void setMsgFromServer3(String newChatSenderIds,
			String allLastChatRecords) {
	}

	// 接收fragment传递回来的，发送信息人id
	@Override
	public void sendMessageToActivity(String msg) {
		System.out.println("准备删除一个，前：" + allSendedIds);
		allSendedIds = allSendedIds.replace(msg + ",", "");
		System.out.println("后：" + allSendedIds);
	}

	public String getAllSenderIds() {
		System.out.println("MainActivity获取allSendedIds=" + allSendedIds);
		return allSendedIds;
	}

	public void setAllSendedIds(String allSendedIds) {
		this.allSendedIds = allSendedIds;
	}

	public Tab getTab() {
		return tab;
	}

	public void setTab(Tab tab) {
		this.tab = tab;
	}

	public Tab getTab2() {
		return tab2;
	}

	public void setTab2(Tab tab2) {
		this.tab2 = tab2;
	}

	public Integer getCurrentChatFriendId() {
		return currentChatFriendId;
	}

	public void setCurrentChatFriendId(Integer currentChatFriendId) {
		this.currentChatFriendId = currentChatFriendId;
	}

	public boolean isShowRed() {
		return showRed;
	}

	public void setShowRed(boolean showRed) {
		this.showRed = showRed;
	}

}
