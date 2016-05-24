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
import com.rwproto.MagicSecretProto.msRewardBox;

public class MSConditionJudger {
	public final static int STAGE_COUNT_EACH_CHATPER = 8;
	public final static int DUNGEON_FINISH_MAX_STAR = 3;
	public final static int DUNGEON_MAX_LEVEL = 3;
	public final static float SCORE_COEFFICIENT = 0.08f;
	
	public final static float ONE_STAR_SCORE_COEFFICIENT = 1.0f;
	public final static float TWO_STAR_SCORE_COEFFICIENT = 1.5f;
	public final static float THREE_STAR_SCORE_COEFFICIENT = 2.5f;
	
	protected MagicChapterInfoHolder mChapterHolder;
	protected UserMagicSecretHolder userMSHolder;
	
	protected Player m_pPlayer = null;
	protected String userId;
	
	/**
	 * 判断玩家等级
	 * @param dungeonID
	 * @return
	 */
	protected boolean judgeUserLevel(String dungeonID){
		DungeonsDataCfg dungDataCfg = DungeonsDataCfgDAO.getInstance().getCfgById(dungeonID);
		MagicChapterCfg mcCfg = MagicChapterCfgDAO.getInstance().getCfgById(String.valueOf(dungDataCfg.getChapterID()));
		int userLevel = m_pPlayer.getUserDataMgr().getUser().getLevel();
		if(userLevel < mcCfg.getLevelLimit()){
			GameLog.error(LogModule.MagicSecret, userId, String.format("judgeUserLevel, 角色等级[%s]没有达到副本[%s]要求[%s]", userLevel, dungeonID, mcCfg.getLevelLimit()), null);
			return false;
		}
		return true;
	}
	
	/**
	 * 判断进入副本的条件
	 * @param dungeonID
	 * @return
	 */
	protected boolean judgeDungeonsCondition(String dungeonID){
		UserMagicSecretData msData = userMSHolder.get();
		int maxStageID = msData.getMaxStageID();
		int reqStageID = fromDungeonIDToStageID(dungeonID);
		if(reqStageID > maxStageID || reqStageID < 0){
			GameLog.error(LogModule.MagicSecret, userId, String.format("judgeDungeonsCondition, reqStageID[%s]超过了最高纪录[%s]", dungeonID, maxStageID), null);
			return false;
		}
		return true;
	}
	
	/**
	 * 判断副本的合法性
	 * @param dungeonID
	 * @return
	 */
	protected boolean judgeDungeonsLegal(String dungeonID){
		DungeonsDataCfg dungDataCfg = DungeonsDataCfgDAO.getInstance().getCfgById(dungeonID);
		if(dungDataCfg == null){
			GameLog.error(LogModule.MagicSecret, userId, String.format("judgeDungeonsLegal, 一个非法的dungeonID[%s], 静态数据中不包含", dungeonID), null);
			return false;
		}
		MagicChapterInfo mcInfo = mChapterHolder.getItem(userId, String.valueOf(dungDataCfg.getChapterID()));
		if(mcInfo == null){
			GameLog.error(LogModule.MagicSecret, userId, String.format("judgeDungeonsLegal, 不合法的章节[%s], 有可能是没开启或不存在", dungDataCfg.getChapterID()), null);
			return false;
		}
		if(mcInfo.getSelectedDungeonIndex() >= 0){
			//表示曾经打过这个关卡
			MSDungeonInfo dungeon = mcInfo.getSelectableDungeons().get(mcInfo.getSelectedDungeonIndex());
			if(dungeon != null && dungeon.getDungeonKey().equalsIgnoreCase(dungeonID))
				return true;
			else{
				GameLog.error(LogModule.MagicSecret, userId, String.format("judgeDungeonsLegal, 曾选择了打难度[%s], 现在不能打不同的难度副本[%s]", mcInfo.getSelectedDungeonIndex(), dungeonID), null);
				return false;
			}
		}
		for(MSDungeonInfo dungeon : mcInfo.getSelectableDungeons()){
			if(dungeon.getDungeonKey().equalsIgnoreCase(dungeonID))
				return true;
		}
		GameLog.error(LogModule.MagicSecret, userId, String.format("judgeDungeonsLegal, 该副本[%s]目前不可选择", dungeonID), null);
		return false;
	}
	
	/**
	 * 判断副本的次数（主要是看今天有没有通关过）
	 * @param dungeonID
	 * @return
	 */
	protected boolean judgeDungeonsCount(String dungeonID){
		int stageID = fromDungeonIDToStageID(dungeonID);
		String chapterID = String.valueOf(fromStageIDToChapterID(stageID));
		MagicChapterInfo mcInfo = mChapterHolder.getItem(userId, chapterID);
		if(mcInfo == null){
			GameLog.error(LogModule.MagicSecret, userId, String.format("judgeDungeonsCount, 不合法的章节[%s], dungeonID[%s]", chapterID, dungeonID), null);
			return false;
		}
		if(mcInfo.getFinishedStages().contains(stageID)) {
			GameLog.info(LogModule.MagicSecret.toString(), userId, String.format("judgeDungeonsCount, stage[%s]今日已经通关，次数不够, 准备打dungeonID[%s]", stageID, dungeonID), null);
			return false;
		}
		return true;
	}

	/**
	 * 判断buff的合法性
	 * @param chapterID
	 * @param buffID
	 * @return
	 */
	protected boolean judgeBuffLegal(String chapterID, String buffID){
		BuffBonusCfg buffCfg = BuffBonusCfgDAO.getInstance().getCfgById(buffID);
		if(buffCfg == null){
			GameLog.error(LogModule.MagicSecret, userId, String.format("judgeBuffLegal, buff[%s]在配置表中不存在", buffID), null);
			return false;
		}
		MagicChapterInfo mcInfo = mChapterHolder.getItem(userId, chapterID);
		if(mcInfo == null){
			GameLog.error(LogModule.MagicSecret, userId, String.format("judgeBuffLegal, 不合法的章节[%s], 有可能是没开启或不存在", chapterID), null);
			return false;
		}
		List<Integer> unselect = mcInfo.getUnselectedBuff();
		if(unselect == null || unselect.isEmpty()) {
			GameLog.error(LogModule.MagicSecret, userId, String.format("judgeBuffLegal, 章节[%s]中没有可选的buff", chapterID), null);
			return false;
		}
		for(int bID : unselect){
			if(bID == Integer.parseInt(buffID)) return true;
		}
		GameLog.error(LogModule.MagicSecret, userId, String.format("judgeBuffLegal, 章节[%s]的可选buff中没有找到目标buff[%s]", chapterID, buffID), null);
		return false;
	}
	
	/**
	 * 判断星星数量
	 * @param chapterID
	 * @param buffID
	 * @return
	 */
	protected boolean judgeEnoughStar(String chapterID, String buffID){
		BuffBonusCfg buffCfg = BuffBonusCfgDAO.getInstance().getCfgById(buffID);
		MagicChapterInfo mcInfo = mChapterHolder.getItem(userId, chapterID);
		int starCount = mcInfo.getStarCount();
		if(starCount < buffCfg.getCost()){
			GameLog.error(LogModule.MagicSecret, userId, String.format("judgeBuffLegal, 章节[%s]中星星数[%s]不够换取buff[%s]", chapterID, starCount, buffID), null);
			return false;
		}
		return true;
	}

	/**
	 * 判断箱子数量
	 * @param chapterID
	 * @param msRwdBox
	 * @return
	 */
	protected boolean judgeRewardBoxLegal(String chapterID, msRewardBox msRwdBox){
		MagicChapterInfo mcInfo = mChapterHolder.getItem(userId, chapterID);
		if(mcInfo == null){
			GameLog.error(LogModule.MagicSecret, userId, String.format("judgeRewardBoxLegal, 不合法的章节[%s], 有可能是没开启或不存在", chapterID), null);
			return false;
		}
		for(ItemInfo itm : mcInfo.getCanOpenBoxes()){
			if(itm.getItemID() == Integer.parseInt(msRwdBox.getBoxID()) &&
					itm.getItemNum() >= msRwdBox.getBoxCount())
				return true;
		}
		GameLog.error(LogModule.MagicSecret, userId, String.format("judgeRewardBoxLegal, 章节[%s]中需要打开的箱子[%s]数量[%s]不足", chapterID, msRwdBox.getBoxID(), msRwdBox.getBoxCount()), null);
		return false;
	}
	
	/**
	 * 判断打开箱子的花费(如果够，直接扣除)
	 * @param chapterID
	 * @param msRwdBox
	 * @return
	 */
	protected boolean judgeOpenBoxCost(String chapterID, msRewardBox msRwdBox){
		String firstDungeonID = chapterID + "01_1";
		DungeonsDataCfg dungDataCfg = DungeonsDataCfgDAO.getInstance().getCfgById(firstDungeonID);
		if(dungDataCfg == null){
			GameLog.error(LogModule.MagicSecret, userId, String.format("judgeOpenBoxCost, 章节[%s]中缺失第一个关卡[%s]的信息", chapterID, firstDungeonID), null);
			return false;
		}
		ItemInfo cost = new ItemInfo();
		if(msRwdBox.getBoxID().equalsIgnoreCase("1")){
			cost.setItemID(dungDataCfg.getObjCoBox().getBoxCost().getItemID());
			cost.setItemNum(dungDataCfg.getObjCoBox().getBoxCost().getItemNum());
		}else{
			cost.setItemID(dungDataCfg.getObjHiBox().getBoxCost().getItemID());
			cost.setItemNum(dungDataCfg.getObjHiBox().getBoxCost().getItemNum());
		}
		cost.setItemNum(cost.getItemNum() * msRwdBox.getBoxCount());
		// 资源比对
		if (cost.getItemID() <= eSpecialItemId.eSpecial_End.getValue()) {
			if (cost.getItemID() == eSpecialItemId.Coin.getValue()) {
				if(cost.getItemNum() > m_pPlayer.getUserGameDataMgr().getCoin())
					return false;
			} else if (cost.getItemID() == eSpecialItemId.Gold.getValue()) {
				if(cost.getItemNum() > m_pPlayer.getUserGameDataMgr().getGold())
					return false;
			} else if (cost.getItemID() == eSpecialItemId.MagicSecretCoin.getValue()){
				if(cost.getItemNum() > m_pPlayer.getMagicSecretMgr().userMSHolder.get().getSecretGold())
					return false;
			} else{
				GameLog.error(LogModule.MagicSecret, userId, String.format("judgeOpenBoxCost, 需要消耗一种未处理的资源[%s]", cost.getItemID()), null);
				return false;
			}
		}
		// 资源扣除
		ItemBagMgr bagMgr = m_pPlayer.getItemBagMgr();
		if(!bagMgr.addItem(cost.getItemID(), cost.getItemNum())){
			GameLog.error(LogModule.MagicSecret, userId, String.format("judgeOpenBoxCost, 扣除物品[%s]的时候不成功，有[%s]未能扣除", cost.getItemID(), cost.getItemNum()), null);
			return false;
		}
		return true;
	}
	
	/**
	 * 判断今天是否有扫荡次数
	 * @param chapterID
	 * @return
	 */
	protected boolean judgeSweepCount(String chapterID){
		MagicChapterInfo mcInfo = mChapterHolder.getItem(userId, chapterID);
		if(mcInfo == null || mcInfo.getFinishedStages().size() == STAGE_COUNT_EACH_CHATPER) return true;
		GameLog.error(LogModule.MagicSecret.toString(), userId, String.format("judgeSweepCount, 章节[%s]当日全部通关，扫荡次数不足", chapterID), null);
		return false;
	}
	
	/**
	 * 判断章节是否可以扫荡(根据历史最高纪录)
	 * @param chapterID
	 * @return
	 */
	protected boolean judgeSweepAble(String chapterID){		
		// 判断历史通关
		UserMagicSecretData msData = userMSHolder.get();
		int maxStageID = msData.getMaxStageID();
		int maxChatperID = fromStageIDToChapterID(maxStageID);
		if(maxChatperID > Integer.parseInt(chapterID)) return true;
		if(fromStageIDToLayerID(maxStageID) == STAGE_COUNT_EACH_CHATPER) return true;
		GameLog.error(LogModule.MagicSecret.toString(), userId, String.format("judgeSweepCount, 章节[%s]当日全部通关，扫荡次数不足", chapterID), null);
		return true;
	}
	
	protected int fromDungeonIDToStageID(String dungeonID){
		String[] splitArr = dungeonID.split("_");
		if(splitArr.length != 2) {
			GameLog.error(LogModule.MagicSecret, userId, String.format("fromStageIDToSpaceID, dungeonID[%s] error", dungeonID), null);
			return -1;
		}
		return Integer.parseInt(splitArr[0]);
	}
	
	protected int fromStageIDToChapterID(int stageID){
		return stageID/100;
	}
	
	protected int fromStageIDToLayerID(int stageID){
		return stageID%100;
	}
}
