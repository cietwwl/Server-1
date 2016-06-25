package com.groupCopy.rwbase.dao.groupCopy.db;

import com.monster.cfg.CopyMonsterCfg;
import com.playerdata.dataSyn.annotation.SynClass;


/**
 * 帮派体系副本前后端同步怪物结构
 * @author Alex
 * 2016年6月22日 下午5:18:08
 */
@SynClass
public class GroupCopyMonsterSynStruct {
	
	private String id;
	private int totalHP;
	private int curHP;
	private int totalMP;
	private int curMP;
	
	
	
	public GroupCopyMonsterSynStruct() {
		
	}
	public GroupCopyMonsterSynStruct(CopyMonsterCfg monsterCfg) {
		id = monsterCfg.getId();
		totalHP = monsterCfg.getHpCount() * monsterCfg.getLife();
		curHP = totalHP;
		totalMP = monsterCfg.getEnergy();
		curMP = totalMP;
	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getTotalHP() {
		return totalHP;
	}
	public void setTotalHP(int totalHP) {
		this.totalHP = totalHP;
	}
	public int getCurHP() {
		return curHP;
	}
	public void setCurHP(int curHP) {
		this.curHP = curHP;
	}
	public int getTotalMP() {
		return totalMP;
	}
	public void setTotalMP(int totalMP) {
		this.totalMP = totalMP;
	}
	public int getCurMP() {
		return curMP;
	}
	public void setCurMP(int curMP) {
		this.curMP = curMP;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		GroupCopyMonsterSynStruct struct = new GroupCopyMonsterSynStruct();
		struct.id = this.id;
		struct.totalHP = this.totalHP;
		struct.curHP = this.curHP;
		struct.totalMP = this.totalMP;
		struct.curMP = this.curMP;
		return struct;
	}

}
