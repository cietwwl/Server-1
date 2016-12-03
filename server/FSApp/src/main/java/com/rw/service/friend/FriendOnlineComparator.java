package com.rw.service.friend;

import java.util.Comparator;

import com.rwbase.dao.friend.vo.FriendItem;

public class FriendOnlineComparator implements Comparator<FriendItem> {

	@Override
	public int compare(FriendItem o1, FriendItem o2) {
		// 同样在线时，按照等级排列（越高越前）
		// 同样等级时，按照贵族等级排列（越高越前）
		// 同样贵族等级时，按照登录时间排序（越新越前）
		int param1 = o1.getLevel();
		int param2 = o2.getLevel();
		if (param1 > param2) {
			return -1;
		} else if (param2 > param1) {
			return 1;
		}

		param1 = o1.getVip();
		param2 = o2.getVip();
		if (param1 > param2) {
			return -1;
		} else if (param2 > param1) {
			return 1;
		}

		if (o1.getLastLoginTime() - o2.getLastLoginTime() > 0) {
			return -1;
		} else {
			return 1;
		}
	}

}
