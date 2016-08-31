package com.playerdata.groupcompetition.fightRecord;

import com.playerdata.dataSyn.annotation.SynClass;

/**
 * 战斗结果记录的结构
 * @author aken
 */
@SynClass
public class GCFightRecord implements Comparable<GCFightRecord>{
	
	private GCUserSimpleInfo offend;	//进攻方信息
	
	private String defenderName;	//防守方信息
	
	private int state;	//-1:进攻失败,1:进攻胜利,0:平局
	
	private long createTime;	//创建时间
	
	public GCUserSimpleInfo getOffend() {
		return offend;
	}
	
	public void setOffend(GCUserSimpleInfo offend) {
		this.offend = offend;
	}
	
	public String getDefenderName() {
		return defenderName;
	}
	
	public void setDefenderName(String defenderName) {
		this.defenderName = defenderName;
	}
	
	public int getState() {
		return state;
	}
	
	public void setState(int state) {
		this.state = state;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	@Override
	public int compareTo(GCFightRecord o) {
		if(createTime < o.createTime)
			return -1;
		if(createTime > o.createTime)
			return 1;
		return 0;
	}
}
