package com.bm.worldBoss.state;

import com.bm.worldBoss.WBMgr;
import com.bm.worldBoss.data.WBData;
import com.bm.worldBoss.data.WBDataHolder;
import com.bm.worldBoss.data.WBState;
import com.bm.worldBoss.rank.WBHurtRankMgr;
import com.rw.fsutil.util.DateUtils;

 class WBFightStartState implements  IwbState{

	
	final private WBState state = WBState.FightStart;
	
	@Override
	public IwbState doTransfer() {
		WBData wbData = WBDataHolder.getInstance().get();
		
		long currentTimeMillis = System.currentTimeMillis();
		//System.out.println("fight start state, cur minunt:" + DateUtils.getDateTimeFormatString(currentTimeMillis, "yyyy-MM-dd HH:mm:ss")
		//		+ ",END stat :" + DateUtils.getDateTimeFormatString(wbData.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
		if((wbData.getEndTime()) <= currentTimeMillis || wbData.getCurLife() <= 0){
			return new WBFightEndState();
		}
		
		return 	null;	
	}

	@Override
	public WBState getState() {
		return state;
	}

	
	@Override
	public void doEnter() {
		
		WBData wbData = WBDataHolder.getInstance().get();
		wbData.setState(state);
		WBDataHolder.getInstance().update();		
		WBHurtRankMgr.clearRank();	
		WBMgr.getInstance().broatBossChange(false);
	}
}
