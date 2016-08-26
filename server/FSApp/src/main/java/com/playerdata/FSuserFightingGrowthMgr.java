package com.playerdata;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.fightgrowth.FSUserFightingGrowthData;
import com.playerdata.fightgrowth.FSUserFightingGrowthSynData;
import com.playerdata.fightgrowth.FSUserFightingGrowthWaySynData;
import com.rwbase.dao.fightgrowth.FSUserFightingGrowthDataDAO;
import com.rwbase.dao.fightgrowth.FSUserFightingGrowthTitleCfgDAO;
import com.rwbase.dao.fightgrowth.FSUserFightingGrowthWayInfoCfgDAO;
import com.rwbase.dao.fightgrowth.pojo.FSUserFightingGrowthTitleCfg;

public class FSuserFightingGrowthMgr {

	private static final FSuserFightingGrowthMgr _instance = new FSuserFightingGrowthMgr();
	
	protected FSuserFightingGrowthMgr() {}
	
	public static FSuserFightingGrowthMgr getInstance() {
		return _instance;
	}
	
	private List<FSUserFightingGrowthWaySynData> getFightingGrowthWaySynData(Player player) {
		List<FSUserFightingGrowthWaySynData> list = new ArrayList<FSUserFightingGrowthWaySynData>();
		return list;
	}
	
	public FSUserFightingGrowthSynData getFightingGrowthSynData(Player player) {
		FSUserFightingGrowthData userFightingGrowthData = FSUserFightingGrowthDataDAO.getInstance().get(player.getUserId());
		FSUserFightingGrowthWayInfoCfgDAO.getInstance();
		FSUserFightingGrowthTitleCfg titleCfg = null;
		boolean hasTitle = true;
		if(userFightingGrowthData.getCurrentTitleKey() == null || userFightingGrowthData.getCurrentTitleKey().isEmpty()) {
			titleCfg = FSUserFightingGrowthTitleCfgDAO.getInstance().getCfgById(userFightingGrowthData.getCurrentTitleKey());
			hasTitle = false;
		} else {
			titleCfg = FSUserFightingGrowthTitleCfgDAO.getInstance().getFirstTitleCfg();
		}
		FSUserFightingGrowthSynData synData = new FSUserFightingGrowthSynData();
		synData.userId = player.getUserId();
		synData.fightingRequired = titleCfg.getFightingRequired();
		synData.itemsRequired = titleCfg.getItemRequiredMap();
		if(hasTitle) {
			synData.currentTitle = titleCfg.getFightingTitle();
			synData.titleIcon = titleCfg.getFightingIcon();
		}
		return synData;
	}
}
