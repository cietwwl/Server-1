package com.playerdata.groupFightOnline.dataExtend;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class GFFightRecord implements Comparable<GFFightRecord>{
	
	private GFUserSimpleInfo offend;	//进攻方信息
	
	private GFUserSimpleInfo defend;	//防守方信息
	
	private int state;	//0-进攻失败,1-进攻胜利
	
	private long createTime;	//创建时间
	
	public GFUserSimpleInfo getOffend() {
		return offend;
	}
	
	public void setOffend(GFUserSimpleInfo offend) {
		this.offend = offend;
	}
	
	public GFUserSimpleInfo getDefend() {
		return defend;
	}
	
	public void setDefend(GFUserSimpleInfo defend) {
		this.defend = defend;
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
	public int compareTo(GFFightRecord o) {
		if(createTime < o.createTime)
			return -1;
		if(createTime > o.createTime)
			return 1;
		return 0;
	}
}
