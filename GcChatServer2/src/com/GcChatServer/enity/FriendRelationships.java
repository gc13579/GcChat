package com.GcChatServer.enity;

public class FriendRelationships {
	private Integer id;
	private Integer selfId;
	private Integer friendId;
	private String friendAccount;
	private String friendPetName;

	public FriendRelationships() {
	}

	protected FriendRelationships(Integer id, Integer selfId,
			Integer friendId, String friendAccount, String friendPetName) {
		super();
		this.id = id;
		this.selfId = selfId;
		this.friendId = friendId;
		this.friendAccount = friendAccount;
		this.friendPetName = friendPetName;
	}

	@Override
	public String toString() {
		return "Friends [id=" + id + ", selfId=" + selfId + ", friendId="
				+ friendId + ", friendAccount=" + friendAccount
				+ ", friendPetName=" + friendPetName + "]";
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSelfId() {
		return selfId;
	}

	public void setSelfId(Integer selfId) {
		this.selfId = selfId;
	}

	public Integer getFriendId() {
		return friendId;
	}

	public void setFriendId(Integer friendId) {
		this.friendId = friendId;
	}

	public String getFriendAccount() {
		return friendAccount;
	}

	public void setFriendAccount(String friendAccount) {
		this.friendAccount = friendAccount;
	}

	public String getFriendPetName() {
		return friendPetName;
	}

	public void setFriendPetName(String friendPetName) {
		this.friendPetName = friendPetName;
	}

}
