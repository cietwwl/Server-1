package com.playerdata.fightgrowth;

import java.util.List;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class FSUserFightingGrowthWaySynData {

	public String key;
	public String name;
	public int currentValue;
	public int maxValue;
	public List<Integer> gainWays;
	public int gotoType;
}
