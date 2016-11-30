package com.bm.worldBoss.state;

import com.bm.worldBoss.cfg.WBCfg;
import com.bm.worldBoss.cfg.WBCfgDAO;
import com.bm.worldBoss.data.WBData;
import com.bm.worldBoss.data.WBDataHolder;
import com.bm.worldBoss.data.WBState;
import com.log.GameLog;
import com.log.LogModule;
import com.rw.fsutil.util.DateUtils;
import com.rw.manager.ServerSwitch;

public class WBStateFSM {
	
	private static WBStateFSM instance = new WBStateFSM(); 
	
	public static WBStateFSM getInstance(){
		return instance;
	}

	private IwbState curState;
	
	public void init(){		
		
		WBData wbData = WBDataHolder.getInstance().get();
		if(wbData == null || DateUtils.dayChanged(wbData.getStartTime())){
			curState =  new WBFinishState();
		}else{
			WBCfg cfg = WBCfgDAO.getInstance().getCfgById(wbData.getWbcfgId());//如果策划改表，重新初始化
			if(cfg == null){
				curState =  new WBFinishState();
			}else{
				curState = initFromWbData(wbData);
			}
		}
		curState.doEnter();
		GameLog.info(LogModule.WorldBoss.getName(), "WBStateFSM[init]", "world boss init finish");
	}
	
	private IwbState initFromWbData(WBData wbData) {
		WBState state = wbData.getState();
		IwbState curStateTmp = null;
		switch (state) {
			case NewBoss:
				curStateTmp = new WBNewBossState();
				break;

			case PreStart:
				curStateTmp = new WBPreStartState();
				break;
			
			case FightStart:
				curStateTmp = new WBFightStartState();
				break;
			
			case FightEnd:
				curStateTmp = new WBFightEndState();
				break;
				
			case SendAward:
				curStateTmp = new WBSendAwardState();
				break;
				
			case Finish:
				curStateTmp = new WBFinishState();
				break;			
				
		default:
			break;
		}
		
		//检查一下配置表
		checkCfg();
		
		return curStateTmp;
		
	}
	
	/**
	 * 检查一下配置文件,并且设置是否开启世界boss
	 */
	private void checkCfg(){
		WBData data = WBDataHolder.getInstance().get();
		data.setOpen(ServerSwitch.isOpenWorldBoss());
		WBCfg cfg = WBCfgDAO.getInstance().getCfgById(data.getWbcfgId());
		WBDataHolder.getInstance().reCfg(data, cfg);
	}

	public void tranfer(){
		if(curState == null){
			return;
		}
		IwbState nextState = curState.doTransfer();		
		if(nextState!=null){	
			GameLog.info(LogModule.WorldBoss.getName(), "WBStateFSM[tranfer]", "world boss state transfer to " + nextState);
			nextState.doEnter();
			curState = nextState;
		}	
		
	}

	
	public WBState getState(){
		return curState.getState();
	}
	
}
