package com.playerdata.mgcsecret.manager;

import java.util.List;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.mgcsecret.cfg.BuffBonusCfg;
import com.playerdata.mgcsecret.cfg.BuffBonusCfgDAO;
import com.playerdata.mgcsecret.cfg.DungeonsDataCfg;
import com.playerdata.mgcsecret.cfg.DungeonsDataCfgDAO;
import com.playerdata.mgcsecret.cfg.MagicChapterCfg;
import com.playerdata.mgcsecret.cfg.MagicChapterCfgDAO;
import com.playerdata.mgcsecret.data.MSStageInfo;
import com.playerdata.mgcsecret.data.MagicChapterInfo;
import com.playerdata.mgcsecret.data.MagicChapterInfoHolder;
import com.playerdata.mgcsecret.data.UserMagicSecretData;
import com.playerdata.mgcsecret.data.UserMagicSecretHolder;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwproto.MagicSecretProto.msRewardBox;

public class MSConditionJudger {
	public final static int STAGE_COUNT_EACH_CHATPER = 8;
	
	protected MagicChapterInfoHolder mChapterHolder;
	protected UserMagicSecretHolder userMSHolder;
	
	protected Player m_pPlayer = null;
	protected String userId;
	
	// 判断玩家等级
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
	
	// 判断进入副本的条件
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
	
	// 判断副本的合法性
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
		for(MSStageInfo stage : mcInfo.getSelectableStages()){
			if(stage.getStageKey().equalsIgnoreCase(dungeonID))
				return true;
		}
		GameLog.error(LogModule.MagicSecret, userId, String.format("judgeDungeonsLegal, 该副本[%s]目前不可选择", dungeonID), null);
		return false;
	}
	
	// 判断副本的次数（主要是看今天有没有通关过）
	protected boolean judgeDungeonsCount(String dungeonID){
		int stageID = fromDungeonIDToStageID(dungeonID);
		String chapterID = String.valueOf(stageIDToChapterID(stageID));
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
	
	// 判断buff的合法性
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
	
	// 判断星星数量
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

	// 判断箱子数量
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
	
	// 判断打开箱子的花费
	protected boolean judgeOpenBoxCost(String chapterID, msRewardBox msRwdBox){
		
		return true;
	}
	
	// 判断今天是否有扫荡次数
	protected boolean judgeSweepCount(String chapterID){
		MagicChapterInfo mcInfo = mChapterHolder.getItem(userId, chapterID);
		if(mcInfo == null || mcInfo.getFinishedStages().size() == STAGE_COUNT_EACH_CHATPER) return true;
		GameLog.error(LogModule.MagicSecret.toString(), userId, String.format("judgeSweepCount, 章节[%s]当日全部通关，扫荡次数不足", chapterID), null);
		return false;
	}
	
	// 判断章节是否可以扫荡(根据历史最高纪录)
	protected boolean judgeSweepAble(String chapterID){		
		// 判断历史通关
		UserMagicSecretData msData = userMSHolder.get();
		int maxStageID = msData.getMaxStageID();
		int maxChatperID = stageIDToChapterID(maxStageID);
		if(maxChatperID > Integer.parseInt(chapterID)) return true;
		if(stageIDToLayerID(maxStageID) == STAGE_COUNT_EACH_CHATPER) return true;
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
	
	protected int stageIDToChapterID(int stageID){
		return stageID/100;
	}
	
	protected int stageIDToLayerID(int stageID){
		return stageID%100;
	}
	
	public static void main(String[] args) {
		// userMSHolder.get().getMaxStageID();
//				List<MSStageInfo> newSelectableStages = new ArrayList<MSStageInfo>();
//				for(MSStageInfo stage : mcInfo.getSelectableStages()){	
//					if(stage.getStageKey().equalsIgnoreCase(dungeonID))
//						newSelectableStages.add(stage);
//				}
//				if(newSelectableStages.isEmpty()) return false;
//				mcInfo.setSelectableStages(newSelectableStages);
//				return true;
	}
}
