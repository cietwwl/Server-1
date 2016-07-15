package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.Collections;
import java.util.LinkedList;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.groupCopy.bm.groupCopy.GroupCopyMgr;

/**
 * 帮派副本地图前10伤害排行榜(单次伤害)
 * @author Alex
 * 2016年6月12日 下午3:01:47
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupCopyDamegeRankInfo {
	
	private LinkedList<GroupCopyArmyDamageInfo> damageRank = new LinkedList<GroupCopyArmyDamageInfo>();

	public LinkedList<GroupCopyArmyDamageInfo> getDamageRank() {
		return damageRank;
	}



	public void setDamageRank(LinkedList<GroupCopyArmyDamageInfo> damageRank) {
		this.damageRank = damageRank;
	}



	public synchronized boolean addInfo(GroupCopyArmyDamageInfo info) {
		//先与最后的比较
		GroupCopyArmyDamageInfo tem = null;
		if(!damageRank.isEmpty() && damageRank.size() >= GroupCopyMgr.MAX_RANK_RECORDS){
			tem = damageRank.getLast();
			if(tem.getDamage() >= info.getDamage()){
				return false;
			}
		}
		//检查一下是否有记录
		for (GroupCopyArmyDamageInfo dInfo : damageRank) {
			if(dInfo.getPlayerID().equals(info.getPlayerID())){
				tem = dInfo;
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

	public synchronized void clear(){
		damageRank.clear();
	}
}
