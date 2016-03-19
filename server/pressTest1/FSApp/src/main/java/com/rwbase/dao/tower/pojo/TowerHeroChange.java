package com.rwbase.dao.tower.pojo;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.dao.annotation.SaveAsJson;
import com.rwproto.TowerServiceProtos.eTowerDeadType;
@SynClass
public class TowerHeroChange implements TowerHeroChangeIF{//记录玩家血量变化
	private String roleId;//主角UUid即人物userId 或佣兵 uuid
	@SaveAsJson
	private eTowerDeadType isDead;//是否死亡
	private int  reduceLife;//当前血量
	private int reduceEnegy;//当前能量
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
	public eTowerDeadType getIsDead() {
		return isDead;
	}
	public void setIsDead(eTowerDeadType isDead) {
		this.isDead = isDead;
	}

}
