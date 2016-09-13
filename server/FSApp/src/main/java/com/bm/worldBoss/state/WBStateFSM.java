package com.bm.worldBoss.state;

import com.bm.worldBoss.data.WBData;
import com.bm.worldBoss.data.WBDataHolder;
import com.bm.worldBoss.data.WBState;

public class WBStateFSM {
	
	private static WBStateFSM instance = new WBStateFSM(); 
	
	public static WBStateFSM getInstance(){
		return instance;
	}

	private IwbState curState;
	
	public WBStateFSM(){		
		
		WBData wbData = WBDataHolder.getInstance().get();
		
		if(wbData == null){
			curState =  new WBFinishState();
		}else{
			curState = initFromWbData(wbData);
		}
		curState.doEnter();
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
		return curStateTmp;
		
	}

	public void tranfer(){
		
		IwbState nextState = curState.doTransfer();		
		if(nextState!=null){	
			nextState.doEnter();
			curState = nextState;
		}		
	}

	
	public WBState getState(){
		return curState.getState();
	}
	
}
