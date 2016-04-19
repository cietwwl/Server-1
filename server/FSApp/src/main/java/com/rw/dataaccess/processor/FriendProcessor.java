package com.rw.dataaccess.processor;

import com.rw.dataaccess.PlayerCreatedParam;
import com.rw.dataaccess.PlayerCreatedProcessor;
import com.rwbase.dao.friend.TableFriend;

public class FriendProcessor implements PlayerCreatedProcessor<TableFriend> {

	@Override
	public TableFriend create(PlayerCreatedParam param) {
		TableFriend tableFriend = new TableFriend();
		tableFriend.setUserId(param.getUserId());
		return tableFriend;
	}

}
