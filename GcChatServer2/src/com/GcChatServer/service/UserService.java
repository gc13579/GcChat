package com.GcChatServer.service;

import java.util.List;

import com.GcChatServer.enity.User;
import com.GcChatServer.exception.LoginFailException;
import com.GcChatServer.exception.UserExistedException;

public interface UserService {
	// 登录
	public User login(String account, String password)
			throws LoginFailException, Exception;

	// 根据账号查询用户
	public User getUserByAccount(String account);

	// 注册
	public void enroll(String account, String password, String petName)
			throws UserExistedException;

	// 查询添加新朋友,且排除自己
	public List<User> getUsersByCondition(String condition, Integer id);

	// 修改用户状态(未/已登录)
	public void modifyUserStatus(Integer userId, Integer status);
}
