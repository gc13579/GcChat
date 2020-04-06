package com.GcChatServer.service.proxy;

import java.util.List;

import com.GcChatServer.enity.User;
import com.GcChatServer.exception.LoginFailException;
import com.GcChatServer.exception.UserExistedException;
import com.GcChatServer.factory.ObjectFactory;
import com.GcChatServer.service.UserService;
import com.GcChatServer.trans.Transation;

public class UserServiceProxy implements UserService {
	Transation trans = (Transation) ObjectFactory.getObject("trans");
	UserService userService = (UserService) ObjectFactory
			.getObject("userServiceImpl");

	// 登录
	@Override
	public User login(String username, String password)
			throws LoginFailException {
		User user = null;
		trans.begin();
		try {
			user = userService.login(username, password);
			trans.commit();
		} catch (LoginFailException e) {
			trans.rollback();
			throw e;
		} catch (Exception e) {
			trans.rollback();
			e = new Exception("连接出现异常，请稍后再试");
		}
		return user;
	}

	// 根据账号查询用户
	@Override
	public User getUserByAccount(String account) {
		trans.begin();
		User user = null;
		try {
			user = userService.getUserByAccount(account);
			trans.commit();
		} catch (Exception e) {
			e.printStackTrace();
			trans.rollback();
		}
		return user;
	}

	// 注册
	@Override
	public void enroll(String account, String password, String petName)
			throws UserExistedException {
		trans.begin();
		try {
			userService.enroll(account, password, petName);
			trans.commit();
		} catch (UserExistedException e) {
			e.printStackTrace();
			trans.rollback();
			throw e;
		} catch (Exception e) {
			trans.rollback();
			e.printStackTrace();
		}
	}

	// 查询添加新朋友,且排除自己
	@Override
	public List<User> getUsersByCondition(String condition, Integer id) {
		trans.begin();
		List<User> list = null;
		try {
			list = userService.getUsersByCondition(condition, id);
			trans.commit();
		} catch (Exception e) {
			e.printStackTrace();
			trans.rollback();
		}
		return list;
	}

	// 修改用户状态(未/已登录)
	@Override
	public void modifyUserStatus(Integer userId, Integer status) {
		trans.begin();
		try {
			userService.modifyUserStatus(userId, status);
			trans.commit();
		} catch (Exception e) {
			e.printStackTrace();
			trans.rollback();
		}
	}

}
