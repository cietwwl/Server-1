package com.playerdata.fightinggrowth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.util.StringUtils;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwbase.dao.fightinggrowth.FSUserFightingGrowthDataDAO;
import com.rwbase.dao.fightinggrowth.FSUserFightingGrowthTitleCfgDAO;
import com.rwbase.dao.fightinggrowth.FSUserFightingGrowthWayInfoCfgDAO;
import com.rwbase.dao.fightinggrowth.pojo.FSUserFightingGrowthTitleCfg;
import com.rwbase.dao.fightinggrowth.pojo.FSUserFightingGrowthWayInfoCfg;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class FSUserFightingGrowthHolder {
	
	private static FSUserFightingGrowthHolder _instance = new FSUserFightingGrowthHolder();
	
	public static FSUserFightingGrowthHolder getInstance() {
		return _instance;
	}
	
	private eOpenLevelType getOpenLevelType(int type) {
		eOpenLevelType[] all = eOpenLevelType.values();
		for (int i = 0; i < all.length; i++) {
			eOpenLevelType temp = all[i];
			if (temp.getOrder() == type) {
				return temp;
			}
		}
		return null;
	}
	
	private List<FSUserFightingGrowthWaySynData> getFightingGrowthWaySynData(Player player) {
		List<String> wayKeys = FSUserFightingGrowthWayInfoCfgDAO.getInstance().getDisplaySeqRO();
		List<FSUserFightingGrowthWaySynData> list = new ArrayList<FSUserFightingGrowthWaySynData>(wayKeys.size());
		FSUserFightingGrowthWayInfoCfg cfg;
		FSUserFightingGrowthWaySynData synData;
		FSFightingGrowthWayType type;
		for(int i = 0; i < wayKeys.size(); i++) {
			cfg = FSUserFightingGrowthWayInfoCfgDAO.getInstance().getCfgById(wayKeys.get(i));
			if (cfg.getFightingOriginFuncId() > 0) {
				// 检查是否开放
				eOpenLevelType openLevelType = this.getOpenLevelType(cfg.getFightingOriginFuncId());
				if (!CfgOpenLevelLimitDAO.getInstance().isOpen(openLevelType, player.getLevel())) {
					continue;
				}
			}
			type = FSFightingGrowthWayType.getBySign(cfg.getTypeForServer());
			synData = new FSUserFightingGrowthWaySynData();
			synData.key = cfg.getKey(); // key
			synData.name = cfg.getFightingOrigin(); // 名字
			synData.gotoType = cfg.getGotoType(); // 打开界面
			synData.gainWays = cfg.getGrowthWayList(); // 获取途径
			synData.currentValue = type.getGetCurrentFightingFunc().apply(player); // 当前的战斗力
			synData.maxValue = type.getGetMaxFightingFunc().apply(player); // 当前等级的最大值
			System.out.println("type=" + type + ", currentValue=" + synData.currentValue + ", maxValue=" + synData.maxValue);
			list.add(synData);
		}
		return list;
	}
	
	private FSUserFightingGrowthSynData genSynData(Player player, FSUserFightingGrowthData userFightingGrowthData, FSUserFightingGrowthTitleCfg currentTitleCfg, FSUserFightingGrowthTitleCfg nextTitleCfg) {
		FSUserFightingGrowthSynData synData = new FSUserFightingGrowthSynData();
		synData.userId = userFightingGrowthData.getUserId();
		if (nextTitleCfg != null) {
			// 有下一级的称号
			synData.fightingRequired = nextTitleCfg.getFightingRequired(); // 当前称号所需要达到的战斗力
			synData.itemsRequired = nextTitleCfg.getItemRequiredMap(); // 提升称号所需要的材料
		} else {
			// 没有下一级的称号
			synData.hasNextTitle = false;
			synData.fightingRequired = 0;
			synData.itemsRequired = Collections.emptyMap();
		}
		if (StringUtils.isEmpty(userFightingGrowthData.getCurrentTitleKey())) {
			// 当前还没有达成任何的称号
			synData.currentTitle = "";
			synData.titleIcon = "";
		} else {
			synData.currentTitle = currentTitleCfg.getFightingTitle();
			synData.titleIcon = currentTitleCfg.getFightingIcon();
		}
		synData.growthWayInfos = this.getFightingGrowthWaySynData(player); // 战斗力提升途径
		return synData;
	}
	
	/**
	 * 
	 * 创建一个战力提升的前后端同步数据
	 * 
	 * @param player
	 * @return
	 */
	public FSUserFightingGrowthSynData createFightingGrowthSynData(Player player) {
		FSUserFightingGrowthData userFightingGrowthData = this.getUserFightingGrowthData(player);
		// 当前的称号
		FSUserFightingGrowthTitleCfg titleCfg = FSUserFightingGrowthTitleCfgDAO.getInstance().getFightingGrowthTitleCfgSafely(userFightingGrowthData.getCurrentTitleKey());
		// 下一级称号
		FSUserFightingGrowthTitleCfg nextTitleCfg = FSUserFightingGrowthTitleCfgDAO.getInstance().getNextFightingGrowthTitleCfgSafely(userFightingGrowthData.getCurrentTitleKey());
		return this.genSynData(player, userFightingGrowthData, titleCfg, nextTitleCfg);
	}
	
	/**
	 * 
	 * 获取玩家战力成长数据
	 * 
	 * @param player
	 * @return
	 */
	public FSUserFightingGrowthData getUserFightingGrowthData(Player player) {
		return FSUserFightingGrowthDataDAO.getInstance().get(player.getUserId());
	}
	
	/**
	 * 
	 * 更新数据到db
	 * 
	 * @param player
	 */
	public void updateToDB(Player player) {
		FSUserFightingGrowthDataDAO.getInstance().update(this.getUserFightingGrowthData(player));
	}

	/**
	 * 
	 * 同步数据
	 * 
	 * @param player
	 */
	public void synData(Player player) {
		FSUserFightingGrowthSynData synData = this.createFightingGrowthSynData(player);
		ClientDataSynMgr.synData(player, synData, eSynType.FIGHTING_GROWTH_DATA, eSynOpType.UPDATE_SINGLE);
	}
}
