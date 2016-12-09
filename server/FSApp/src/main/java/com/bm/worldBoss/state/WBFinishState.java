package com.bm.worldBoss.state;

import com.bm.worldBoss.WBMgr;
import com.bm.worldBoss.cfg.WBCfg;
import com.bm.worldBoss.cfg.WBCfgDAO;
import com.bm.worldBoss.data.WBData;
import com.bm.worldBoss.data.WBDataHolder;
import com.bm.worldBoss.data.WBState;
import com.log.GameLog;
import com.log.LogModule;
import com.rw.fsutil.util.DateUtils;
import com.rw.manager.ServerSwitch;

public class WBFinishState implements  IwbState{

	final private WBState state = WBState.Finish;
	
	@Override
	public IwbState doTransfer() {		
		
		boolean success = tryNextBoss();
		if(success){
			return new WBNewBossState();
		}
		
		return 	null;	
	}
	
	public boolean tryNextBoss(){
		boolean success = false;
		WBData wbData = WBDataHolder.getInstance().get();
		long curTime = System.currentTimeMillis();
//		if(wbData != null)
//			System.out.println("end time : " + DateUtils.getDateTimeFormatString(wbData.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
		if(wbData == null || wbData.getEndTime() < curTime){
			WBCfg nextCfg = WBCfgDAO.getInstance().getNextCfg();
			
			if(nextCfg!=null){
				success = WBMgr.getInstance().initNewBoss(nextCfg);
//				GameLog.info(LogModule.WorldBoss.getName(), "WBFinishState[tryNextBoss]", "result: " + success);
			}else{
//				GameLog.info(LogModule.WorldBoss.getName(), "WBFinishState[tryNextBoss]", "no wbcfg for today ");
			}
		}
		return success;
		
	}

	@Override
	public WBState getState() {
		return state;
	}
	
	@Override
	public void doEnter() {
		WBData wbData = WBDataHolder.getInstance().get();
		if (wbData != null) {
			wbData.setState(state);
			WBDataHolder.getInstance().update();

			//如果是在关闭状态，则不调整boss等级
			if(ServerSwitch.isOpenWorldBoss()){
				WBMgr.getInstance().adjustBossLevel();
			}
		}
		
		
	}

}
