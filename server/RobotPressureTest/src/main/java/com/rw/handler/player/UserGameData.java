package com.rw.handler.player;

import com.rw.dataSyn.SynItem;

public class UserGameData implements SynItem{
	private String userId;
	private long carrerChangeTime;// 角色变换的时间

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return userId;
	}

	public long getCarrerChangeTime() {
		return carrerChangeTime;
	}
}
