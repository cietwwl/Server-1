package com.rw.service.friend;

import java.util.Enumeration;

import com.rwbase.dao.friend.TableFriend;
import com.rwbase.dao.friend.vo.FriendItem;
import com.rwbase.dao.friend.vo.FriendResultVo;
import com.rwproto.FriendServiceProtos.EFriendResultType;

public class FriendOperationImpl implements FriendGetOperation {

	private final int FRIEND_LIMIT = 100;// 好友上限

	@Override
	public FriendItem getItem(TableFriend tableFriend, String userId) {
		return tableFriend.getFriendItem(userId);
	}

	@Override
	public Enumeration<FriendItem> getItemEnumeration(TableFriend tableFriend) {
		return tableFriend.getFriendMap();
	}
  
//	@Override
//	public boolean addFriendItem(TableFriend tableFriend, String otherUserId, FriendResultVo resultVo) {
//		if (otherUserId.equals(tableFriend.getUserId())) {
//			addResultMsg(resultVo, EFriendResultType.FAIL, "该玩家是自己");
//			return true;
//		}
//		if (tableFriend.getFriendList().size() >= FRIEND_LIMIT) {// 自己好友达到上限
//			addResultMsg(resultVo, EFriendResultType.FAIL, "好友数量已达上限");
//			return false;
//		}
//		if (tableFriend.getFriendList().containsKey(otherUserId)) {// 已经是好友了
//			addResultMsg(resultVo, EFriendResultType.FAIL_2, "对方已经是自己的好友");
//			return true;
//		}
//		if (!isOtherFriendLimit(otherUserId, this.userId)) {// 对方好友达到上限
//			addResultMsg(resultVo, EFriendResultType.FAIL, "对方好友数量已达上限");
//			return true;
//		}
//		addRobotOrPlayerToFriend(otherUserId, tableFriend, resultVo);
//		return true;
//	}

//	private void addResultMsg(FriendResultVo resultVo, EFriendResultType type, String tips) {
//		if (resultVo != null) {
//			resultVo.resultType = type;
//			resultVo.resultMsg = tips;
//		}
//	}
}
