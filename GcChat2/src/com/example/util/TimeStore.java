package com.example.util;
/**
 * 记录每次查询是否有新好友请求的时间
 * @author lenovo
 *
 */
public class TimeStore {
	private long lastSendTime;

	// 多线程下读取需要加锁
	public synchronized long getLastSendTime() {
		return lastSendTime;
	}

	// 同样，多线程下写入需要加锁
	public synchronized void setLastSendTime(long lastSendTime) {
		this.lastSendTime = lastSendTime; // 把时间放到私有属性
//		System.out.println("最后一次发包时间"
//				+ new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
//						.format(new java.util.Date(lastSendTime))); // 把发包时间打印到屏幕上
	}
}
