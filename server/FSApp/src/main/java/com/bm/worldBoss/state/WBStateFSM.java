package com.bm.worldBoss.state;

import com.bm.worldBoss.data.WBDataHolder;
import com.bm.worldBoss.data.WBState;

public class WBStateFSM {
	
	private static WBStateFSM instance = new WBStateFSM(); 
	
	public static WBStateFSM getInstance(){
		return instance;
	}

	private IwbState curState;
	
	public WBStateFSM(){		
		
		WBDataHolder.getInstance().get();
		
		if(curState == null){
			curState =  new WBFinishState();
			curState.doEnter();
		}
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
