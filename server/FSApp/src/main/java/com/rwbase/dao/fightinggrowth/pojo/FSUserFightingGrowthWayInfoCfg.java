package com.rwbase.dao.fightinggrowth.pojo;

import java.util.Collections;
import java.util.List;

public class FSUserFightingGrowthWayInfoCfg implements Comparable<FSUserFightingGrowthWayInfoCfg>{

	private String key;
	private int seq;
	private int typeForServer;
	private String fightingOrigin;
	private int fightingOriginFuncId;
	private String growthWay;
	private int gotoType;
	private List<Integer> _growthWayList;
	
	public void setGrowthWayList(List<Integer> pGrowthWayList) {
		this._growthWayList = Collections.unmodifiableList(pGrowthWayList);
	}
	
	public String getKey() {
		return key;
	}
	
	public int getSeq() {
		return seq;
	}
	
	public int getTypeForServer() {
		return typeForServer;
	}
	
	public String getFightingOrigin() {
		return fightingOrigin;
	}
	
	public int getFightingOriginFuncId() {
		return fightingOriginFuncId;
	}
	
	public String getGrowthWay() {
		return growthWay;
	}
	
	public int getGotoType() {
		return gotoType;
	}
	
	public List<Integer> getGrowthWayList() {
		return _growthWayList;
	}

	@Override
	public int compareTo(FSUserFightingGrowthWayInfoCfg o) {
		return this.seq < o.seq ? -1 : 1;
	}
}
