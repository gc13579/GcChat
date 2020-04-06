package com.GcChatServer.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import com.GcChatServer.enity.FriendRelationships;

public class FriendsRelationshipsMapper implements
		RowMapper<FriendRelationships> {

	@Override
	public FriendRelationships mapperObject(ResultSet rs) throws SQLException {
		FriendRelationships friendsRelationships = new FriendRelationships();
		friendsRelationships.setId(rs.getInt("id"));
		friendsRelationships.setSelfId(rs.getInt("self_id"));
		friendsRelationships.setFriendId(rs.getInt("friend_id"));
		friendsRelationships.setFriendAccount(rs.getString("friend_account"));
		friendsRelationships.setFriendPetName(rs.getString("friend_pet_name"));
		return friendsRelationships;
	}

}
