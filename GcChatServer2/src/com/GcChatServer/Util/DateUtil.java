package com.GcChatServer.Util;

import java.util.Date;

public class DateUtil {
	public static String getCurrentTime() {
		Date date = new Date();
		String currentTime = date.getYear()
				+ 1900
				+ "-"
				+ ((date.getMonth() + 1) < 10 ? "0" + (date.getMonth() + 1)
						: (date.getMonth() + 1))
				+ "-"
				+ ((date.getDate()) < 10 ? "0" + (date.getDate()) : (date
						.getDate()))
				+ " "
				+ ((date.getHours()) < 10 ? "0" + (date.getHours()) : (date
						.getHours()))
				+ ":"
				+ ((date.getMinutes()) < 10 ? "0" + (date.getMinutes()) : (date
						.getMinutes()))
				+ ":"
				+ ((date.getSeconds()) < 10 ? "0" + (date.getSeconds()) : (date
						.getSeconds()));
		return currentTime;
	}
}
