package com.playerdata.groupFightOnline.state;

public class GFightStateTransfer {

	private IGFightState curState = new GFightInit();
	
	
	public void checkTransfer(){
		
		if(curState.canExit()){
			curState = curState.getNext();
			curState.Enter();
		}
		
	}
	
}
