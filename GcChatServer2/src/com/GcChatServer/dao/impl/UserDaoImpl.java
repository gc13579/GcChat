package com.GcChatServer.dao.impl;

import java.sql.SQLException;
import java.util.List;

import com.GcChatServer.Mapper.UserMapper;
import com.GcChatServer.Util.JDBCTemplate;
import com.GcChatServer.dao.UserDao;
import com.GcChatServer.enity.User;

public class UserDaoImpl implements UserDao {

	JDBCTemplate<User> temp = new JDBCTemplate<User>();

	// 登录
	@Override
	public User selectUserByUsernameAndPwd(String username, String password) {
		String sql = new StringBuffer().append(" select ")
				.append(" 	id,account,password,pet_name,status ")
				.append(" from ").append(" 	t_user ").append(" where ")
				.append(" 	account = ? and ").append("  	password = ? ")
				.toString();
		User user = temp.selectOne(new UserMapper(), sql, username, password);
		return user;
	}

	// 根据账号查询用户
	@Override
	public User selectUserByAccount(String account) {
		StringBuffer sql = new StringBuffer().append(" select ")
				.append(" 	id,account,password,pet_name,status ")
				.append(" from ").append(" 	t_user ").append(" where ")
				.append(" 	account = ? ");
		return temp.selectOne(new UserMapper(), sql.toString(), account);
	}

	// 注册新用户
	@Override
	public void insertUser(String account, String password, String petName)
			throws SQLException {
		System.out.println("用户注册 userDaoImpl petName=" + petName);
		System.out.println(getEncoding(petName));
		StringBuffer sql = new StringBuffer().append(" insert into ")
				.append(" 	t_user ")
				.append(" 	(id,account,password,pet_name,status) ")
				.append(" values ").append(" 	(null,?,?,?,0) ");
		temp.insert(sql.toString(), account, password, petName);
	}

	// 查询添加新朋友,且排除自己
	@Override
	public List<User> selectUsersBySearchCondition(String searchCondition,
			Integer id) {
		System.out.println("dao " + searchCondition + " " + id);
		StringBuffer sql = new StringBuffer().append(" select ")
				.append(" 	id,account,password,pet_name,status ")
				.append(" from ").append(" 	t_user ").append(" where ")
				.append(" 	(account like ? ").append(" 	or pet_name like ? ) ")
				.append(" and ").append(" id != ? ");
		List<User> list = temp.selectAll(new UserMapper(), sql.toString(),
				searchCondition, searchCondition, id);
		return list;
	}

	// 修改用户状态(未/已登录)
	@Override
	public void updateUserStatus(Integer userId, Integer status) {
		StringBuffer sql = new StringBuffer().append(" update ")
				.append(" 	t_user ").append(" set ").append(" 	status = ? ")
				.append(" where ").append(" 	id = ? ");
		try {
			temp.update(sql.toString(), status, userId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static String getEncoding(String str) {
		String encode = "GB2312";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) { // 判断是不是GB2312
				String s = encode;
				return s; // 是的话，返回“GB2312“，以下代码同理
			}
		} catch (Exception exception) {
		}
		encode = "ISO-8859-1";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) { // 判断是不是ISO-8859-1
				String s1 = encode;
				return s1;
			}
		} catch (Exception exception1) {
		}
		encode = "UTF-8";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) { // 判断是不是UTF-8
				String s2 = encode;
				return s2;
			}
		} catch (Exception exception2) {
		}
		encode = "gbk";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) { // 判断是不是GBK
				String s3 = encode;
				return s3;
			}
		} catch (Exception exception3) {
		}
		return "";
	}
}
