package com.rw.dataaccess.processor;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rwbase.dao.friend.TableFriend;

public class FriendCreator implements DataExtensionCreator<TableFriend> {

	@Override
	public TableFriend create(String userId) {
		TableFriend tableFriend = new TableFriend();
		tableFriend.setUserId(userId);
		return tableFriend;
	}

}
