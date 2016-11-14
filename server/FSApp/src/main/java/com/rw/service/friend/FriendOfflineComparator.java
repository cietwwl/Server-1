package com.rw.service.friend;

import java.util.Comparator;
import com.rwbase.dao.friend.vo.FriendItem;

//不能用于TreeSet等使用
public class FriendOfflineComparator implements Comparator<FriendItem> {

	@Override
	public int compare(FriendItem o1, FriendItem o2) {
		if (o1.getLastLoginTime() - o2.getLastLoginTime() > 0) {
			return -1;
		} else {
			return 1;
		}
	}

}
