package com.example.enity;

public class RecentChat {
	private Integer id;
	private Integer senderId;
	private Integer recieverId;

	public RecentChat() {
	}

	public RecentChat(Integer senderId, Integer recieverId) {
		super();
		this.senderId = senderId;
		this.recieverId = recieverId;
	}

	public RecentChat(Integer id, Integer senderId, Integer recieverId) {
		super();
		this.id = id;
		this.senderId = senderId;
		this.recieverId = recieverId;
	}

	@Override
	public String toString() {
		return "RecentChat [id=" + id + ", senderId=" + senderId
				+ ", recieverId=" + recieverId + "]";
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
		RecentChat chat = null;
		if (obj instanceof RecentChat) {
			chat = (RecentChat) obj;
		}
		return (this.getRecieverId().equals(chat.getRecieverId()) && this
				.getSenderId().equals(chat.getSenderId()));
	}
}
