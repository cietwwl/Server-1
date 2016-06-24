package com.playerdata.groupFightOnline.manager;

import com.playerdata.Player;
import com.playerdata.groupFightOnline.data.GFDefendArmyItemHolder;
import com.playerdata.groupFightOnline.data.UserGFightOnlineData;
import com.playerdata.groupFightOnline.data.UserGFightOnlineHolder;
import com.playerdata.groupFightOnline.dataForClient.DefendArmySimpleInfo;
import com.rwproto.GrouFightOnlineProto.GFResultType;


class GFightConditionJudge {
	private static class InstanceHolder{
		private static GFightConditionJudge instance = new GFightConditionJudge();
	}
	
	public static GFightConditionJudge getInstance(){
		return InstanceHolder.instance;
	}
	
	private GFightConditionJudge() { }
	
	public GFResultType canBidForGroup(Player player, int resourceID, int bidCount) {
		
		return GFResultType.SUCCESS;
	}
	
	public boolean isBidPeriod(int resourceID) {
		return true;
	}
	
	public boolean isPreparePeriod(int resourceID) {
		return true;
	}
	
	public boolean isFightPeriod(int resourceID) {
		return true;
	}
	
	public boolean isFinalPeriod(int resourceID) {
		return true;
	}
	
	public boolean isLegalBidCount(int resourceID, int oriCount, int addCount) {
		return true;
	}
	
	public boolean haveSelectedEnimy(String userID){
		UserGFightOnlineData userGFData = UserGFightOnlineHolder.getInstance().get(userID);
		if(userGFData == null) return false;
		DefendArmySimpleInfo defenderSimple = userGFData.getRandomDefender();
		if(defenderSimple == null) return false;
		return true;
	}
	
	public boolean isLockExpired(DefendArmySimpleInfo defenderSimple){
		if(System.currentTimeMillis() - defenderSimple.getLockArmyTime() > GFDefendArmyItemHolder.LOCK_ITEM_MAX_TIME)
			return false;  // TODO
		return false;
	}
	
}
