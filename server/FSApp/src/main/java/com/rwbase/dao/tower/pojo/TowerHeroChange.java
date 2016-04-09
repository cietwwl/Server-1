package com.rwbase.dao.tower.pojo;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rwproto.TowerServiceProtos.eTowerDeadType;

@SynClass
public class TowerHeroChange implements TowerHeroChangeIF {// 记录玩家血量变化
	private String roleId;// roleId
	private int heroState;// 英雄的状态
	private int reduceLife;// 剩余生命值
	private int reduceEnegy;// 剩余能量值

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public int getReduceLife() {
		return reduceLife;
	}

	public void setReduceLife(int reduceLife) {
		this.reduceLife = reduceLife;
	}

	public int getReduceEnegy() {
		return reduceEnegy;
	}

	public void setReduceEnegy(int reduceEnegy) {
		this.reduceEnegy = reduceEnegy;
	}

	public int getHeroState() {
		return heroState;
	}

	public void setHeroState(int heroState) {
		this.heroState = heroState;
	}

	@JsonIgnore
	public eTowerDeadType getIsDead() {
		if (reduceLife <= 0) {
			return eTowerDeadType.TOWER_DEAD;
		}

		return eTowerDeadType.TOWER_LIVING;
	}
}