package com.playerdata.groupcompetition.syn;


public interface ISameSceneDataSignal {
	
	public void setChangedSignal(boolean signal);
	
	public void setRemovedSignal(boolean signal);
	
	public void setNewAddSignal(boolean signal);
	
	public boolean getChangedSignal();
	
	public boolean getRemovedSignal();
	
	public boolean getNewAddSignal();
}
