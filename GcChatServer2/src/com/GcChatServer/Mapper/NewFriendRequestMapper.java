package com.GcChatServer.Mapper;

import java.sql.ResultSet;

import com.GcChatServer.enity.NewFriendRequest;

public class NewFriendRequestMapper implements RowMapper<NewFriendRequest> {

	@Override
	public NewFriendRequest mapperObject(ResultSet rs) throws Exception {
		NewFriendRequest newFriendRequest = new NewFriendRequest();
		newFriendRequest.setId(rs.getInt("id"));
		newFriendRequest.setMessage(rs.getString("message"));
		newFriendRequest.setStatus(rs.getInt("status"));
		newFriendRequest.setSourceUserId(rs.getInt("source_user_id"));
		newFriendRequest.setTargetUserId(rs.getInt("target_user_id"));
		newFriendRequest.setVisitedTimes(rs.getInt("visited_times"));
		newFriendRequest.setSourceUserPetName(rs
				.getString("source_user_pet_name"));
		newFriendRequest.setSourceUserAccount(rs
				.getString("source_user_account"));
		return newFriendRequest;
	}

}
