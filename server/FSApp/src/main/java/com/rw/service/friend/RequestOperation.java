package com.rw.service.friend;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import com.playerdata.PlayerMgr;
import com.rwbase.dao.friend.TableFriend;
import com.rwbase.dao.friend.TableFriendDAO;
import com.rwbase.dao.friend.vo.FriendItem;
import com.rwbase.dao.friend.vo.FriendResultVo;
import com.rwproto.FriendServiceProtos.EFriendResultType;

/**
 * <pre>
 * 好友申请列表的操作
 * TODO 抽象接口
 * </pre>
 * 
 * @author Jamaz
 *
 */
public class RequestOperation {

	private static int requestLimit = 30;

	/**
	 * 获取指定某个关系的FriendItem
	 * 
	 * @param tableFriend
	 * @param userId
	 * @return
	 */
	public FriendItem getItem(TableFriend tableFriend, String userId) {
		return tableFriend.getRequestItem(userId);
	}

	/**
	 * 获取指定某种关系集
	 * 
	 * @param tableFriend
	 * @return
	 */
	public Enumeration<FriendItem> getItemEnumeration(TableFriend tableFriend) {
		return tableFriend.getRequestMap();
	}

	/**
	 * <pre>
	 * host向指定的guest发起添加好友的申请
	 * 即把host加入到guest的申请列表中
	 * </pre>
	 * 
	 * @param guest
	 * @param guestUserId
	 * @return
	 */
	public boolean addFriendItem(TableFriend guest, TableFriend host, FriendResultVo resultVo) {
		if (resultVo != null) {
			resultVo.resultType = EFriendResultType.SUCCESS;
			resultVo.resultMsg = "已向对方发送添加好友请求";
		}
		String hostUserId = host.getUserId();
		String guestUserId = guest.getUserId();
		if (guest.getBlackList().containsKey(guestUserId)) {
			return false;
		} else {
			FriendItem friendItem = FriendHandler.getInstance().newFriendItem(hostUserId);
			ConcurrentHashMap<String, FriendItem> requestMap = guest.getRequestList();
			if (requestMap.putIfAbsent(hostUserId, friendItem) == null) {
				FriendHandler.getInstance().pushRequestAddFriend(guestUserId, friendItem);
				host.removeFromBlackList(guestUserId);

				if (requestMap.size() > requestLimit) {
					long createTime = Long.MAX_VALUE;
					String removeUserId = null;
					for (FriendItem item : requestMap.values()) {
						long itemCreateTime = item.getCreateTime();
						if (itemCreateTime < createTime) {
							createTime = itemCreateTime;
							removeUserId = item.getUserId();
						}
					}
					if (removeUserId != null) {
						requestMap.remove(removeUserId);
					}
				}

				TableFriendDAO.getInstance().update(guestUserId);
				PlayerMgr.getInstance().setRedPointForHeartBeat(guestUserId);
				return true;
			} else {
				return false;
			}
		}
	}

}
