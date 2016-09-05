package com.rw.dataaccess.mapitem;

public class MapItemValidateParam {

	private final int level;		//角色等级
	private final long currentTime;	//当前时间

	public MapItemValidateParam(int level, long currentTime) {
		super();
		this.level = level;
		this.currentTime = currentTime;
	}

	public int getLevel() {
		return level;
	}

	public long getCurrentTime() {
		return currentTime;
	}

}
