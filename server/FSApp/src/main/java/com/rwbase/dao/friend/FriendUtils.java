package com.rwbase.dao.friend;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import com.playerdata.Player;
import com.playerdata.readonly.PlayerIF;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.dao.friend.vo.FriendGiveState;
import com.rwbase.dao.friend.vo.FriendItem;

public class FriendUtils {

	private static long ONE_MONTH = TimeUnit.DAYS.toMillis(30);
	private static long ONE_DAY = TimeUnit.DAYS.toMillis(1);
	private static long ONE_HOUR = TimeUnit.HOURS.toMillis(1);
	private static long ONE_MINUTE = TimeUnit.MINUTES.toMillis(1);
	private static FriendUtils instance = new FriendUtils();
	private String[] DAYS_TIPS;
	private String[] HOURS_TIPS;
	private String[] MINUTE_TIPS;

	public static FriendUtils getInstance() {
		return instance;
	}

	public FriendUtils() {
		DAYS_TIPS = new String[32];
		for (int i = 0; i < 32; i++) {
			DAYS_TIPS[i] = i + "天前登陆";
		}
		HOURS_TIPS = new String[25];
		for (int i = 0; i < 25; i++) {
			HOURS_TIPS[i] = i + "小时前登陆";
		}
		MINUTE_TIPS = new String[61];
		for (int i = 0; i < 61; i++) {
			MINUTE_TIPS[i] = i + "分钟前登录";
		}
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

	/**
	 * 查找用户有没有在黑名单中
	 * 
	 * @param player
	 * @param checkPlayerId
	 * @return
	 */
	public boolean isBlack(String userId, String checkPlayerId) {
		TableFriend tableFriend = TableFriendDAO.getInstance().get(userId);
		if (tableFriend == null) {
			return false;
		}
		if (tableFriend.getBlackItem(checkPlayerId) != null) {
			return true;
		}
		return false;
	}

	/** 时间换算 */
	public String getLastLoginTip(long lastLoginTime) {
		long diffTime = DateUtils.getSecondLevelMillis() - lastLoginTime;
		if (diffTime > ONE_MONTH) {
			return "1个月前登陆";
		}
		if (diffTime > ONE_DAY) {
			long day = diffTime / ONE_DAY;
			return DAYS_TIPS[(int) day];
		}
		if (diffTime > ONE_HOUR) {
			long hour = diffTime / ONE_HOUR;
			return HOURS_TIPS[(int) hour];
		}
		if (diffTime > ONE_MINUTE) {
			long minute = diffTime / ONE_MINUTE;
			return MINUTE_TIPS[(int) minute];
		}
		return "1分钟前登陆";
	}

	/** 检查是否还有未领取体力 */
	public void checkHasNotReceive(Player player, TableFriend tableFriend) {
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
