package com.rwbase.dao.arena.pojo;

import com.rwproto.BattleCommon.ePlayerCamp;
import com.rwproto.BattleCommon.ePlayerType;

public class HurtValueRecord {
	private String heroId;
	private float value; // 伤害值
	private String icon; // 图标
	private int startlevel; // 星级
	private int level; // 等级
	private boolean isDead; // 是否死亡
	private ePlayerType playerType; // 角色类型
	private ePlayerCamp camp; // 阵营
	private float Hp; // 剩余血量
	private float Sp; // 剩余蓝量

	public String getHeroId() {
		return heroId;
	}

	public void setHeroId(String heroId) {
		this.heroId = heroId;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getStartlevel() {
		return startlevel;
	}

	public void setStartlevel(int startlevel) {
		this.startlevel = startlevel;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean isDead() {
		return isDead;
	}

	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}

	public ePlayerType getPlayerType() {
		return playerType;
	}

	public void setPlayerType(ePlayerType playerType) {
		this.playerType = playerType;
	}

	public ePlayerCamp getCamp() {
		return camp;
	}

	public void setCamp(ePlayerCamp camp) {
		this.camp = camp;
	}

	public float getHp() {
		return Hp;
	}

	public void setHp(float hp) {
		Hp = hp;
	}

	public float getSp() {
		return Sp;
	}

	public void setSp(float sp) {
		Sp = sp;
	}

}
