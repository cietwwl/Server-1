package com.rw.service.friend;

import java.util.Enumeration;

import com.rwbase.dao.friend.TableFriend;
import com.rwbase.dao.friend.vo.FriendItem;

public class FriendOperationFactory {

	/** 好友实现 **/
	private static FriendGetOperation friendOperation = new FriendGetOperation() {

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
	private static FriendGetOperation blackListOperation = new FriendGetOperation() {

		@Override
		public FriendItem getItem(TableFriend tableFriend, String userId) {
			return tableFriend.getBlackItem(userId);
		}

		@Override
		public Enumeration<FriendItem> getItemEnumeration(TableFriend tableFriend) {
			return tableFriend.getBlackMap();
		}
	};

	private static RequestOperation requestOperation = new RequestOperation();

	public static FriendGetOperation getFriendOperation() {
		return friendOperation;
	}

	public static FriendGetOperation getBlackListOperation() {
		return blackListOperation;
	}

	public static RequestOperation getRequestOperation() {
		return requestOperation;
	}
}
