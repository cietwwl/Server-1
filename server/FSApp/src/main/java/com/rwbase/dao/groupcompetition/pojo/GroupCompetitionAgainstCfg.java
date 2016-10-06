package com.rwbase.dao.groupcompetition.pojo;

import java.util.ArrayList;
import java.util.List;

import com.rw.fsutil.common.IReadOnlyPair;

public class GroupCompetitionAgainstCfg {

	private int startType; // 开始类型
	private String againstInfo; // 对阵信息的原生字符串
	private List<IReadOnlyPair<Integer, Integer>> againstInfoList;
	
	public int getStartType() {
		return startType;
	}
	
	public String getAgainstInfo() {
		return againstInfo;
	}
	
	public void setAgainstInfoList(List<? extends IReadOnlyPair<Integer, Integer>> list) {
		againstInfoList = new ArrayList<IReadOnlyPair<Integer,Integer>>(list);
	}
	
	public List<IReadOnlyPair<Integer, Integer>> getAgainstInfoList() {
		return againstInfoList;
	}
	
}
