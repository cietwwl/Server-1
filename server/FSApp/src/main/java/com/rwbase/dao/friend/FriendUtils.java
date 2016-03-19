package com.rwbase.dao.friend;

import java.util.Calendar;
import java.util.Iterator;

import com.playerdata.HotPointMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.readonly.PlayerIF;
import com.rwbase.dao.friend.vo.FriendGiveState;
import com.rwbase.dao.friend.vo.FriendItem;
import com.rwbase.dao.hotPoint.EHotPointType;

public class FriendUtils {
	/**
	 * 查找用户有没有在黑名单中
	 * 
	 * @param playerId
	 * @param checkPlayerId
	 * @return
	 */
	public static boolean isBlack(String playerId, String checkPlayerId) {
		PlayerIF player = PlayerMgr.getInstance().getReadOnlyPlayer(playerId);
		if (isBlack(player, checkPlayerId)) {
			return true;
		}

		return false;
	}

	/**
	 * 查找用户有没有在黑名单中
	 * 
	 * @param player
	 * @param checkPlayerId
	 * @return
	 */
	public static boolean isBlack(PlayerIF player, String checkPlayerId) {
		if (player != null && (player.getFriendMgr().getTableFriend().getBlackItem(checkPlayerId) != null)) {
			return true;
		}

		return false;
	}

	/** 时间换算 */
	public static String getLastLoginTip(long lastLoginTime) {
		long diffTime = Calendar.getInstance().getTime().getTime() - lastLoginTime;
		int month = (int) (diffTime / 1000 / 60 / 60 / 24 / 30);
		if (month > 0) {
			// return month + "个月前";
			return "一个月前";
		}
		int day = (int) (diffTime / 1000 / 60 / 60 / 24);
		if (day > 0) {
			return day + "天前";
		}
		int hour = (int) (diffTime / 1000 / 60 / 60);
		if (hour > 0) {
			return hour + "小时前";
		}
		int minute = (int) (diffTime / 1000 / 60);
		if (minute > 0) {
			return minute + "分钟前";
		}
		return "1分钟前";
	}

	/** 检查是否还有未领取体力 */
	public static void checkHasNotReceive(Player player, TableFriend tableFriend) {
		if (player == null || tableFriend == null) {
			return;
		}
		boolean hasNotReceive = false;
		if (tableFriend.getFriendVo().isCanReceive(player.getLevel())) {
			Iterator<FriendItem> it = tableFriend.getFriendList().values().iterator();
			while (it.hasNext()) {
				FriendItem item = it.next();
				FriendGiveState giveState = tableFriend.getFriendGiveList().get(item.getUserId());
				if (giveState != null) {
					if (giveState.isReceiveState()) {
						hasNotReceive = true;
						break;
					}
				}
			}
		} else {
			hasNotReceive = false;
		}
	}
}
