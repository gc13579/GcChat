package com.GcChatServer.service.impl;

import java.sql.SQLException;
import java.util.List;

import com.GcChatServer.dao.UserDao;
import com.GcChatServer.enity.User;
import com.GcChatServer.exception.LoginFailException;
import com.GcChatServer.exception.UserExistedException;
import com.GcChatServer.factory.ObjectFactory;
import com.GcChatServer.service.UserService;

public class UserServiceImpl implements UserService {
	UserDao userDao = (UserDao) ObjectFactory.getObject("userDao");

	// 登录
	@Override
	public User login(String account, String password)
			throws LoginFailException, Exception {
		User user = userDao.selectUserByUsernameAndPwd(account, password);
		// 业务逻辑判断
		if (user == null) {
			throw new LoginFailException("用户名或密码错误");
		}
		if (user != null && user.getStatus() == 1) {
			throw new LoginFailException("该用户已登录");
		}
		return user;
	}

	// 根据账号查询用户
	@Override
	public User getUserByAccount(String account) {
		User user = userDao.selectUserByAccount(account);
		return user;
	}

	// 注册
	@Override
	public void enroll(String account, String password, String petName)
			throws UserExistedException {
		User user = null;
		try {
			user = userDao.selectUserByAccount(account);
			if (user != null) {
				throw new UserExistedException("此账号已被注册");
			}
			userDao.insertUser(account, password, petName);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (UserExistedException e) {
			e.printStackTrace();
			throw e;
		}
	}

	// 查询添加新朋友,且排除自己
	@Override
	public List<User> getUsersByCondition(String condition, Integer id) {
		List<User> list = userDao.selectUsersBySearchCondition(condition, id);
		return list;
	}

	// 修改用户状态(未/已登录)
	@Override
	public void modifyUserStatus(Integer userId, Integer status) {
		userDao.updateUserStatus(userId, status);
	}

}
