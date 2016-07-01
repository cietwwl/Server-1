package com.playerdata.groupFightOnline.manager;

import com.playerdata.Player;
import com.playerdata.groupFightOnline.data.GFightOnlineResourceData;
import com.playerdata.groupFightOnline.data.GFightOnlineResourceHolder;
import com.playerdata.groupFightOnline.data.UserGFightOnlineData;
import com.playerdata.groupFightOnline.data.UserGFightOnlineHolder;
import com.playerdata.groupFightOnline.dataForClient.DefendArmySimpleInfo;
import com.playerdata.groupFightOnline.dataForClient.GFResourceState;
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
		GFightOnlineResourceData resData = GFightOnlineResourceHolder.getInstance().get(resourceID);
		return GFResourceState.BIDDING.equals(resData.getState());
	}
	
	public boolean isPreparePeriod(int resourceID) {
		GFightOnlineResourceData resData = GFightOnlineResourceHolder.getInstance().get(resourceID);
		return GFResourceState.PREPARE.equals(resData.getState());
	}
	
	public boolean isFightPeriod(int resourceID) {
		GFightOnlineResourceData resData = GFightOnlineResourceHolder.getInstance().get(resourceID);
		return GFResourceState.FIGHT.equals(resData.getState());
	}
	
	public boolean isRestPeriod(int resourceID) {
		GFightOnlineResourceData resData = GFightOnlineResourceHolder.getInstance().get(resourceID);
		return GFResourceState.REST.equals(resData.getState());
	}
	
	public boolean isLegalBidCount(int resourceID, int oriCount, int addCount) {
		return true;
	}
	
	/**
	 * 判断个人是否有选中的对手
	 * @param userID
	 * @return
	 */
	public boolean haveSelectedEnimy(String userID){
		UserGFightOnlineData userGFData = UserGFightOnlineHolder.getInstance().get(userID);
		if(userGFData == null) return false;
		DefendArmySimpleInfo defenderSimple = userGFData.getRandomDefender();
		if(defenderSimple == null) return false;
		return true;
	}
	
	/**
	 * 锁定的时间是否过期
	 * @param defenderSimple
	 * @return
	 */
	public boolean isLockExpired(DefendArmySimpleInfo defenderSimple){
		if(System.currentTimeMillis() - defenderSimple.getLockArmyTime() > GFDefendArmyMgr.LOCK_ITEM_MAX_TIME)
			return false;
		return false;
	}
}
