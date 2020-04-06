package com.example.enity;

import java.util.Date;

public class ChatRecord {
	private Integer id;
	private Integer senderId;
	private String senderPetName;
	private Integer recieverId;
	private String recieverPetName;
	private String message;
	private String date;

	// private Integer visitedTimes;

	public ChatRecord() {
	}

	public ChatRecord(Integer id, Integer senderId, String senderPetName,
			Integer recieverId, String recieverPetName, String message,
			String date) {
		super();
		this.id = id;
		this.senderId = senderId;
		this.senderPetName = senderPetName;
		this.recieverId = recieverId;
		this.recieverPetName = recieverPetName;
		this.message = message;
		this.date = date;
	}

	@Override
	public String toString() {
		return "ChatRecord [id=" + id + ", senderId=" + senderId
				+ ", senderPetName=" + senderPetName + ", recieverId="
				+ recieverId + ", recieverPetName=" + recieverPetName
				+ ", message=" + message + ", date=" + date;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSenderId() {
		return senderId;
	}

	public void setSenderId(Integer senderId) {
		this.senderId = senderId;
	}

	public String getSenderPetName() {
		return senderPetName;
	}

	public void setSenderPetName(String senderPetName) {
		this.senderPetName = senderPetName;
	}

	public Integer getRecieverId() {
		return recieverId;
	}

	public void setRecieverId(Integer recieverId) {
		this.recieverId = recieverId;
	}

	public String getRecieverPetName() {
		return recieverPetName;
	}

	public void setRecieverPetName(String recieverPetName) {
		this.recieverPetName = recieverPetName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
