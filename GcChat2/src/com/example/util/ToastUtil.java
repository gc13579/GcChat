package com.example.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtil {
	// 屏幕中央打印错误消息
	public static void toastInScreenMiddle(String string, Context context) {
		Toast toast = Toast.makeText(context, string, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
}
