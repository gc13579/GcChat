package com.example.util;
/**
 * ��¼ÿ�β�ѯ�Ƿ����º��������ʱ��
 * @author lenovo
 *
 */
public class TimeStore {
	private long lastSendTime;

	// ���߳��¶�ȡ��Ҫ����
	public synchronized long getLastSendTime() {
		return lastSendTime;
	}

	// ͬ�������߳���д����Ҫ����
	public synchronized void setLastSendTime(long lastSendTime) {
		this.lastSendTime = lastSendTime; // ��ʱ��ŵ�˽������
//		System.out.println("���һ�η���ʱ��"
//				+ new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
//						.format(new java.util.Date(lastSendTime))); // �ѷ���ʱ���ӡ����Ļ��
	}
}
