package com.bm.worldBoss.state;

import com.bm.worldBoss.data.WBState;

public class WBStateFSM {
	
	private static WBStateFSM instance = new WBStateFSM(); 
	
	public static WBStateFSM getInstance(){
		return instance;
	}

	private IwbState curState =  new WBFinishState();
	
	public void tranfer(){
		IwbState nextState = curState.doTransfer();		
		if(nextState!=null){	
			nextState.doEnter();
			curState = nextState;
		}		
	}
	
	public void newBoss(){
		curState = new WBPreStartState();
	}
	
	public WBState getState(){
		return curState.getState();
	}
	
}
