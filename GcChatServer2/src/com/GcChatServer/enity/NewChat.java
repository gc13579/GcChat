package com.GcChatServer.enity;

public class NewChat {
	private Integer id;
	private Integer senderId;
	private Integer recieverId;

	public NewChat() {
	}

	public NewChat(Integer senderId, Integer recieverId) {
		super();
		this.senderId = senderId;
		this.recieverId = recieverId;
	}

	public NewChat(Integer id, Integer senderId, Integer recieverId) {
		super();
		this.id = id;
		this.senderId = senderId;
		this.recieverId = recieverId;
	}

	@Override
	public String toString() {
		return "NewChat [id=" + id + ", senderId=" + senderId + ", recieverId="
				+ recieverId;
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

	public Integer getRecieverId() {
		return recieverId;
	}

	public void setRecieverId(Integer recieverId) {
		this.recieverId = recieverId;
	}

	@Override
	public boolean equals(Object obj) {
		NewChat newChat = null;
		if (obj instanceof NewChat) {
			newChat = (NewChat) obj;
		}
		return (this.getRecieverId().equals(newChat.getRecieverId()) && this
				.getSenderId().equals(newChat.getSenderId()));
	}
}
