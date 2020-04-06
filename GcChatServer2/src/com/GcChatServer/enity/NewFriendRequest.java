package com.GcChatServer.enity;

public class NewFriendRequest {
	private Integer id;
	private Integer sourceUserId;
	private String sourceUserAccount;
	private String sourceUserPetName;
	private Integer targetUserId;
	private String message;
	private Integer status;
	private Integer visitedTimes;

	public NewFriendRequest() {
	}

	public NewFriendRequest(Integer id, Integer sourceUserId,
			String sourceUserAccount, String sourceUserPetName,
			Integer targetUserId, String message, Integer status,
			Integer visitedTimes) {
		super();
		this.id = id;
		this.sourceUserId = sourceUserId;
		this.sourceUserAccount = sourceUserAccount;
		this.sourceUserPetName = sourceUserPetName;
		this.targetUserId = targetUserId;
		this.message = message;
		this.status = status;
		this.visitedTimes = visitedTimes;
	}

	@Override
	public String toString() {
		return "NewFriendRequest [id=" + id + ", sourceUserId=" + sourceUserId
				+ ", sourceUserAccount=" + sourceUserAccount
				+ ", sourceUserPetName=" + sourceUserPetName
				+ ", targetUserId=" + targetUserId + ", message=" + message
				+ ", status=" + status + ", visitedTimes=" + visitedTimes + "]";
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSourceUserId() {
		return sourceUserId;
	}

	public void setSourceUserId(Integer sourceUserId) {
		this.sourceUserId = sourceUserId;
	}

	public String getSourceUserPetName() {
		return sourceUserPetName;
	}

	public void setSourceUserPetName(String sourceUserPetName) {
		this.sourceUserPetName = sourceUserPetName;
	}

	public Integer getTargetUserId() {
		return targetUserId;
	}

	public void setTargetUserId(Integer targetUserId) {
		this.targetUserId = targetUserId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getVisitedTimes() {
		return visitedTimes;
	}

	public void setVisitedTimes(Integer visitedTimes) {
		this.visitedTimes = visitedTimes;
	}

	public String getSourceUserAccount() {
		return sourceUserAccount;
	}

	public void setSourceUserAccount(String sourceUserAccount) {
		this.sourceUserAccount = sourceUserAccount;
	}

}
