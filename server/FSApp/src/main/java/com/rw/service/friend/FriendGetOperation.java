package com.rw.service.friend;

import java.util.Enumeration;

import com.rwbase.dao.friend.TableFriend;
import com.rwbase.dao.friend.vo.FriendItem;

/**
 * 基于TableFriend数据结构的封装
 * 
 * @author Jamaz
 *
 */
public interface FriendGetOperation {

	/**
	 * 获取指定某个关系的FriendItem
	 * @param tableFriend
	 * @param userId
	 * @return
	 */
	public FriendItem getItem(TableFriend tableFriend, String userId);

	/**
	 * 获取指定某种关系集
	 * @param tableFriend
	 * @return
	 */
	public Enumeration<FriendItem> getItemEnumeration(TableFriend tableFriend);
	
	/** 好友实现 **/
	FriendGetOperation FRIEND = new FriendGetOperation() {

		@Override
		public FriendItem getItem(TableFriend tableFriend, String userId) {
			return tableFriend.getFriendItem(userId);
		}

		@Override
		public Enumeration<FriendItem> getItemEnumeration(TableFriend tableFriend) {
			return tableFriend.getFriendMap();
		}
	};

	/** 黑名单实现 **/
	FriendGetOperation BLACKLIST = new FriendGetOperation() {

		@Override
		public FriendItem getItem(TableFriend tableFriend, String userId) {
			return tableFriend.getBlackItem(userId);
		}

		@Override
		public Enumeration<FriendItem> getItemEnumeration(TableFriend tableFriend) {
			return tableFriend.getBlackMap();
		}
	};
}
