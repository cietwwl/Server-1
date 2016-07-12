package com.playerdata.groupFightOnline.bm;

import com.bm.player.PlayerChangePopertyObserver;
import com.bm.player.PlayerChangePopertySubscribe;
import com.bm.rank.groupFightOnline.GFOnlineHurtRankMgr;
import com.bm.rank.groupFightOnline.GFOnlineKillRankMgr;
import com.playerdata.Player;
import com.playerdata.groupFightOnline.data.GFDefendArmyItemHolder;
import com.playerdata.groupFightOnline.manager.GFDefendArmyMgr;

public class GFOnlineListenerPlayerChange extends PlayerChangePopertySubscribe {

	public GFOnlineListenerPlayerChange(PlayerChangePopertyObserver observer) {
		super(observer);
	}

	@Override
	public void playerChangeName(Player p) {
		GFOnlineHurtRankMgr.updateGFHurtRankInfo(p);
		GFOnlineKillRankMgr.updateGFKillRankInfo(p);
	}

	@Override
	public void playerChangeLevel(Player p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerChangeVipLevel(Player p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerChangeTemplateId(Player p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerChangeHeadIcon(Player p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerChangeHeadBox(Player p) {
		// TODO Auto-generated method stub
		
	}
	
	public static void userLeaveGroupHandler(String userID, String groupID){
		GFDefendArmyItemHolder.getInstance().removePersonalDefendArmy(userID, groupID);
	}
	
	public static void heroChangeHandler(Player player){
		GFDefendArmyMgr.getInstance().heroChanged(player);
	}
}
