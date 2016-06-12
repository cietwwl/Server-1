package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.Collections;
import java.util.LinkedList;

import com.groupCopy.bm.groupCopy.GroupCopyMgr;

/**
 * 帮派副本地图前10伤害排行榜(单次伤害)
 * @author Alex
 * 2016年6月12日 下午3:01:47
 */
public class GroupCopyDamegeRankInfo {
	
	
	
	private LinkedList<GroupCopyArmyDamageInfo> damageRank = new LinkedList<GroupCopyArmyDamageInfo>();

	
	
	public synchronized boolean addInfo(GroupCopyArmyDamageInfo info) {
		//先与最后的比较
		GroupCopyArmyDamageInfo tem = null;
		if(!damageRank.isEmpty()){
			tem = damageRank.getLast();
			if(tem.getDamage() >= info.getDamage()){
				return false;
			}
		}
		//检查一下是否有记录
		for (GroupCopyArmyDamageInfo dInfo : damageRank) {
			if(dInfo.getPlayerID() == info.getPlayerID()){
				tem = info;
			}
		}
		if(tem != null){
			//与旧记录比较
			if(tem.getDamage() >= info.getDamage()){
				return false;
			}else{
				damageRank.remove(tem);
			}
		}
		
		damageRank.add(info);
		Collections.sort(damageRank, GroupCopyMgr.RANK_COMPARATOR);
		if(damageRank.size() > GroupCopyMgr.MAX_RANK_RECORDS){
			damageRank.removeLast();
		}
		
		return true;
	}

}
