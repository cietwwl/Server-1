package com.playerdata.mgcsecret.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.bm.rank.magicsecret.MSScoreRankMgr;
import com.bm.rank.teaminfo.AngelArrayTeamInfoHelper;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.retrieve.userFeatures.UserFeatruesMgr;
import com.playerdata.activity.retrieve.userFeatures.UserFeaturesEnum;
import com.playerdata.army.ArmyInfo;
import com.playerdata.mgcsecret.cfg.BuffBonusCfg;
import com.playerdata.mgcsecret.cfg.BuffBonusCfgDAO;
import com.playerdata.mgcsecret.cfg.DungeonScoreCfg;
import com.playerdata.mgcsecret.cfg.DungeonScoreCfgDAO;
import com.playerdata.mgcsecret.cfg.DungeonsDataCfg;
import com.playerdata.mgcsecret.cfg.DungeonsDataCfgDAO;
import com.playerdata.mgcsecret.cfg.MagicChapterCfg;
import com.playerdata.mgcsecret.cfg.MagicChapterCfgDAO;
import com.playerdata.mgcsecret.data.MSDungeonInfo;
import com.playerdata.mgcsecret.data.MSScoreDataItem;
import com.playerdata.mgcsecret.data.MagicChapterInfo;
import com.playerdata.mgcsecret.data.MagicChapterInfoHolder;
import com.playerdata.mgcsecret.data.UserMagicSecretData;
import com.playerdata.mgcsecret.data.UserMagicSecretHolder;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwproto.MagicSecretProto.MSItemInfo;
import com.rwproto.MagicSecretProto.MSScoreRankItem;
import com.rwproto.MagicSecretProto.MagicSecretRspMsg;
import com.rwproto.MagicSecretProto.msResultType;

public class MagicSecretMgr {
	public final static int STAGE_COUNT_EACH_CHATPER = 8;
	public final static int DUNGEON_FINISH_MAX_STAR = 3;
	public final static int DUNGEON_MAX_LEVEL = 3;
	public final static float SCORE_COEFFICIENT = 0.08f;
	
	public final static float ONE_STAR_SCORE_COEFFICIENT = 1.0f;
	public final static float TWO_STAR_SCORE_COEFFICIENT = 1.5f;
	public final static float THREE_STAR_SCORE_COEFFICIENT = 2.5f;
	
	public final static int MS_RANK_FETCH_COUNT = 50;
	
	public final static String CHAPTER_INIT_ID = "1";
	public final static int MS_STAR_ID = -301;
	public final static int MS_SCORE_ID = -302;
	
	private static class InstanceHolder{
		private static MagicSecretMgr instance = new MagicSecretMgr();
	}
	
	public static MagicSecretMgr getInstance(){
		return InstanceHolder.instance;
	}
	
	private MagicSecretMgr() { }
	
	/**
	 * 获取秘境排行
	 * @param player
	 * @param msRsp
	 */
	public void getMSRankData(Player player, MagicSecretRspMsg.Builder msRsp) {
		List<MSScoreDataItem> rankList = MSScoreRankMgr.getMSScoreRankList();
		int size = rankList.size();
		for(int i = 0; i < size; i++){
			MSScoreRankItem.Builder builder = MSScoreRankItem.newBuilder();
			builder.setUserId(rankList.get(i).getUserId());
			builder.setTotalScore(rankList.get(i).getTotalScore());
			builder.setUserName(rankList.get(i).getUserName());
			builder.setLevel(rankList.get(i).getLevel());
			builder.setHeadImage(rankList.get(i).getHeadImage());
			builder.setJob(rankList.get(i).getJob());
			builder.setTitle(rankList.get(i).getTitle());	
			msRsp.addMsRankData(builder.build());
		}
		msRsp.setSelfRank(MSScoreRankMgr.getRankIndex(player.getUserId()));
		msRsp.setRstType(msResultType.SUCCESS);
	}
	
	/**
	 * 进入关卡战斗
	 * @param player
	 * @param dungeonID
	 * @return
	 */
	public void enterMSFight(Player player, MagicSecretRspMsg.Builder msRsp, String dungeonID){
		if(!MSConditionJudger.judgeUserLevel(player, dungeonID)) {
			msRsp.setRstType(msResultType.LOW_LEVEL);
			return;
		}
		if(!judgeDungeonsCondition(player, dungeonID)) {
			msRsp.setRstType(msResultType.CONDITION_UNREACH);
			return;
		}
		if(!MSConditionJudger.judgeDungeonsLegal(player, dungeonID)) {
			msRsp.setRstType(msResultType.DATA_ERROR);
			return;
		}
		if(!MSConditionJudger.judgeDungeonsCount(player, dungeonID)) {
			msRsp.setRstType(msResultType.TIMES_NOT_ENOUGH);
			return;
		}
		
		//进入副本的时候更新可以选的副本（如果有三个，进入其中一个之后，如果没打过，以后也只有一个选择）
		DungeonsDataCfg dungDataCfg = DungeonsDataCfgDAO.getInstance().getCfgById(dungeonID);
		String chapterID = String.valueOf(dungDataCfg.getChapterID());
		MagicChapterInfo mcInfo = MagicChapterInfoHolder.getInstance().getItem(player.getUserId(), chapterID);
		MSDungeonInfo enterDungeon = null;
		for(MSDungeonInfo dungeon : mcInfo.getSelectableDungeons()){
			if(dungeon.getDungeonKey().equalsIgnoreCase(dungeonID)){
				enterDungeon = dungeon;
				mcInfo.setSelectedDungeonIndex(mcInfo.getSelectableDungeons().indexOf(dungeon));
			}	
		}
		
		//进副本的时候，如果有没购买的buff，需要清空
		MSInnerProcessor.dropSelectableBuff(player, chapterID);
		//如果有没有打开的箱子，则视为放弃，需要清空
		giveUpRewardBox(player, chapterID);
		
		//设置战斗中的副本(是为了获取奖励的时候，作合法性判断)
		UserMagicSecretData umsData = UserMagicSecretHolder.getInstance().get(player);
		if(umsData.getCurrentDungeonID() != null)
			GameLog.error(LogModule.MagicSecret.getName(), player.getUserId(), String.format("enterMSFight, 进入副本[%s]时，仍有一个战斗dungeonID[%s]没解除", dungeonID, umsData.getCurrentDungeonID()), null);
		umsData.setCurrentDungeonID(dungeonID);
		
		UserMagicSecretHolder.getInstance().update(player);
		MagicChapterInfoHolder.getInstance().updateItem(player, mcInfo);
		msRsp.setRstType(msResultType.SUCCESS);
		UserFeatruesMgr.getInstance().doFinish(player, UserFeaturesEnum.magicSecert);
		ArmyInfo enimyArmy = AngelArrayTeamInfoHelper.parseTeamInfo2ArmyInfo(enterDungeon.getEnimyTeam());
		try {
			enimyArmy.genVCode();
			msRsp.setArmyInfo(enimyArmy.toJson());
		} catch (Exception e) {
			GameLog.error(LogModule.MagicSecret.getName(), player.getUserId(), String.format("enterMSFight, 进入副本[%s]时，enimyArmy转json异常", dungeonID), e);
		}
	}
	
	/**
	 * 获取单关奖励
	 * @param player
	 * @param msRsp
	 * @param dungeonID
	 * @param finishState
	 */
	public void getSingleReward(Player player, MagicSecretRspMsg.Builder msRsp, String dungeonID, String finishState) {
		//判断是否刚战斗的关卡
		UserMagicSecretData msData = UserMagicSecretHolder.getInstance().get(player);
		if(msData.getCurrentDungeonID() == null || !msData.getCurrentDungeonID().equalsIgnoreCase(dungeonID)){
			msRsp.setRstType(msResultType.DATA_ERROR);
			GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("getSingleReward, 结算的副本[%s]不是刚刚战斗过的副本[%s]", dungeonID, String.valueOf(msData.getCurrentDungeonID())), null);
			return;
		}
		//判断是否已选择的关卡
		int stageID = MSConditionJudger.fromDungeonIDToStageID(player, dungeonID);
		int chapterID = MSConditionJudger.fromStageIDToChapterID(stageID);
		MagicChapterInfo mcInfo = MagicChapterInfoHolder.getInstance().getItem(player.getUserId(), String.valueOf(chapterID));
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
				MSItemInfo.Builder msItem = MSItemInfo.newBuilder();
				ItemInfo item = rewardItems.get(i);
				msItem.setItemID(String.valueOf(item.getItemID()));
				msItem.setItemCount(item.getItemNum());
				msRsp.addRewardData(msItem.build());
			}
			// 更新最高纪录，添加首次通关章节的奖励(如果刷新了纪录，并且是本章最后一关)
			if(MSInnerProcessor.updateSelfMaxStage(player, dungeonID) && MSConditionJudger.fromStageIDToLayerID(stageID) == STAGE_COUNT_EACH_CHATPER){
				MagicChapterCfg mcCfg = MagicChapterCfgDAO.getInstance().getCfgById(String.valueOf(chapterID));
				for(int i = 0; i < mcCfg.getPassBonus().size(); i++){
					MSItemInfo.Builder msItem = MSItemInfo.newBuilder();
					ItemInfo item = mcCfg.getPassBonus().get(i);
					msItem.setItemID(String.valueOf(item.getItemID()));
					msItem.setItemCount(item.getItemNum());
					msRsp.addRewardData(msItem.build());
				}
				MSInnerProcessor.handleDropItem(player, mcCfg.getPassBonus());
				msRsp.setIsFirstFinish(true);
			}else msRsp.setIsFirstFinish(false);
			// 如果闯完一章节，初始化下一章节的内容（如果不是，就准备下一关卡）
			if(MSConditionJudger.fromStageIDToLayerID(stageID) == STAGE_COUNT_EACH_CHATPER)
				MagicChapterInfoHolder.getInstance().initMagicChapterInfo(player, String.valueOf(chapterID + 1), true);
			else handleNextDungeonPrepare(player, dungeonID);
		}
		//清空刚战斗的关卡
		msData.setCurrentDungeonID(null);
		msRsp.setRstType(msResultType.SUCCESS);
		UserMagicSecretHolder.getInstance().update(player);
		MagicChapterInfoHolder.getInstance().updateItem(player, mcInfo);
	}
	
	/**
	 * 副本扫荡
	 * @param player
	 * @param msRsp
	 * @param chapterID
	 */
	public void getMSSweepReward(Player player, MagicSecretRspMsg.Builder msRsp, String chapterID) {
		if(!MSConditionJudger.judgeSweepCount(player, chapterID)) {
			msRsp.setRstType(msResultType.TIMES_NOT_ENOUGH);
			return;
		}
		if(!MSConditionJudger.judgeSweepAble(player, chapterID)) {
			msRsp.setRstType(msResultType.CONDITION_UNREACH);
			return;
		}
		//扫荡之前，如果有没有打开的箱子，则视为放弃，需要清空
		giveUpRewardBox(player, chapterID);
		
		//获取扫荡奖励
		ArrayList<ItemInfo> rewardItems = new ArrayList<ItemInfo>();
		DungeonsDataCfgDAO dungeonsDataCfgDAO = DungeonsDataCfgDAO.getInstance();
		for(int i = 1; i <= STAGE_COUNT_EACH_CHATPER; i++){
			String dungeonID = (Integer.parseInt(chapterID)*100 + i) + "_" + DUNGEON_MAX_LEVEL;
			if(!MSConditionJudger.judgeDungeonsCount(player, dungeonID)) continue;
			DungeonsDataCfg dungDataCfg = dungeonsDataCfgDAO.getCfgById(dungeonID);
			if(dungDataCfg == null) {
				GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("getMSSweepReward, 扫荡章节[%s]时副本[%s]静态数据不存在", chapterID, dungeonID), null);
				continue;
			}
			MSDungeonInfo msdInfo = new MSDungeonInfo(dungeonID, null, null, MSInnerProcessor.generateDropItem(player, dungDataCfg.getDrop()));
			rewardItems.addAll(singleDungeonReward(player, msdInfo, DUNGEON_FINISH_MAX_STAR));
		}
		// 获得的物品
		for(int i = 0; i < rewardItems.size(); i++){	
			MSItemInfo.Builder msItem = MSItemInfo.newBuilder();
			ItemInfo item = rewardItems.get(i);
			msItem.setItemID(String.valueOf(item.getItemID()));
			msItem.setItemCount(item.getItemNum());
			msRsp.addRewardData(msItem.build());
		}
		UserFeatruesMgr.getInstance().doFinish(player, UserFeaturesEnum.magicSecert);
		msRsp.setRstType(msResultType.SUCCESS);
		UserMagicSecretHolder.getInstance().update(player);
		MagicChapterInfoHolder.getInstance().updateItem(player, chapterID);
	}

	/**
	 * 用星星交换章节内的buff
	 * @param player
	 * @param chapterID
	 * @param buffID
	 * @return
	 */
	public msResultType exchangeBuff(Player player, String chapterID, String buffID){
		if(!MSConditionJudger.judgeBuffLegal(player, chapterID, buffID)) return msResultType.DATA_ERROR;
		if(!MSConditionJudger.judgeEnoughStar(player, chapterID, buffID)) return msResultType.NOT_ENOUGH_STAR;
		
		// 将buff从可选列表，转移到已选择列表
		MagicChapterInfo mcInfo = MagicChapterInfoHolder.getInstance().getItem(player.getUserId(), chapterID);
		Iterator<Integer> unselectItor = mcInfo.getUnselectedBuff().iterator();
		while(unselectItor.hasNext()){
			int bID= unselectItor.next();
			if(bID == Integer.parseInt(buffID)) unselectItor.remove();
		}
		mcInfo.getSelectedBuff().add(Integer.parseInt(buffID));
		
		// 扣除星星数
		BuffBonusCfg buffCfg = BuffBonusCfgDAO.getInstance().getCfgById(buffID);
		mcInfo.setStarCount(mcInfo.getStarCount() - buffCfg.getCost());

		UserMagicSecretHolder.getInstance().update(player);
		MagicChapterInfoHolder.getInstance().updateItem(player, mcInfo);
		return msResultType.SUCCESS;
	}
	
	/**
	 * 打开奖励的宝箱
	 * @param player
	 * @param msRsp
	 * @param chapterID
	 * @param msRwdBox
	 */
	public void openRewardBox(Player player, MagicSecretRspMsg.Builder msRsp, String chapterID, MSItemInfo msRwdBox) {
		if(!MSConditionJudger.judgeRewardBoxLegal(player, chapterID, msRwdBox)){
			msRsp.setRstType(msResultType.NO_REWARD_BOX);
			return;
		}
		if(!MSConditionJudger.judgeOpenBoxCost(player, chapterID, msRwdBox)){ // 该方法中包括了资源的扣除
			msRsp.setRstType(msResultType.NOT_ENOUGH_GOLD);
			return;
		}
		// 扣除箱子数量
		MagicChapterInfo mcInfo = MagicChapterInfoHolder.getInstance().getItem(player.getUserId(), chapterID);
		Iterator<ItemInfo> itor = mcInfo.getCanOpenBoxes().iterator();
		while(itor.hasNext()){
			ItemInfo itm = itor.next();
			if(itm.getItemID() == Integer.parseInt(msRwdBox.getItemID())){
				itm.setItemNum(itm.getItemNum() - msRwdBox.getItemCount());
				if(itm.getItemNum() <= 0) itor.remove();
			}
		}
		//箱子打开的物品
		String firstDungeonID = chapterID + "01_1";
		DungeonsDataCfg dungDataCfg = DungeonsDataCfgDAO.getInstance().getCfgById(firstDungeonID);
		String dropStr = "";
		if(msRwdBox.getItemID().equalsIgnoreCase("1")) dropStr = dungDataCfg.getObjCoBox().getDropStr();
		if(msRwdBox.getItemID().equalsIgnoreCase("2")) dropStr = dungDataCfg.getObjHiBox().getDropStr();
		ArrayList<ItemInfo> items = new ArrayList<ItemInfo>();
		for(int i = 0; i < msRwdBox.getItemCount(); i++)
			items.addAll(MSInnerProcessor.generateDropItem(player, dropStr));
		MSInnerProcessor.handleDropItem(player, items);
		for(int i = 0; i < items.size(); i++){
			MSItemInfo.Builder msItem = MSItemInfo.newBuilder();
			ItemInfo item = items.get(i);
			msItem.setItemID(String.valueOf(item.getItemID()));
			msItem.setItemCount(item.getItemNum());
			msRsp.addRewardData(msItem.build());
		}
		msRsp.setRstType(msResultType.SUCCESS);
		UserMagicSecretHolder.getInstance().update(player);
		MagicChapterInfoHolder.getInstance().updateItem(player, mcInfo);
	}
	
	/**
	 * 更改队伍信息
	 * @param player
	 * @param armyInfo
	 * @return
	 */
	public msResultType changeMSArmy(Player player, String armyInfo) {
		UserMagicSecretData umsData = UserMagicSecretHolder.getInstance().get(player);
		umsData.setSecretArmy(armyInfo);
		UserMagicSecretHolder.getInstance().update(player);
		return msResultType.SUCCESS;
	}
	
	/**
	 * 获取积分奖励
	 * @param player
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
		UserMagicSecretData umsData = UserMagicSecretHolder.getInstance().get(player);
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
		MSInnerProcessor.handleDropItem(player, dungScoreCfg.getRewardList());
		for(int i = 0; i < dungScoreCfg.getRewardList().size(); i++){
			MSItemInfo.Builder msItem = MSItemInfo.newBuilder();
			ItemInfo item = dungScoreCfg.getRewardList().get(i);
			msItem.setItemID(String.valueOf(item.getItemID()));
			msItem.setItemCount(item.getItemNum());
			msRsp.addRewardData(msItem.build());
		}
		umsData.getGotScoreReward().add(scoreRewardID);
		msRsp.setRstType(msResultType.SUCCESS);
		UserMagicSecretHolder.getInstance().update(player);
	}
	
	/**
	 * 获取个人排名
	 * @param player
	 * @param msRsp
	 */
	public void getSelfMSRank(Player player, MagicSecretRspMsg.Builder msRsp) {
		msRsp.setSelfRank(MSScoreRankMgr.getRankIndex(player.getUserId()));
		msRsp.setRstType(msResultType.SUCCESS);
	}
	
	/**
	 * 放弃可以开启的箱子
	 * @param player
	 * @param chapterID
	 */
	public msResultType giveUpRewardBox(Player player, String chapterID) {		
		MagicChapterInfo mcInfo = MagicChapterInfoHolder.getInstance().getItem(player.getUserId(), chapterID);
		if(mcInfo == null) return msResultType.DATA_ERROR;
		if(!mcInfo.getCanOpenBoxes().isEmpty()){
			mcInfo.getCanOpenBoxes().clear();
			MagicChapterInfoHolder.getInstance().updateItem(player, mcInfo);
		}
		return msResultType.SUCCESS;
	}
	
	/**
	 * 放弃可以选择的buff
	 * @param player
	 * @param chapterId
	 * @return
	 */
	public msResultType giveBuff(Player player, String chapterId) {
		MagicChapterInfo mcInfo = MagicChapterInfoHolder.getInstance().getItem(player.getUserId(), chapterId);
		if(mcInfo == null) return msResultType.DATA_ERROR;
		if(!mcInfo.getUnselectedBuff().isEmpty()){
			mcInfo.getUnselectedBuff().clear();
			MagicChapterInfoHolder.getInstance().updateItem(player, mcInfo);
		}
		return msResultType.SUCCESS;
	}

	/**
	 * 扣除秘境货币
	 * @param player
	 * @param secretGold
	 * @return
	 */
	public boolean deductSecretGold(Player player, int secretGold){
		if(secretGold < 0) return false;
		UserMagicSecretData umsData = UserMagicSecretHolder.getInstance().get(player);
		int currentGold = umsData.getSecretGold();
		if(currentGold < secretGold) return false;
		umsData.setSecretGold(currentGold - secretGold);
		UserMagicSecretHolder.getInstance().update(player);
		return true;
	}
	
	/**
	 * 获取玩家的法宝秘境货币
	 * @param player
	 * @return
	 */
	public int getSecretGold(Player player){
		UserMagicSecretData umsData = UserMagicSecretHolder.getInstance().get(player);
		return umsData.getSecretGold();
	}
	
	/**
	 * 增加秘境货币
	 * @param player
	 * @param secretGold
	 * @return
	 */
	public boolean addSecretGold(Player player, int secretGold){
		if(secretGold < 0) return deductSecretGold(player, -secretGold);
		UserMagicSecretData umsData = UserMagicSecretHolder.getInstance().get(player);
		umsData.setSecretGold(umsData.getSecretGold() + secretGold);
		UserMagicSecretHolder.getInstance().update(player);
		return true;
	}
	
	/**
	 * 单关奖励
	 * 有些合法性的检查需要在函数外
	 * @param player
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
		String chapterID = String.valueOf(MSConditionJudger.fromStageIDToChapterID(MSConditionJudger.fromDungeonIDToStageID(player, fightingDung.getDungeonKey())));
		MagicChapterInfo mcInfo = MagicChapterInfoHolder.getInstance().getItem(player.getUserId(), chapterID);
		mcInfo.setStarCount(mcInfo.getStarCount() + dungCfg.getStarReward());
		mcInfo.getFinishedStages().add(MSConditionJudger.fromDungeonIDToStageID(player, fightingDung.getDungeonKey()));
		ItemInfo starItem = new ItemInfo();
		starItem.setItemID(MS_STAR_ID);
		starItem.setItemNum(dungCfg.getStarReward());
		result.add(starItem);
		
		// 积分奖励
		UserMagicSecretData umsData = UserMagicSecretHolder.getInstance().get(player);
		int score = (int)(dungCfg.getScore() * getScoreRatio(finishStar));
		umsData.setTodayScore(umsData.getTodayScore() + score);
		ItemInfo scoreItem = new ItemInfo();
		scoreItem.setItemID(MS_SCORE_ID);
		scoreItem.setItemNum(score);
		result.add(scoreItem);
		
		// 排名更改
		MSInnerProcessor.informRankModule(player);
		
		// 掉落物品(需要回传给前端做显示用)
		MSInnerProcessor.handleDropItem(player, fightingDung.getDropItem());
		
		// 增加可以购买的箱子
		MSInnerProcessor.addCanOpenBoxes(player, chapterID);
		
		result.addAll(fightingDung.getDropItem());
		player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.UNENDINGWAR, 1);
		return result;
	}
	
	/**
	 * 处理下一个关卡开始之前的各项准备
	 * @param player
	 * @param dungeonID
	 */
	protected void handleNextDungeonPrepare(Player player, String dungeonID){	
		MSInnerProcessor.provideNextSelectalbeBuff(player, dungeonID);
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
	
	public void synUserMSData(Player player) {
		UserMagicSecretHolder.getInstance().syn(player);
	}
	
	public void synMagicChapterData(Player player) {
		MagicChapterInfoHolder.getInstance().synAllData(player);
	}
	
	/**
	 * 判断副本是否开启
	 * @param player
	 * @param dungeonID
	 * @return
	 */
	public boolean judgeDungeonsCondition(Player player, String dungeonID) {
		return MSConditionJudger.judgeDungeonsCondition(player, dungeonID);
	}
	
	/**
	 * 为下一层生成副本数据
	 * @param player
	 * @param currentDungeonID
	 */
	public void createDungeonsDataForNextStage(Player player, String currentDungeonID) {
		MSInnerProcessor.createDungeonsDataForNextStage(player, currentDungeonID);
	}
	
	/**
	 * 判断玩家等级
	 * @param player
	 * @param chapterID 章节ID
	 * @return
	 */
	public boolean judgeUserLevel(Player player, int chapterID) {
		return MSConditionJudger.judgeUserLevel(player, chapterID);
	}
	
	/**
	 * 法宝秘境数据跨天刷新
	 * @param player
	 */
	public void resetDailyMSInfo(Player player){
		MagicChapterInfoHolder.getInstance().resetAllItem(player);
		UserMagicSecretHolder.getInstance().get(player).saveDailyScoreData();
		MagicChapterInfoHolder.getInstance().synAllData(player);
	}
	
	/**
	 * 用于前端判断红点
	 * @param player
	 * @return
	 */
	public boolean hasScoreReward(Player player){
		List<DungeonScoreCfg> dungScoreCfgList = DungeonScoreCfgDAO.getInstance().getAllCfg();
		UserMagicSecretData umsData = UserMagicSecretHolder.getInstance().get(player);
		if(umsData == null) return false;
		int totalScore = getTotalScore(umsData.getHistoryScore(), umsData.getTodayScore());
		for(DungeonScoreCfg cfg : dungScoreCfgList) {
			if(umsData.getGotScoreReward().contains(cfg.getKey())) continue;
			if(totalScore >= cfg.getScore()) return true;
		}
		return false;
	}
	
	/**
	 * 检查是否某章可以扫荡
	 * 
	 * @param player
	 * @param chapterID
	 * @return
	 */
	public boolean canSweep(Player player, String chapterID) {
		if (!MSConditionJudger.judgeSweepCount(player, chapterID)) {
			return false;
		}

		if (!MSConditionJudger.judgeSweepAble(player, chapterID)) {
			return false;
		}

		return true;
	}

	/**
	 * 获取副本星级对应的得分系数
	 * @param finishStar
	 * @return
	 */
	private static float getScoreRatio(int finishStar){
		switch (finishStar) {
		case 1:
			return ONE_STAR_SCORE_COEFFICIENT;
		case 2:
			return TWO_STAR_SCORE_COEFFICIENT;
		case 3:
			return THREE_STAR_SCORE_COEFFICIENT;
		default:
			return THREE_STAR_SCORE_COEFFICIENT;
		}
	}
}