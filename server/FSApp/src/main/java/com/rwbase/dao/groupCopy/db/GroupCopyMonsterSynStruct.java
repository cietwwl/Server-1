package com.rwbase.dao.groupCopy.db;

import com.common.GameUtil;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.battleVerify.MonsterCfg;
import com.playerdata.dataSyn.annotation.SynClass;


/**
 * 帮派体系副本前后端同步怪物结构
 * @author Alex
 * 2016年6月22日 下午5:18:08
 */
@SynClass
public class GroupCopyMonsterSynStruct {
	
	private String id;
	private long totalHP;
	private long curHP;
	private long totalMP;
	private long curMP;
	
	
	
	public GroupCopyMonsterSynStruct() {
		
	}
	public GroupCopyMonsterSynStruct(MonsterCfg monsterCfg) {
		id = monsterCfg.getId();
		totalHP = monsterCfg.getLife();
		curHP = totalHP;
		totalMP = monsterCfg.getEnergy();
		curMP = totalMP;
		if(totalHP == 0){
			GameLog.error(LogModule.GroupCopy, "GroupCopyMonsterSynStruct", "创建怪物时发现怪物["+id+"]的总血量为0", null);
		}
	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getTotalHP() {
		return totalHP;
	}
	public void setTotalHP(long totalHP) {
		this.totalHP = totalHP;
	}
	public long getCurHP() {
		return curHP;
	}
	public void setCurHP(long curHP) {
		this.curHP = curHP;
	}
	public long getTotalMP() {
		return totalMP;
	}
	public void setTotalMP(long totalMP) {
		this.totalMP = totalMP;
	}
	public long getCurMP() {
		return curMP;
	}
	public void setCurMP(long curMP) {
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
