package com.playerdata.army;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurAttrData {

	private String id;

	private long curLife;// 当前生命值

	private int curEnergy;//剩余的能量

	private long maxLife;// 全部的血量
	
	private int maxEnergy;// 全部的能量

	public String getId() {
		return id;
	}

	public long getCurLife() {
		return curLife;
	}

	public void setCurLife(long curLife) {
		this.curLife = curLife;
	}

	public int getCurEnergy() {
		return curEnergy;
	}

	public void setCurEnergy(int curEnergy) {
		this.curEnergy = curEnergy;
	}

	public long getMaxLife() {
		return maxLife;
	}

	public void setMaxLife(long maxLife) {
		this.maxLife = maxLife;
	}

	public int getMaxEnergy() {
		return maxEnergy;
	}

	public void setMaxEnergy(int maxEnergy) {
		this.maxEnergy = maxEnergy;
	}

	public void setId(String id) {
		this.id = id;
	}



	
}