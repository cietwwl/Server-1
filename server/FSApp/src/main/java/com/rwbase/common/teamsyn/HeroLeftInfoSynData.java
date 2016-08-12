package com.rwbase.common.teamsyn;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;

/*
 * @author HC
 * @date 2016年5月30日 下午12:16:15
 * @Description 英雄剩余血量能量的信息
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@SynClass
public class HeroLeftInfoSynData {
	private int life;// 剩余的生命
	private int energy;// 剩余的能量
	private int maxLife;// 全部的血量
	private int maxEnergy;// 全部的能量

	public HeroLeftInfoSynData() {
	}

	public HeroLeftInfoSynData(int life, int energy, int maxLife, int maxEnergy) {
		this.life = life;
		this.energy = energy;
		this.maxLife = maxLife;
		this.maxEnergy = maxEnergy;
	}

	public int getLife() {
		return life;
	}

	public int getEnergy() {
		return energy;
	}

	public int getMaxLife() {
		return maxLife;
	}

	public int getMaxEnergy() {
		return maxEnergy;
	}
}