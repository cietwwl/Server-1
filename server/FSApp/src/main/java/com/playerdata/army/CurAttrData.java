package com.playerdata.army;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class CurAttrData {

	private String id;
	
	private int curLife;//当前生命值
	
	private int curEnergy;
	
	public int getCurLife() {
		return curLife;
	}
	public void setCurLife(int curLife) {
		this.curLife = curLife;
	}
	public int getCurEnergy() {
		return curEnergy;
	}
	public void setCurEnergy(int curEnergy) {
		this.curEnergy = curEnergy;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
	
}
