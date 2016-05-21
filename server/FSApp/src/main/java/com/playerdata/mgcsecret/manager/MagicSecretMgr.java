package com.playerdata.mgcsecret.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.mgcsecret.cfg.BuffBonusCfg;
import com.playerdata.mgcsecret.cfg.BuffBonusCfgDAO;
import com.playerdata.mgcsecret.cfg.DungeonsDataCfg;
import com.playerdata.mgcsecret.cfg.DungeonsDataCfgDAO;
import com.playerdata.mgcsecret.data.MSStageInfo;
import com.playerdata.mgcsecret.data.MagicChapterInfo;
import com.playerdata.mgcsecret.data.MagicChapterInfoHolder;
import com.playerdata.mgcsecret.data.UserMagicSecretData;
import com.playerdata.mgcsecret.data.UserMagicSecretHolder;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwproto.MagicSecretProto.MagicSecretRspMsg;
import com.rwproto.MagicSecretProto.msResultType;
import com.rwproto.MagicSecretProto.msRewardBox;

// 有积分改变的时候通知排行榜

public class MagicSecretMgr extends MSInnerProcessor{
	public final static int DUNGEON_FINISH_MAX_STAR = 3;
	
	// 初始化
	public void init(Player playerP) {
		m_pPlayer = playerP;
		this.userId = playerP.getUserId();
		mChapterHolder = MagicChapterInfoHolder.getInstance();
		userMSHolder = new UserMagicSecretHolder(userId);
	}
	
	public msResultType enterMSFight(String dungeonID){
		if(!judgeUserLevel(dungeonID)) return msResultType.LOW_LEVEL;
		if(!judgeDungeonsCondition(dungeonID)) return msResultType.CONDITION_UNREACH;
		if(!judgeDungeonsLegal(dungeonID)) return msResultType.DATA_ERROR;
		if(!judgeDungeonsCount(dungeonID)) return msResultType.TIMES_NOT_ENOUGH;
		
		//进入副本的时候更新可以选的副本（如果有三个，进入其中一个之后，如果没打过，以后也只有一个选择）
		DungeonsDataCfg dungDataCfg = DungeonsDataCfgDAO.getInstance().getCfgById(dungeonID);
		String chapteID = String.valueOf(dungDataCfg.getChapterID());
		MagicChapterInfo mcInfo = mChapterHolder.getItem(userId, chapteID);
		List<MSStageInfo> newStgList = new ArrayList<MSStageInfo>();
		for(MSStageInfo stage : mcInfo.getSelectableStages()){
			if(stage.getStageKey().equalsIgnoreCase(dungeonID))
				newStgList.add(stage);
		}
		mcInfo.setSelectableStages(newStgList);
		
		//进副本的时候，如果有没购买的buff，需要清空
		dropSelectableBuff(chapteID);
		
		//设置战斗中的副本(是为了获取奖励的时候，作合法性判断)
		UserMagicSecretData umsData = userMSHolder.get();
		if(umsData.getCurrentDungeonID() != null)
			GameLog.error(LogModule.MagicSecret, userId, String.format("enterMSFight, 进入副本[%s]时，仍有一个战斗dungeonID[%s]没解除", umsData.getCurrentDungeonID()), null);
		umsData.setCurrentDungeonID(dungeonID);
		
		userMSHolder.update(m_pPlayer);
		mChapterHolder.updateItem(m_pPlayer, mcInfo);
		return msResultType.SUCCESS;
	}

	public msResultType exchangeBuff(String chapterID, String buffID){
		if(!judgeBuffLegal(chapterID, buffID)) return msResultType.DATA_ERROR;
		if(!judgeEnoughStar(chapterID, buffID)) return msResultType.NOT_ENOUGH_STAR;
		
		// 将buff从可选列表，转移到已选择列表
		MagicChapterInfo mcInfo = mChapterHolder.getItem(userId, chapterID);
		Iterator<Integer> unselectItor = mcInfo.getUnselectedBuff().iterator();
		while(unselectItor.hasNext()){
			int bID= unselectItor.next();
			if(bID == Integer.parseInt(buffID)) unselectItor.remove();
		}
		mcInfo.getUnselectedBuff().add(Integer.parseInt(buffID));
		
		// 扣除星星数
		BuffBonusCfg buffCfg = BuffBonusCfgDAO.getInstance().getCfgById(buffID);
		mcInfo.setStarCount(mcInfo.getStarCount() - buffCfg.getCost());
		
		userMSHolder.update(m_pPlayer);
		mChapterHolder.updateItem(m_pPlayer, mcInfo);
		return msResultType.SUCCESS;
	}
	
	public void openRewardBox(MagicSecretRspMsg.Builder msRsp, String chapterID, msRewardBox msRwdBox) {
		if(!judgeRewardBoxLegal(chapterID, msRwdBox)){
			msRsp.setRstType(msResultType.NO_REWARD_BOX);
			return;
		}
		if(!judgeOpenBoxCost(chapterID, msRwdBox)){
			msRsp.setRstType(msResultType.NOT_ENOUGH_GOLD);
			return;
		}
		
		// 扣除箱子数量
		MagicChapterInfo mcInfo = mChapterHolder.getItem(userId, chapterID);
		Iterator<ItemInfo> itor = mcInfo.getCanOpenBoxes().iterator();
		while(itor.hasNext()){
			ItemInfo itm = itor.next();
			if(itm.getItemID() == Integer.parseInt(msRwdBox.getBoxID())){
				itm.setItemNum(itm.getItemNum() - msRwdBox.getBoxCount());
				if(itm.getItemNum() <= 0) itor.remove();
			}
		}
		
		//TODO 扣除开箱子的花费
		
		//TODO 需要给玩家添加物品（箱子打开的物品）
		
		msRsp.setRstType(msResultType.SUCCESS);
		userMSHolder.update(m_pPlayer);
		mChapterHolder.updateItem(m_pPlayer, mcInfo);
	}

	public void getMSSweepReward(MagicSecretRspMsg.Builder msRsp, String chapterID) {
		if(!judgeSweepCount(chapterID)) {
			msRsp.setRstType(msResultType.TIMES_NOT_ENOUGH);
			return;
		}
		if(!judgeSweepAble(chapterID)) {
			msRsp.setRstType(msResultType.CONDITION_UNREACH);
			return;
		}
		
		for(int i = 1; i < STAGE_COUNT_EACH_CHATPER; i++){
			String dungeonID = (Integer.parseInt(chapterID)*100 + i) + "_" + 3;
			if(!judgeDungeonsCount(dungeonID)) continue;
			singleDungeonReward(dungeonID, DUNGEON_FINISH_MAX_STAR);
		}
	}
	
	public void getSingleReward(MagicSecretRspMsg.Builder msRsp, String dungeonID, String finishState) {
		//判断是否刚战斗的关卡
		UserMagicSecretData msData = userMSHolder.get();
		if(!msData.getCurrentDungeonID().equalsIgnoreCase(dungeonID)){
			msRsp.setRstType(msResultType.DATA_ERROR);
			return;
		}
		//战斗胜利
		int finishStar = Integer.parseInt(finishState);
		if(finishStar > 0) {
			singleDungeonReward(dungeonID, finishStar);
			updateSelfMaxStage(dungeonID);
		}
		//清空刚战斗的关卡
		msData.setCurrentDungeonID(null);
		handleNextDungeonPrepare(dungeonID);
	}

	public void getMSRankData(MagicSecretRspMsg.Builder msRsp) {
		
	}
	
	public boolean deductSecretGold(int secretGold){
		return true;
	}
	
	public boolean addSecretGold(int secretGold){
		return true;
	}

	/**
	 * 单关奖励
	 * 有些合法性的检查需要在函数外
	 * @param dungeonID 关卡id
	 * @param finishStar 通关评价
	 */
	protected void singleDungeonReward(String dungeonID, int finishStar){
		DungeonsDataCfg dungCfg = DungeonsDataCfgDAO.getInstance().getCfgById(dungeonID);
		if(dungCfg == null) {
			GameLog.error(LogModule.MagicSecret, userId, String.format("singleDungeonReward, 副本[%s]不存在，无法进行结算", dungeonID), null);
			return;
		}
		
		// 星星奖励
		String chapterID = String.valueOf(stageIDToChapterID(fromDungeonIDToStageID(dungeonID)));
		MagicChapterInfo mcInfo = mChapterHolder.getItem(userId, chapterID);
		mcInfo.setStarCount(mcInfo.getStarCount() + dungCfg.getStarReward());
		
		// 积分奖励
		UserMagicSecretData umsData = userMSHolder.get();
		int score = (int)(dungCfg.getScore() * getScoreRatio(finishStar));
		umsData.setTodayScore(umsData.getTodayScore() + score);
		
		// 排名更改
		informRankModule();
		
		// 掉落物品（打之前决定还是打之后决定），参数类型
		handleDropItem();
		
		// 增加可以购买的箱子
		getCanOpenBoxes();
	}
	
	// 处理下一个关卡开始之前的各项准备
	protected void handleNextDungeonPrepare(String dungeonID){
		provideSelectalbeBuff();
		generateEnimyForNextStage();
	}
}