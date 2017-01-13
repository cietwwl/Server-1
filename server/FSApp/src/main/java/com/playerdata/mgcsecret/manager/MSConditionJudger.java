package com.playerdata.mgcsecret.manager;

import java.util.List;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.mgcsecret.cfg.BuffBonusCfg;
import com.playerdata.mgcsecret.cfg.BuffBonusCfgDAO;
import com.playerdata.mgcsecret.cfg.DungeonsDataCfg;
import com.playerdata.mgcsecret.cfg.DungeonsDataCfgDAO;
import com.playerdata.mgcsecret.cfg.MagicChapterCfg;
import com.playerdata.mgcsecret.cfg.MagicChapterCfgDAO;
import com.playerdata.mgcsecret.data.MSDungeonInfo;
import com.playerdata.mgcsecret.data.MagicChapterInfo;
import com.playerdata.mgcsecret.data.MagicChapterInfoHolder;
import com.playerdata.mgcsecret.data.UserMagicSecretData;
import com.playerdata.mgcsecret.data.UserMagicSecretHolder;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwproto.MagicSecretProto.MSItemInfo;

class MSConditionJudger {

	/**
	 * 判断玩家等级
	 * 
	 * @param player
	 * @param dungeonID 副本ID
	 * @return
	 */
	public static boolean judgeUserLevel(Player player, String dungeonID) {
		DungeonsDataCfg dungDataCfg = DungeonsDataCfgDAO.getInstance().getCfgById(dungeonID);
		return judgeUserLevel(player, dungDataCfg.getChapterID());
	}

	/**
	 * 判断玩家等级
	 * 
	 * @param player
	 * @param chapterID 章节ID
	 * @return
	 */
	public static boolean judgeUserLevel(Player player, int chapterID) {
		MagicChapterCfg mcCfg = MagicChapterCfgDAO.getInstance().getCfgById(String.valueOf(chapterID));
		int userLevel = player.getLevel();
		if (userLevel < mcCfg.getLevelLimit()) {
			// GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("judgeUserLevel, 角色等级[%s]没有达到章节[%s]要求[%s]", userLevel, chapterID, mcCfg.getLevelLimit()), null);
			return false;
		}
		return true;
	}

	/**
	 * 判断进入副本的条件
	 * 
	 * @param player
	 * @param dungeonID
	 * @return
	 */
	public static boolean judgeDungeonsCondition(Player player, String dungeonID) {
		UserMagicSecretData msData = UserMagicSecretHolder.getInstance().get(player);
		if (msData == null) {
			GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("judgeDungeonsCondition, UserMagicSecretData[%s]不存在", player.getUserId()), null);
			return false;
		}
		int maxStageID = msData.getMaxStageID() == 0 ? 101 : msData.getMaxStageID();
		int reqStageID = fromDungeonIDToStageID(player, dungeonID);
		if (reqStageID > getNextStage(maxStageID) || reqStageID < 0) {
			GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("judgeDungeonsCondition, reqStageID[%s]超过了最高纪录[%s]", dungeonID, maxStageID), null);
			return false;
		}
		return true;
	}

	/**
	 * 判断副本的合法性
	 * 
	 * @param player
	 * @param dungeonID
	 * @return
	 */
	public static boolean judgeDungeonsLegal(Player player, String dungeonID) {
		DungeonsDataCfg dungDataCfg = DungeonsDataCfgDAO.getInstance().getCfgById(dungeonID);
		if (dungDataCfg == null) {
			GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("judgeDungeonsLegal, 一个非法的dungeonID[%s], 静态数据中不包含", dungeonID), null);
			return false;
		}
		MagicChapterInfo mcInfo = MagicChapterInfoHolder.getInstance().getItem(player.getUserId(), String.valueOf(dungDataCfg.getChapterID()));
		if (mcInfo == null) {
			GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("judgeDungeonsLegal, 不合法的章节[%s], 有可能是没开启或不存在", dungDataCfg.getChapterID()), null);
			return false;
		}
		if (mcInfo.getSelectedDungeonIndex() >= 0) {
			// 表示曾经打过这个关卡
			MSDungeonInfo dungeon = mcInfo.getSelectableDungeons().get(mcInfo.getSelectedDungeonIndex());
			if (dungeon != null && dungeon.getDungeonKey().equalsIgnoreCase(dungeonID))
				return true;
			else {
				GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("judgeDungeonsLegal, 曾选择了打难度[%s], 现在不能打不同的难度副本[%s]", mcInfo.getSelectedDungeonIndex(), dungeonID), null);
				return false;
			}
		}
		for (MSDungeonInfo dungeon : mcInfo.getSelectableDungeons()) {
			if (dungeon.getDungeonKey().equalsIgnoreCase(dungeonID))
				return true;
		}
		GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("judgeDungeonsLegal, 该副本[%s]目前不可选择", dungeonID), null);
		return false;
	}

	/**
	 * 判断副本的次数（主要是看今天有没有通关过）
	 * 
	 * @param player
	 * @param dungeonID
	 * @return
	 */
	public static boolean judgeDungeonsCount(Player player, String dungeonID) {
		int stageID = fromDungeonIDToStageID(player, dungeonID);
		String chapterID = String.valueOf(fromStageIDToChapterID(stageID));
		MagicChapterInfo mcInfo = MagicChapterInfoHolder.getInstance().getItem(player.getUserId(), chapterID);
		if (mcInfo == null) {
			GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("judgeDungeonsCount, 不合法的章节[%s], dungeonID[%s]", chapterID, dungeonID), null);
			return false;
		}
		if (mcInfo.getFinishedStages().contains(stageID)) {
			GameLog.info(LogModule.MagicSecret.toString(), player.getUserId(), String.format("judgeDungeonsCount, stage[%s]今日已经通关，次数不够, 准备打dungeonID[%s]", stageID, dungeonID), null);
			return false;
		}
		return true;
	}

	/**
	 * 判断buff的合法性
	 * 
	 * @param player
	 * @param chapterID
	 * @param buffID
	 * @return
	 */
	public static boolean judgeBuffLegal(Player player, String chapterID, String buffID) {
		BuffBonusCfg buffCfg = BuffBonusCfgDAO.getInstance().getCfgById(buffID);
		if (buffCfg == null) {
			GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("judgeBuffLegal, buff[%s]在配置表中不存在", buffID), null);
			return false;
		}
		MagicChapterInfo mcInfo = MagicChapterInfoHolder.getInstance().getItem(player.getUserId(), chapterID);
		if (mcInfo == null) {
			GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("judgeBuffLegal, 不合法的章节[%s], 有可能是没开启或不存在", chapterID), null);
			return false;
		}
		List<Integer> unselect = mcInfo.getUnselectedBuff();
		if (unselect == null || unselect.isEmpty()) {
			GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("judgeBuffLegal, 章节[%s]中没有可选的buff", chapterID), null);
			return false;
		}
		for (int bID : unselect) {
			if (bID == Integer.parseInt(buffID))
				return true;
		}
		GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("judgeBuffLegal, 章节[%s]的可选buff中没有找到目标buff[%s]", chapterID, buffID), null);
		return false;
	}

	/**
	 * 判断星星数量
	 * 
	 * @param player
	 * @param chapterID
	 * @param buffID
	 * @return
	 */
	public static boolean judgeEnoughStar(Player player, String chapterID, String buffID) {
		BuffBonusCfg buffCfg = BuffBonusCfgDAO.getInstance().getCfgById(buffID);
		MagicChapterInfo mcInfo = MagicChapterInfoHolder.getInstance().getItem(player.getUserId(), chapterID);
		int starCount = mcInfo.getStarCount();
		if (starCount < buffCfg.getCost()) {
			GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("judgeBuffLegal, 章节[%s]中星星数[%s]不够换取buff[%s]", chapterID, starCount, buffID), null);
			return false;
		}
		return true;
	}

	/**
	 * 判断箱子数量
	 * 
	 * @param player
	 * @param chapterID
	 * @param msRwdBox
	 * @return
	 */
	public static boolean judgeRewardBoxLegal(Player player, String chapterID, MSItemInfo msRwdBox) {
		MagicChapterInfo mcInfo = MagicChapterInfoHolder.getInstance().getItem(player.getUserId(), chapterID);
		if (mcInfo == null) {
			GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("judgeRewardBoxLegal, 不合法的章节[%s], 有可能是没开启或不存在", chapterID), null);
			return false;
		}
		for (ItemInfo itm : mcInfo.getCanOpenBoxes()) {
			if (itm.getItemID() == Integer.parseInt(msRwdBox.getItemID()) && itm.getItemNum() >= msRwdBox.getItemCount())
				return true;
		}
		GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("judgeRewardBoxLegal, 章节[%s]中需要打开的箱子[%s]数量[%s]不足", chapterID, msRwdBox.getItemID(), msRwdBox.getItemCount()), null);
		return false;
	}

	/**
	 * 判断打开箱子的花费(如果够，直接扣除)
	 * 
	 * @param player
	 * @param chapterID
	 * @param msRwdBox
	 * @return
	 */
	public static boolean judgeOpenBoxCost(Player player, String chapterID, MSItemInfo msRwdBox) {
		String firstDungeonID = chapterID + "01_1";
		DungeonsDataCfg dungDataCfg = DungeonsDataCfgDAO.getInstance().getCfgById(firstDungeonID);
		if (dungDataCfg == null) {
			GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("judgeOpenBoxCost, 章节[%s]中缺失第一个关卡[%s]的信息", chapterID, firstDungeonID), null);
			return false;
		}
		ItemInfo cost = new ItemInfo();
		if (msRwdBox.getItemID().equalsIgnoreCase("1")) {
			cost.setItemID(dungDataCfg.getObjCoBox().getBoxCost().getItemID());
			cost.setItemNum(dungDataCfg.getObjCoBox().getBoxCost().getItemNum());
		} else {
			cost.setItemID(dungDataCfg.getObjHiBox().getBoxCost().getItemID());
			cost.setItemNum(dungDataCfg.getObjHiBox().getBoxCost().getItemNum());
		}
		cost.setItemNum(cost.getItemNum() * msRwdBox.getItemCount());
		// 资源比对
		if (cost.getItemID() <= eSpecialItemId.eSpecial_End.getValue()) {
			if (cost.getItemID() == eSpecialItemId.Coin.getValue()) {
				if (cost.getItemNum() > player.getUserGameDataMgr().getCoin())
					return false;
			} else if (cost.getItemID() == eSpecialItemId.Gold.getValue()) {
				if (cost.getItemNum() > player.getUserGameDataMgr().getGold())
					return false;
			} else if (cost.getItemID() == eSpecialItemId.MagicSecretCoin.getValue()) {
				if (cost.getItemNum() > UserMagicSecretHolder.getInstance().get(player).getSecretGold())
					return false;
			} else if (cost.getItemID() == eSpecialItemId.TEAM_BATTLE_GOLD.getValue()) {
				if (cost.getItemNum() > player.getUserGameDataMgr().getTeamBattleCoin())
					return false;
			} else {
				GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("judgeOpenBoxCost, 需要消耗一种未处理的资源[%s]", cost.getItemID()), null);
				return false;
			}
		}
		// 资源扣除
		ItemBagMgr bagMgr = ItemBagMgr.getInstance();
		if (!bagMgr.addItem(player, cost.getItemID(), -cost.getItemNum())) {
			GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("judgeOpenBoxCost, 扣除物品[%s]的时候不成功，有[%s]未能扣除", cost.getItemID(), cost.getItemNum()), null);
			return false;
		}
		return true;
	}

	/**
	 * 判断今天是否有扫荡次数
	 * 
	 * @param player
	 * @param chapterID
	 * @return
	 */
	public static boolean judgeSweepCount(Player player, String chapterID) {
		MagicChapterInfo mcInfo = MagicChapterInfoHolder.getInstance().getItem(player.getUserId(), chapterID);
		if (mcInfo == null || mcInfo.getFinishedStages().size() == MagicSecretMgr.STAGE_COUNT_EACH_CHATPER) {
			// GameLog.error(LogModule.MagicSecret.toString(), player.getUserId(), String.format("judgeSweepCount, 章节[%s]当日全部通关，扫荡次数不足", chapterID), null);
			return false;
		}
		return true;
	}

	/**
	 * 判断章节是否可以扫荡(根据历史最高纪录)
	 * 
	 * @param player
	 * @param chapterID
	 * @return
	 */
	public static boolean judgeSweepAble(Player player, String chapterID) {
		// 判断历史通关
		UserMagicSecretData msData = UserMagicSecretHolder.getInstance().get(player);
		int maxStageID = msData.getMaxStageID();
		int maxChatperID = fromStageIDToChapterID(maxStageID);
		if (maxChatperID > Integer.parseInt(chapterID))
			return true;
		if (maxChatperID == Integer.parseInt(chapterID) && fromStageIDToLayerID(maxStageID) == MagicSecretMgr.STAGE_COUNT_EACH_CHATPER)
			return true;
		// GameLog.error(LogModule.MagicSecret.toString(), player.getUserId(), String.format("judgeSweepAble, 章节[%s]还未全部通关，不能扫荡", chapterID), null);
		return false;
	}

	public static int fromDungeonIDToStageID(Player player, String dungeonID) {
		String[] splitArr = dungeonID.split("_");
		if (splitArr.length != 2) {
			// GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("fromStageIDToSpaceID, dungeonID[%s] error", dungeonID), null);
			return -1;
		}
		return Integer.parseInt(splitArr[0]);
	}

	public static int fromStageIDToChapterID(int stageID) {
		return stageID / 100;
	}

	public static int fromStageIDToLayerID(int stageID) {
		return stageID % 100;
	}

	private static int getNextStage(int stageID) {
		int layerID = fromStageIDToLayerID(stageID);
		int chapterID = fromStageIDToChapterID(stageID);
		if (layerID >= MagicSecretMgr.STAGE_COUNT_EACH_CHATPER) {
			layerID = 1;
			chapterID++;
		} else
			layerID++;
		return chapterID * 100 + layerID;
	}
}
