package com.playerdata.fightinggrowth;

import java.util.List;
import java.util.Map;

import javax.persistence.Id;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class FSUserFightingGrowthSynData {

	@Id
	public String userId;
	// 当前的称号
	public String currentTitle;
	// 当前的称号图标
	public String titleIcon;
	// 晋级到下一级的战斗力需求
	public int fightingRequired;
	// 是否有下一级
	public boolean hasNextTitle = true;
	// 晋级到下一级需要的道具
	public Map<Integer, Integer> itemsRequired;
	// 战力提升途径
	public List<FSUserFightingGrowthWaySynData> growthWayInfos;
}
