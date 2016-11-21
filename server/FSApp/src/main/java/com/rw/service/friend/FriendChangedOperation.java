package com.rw.service.friend;

import com.rwbase.dao.friend.TableFriend;
import com.rwbase.dao.friend.vo.FriendItem;

public interface FriendChangedOperation {

	/**
	 * <pre>
	 * 向指定关系添加指定{@link FriendItem}
	 * </pre>
	 * @param host
	 * @param guestUserId
	 * @return
	 */
	public String addFriendItem(TableFriend host, String guestUserId);

}
