package com.playerdata.army;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurAttrData {

	private String id;

	private int curLife;// 当前生命值

	private int curEnergy;//剩余的能量

	private int maxLife;// 全部的血量
	
	private int maxEnergy;// 全部的能量

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

	public int getMaxLife() {
		return maxLife;
	}

	public void setMaxLife(int maxLife) {
		this.maxLife = maxLife;
	}

	public int getMaxEnergy() {
		return maxEnergy;
	}

	public void setMaxEnergy(int maxEnergy) {
		this.maxEnergy = maxEnergy;
	}
	
	
}