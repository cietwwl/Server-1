package com.playerdata.fightgrowth;

import java.util.List;
import java.util.Map;

import javax.persistence.Id;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class FSUserFightingGrowthSynData {

	@Id
	public String userId;
	public int fightingRequired;
	public String currentTitle;
	public String titleIcon;
	public Map<String, Integer> itemsRequired;
	public List<FSUserFightingGrowthWaySynData> growthWayInfos;
}
