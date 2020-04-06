package com.GcChatServer.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import com.GcChatServer.enity.User;

public class UserMapper implements RowMapper<User> {
	@Override
	public User mapperObject(ResultSet rs) throws SQLException {
		User user = new User();
		user.setId(rs.getInt("id"));
		user.setAccount(rs.getString("account"));
		user.setPassword(rs.getString("password"));
		user.setPetName(rs.getString("pet_name"));
		user.setStatus(rs.getInt("status"));
		return user;
	}

}
