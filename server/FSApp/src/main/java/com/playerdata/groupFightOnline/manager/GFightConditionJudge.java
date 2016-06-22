package com.playerdata.groupFightOnline.manager;

import com.playerdata.Player;
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
}
