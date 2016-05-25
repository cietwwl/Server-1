package com.playerdata.mgcsecret.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.bm.rank.magicsecret.MSScoreRankMgr;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.army.ArmyInfo;
import com.playerdata.mgcsecret.cfg.BuffBonusCfg;
import com.playerdata.mgcsecret.cfg.BuffBonusCfgDAO;
import com.playerdata.mgcsecret.cfg.DungeonScoreCfg;
import com.playerdata.mgcsecret.cfg.DungeonScoreCfgDAO;
import com.playerdata.mgcsecret.cfg.DungeonsDataCfg;
import com.playerdata.mgcsecret.cfg.DungeonsDataCfgDAO;
import com.playerdata.mgcsecret.data.MSDungeonInfo;
import com.playerdata.mgcsecret.data.MSScoreDataItem;
import com.playerdata.mgcsecret.data.MagicChapterInfo;
import com.playerdata.mgcsecret.data.MagicChapterInfoHolder;
import com.playerdata.mgcsecret.data.UserMagicSecretData;
import com.playerdata.mgcsecret.data.UserMagicSecretHolder;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwproto.MagicSecretProto.MagicSecretRspMsg;
import com.rwproto.MagicSecretProto.msResultType;
import com.rwproto.MagicSecretProto.msRewardBox;

public class MagicSecretMgr extends MSInnerProcessor{

	// 初始化
	public void init(Player playerP) {
		m_pPlayer = playerP;
		this.userId = playerP.getUserId();
		userMSHolder = new UserMagicSecretHolder(userId);
		mChapterHolder = MagicChapterInfoHolder.getInstance();
		if(mChapterHolder.getItemList(userId).size() == 0){
			mChapterHolder.initMagicChapterInfo(playerP, CHAPTER_INIT_ID);
		}
	}
	
	/**
	 * 获取秘境排行
	 * @param msRsp
	 */
	public void getMSRankData(MagicSecretRspMsg.Builder msRsp) {
		List<MSScoreDataItem> rankList = MSScoreRankMgr.getMSScoreRankList();
		int size = rankList.size();
		for(int i = 0; i < size; i++){
			msRsp.setMsRankData(i, JsonUtil.writeValue(rankList.get(i)));
		}
		msRsp.setSelfRank(MSScoreRankMgr.getRankIndex(userId));
		msRsp.setRstType(msResultType.SUCCESS);
	}
	
	/**
	 * 进入关卡战斗
	 * @param dungeonID
	 * @return
	 */
	public msResultType enterMSFight(String dungeonID){
		if(!judgeUserLevel(dungeonID)) return msResultType.LOW_LEVEL;
		if(!judgeDungeonsCondition(dungeonID)) return msResultType.CONDITION_UNREACH;
		if(!judgeDungeonsLegal(dungeonID)) return msResultType.DATA_ERROR;
		if(!judgeDungeonsCount(dungeonID)) return msResultType.TIMES_NOT_ENOUGH;
		
		//进入副本的时候更新可以选的副本（如果有三个，进入其中一个之后，如果没打过，以后也只有一个选择）
		DungeonsDataCfg dungDataCfg = DungeonsDataCfgDAO.getInstance().getCfgById(dungeonID);
		String chapterID = String.valueOf(dungDataCfg.getChapterID());
		MagicChapterInfo mcInfo = mChapterHolder.getItem(userId, chapterID);
		for(MSDungeonInfo dungeon : mcInfo.getSelectableDungeons()){
			if(dungeon.getDungeonKey().equalsIgnoreCase(dungeonID))
				mcInfo.setSelectedDungeonIndex(mcInfo.getSelectableDungeons().indexOf(dungeon));
		}
		
		//进副本的时候，如果有没购买的buff，需要清空
		dropSelectableBuff(chapterID);
		//如果有没有打开的箱子，则视为放弃，需要清空
		giveUpRewardBox(chapterID);
		
		//设置战斗中的副本(是为了获取奖励的时候，作合法性判断)
		UserMagicSecretData umsData = userMSHolder.get();
		if(umsData.getCurrentDungeonID() != null)
			GameLog.error(LogModule.MagicSecret, userId, String.format("enterMSFight, 进入副本[%s]时，仍有一个战斗dungeonID[%s]没解除", umsData.getCurrentDungeonID()), null);
		umsData.setCurrentDungeonID(dungeonID);
		
		userMSHolder.update(m_pPlayer);
		mChapterHolder.updateItem(m_pPlayer, mcInfo);
		return msResultType.SUCCESS;
	}
	
	/**
	 * 获取单关奖励
	 * @param msRsp
	 * @param dungeonID
	 * @param finishState
	 */
	public void getSingleReward(MagicSecretRspMsg.Builder msRsp, String dungeonID, String finishState) {
		//判断是否刚战斗的关卡
		UserMagicSecretData msData = userMSHolder.get();
		if(msData.getCurrentDungeonID() == null || !msData.getCurrentDungeonID().equalsIgnoreCase(dungeonID)){
			msRsp.setRstType(msResultType.DATA_ERROR);
			GameLog.error(LogModule.MagicSecret, userId, String.format("getSingleReward, 结算的副本[%s]不是刚刚战斗过的副本[%s]", dungeonID, String.valueOf(msData.getCurrentDungeonID())), null);
			return;
		}
		//判断是否已选择的关卡
		int stageID = fromDungeonIDToStageID(dungeonID);
		int chapterID = fromStageIDToChapterID(stageID);
		MagicChapterInfo mcInfo = mChapterHolder.getItem(userId, String.valueOf(chapterID));
		MSDungeonInfo fightingDung = mcInfo.getSelectableDungeons().get(mcInfo.getSelectedDungeonIndex());
		if(fightingDung == null){
			msRsp.setRstType(msResultType.DATA_ERROR);
			GameLog.error(LogModule.MagicSecret, userId, String.format("getSingleReward, 可选择的战斗副本[%s]数据有误, 选择的index[%s]", dungeonID, mcInfo.getSelectedDungeonIndex()), null);
			return;
		}
		//战斗胜利
		int finishStar = Integer.parseInt(finishState);
		if(finishStar > 0) {
			// 获取奖励，更新最高纪录，准备下个关卡数据
			ArrayList<ItemInfo> rewardItems = singleDungeonReward(fightingDung, finishStar);
			// 获得的物品
			for(int i = 0; i < rewardItems.size(); i++){
				msRsp.setRewardData(i, JsonUtil.writeValue(rewardItems.get(i)));
			}
			updateSelfMaxStage(dungeonID);
			handleNextDungeonPrepare(dungeonID);
		}
		//清空刚战斗的关卡
		msData.setCurrentDungeonID(null);
		userMSHolder.update(m_pPlayer);
		mChapterHolder.updateItem(m_pPlayer, mcInfo);
	}
	
	/**
	 * 副本扫荡
	 * @param msRsp
	 * @param chapterID
	 */
	public void getMSSweepReward(MagicSecretRspMsg.Builder msRsp, String chapterID) {
		if(!judgeSweepCount(chapterID)) {
			msRsp.setRstType(msResultType.TIMES_NOT_ENOUGH);
			return;
		}
		if(!judgeSweepAble(chapterID)) {
			msRsp.setRstType(msResultType.CONDITION_UNREACH);
			return;
		}
		//扫荡之前，如果有没有打开的箱子，则视为放弃，需要清空
		giveUpRewardBox(chapterID);
		
		//获取扫荡奖励
		ArrayList<ItemInfo> rewardItems = new ArrayList<ItemInfo>();		
		for(int i = 1; i < STAGE_COUNT_EACH_CHATPER; i++){
			String dungeonID = (Integer.parseInt(chapterID)*100 + i) + "_" + DUNGEON_MAX_LEVEL;
			if(!judgeDungeonsCount(dungeonID)) continue;
			DungeonsDataCfg dungDataCfg = DungeonsDataCfgDAO.getInstance().getCfgById(dungeonID);
			if(dungDataCfg == null) {
				GameLog.error(LogModule.MagicSecret, userId, String.format("getMSSweepReward, 扫荡章节[%s]时副本[%s]静态数据不存在", chapterID, dungeonID), null);
				continue;
			}
			MSDungeonInfo msdInfo = new MSDungeonInfo(dungeonID, null, -1, generateDropItem(dungDataCfg.getDrop()));
			rewardItems.addAll(singleDungeonReward(msdInfo, DUNGEON_FINISH_MAX_STAR));
		}
		// 获得的物品
		for(int i = 0; i < rewardItems.size(); i++){
			msRsp.setRewardData(i, JsonUtil.writeValue(rewardItems.get(i)));
		}
		
		userMSHolder.update(m_pPlayer);
		mChapterHolder.updateItem(m_pPlayer, mChapterHolder.getItem(userId, chapterID));
	}

	/**
	 * 用星星交换章节内的buff
	 * @param chapterID
	 * @param buffID
	 * @return
	 */
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
		mcInfo.getSelectedBuff().add(Integer.parseInt(buffID));
		
		// 扣除星星数
		BuffBonusCfg buffCfg = BuffBonusCfgDAO.getInstance().getCfgById(buffID);
		mcInfo.setStarCount(mcInfo.getStarCount() - buffCfg.getCost());
		
		userMSHolder.update(m_pPlayer);
		mChapterHolder.updateItem(m_pPlayer, mcInfo);
		return msResultType.SUCCESS;
	}
	
	/**
	 * 打开奖励的宝箱
	 * @param msRsp
	 * @param chapterID
	 * @param msRwdBox
	 */
	public void openRewardBox(MagicSecretRspMsg.Builder msRsp, String chapterID, msRewardBox msRwdBox) {
		if(!judgeRewardBoxLegal(chapterID, msRwdBox)){
			msRsp.setRstType(msResultType.NO_REWARD_BOX);
			return;
		}
		if(!judgeOpenBoxCost(chapterID, msRwdBox)){ // 该方法中包括了资源的扣除
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
		
		//箱子打开的物品
		String firstDungeonID = chapterID + "01_1";
		DungeonsDataCfg dungDataCfg = DungeonsDataCfgDAO.getInstance().getCfgById(firstDungeonID);
		String dropStr = "";
		if(msRwdBox.getBoxID().equalsIgnoreCase("1")) dropStr = dungDataCfg.getObjCoBox().getDropStr();
		if(msRwdBox.getBoxID().equalsIgnoreCase("2")) dropStr = dungDataCfg.getObjHiBox().getDropStr();
		ArrayList<ItemInfo> items = new ArrayList<ItemInfo>();
		for(int i = 0; i < msRwdBox.getBoxCount(); i++)
			items.addAll(generateDropItem(dropStr));
		handleDropItem(items);
		for(int i = 0; i < items.size(); i++){
			msRsp.setRewardData(i, JsonUtil.writeValue(items.get(i)));
		}
		msRsp.setRstType(msResultType.SUCCESS);
		userMSHolder.update(m_pPlayer);
		mChapterHolder.updateItem(m_pPlayer, mcInfo);
	}
	
	/**
	 * 更改队伍信息
	 * @param armyInfo
	 * @return
	 */
	public msResultType changeMSArmy(String armyInfo) {
		ArmyInfo army = JsonUtil.readValue(armyInfo, ArmyInfo.class);
		UserMagicSecretData umsData = userMSHolder.get();
		umsData.setSecretArmy(army);
		userMSHolder.update(m_pPlayer);
		return msResultType.SUCCESS;
	}
	
	/**
	 * 获取积分奖励
	 * @param msRsp
	 * @param scoreRewardID
	 */
	public void getScoreReward(MagicSecretRspMsg.Builder msRsp, int scoreRewardID) {
		DungeonScoreCfg dungScoreCfg = DungeonScoreCfgDAO.getInstance().getCfgById(String.valueOf(scoreRewardID));
		if(dungScoreCfg == null){
			msRsp.setRstType(msResultType.DATA_ERROR);
			GameLog.error(LogModule.MagicSecret, userId, String.format("getScoreReward, 要领取的奖励数据[%s]不存在", scoreRewardID), null);
			return;
		}
		UserMagicSecretData umsData = userMSHolder.get();
		if(umsData == null || umsData.getGotScoreReward().contains(scoreRewardID)){
			msRsp.setRstType(msResultType.NO_REWARD_CAN_GET);
			return;
		}
		int totalScore = getTotalScore(umsData.getHistoryScore(), umsData.getTodayScore());
		if(totalScore < dungScoreCfg.getScore()){
			msRsp.setRstType(msResultType.CONDITION_UNREACH);
			GameLog.error(LogModule.MagicSecret, userId, String.format("getScoreReward, 分数[%s]达不到领取积分[%s]的奖励", totalScore, dungScoreCfg.getScore()), null);
			return;
		}
		handleDropItem(dungScoreCfg.getRewardList());
		for(int i = 0; i < dungScoreCfg.getRewardList().size(); i++){
			msRsp.setRewardData(i, JsonUtil.writeValue(dungScoreCfg.getRewardList().get(i)));
		}
		userMSHolder.update(m_pPlayer);
	}
	
	/**
	 * 获取个人排名
	 * @param msRsp
	 */
	public void getSelfMSRank(MagicSecretRspMsg.Builder msRsp) {
		msRsp.setSelfRank(MSScoreRankMgr.getRankIndex(userId));
		msRsp.setRstType(msResultType.SUCCESS);
	}
	
	/**
	 * 放弃可以开启的箱子
	 * @param chapterID
	 */
	public msResultType giveUpRewardBox(String chapterID) {		
		MagicChapterInfo mcInfo = mChapterHolder.getItem(userId, chapterID);
		if(mcInfo == null) return msResultType.DATA_ERROR;
		if(!mcInfo.getCanOpenBoxes().isEmpty()){
			mcInfo.getCanOpenBoxes().clear();
			mChapterHolder.updateItem(m_pPlayer, mcInfo);
		}
		return msResultType.SUCCESS;
	}

	/**
	 * 扣除秘境货币
	 * @param secretGold
	 * @return
	 */
	public boolean deductSecretGold(int secretGold){
		if(secretGold < 0) return false;
		UserMagicSecretData umsData = userMSHolder.get();
		int currentGold = umsData.getSecretGold();
		if(currentGold < secretGold) return false;
		umsData.setSecretGold(currentGold - secretGold);
		userMSHolder.update(m_pPlayer);
		return true;
	}
	
	/**
	 * 增加秘境货币
	 * @param secretGold
	 * @return
	 */
	public boolean addSecretGold(int secretGold){
		if(secretGold < 0) return deductSecretGold(-secretGold);
		UserMagicSecretData umsData = userMSHolder.get();
		umsData.setSecretGold(umsData.getSecretGold() + secretGold);
		userMSHolder.update(m_pPlayer);
		return true;
	}
	
	/**
	 * 单关奖励
	 * 有些合法性的检查需要在函数外
	 * @param fightingDung 战斗的副本
	 * @param finishStar 通关评价
	 * @return 奖励的物品，用于前端显示
	 */
	private ArrayList<ItemInfo> singleDungeonReward(MSDungeonInfo fightingDung, int finishStar){
		DungeonsDataCfg dungCfg = DungeonsDataCfgDAO.getInstance().getCfgById(fightingDung.getDungeonKey());
		if(dungCfg == null) {
			GameLog.error(LogModule.MagicSecret, userId, String.format("singleDungeonReward, 副本[%s]不存在，无法进行结算", fightingDung.getDungeonKey()), null);
			return new ArrayList<ItemInfo>();
		}
		
		// 星星奖励
		String chapterID = String.valueOf(fromStageIDToChapterID(fromDungeonIDToStageID(fightingDung.getDungeonKey())));
		MagicChapterInfo mcInfo = mChapterHolder.getItem(userId, chapterID);
		mcInfo.setStarCount(mcInfo.getStarCount() + dungCfg.getStarReward());
		
		// 积分奖励
		UserMagicSecretData umsData = userMSHolder.get();
		int score = (int)(dungCfg.getScore() * getScoreRatio(finishStar));
		umsData.setTodayScore(umsData.getTodayScore() + score);
		
		// 排名更改
		informRankModule();
		
		// 掉落物品(需要回传给前端做显示用)
		handleDropItem(fightingDung.getDropItem());
		
		// 增加可以购买的箱子
		addCanOpenBoxes(chapterID);
		return fightingDung.getDropItem();
	}
	
	/**
	 * 处理下一个关卡开始之前的各项准备
	 * @param dungeonID
	 */
	protected void handleNextDungeonPrepare(String dungeonID){	
		provideNextSelectalbeBuff(dungeonID);
		createDungeonsDataForNextStage(dungeonID);
	}

	/**
	 * 计算总积分
	 * @param history
	 * @param today
	 * @return
	 */
	public static int getTotalScore(int history, int today){
		return (int)(history * SCORE_COEFFICIENT) + today;
	}
	
	public void synMagicChapterData() {
		mChapterHolder.synAllData(m_pPlayer);
		
	}
	
	public void synUserMSData() {
		userMSHolder.syn(m_pPlayer, 0);
	}
}