package com.playerdata.dataSyn.sameSceneSyn;


public interface SameSceneDataBaseIF {
	public boolean isChanged();
	public void setChanged(boolean changed);
	public boolean isRemoved();
	public void setRemoved(boolean removed);
	public boolean isNewAdd();
	public void setNewAdd(boolean newAdd);
}
