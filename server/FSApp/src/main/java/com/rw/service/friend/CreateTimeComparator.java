package com.rw.service.friend;

import java.util.Comparator;

import com.rwbase.dao.friend.vo.FriendItem;

public class CreateTimeComparator implements Comparator<FriendItem> {

	@Override
	public int compare(FriendItem o1, FriendItem o2) {
		if (o1.getCreateTime() - o2.getCreateTime() > 0) {
			return -1;
		} else {
			return 1;
		}
	}

}
