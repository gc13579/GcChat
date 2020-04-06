package com.GcChatServer.enity;

import java.io.Serializable;

public class User implements Serializable {
	private Integer id;
	private String account;
	private String password;
	private String petName;
	private Integer status;

	public User() {
	}

	public User(Integer id, String account, String password, String petName,
			Integer status) {
		this.id = id;
		this.account = account;
		this.password = password;
		this.petName = petName;
		this.status = status;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", account=" + account + ", password="
				+ password + ", petName=" + petName + ", status=" + status
				+ "]";
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPetName() {
		return petName;
	}

	public void setPetName(String petName) {
		this.petName = petName;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
