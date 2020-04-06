package com.GcChatServer.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.GcChatServer.enity.User;

public interface UserDao {
	// 鐧诲綍
	public User selectUserByUsernameAndPwd(String account, String password);

	// 鏍规嵁璐﹀彿鏌ヨ鐢ㄦ埛
	public User selectUserByAccount(String account);

	// 娉ㄥ唽
	public void insertUser(String account, String password, String petName)
			throws SQLException;

	// 鏌ヨ娣诲姞鏂版湅鍙�涓旀帓闄よ嚜宸�	
	public List<User> selectUsersBySearchCondition(String searchCondition,
			Integer id);

	// 淇敼鐢ㄦ埛鐘舵�(鏈�宸茬櫥褰�
	public void updateUserStatus(Integer userId, Integer status);
}
