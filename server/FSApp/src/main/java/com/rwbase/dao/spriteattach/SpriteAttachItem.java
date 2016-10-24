package com.rwbase.dao.spriteattach;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class SpriteAttachItem {
	private int id;
	private int level; //附灵等级
	private long exp;   //附灵经验
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public long getExp() {
		return exp;
	}
	public void setExp(long exp) {
		this.exp = exp;
	}
	
}
