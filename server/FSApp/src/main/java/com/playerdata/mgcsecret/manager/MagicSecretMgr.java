package com.playerdata.mgcsecret.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.bm.rank.magicsecret.MSScoreRankMgr;
import com.bm.rank.teaminfo.AngelArrayTeamInfoHelper;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.SimpleArmyInfo;
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
		userMSHolder = UserMagicSecretHolder.getInstance(); //new UserMagicSecretHolder(playerP.getUserId());
		mChapterHolder = MagicChapterInfoHolder.getInstance();
		if(mChapterHolder.getItemList(playerP.getUserId()).size() == 0){
			mChapterHolder.initMagicChapterInfo(playerP, CHAPTER_INIT_ID);
		}
	}
	
	/**
	 * 获取秘境排行
	 * @param msRsp
	 */
	public void getMSRankData(Player player, MagicSecretRspMsg.Builder msRsp) {
		List<MSScoreDataItem> rankList = MSScoreRankMgr.getMSScoreRankList();
		int size = rankList.size();
		for(int i = 0; i < size; i++){
			msRsp.addMsRankData(JsonUtil.writeValue(rankList.get(i)));
		}
		msRsp.setSelfRank(MSScoreRankMgr.getRankIndex(player.getUserId()));
		msRsp.setRstType(msResultType.SUCCESS);
	}
	
	/**
	 * 进入关卡战斗
	 * @param dungeonID
	 * @return
	 */
	public void enterMSFight(Player player, MagicSecretRspMsg.Builder msRsp, String dungeonID){
		if(!judgeUserLevel(player, dungeonID)) {
			msRsp.setRstType(msResultType.LOW_LEVEL);
			return;
		}
		if(!judgeDungeonsCondition(player, dungeonID)) {
			msRsp.setRstType(msResultType.CONDITION_UNREACH);
			return;
		}
		if(!judgeDungeonsLegal(player, dungeonID)) {
			msRsp.setRstType(msResultType.DATA_ERROR);
			return;
		}
		if(!judgeDungeonsCount(player, dungeonID)) {
			msRsp.setRstType(msResultType.TIMES_NOT_ENOUGH);
			return;
		}
		
		//进入副本的时候更新可以选的副本（如果有三个，进入其中一个之后，如果没打过，以后也只有一个选择）
		DungeonsDataCfg dungDataCfg = DungeonsDataCfgDAO.getInstance().getCfgById(dungeonID);
		String chapterID = String.valueOf(dungDataCfg.getChapterID());
		MagicChapterInfo mcInfo = mChapterHolder.getItem(player.getUserId(), chapterID);
		MSDungeonInfo enterDungeon = null;
		for(MSDungeonInfo dungeon : mcInfo.getSelectableDungeons()){
			if(dungeon.getDungeonKey().equalsIgnoreCase(dungeonID)){
				enterDungeon = dungeon;
				mcInfo.setSelectedDungeonIndex(mcInfo.getSelectableDungeons().indexOf(dungeon));
			}	
		}
		
		//进副本的时候，如果有没购买的buff，需要清空
		dropSelectableBuff(player, chapterID);
		//如果有没有打开的箱子，则视为放弃，需要清空
		giveUpRewardBox(player, chapterID);
		
		//设置战斗中的副本(是为了获取奖励的时候，作合法性判断)
		UserMagicSecretData umsData = userMSHolder.get(player);
		if(umsData.getCurrentDungeonID() != null)
			GameLog.error(LogModule.MagicSecret.getName(), player.getUserId(), String.format("enterMSFight, 进入副本[%s]时，仍有一个战斗dungeonID[%s]没解除", dungeonID, umsData.getCurrentDungeonID()), null);
		umsData.setCurrentDungeonID(dungeonID);
		
		userMSHolder.update(player);
		mChapterHolder.updateItem(player, mcInfo);
		msRsp.setRstType(msResultType.SUCCESS);
		ArmyInfo enimyArmy = AngelArrayTeamInfoHelper.parseTeamInfo2ArmyInfo(enterDungeon.getEnimyTeam());
		try {
			msRsp.setArmyInfo(enimyArmy.toJson());
		} catch (Exception e) {
			GameLog.error(LogModule.MagicSecret.getName(), player.getUserId(), String.format("enterMSFight, 进入副本[%s]时，enimyArmy转json异常", dungeonID), e);
		}
	}
	
	/**
	 * 获取单关奖励
	 * @param msRsp
	 * @param dungeonID
	 * @param finishState
	 */
	public void getSingleReward(Player player, MagicSecretRspMsg.Builder msRsp, String dungeonID, String finishState) {
		//判断是否刚战斗的关卡
		UserMagicSecretData msData = userMSHolder.get(player);
		if(msData.getCurrentDungeonID() == null || !msData.getCurrentDungeonID().equalsIgnoreCase(dungeonID)){
			msRsp.setRstType(msResultType.DATA_ERROR);
			GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("getSingleReward, 结算的副本[%s]不是刚刚战斗过的副本[%s]", dungeonID, String.valueOf(msData.getCurrentDungeonID())), null);
			return;
		}
		//判断是否已选择的关卡
		int stageID = fromDungeonIDToStageID(player, dungeonID);
		int chapterID = fromStageIDToChapterID(stageID);
		MagicChapterInfo mcInfo = mChapterHolder.getItem(player.getUserId(), String.valueOf(chapterID));
		MSDungeonInfo fightingDung = mcInfo.getSelectableDungeons().get(mcInfo.getSelectedDungeonIndex());
		if(fightingDung == null){
			msRsp.setRstType(msResultType.DATA_ERROR);
			GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("getSingleReward, 可选择的战斗副本[%s]数据有误, 选择的index[%s]", dungeonID, mcInfo.getSelectedDungeonIndex()), null);
			return;
		}
		//战斗胜利
		int finishStar = Integer.parseInt(finishState);
		if(finishStar > 0) {
			// 获取奖励，更新最高纪录，准备下个关卡数据
			List<? extends ItemInfo> rewardItems = singleDungeonReward(player, fightingDung, finishStar);
			// 获得的物品
			for(int i = 0; i < rewardItems.size(); i++){
				msRsp.addRewardData(JsonUtil.writeValue(rewardItems.get(i)));
			}
			updateSelfMaxStage(player, dungeonID);
			if(fromStageIDToLayerID(stageID) == STAGE_COUNT_EACH_CHATPER)
				mChapterHolder.initMagicChapterInfo(player, String.valueOf(chapterID + 1));
			else handleNextDungeonPrepare(player, dungeonID);
		}
		//清空刚战斗的关卡
		msData.setCurrentDungeonID(null);
		msRsp.setRstType(msResultType.SUCCESS);
		userMSHolder.update(player);
		mChapterHolder.updateItem(player, mcInfo);
	}
	
	/**
	 * 副本扫荡
	 * @param msRsp
	 * @param chapterID
	 */
	public void getMSSweepReward(Player player, MagicSecretRspMsg.Builder msRsp, String chapterID) {
		if(!judgeSweepCount(player, chapterID)) {
			msRsp.setRstType(msResultType.TIMES_NOT_ENOUGH);
			return;
		}
		if(!judgeSweepAble(player, chapterID)) {
			msRsp.setRstType(msResultType.CONDITION_UNREACH);
			return;
		}
		//扫荡之前，如果有没有打开的箱子，则视为放弃，需要清空
		giveUpRewardBox(player, chapterID);
		
		//获取扫荡奖励
		ArrayList<ItemInfo> rewardItems = new ArrayList<ItemInfo>();		
		for(int i = 1; i <= STAGE_COUNT_EACH_CHATPER; i++){
			String dungeonID = (Integer.parseInt(chapterID)*100 + i) + "_" + DUNGEON_MAX_LEVEL;
			if(!judgeDungeonsCount(player, dungeonID)) continue;
			DungeonsDataCfg dungDataCfg = DungeonsDataCfgDAO.getInstance().getCfgById(dungeonID);
			if(dungDataCfg == null) {
				GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("getMSSweepReward, 扫荡章节[%s]时副本[%s]静态数据不存在", chapterID, dungeonID), null);
				continue;
			}
			MSDungeonInfo msdInfo = new MSDungeonInfo(dungeonID, null, null, generateDropItem(player, dungDataCfg.getDrop()));
			rewardItems.addAll(singleDungeonReward(player, msdInfo, DUNGEON_FINISH_MAX_STAR));
		}
		// 获得的物品
		for(int i = 0; i < rewardItems.size(); i++){
			msRsp.addRewardData(JsonUtil.writeValue(rewardItems.get(i)));
		}
		msRsp.setRstType(msResultType.SUCCESS);
		userMSHolder.update(player);
		mChapterHolder.updateItem(player, mChapterHolder.getItem(player.getUserId(), chapterID));
	}

	/**
	 * 用星星交换章节内的buff
	 * @param chapterID
	 * @param buffID
	 * @return
	 */
	public msResultType exchangeBuff(Player player, String chapterID, String buffID){
		if(!judgeBuffLegal(player, chapterID, buffID)) return msResultType.DATA_ERROR;
		if(!judgeEnoughStar(player, chapterID, buffID)) return msResultType.NOT_ENOUGH_STAR;
		
		// 将buff从可选列表，转移到已选择列表
		MagicChapterInfo mcInfo = mChapterHolder.getItem(player.getUserId(), chapterID);
		Iterator<Integer> unselectItor = mcInfo.getUnselectedBuff().iterator();
		while(unselectItor.hasNext()){
			int bID= unselectItor.next();
			if(bID == Integer.parseInt(buffID)) unselectItor.remove();
		}
		mcInfo.getSelectedBuff().add(Integer.parseInt(buffID));
		
		// 扣除星星数
		BuffBonusCfg buffCfg = BuffBonusCfgDAO.getInstance().getCfgById(buffID);
		mcInfo.setStarCount(mcInfo.getStarCount() - buffCfg.getCost());

		userMSHolder.update(player);
		mChapterHolder.updateItem(player, mcInfo);
		return msResultType.SUCCESS;
	}
	
	/**
	 * 打开奖励的宝箱
	 * @param msRsp
	 * @param chapterID
	 * @param msRwdBox
	 */
	public void openRewardBox(Player player, MagicSecretRspMsg.Builder msRsp, String chapterID, msRewardBox msRwdBox) {
		if(!judgeRewardBoxLegal(player, chapterID, msRwdBox)){
			msRsp.setRstType(msResultType.NO_REWARD_BOX);
			return;
		}
		if(!judgeOpenBoxCost(player, chapterID, msRwdBox)){ // 该方法中包括了资源的扣除
			msRsp.setRstType(msResultType.NOT_ENOUGH_GOLD);
			return;
		}
		
		// 扣除箱子数量
		MagicChapterInfo mcInfo = mChapterHolder.getItem(player.getUserId(), chapterID);
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
			items.addAll(generateDropItem(player, dropStr));
		handleDropItem(player, items);
		for(int i = 0; i < items.size(); i++){
			msRsp.addRewardData(JsonUtil.writeValue(items.get(i)));
		}
		msRsp.setRstType(msResultType.SUCCESS);
		userMSHolder.update(player);
		mChapterHolder.updateItem(player, mcInfo);
	}
	
	/**
	 * 更改队伍信息
	 * @param armyInfo
	 * @return
	 */
	public msResultType changeMSArmy(Player player, String armyInfo) {
		SimpleArmyInfo army = JsonUtil.readValue(armyInfo, SimpleArmyInfo.class);
		UserMagicSecretData umsData = userMSHolder.get(player);
		umsData.setSecretArmy(army);
		userMSHolder.update(player);
		return msResultType.SUCCESS;
	}
	
	/**
	 * 获取积分奖励
	 * @param msRsp
	 * @param scoreRewardID
	 */
	public void getScoreReward(Player player, MagicSecretRspMsg.Builder msRsp, int scoreRewardID) {
		DungeonScoreCfg dungScoreCfg = DungeonScoreCfgDAO.getInstance().getCfgById(String.valueOf(scoreRewardID));
		if(dungScoreCfg == null){
			msRsp.setRstType(msResultType.DATA_ERROR);
			GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("getScoreReward, 要领取的奖励数据[%s]不存在", scoreRewardID), null);
			return;
		}
		UserMagicSecretData umsData = userMSHolder.get(player);
		if(umsData == null || umsData.getGotScoreReward().contains(scoreRewardID)){
			msRsp.setRstType(msResultType.NO_REWARD_CAN_GET);
			return;
		}
		int totalScore = getTotalScore(umsData.getHistoryScore(), umsData.getTodayScore());
		if(totalScore < dungScoreCfg.getScore()){
			msRsp.setRstType(msResultType.CONDITION_UNREACH);
			GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("getScoreReward, 分数[%s]达不到领取积分[%s]的奖励", totalScore, dungScoreCfg.getScore()), null);
			return;
		}
		handleDropItem(player, dungScoreCfg.getRewardList());
		for(int i = 0; i < dungScoreCfg.getRewardList().size(); i++){
			msRsp.addRewardData(JsonUtil.writeValue(dungScoreCfg.getRewardList().get(i)));
		}
		umsData.getGotScoreReward().add(scoreRewardID);
		msRsp.setRstType(msResultType.SUCCESS);
		userMSHolder.update(player);
	}
	
	/**
	 * 获取个人排名
	 * @param msRsp
	 */
	public void getSelfMSRank(Player player, MagicSecretRspMsg.Builder msRsp) {
		msRsp.setSelfRank(MSScoreRankMgr.getRankIndex(player.getUserId()));
		msRsp.setRstType(msResultType.SUCCESS);
	}
	
	/**
	 * 放弃可以开启的箱子
	 * @param chapterID
	 */
	public msResultType giveUpRewardBox(Player player, String chapterID) {		
		MagicChapterInfo mcInfo = mChapterHolder.getItem(player.getUserId(), chapterID);
		if(mcInfo == null) return msResultType.DATA_ERROR;
		if(!mcInfo.getCanOpenBoxes().isEmpty()){
			mcInfo.getCanOpenBoxes().clear();
			mChapterHolder.updateItem(player, mcInfo);
		}
		return msResultType.SUCCESS;
	}

	/**
	 * 扣除秘境货币
	 * @param secretGold
	 * @return
	 */
	public boolean deductSecretGold(Player player, int secretGold){
		if(secretGold < 0) return false;
		UserMagicSecretData umsData = userMSHolder.get(player);
		int currentGold = umsData.getSecretGold();
		if(currentGold < secretGold) return false;
		umsData.setSecretGold(currentGold - secretGold);
		userMSHolder.update(player);
		return true;
	}
	
	/**
	 * 获取玩家的法宝秘境货币
	 * @return
	 */
	public int getSecretGold(Player player){
		UserMagicSecretData umsData = userMSHolder.get(player);
		return umsData.getSecretGold();
	}
	
	/**
	 * 增加秘境货币
	 * @param secretGold
	 * @return
	 */
	public boolean addSecretGold(Player player, int secretGold){
		if(secretGold < 0) return deductSecretGold(player, -secretGold);
		UserMagicSecretData umsData = userMSHolder.get(player);
		umsData.setSecretGold(umsData.getSecretGold() + secretGold);
		userMSHolder.update(player);
		return true;
	}
	
	/**
	 * 单关奖励
	 * 有些合法性的检查需要在函数外
	 * @param fightingDung 战斗的副本
	 * @param finishStar 通关评价
	 * @return 奖励的物品，用于前端显示
	 */
	private List<? extends ItemInfo> singleDungeonReward(Player player, MSDungeonInfo fightingDung, int finishStar){
		List<ItemInfo> result = new ArrayList<ItemInfo>();
		DungeonsDataCfg dungCfg = DungeonsDataCfgDAO.getInstance().getCfgById(fightingDung.getDungeonKey());
		if(dungCfg == null) {
			GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("singleDungeonReward, 副本[%s]不存在，无法进行结算", fightingDung.getDungeonKey()), null);
			return new ArrayList<ItemInfo>();
		}
		
		// 星星奖励
		String chapterID = String.valueOf(fromStageIDToChapterID(fromDungeonIDToStageID(player, fightingDung.getDungeonKey())));
		MagicChapterInfo mcInfo = mChapterHolder.getItem(player.getUserId(), chapterID);
		mcInfo.setStarCount(mcInfo.getStarCount() + dungCfg.getStarReward());
		mcInfo.getFinishedStages().add(fromDungeonIDToStageID(player, fightingDung.getDungeonKey()));
		ItemInfo starItem = new ItemInfo();
		starItem.setItemID(MS_STAR_ID);
		starItem.setItemNum(dungCfg.getStarReward());
		result.add(starItem);
		
		// 积分奖励
		UserMagicSecretData umsData = userMSHolder.get(player);
		int score = (int)(dungCfg.getScore() * getScoreRatio(finishStar));
		umsData.setTodayScore(umsData.getTodayScore() + score);
		ItemInfo scoreItem = new ItemInfo();
		scoreItem.setItemID(MS_SCORE_ID);
		scoreItem.setItemNum(score);
		result.add(scoreItem);
		
		// 排名更改
		informRankModule(player);
		
		// 掉落物品(需要回传给前端做显示用)
		handleDropItem(player, fightingDung.getDropItem());
		
		// 增加可以购买的箱子
		addCanOpenBoxes(player, chapterID);
		
		result.addAll(fightingDung.getDropItem());
		return result;
	}
	
	/**
	 * 处理下一个关卡开始之前的各项准备
	 * @param dungeonID
	 */
	protected void handleNextDungeonPrepare(Player player, String dungeonID){	
		provideNextSelectalbeBuff(player, dungeonID);
		createDungeonsDataForNextStage(player, dungeonID);
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
	
	public void synMagicChapterData(Player player) {
		mChapterHolder.synAllData(player);
	}
	
	public void synUserMSData(Player player) {
		userMSHolder.syn(player, 0);
	}
}