package com.groupCopy.bm.groupCopy;

import java.util.Comparator;

import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyArmyDamageInfo;

public class GroupCopyDamegeRankComparator implements Comparator<GroupCopyArmyDamageInfo>{

	@Override
	public int compare(GroupCopyArmyDamageInfo o1, GroupCopyArmyDamageInfo o2) {
		if(o1.getDamage() > o2.getDamage()){
			return 1;
		}else if(o1.getDamage() < o2.getDamage()){
			return -1;
		}
		return o1.getTime() > o2.getTime() ? 1 : -1;
	}

	
}
