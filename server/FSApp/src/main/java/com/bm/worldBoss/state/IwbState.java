package com.bm.worldBoss.state;

import com.bm.worldBoss.data.WBState;

public interface IwbState {
	
	public void doEnter();

	public IwbState doTransfer();
	
	public WBState getState();
	
	
}
