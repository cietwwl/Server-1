package com.playerdata.fightinggrowth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.playerdata.Player;
import com.rwbase.dao.fightinggrowth.FSUserFightingGrowthDataDAO;
import com.rwbase.dao.fightinggrowth.FSUserFightingGrowthTitleCfgDAO;
import com.rwbase.dao.fightinggrowth.FSUserFightingGrowthWayInfoCfgDAO;
import com.rwbase.dao.fightinggrowth.pojo.FSUserFightingGrowthTitleCfg;
import com.rwbase.dao.fightinggrowth.pojo.FSUserFightingGrowthWayInfoCfg;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwproto.FightGrowthProto.UpgradeItemRequired;
import com.rwproto.FightGrowthProto.UserFightingGrowthSynData;
import com.rwproto.FightGrowthProto.UserFightingGrowthWaySynData;
import com.rwproto.MsgDef.Command;

public class FSUserFightingGrowthHolder {
	
	private static FSUserFightingGrowthHolder _instance = new FSUserFightingGrowthHolder();
	
	public static FSUserFightingGrowthHolder getInstance() {
		return _instance;
	}
	
	private FSUserFightingGrowthWayInfoCfgDAO _userFightingGrowthWayInfoCfgDAO;
	private CfgOpenLevelLimitDAO _cfgOpenLevelLimitDAO;
	private FSUserFightingGrowthTitleCfgDAO _userFightingGrowthTitleCfgDAO;
	
	protected FSUserFightingGrowthHolder() {
		_userFightingGrowthWayInfoCfgDAO = FSUserFightingGrowthWayInfoCfgDAO.getInstance();
		_cfgOpenLevelLimitDAO = CfgOpenLevelLimitDAO.getInstance();
		_userFightingGrowthTitleCfgDAO = FSUserFightingGrowthTitleCfgDAO.getInstance();
	}
	
	private List<UserFightingGrowthWaySynData> getFightingGrowthWaySynData(Player player) {
		List<String> wayKeys = _userFightingGrowthWayInfoCfgDAO.getDisplaySeqRO();
		List<UserFightingGrowthWaySynData> list = new ArrayList<UserFightingGrowthWaySynData>(wayKeys.size());
		FSUserFightingGrowthWayInfoCfg cfg;
		FSFightingGrowthWayType type;
		UserFightingGrowthWaySynData.Builder builder;
		for(int i = 0; i < wayKeys.size(); i++) {
			cfg = _userFightingGrowthWayInfoCfgDAO.getCfgById(wayKeys.get(i));
			if (cfg.getFightingOriginFuncId() > 0) {
				// 检查是否开放
				eOpenLevelType openLevelType = eOpenLevelType.getByOrder(cfg.getFightingOriginFuncId());
				if (!_cfgOpenLevelLimitDAO.isOpen(openLevelType, player)) {
					continue;
				}
			}
			type = FSFightingGrowthWayType.getBySign(cfg.getTypeForServer());
			if (type.isPlayerCanUse(player)) {
				builder = UserFightingGrowthWaySynData.newBuilder();
				builder.setKey(cfg.getKey()); // key
				builder.setName(cfg.getFightingOrigin()); // 名字
				builder.setGotoType(cfg.getGotoType()); // 打开界面
				builder.addAllGainWay(cfg.getGrowthWayList()); // 获取途径
				builder.setCurrentFighting(type.getGetCurrentFightingFunc().apply(player)); // 当前的战斗力
				builder.setMaxFighting(type.getGetMaxFightingFunc().apply(player)); // 当前等级的最大值
				System.out.println("type=" + type + ", currentValue=" + builder.getCurrentFighting() + ", maxValue=" + builder.getMaxFighting());
				list.add(builder.build());
			}
		}
		return list;
	}
	
	private UserFightingGrowthSynData genSynData(Player player, FSUserFightingGrowthData userFightingGrowthData, FSUserFightingGrowthTitleCfg currentTitleCfg, FSUserFightingGrowthTitleCfg nextTitleCfg) {
		int fightingRequired;
		Map<Integer, Integer> itemsRequired;
		boolean hasNextTitle = true;
		String currentTitle;
		String titleIcon;
		if (nextTitleCfg != null) {
			// 有下一级的称号
			fightingRequired = nextTitleCfg.getFightingRequired(); // 当前称号所需要达到的战斗力
			itemsRequired = nextTitleCfg.getItemRequiredMap(); // 提升称号所需要的材料
		} else {
			// 没有下一级的称号
			hasNextTitle = false;
			fightingRequired = 0;
			itemsRequired = Collections.emptyMap();
		}
		if (StringUtils.isEmpty(userFightingGrowthData.getCurrentTitleKey())) {
			// 当前还没有达成任何的称号
			currentTitle = "";
			titleIcon = "";
		} else {
			currentTitle = currentTitleCfg.getFightingTitle();
			titleIcon = currentTitleCfg.getFightingIcon();
		}
		List<UserFightingGrowthWaySynData> wayInfoList = this.getFightingGrowthWaySynData(player); // 战斗力提升途径
		UserFightingGrowthSynData.Builder dataBuilder = UserFightingGrowthSynData.newBuilder();
		dataBuilder.setUserId(userFightingGrowthData.getUserId());
		dataBuilder.setCurrentTitle(currentTitle);
		dataBuilder.setTitleIcon(titleIcon);
		dataBuilder.setHasNextTitle(hasNextTitle);
		dataBuilder.setFightingRequired(fightingRequired);
		if (itemsRequired.size() > 0) {
			for(Iterator<Integer> keyItr = itemsRequired.keySet().iterator(); keyItr.hasNext();) {
				Integer itemCfgId = keyItr.next();
				Integer count = itemsRequired.get(itemCfgId);
				dataBuilder.addUpgradeItemRequired(UpgradeItemRequired.newBuilder().setItemCfgId(itemCfgId).setItemCount(count));
			}
		}
		dataBuilder.addAllGrowthWayData(wayInfoList);
		dataBuilder.setCurrentUpKey(currentTitleCfg.getKey());
		return dataBuilder.build();
	}
	
//	/**
//	 * 
//	 * 创建一个战力提升的前后端同步数据
//	 * 
//	 * @param player
//	 * @return
//	 */
//	public FSUserFightingGrowthSynData createFightingGrowthSynData(Player player) {
//		FSUserFightingGrowthData userFightingGrowthData = this.getUserFightingGrowthData(player);
//		// 当前的称号
//		FSUserFightingGrowthTitleCfg titleCfg = FSUserFightingGrowthTitleCfgDAO.getInstance().getFightingGrowthTitleCfgSafely(userFightingGrowthData.getCurrentTitleKey());
//		// 下一级称号
//		FSUserFightingGrowthTitleCfg nextTitleCfg = FSUserFightingGrowthTitleCfgDAO.getInstance().getNextFightingGrowthTitleCfgSafely(userFightingGrowthData.getCurrentTitleKey());
//		return this.genSynData(player, userFightingGrowthData, titleCfg, nextTitleCfg);
//	}
	
	/**
	 * 
	 * 创建一个战力提升的前后端同步数据
	 * 
	 * @param player
	 * @return
	 */
	public UserFightingGrowthSynData createFightingGrowthSynData(Player player) {
		FSUserFightingGrowthData userFightingGrowthData = this.getUserFightingGrowthData(player);
		// 当前的称号
		FSUserFightingGrowthTitleCfg titleCfg = _userFightingGrowthTitleCfgDAO.getFightingGrowthTitleCfgSafely(userFightingGrowthData.getCurrentTitleKey());
		// 下一级称号
		FSUserFightingGrowthTitleCfg nextTitleCfg = _userFightingGrowthTitleCfgDAO.getNextFightingGrowthTitleCfgSafely(userFightingGrowthData.getCurrentTitleKey());
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
		UserFightingGrowthSynData synData = this.createFightingGrowthSynData(player);
//		ClientDataSynMgr.synData(player, synData, eSynType.FIGHTING_GROWTH_DATA, eSynOpType.UPDATE_SINGLE);
		player.SendMsg(Command.MSG_FIGHTING_PUSH_DATA, synData.toByteString());
	}
}
