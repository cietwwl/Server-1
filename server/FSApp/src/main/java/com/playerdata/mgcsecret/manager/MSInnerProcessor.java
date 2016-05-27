package com.playerdata.mgcsecret.manager;

import java.util.ArrayList;
import java.util.List;

import com.bm.rank.magicsecret.MSScoreRankMgr;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ItemBagMgr;
import com.playerdata.mgcsecret.cfg.BuffBonusCfg;
import com.playerdata.mgcsecret.cfg.BuffBonusCfgDAO;
import com.playerdata.mgcsecret.cfg.DungeonsDataCfg;
import com.playerdata.mgcsecret.cfg.DungeonsDataCfgDAO;
import com.playerdata.mgcsecret.data.MSDungeonInfo;
import com.playerdata.mgcsecret.data.MagicChapterInfo;
import com.playerdata.mgcsecret.data.UserMagicSecretData;
import com.rw.fsutil.common.DataAccessTimeoutException;
import com.rw.service.dropitem.DropItemManager;
import com.rwbase.dao.copy.pojo.ItemInfo;


public class MSInnerProcessor extends MSConditionJudger{
	
	/**
	 * 通知排行榜做出排名更改
	 */
	protected void informRankModule(){
		MSScoreRankMgr.addOrUpdateMSScoreRank(userMSHolder.get());
	}
	
	/**
	 * 处理掉落，这里面包括了秘境货币的特殊处理
	 * @param dropItems
	 */
	protected void handleDropItem(List<ItemInfo> dropItems){
		ItemBagMgr bagMgr = m_pPlayer.getItemBagMgr();
		for(ItemInfo itm : dropItems){
			GameLog.info(LogModule.MagicSecret.getName(), userId, String.format("handleDropItem, 准备添加物品[%s]数量[%s]", itm.getItemID(), itm.getItemNum()), null);
			if(!bagMgr.addItem(itm.getItemID(), itm.getItemNum()))
				GameLog.error(LogModule.MagicSecret, userId, String.format("handleDropItem, 添加物品[%s]的时候不成功，有[%s]未添加", itm.getItemID(), itm.getItemNum()), null);
		}
	}
	
	/**
	 * 增加可以购买的箱子(普通和高级各一个)
	 * @param chapterID
	 */
	protected void addCanOpenBoxes(String chapterID){
		MagicChapterInfo mcInfo = mChapterHolder.getItem(userId, chapterID);
		if(mcInfo == null)
			GameLog.error(LogModule.MagicSecret, userId, String.format("addCanOpenBoxes, 不合法的章节[%s], 有可能是没开启或不存在", chapterID), null);
		List<ItemInfo> canOpenBoxList = mcInfo.getCanOpenBoxes();
		if(canOpenBoxList.size() != 2) canOpenBoxList.clear();
		if(canOpenBoxList.size() == 0) {
			for(int i = 1; i <= 2; i++){
				ItemInfo box = new ItemInfo();
				box.setItemID(i);
				box.setItemNum(0);
				canOpenBoxList.add(box);
			}
		}
		for(ItemInfo box : canOpenBoxList){
			box.setItemNum(box.getItemNum() + 1);
		}
	}
	
	/**
	 * 清除可选的buff
	 * @param chapteID
	 */
	protected void dropSelectableBuff(String chapteID){
		MagicChapterInfo mcInfo = mChapterHolder.getItem(userId, chapteID);
		mcInfo.getSelectedBuff().clear();
	}
	
	/**
	 * 设置玩家最高闯关纪录
	 * @param dungeonID
	 */
	protected void updateSelfMaxStage(String dungeonID){
		UserMagicSecretData umsData = userMSHolder.get();
		int paraStageID = fromDungeonIDToStageID(dungeonID);
		if(paraStageID > umsData.getMaxStageID())
			umsData.setMaxStageID(paraStageID);
	}
	
	/**
	 * 获取副本星级对应的得分系数
	 * @param finishStar
	 * @return
	 */
	protected float getScoreRatio(int finishStar){
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

	/**
	 * 提供可以购买的buff
	 * @param currentDungeonID
	 */
	protected void provideNextSelectalbeBuff(String currentDungeonID){
		int stageID = fromDungeonIDToStageID(currentDungeonID);
		int chapterID = fromStageIDToChapterID(stageID);
		MagicChapterInfo mcInfo = mChapterHolder.getItem(userId, String.valueOf(chapterID));
		if(mcInfo == null){
			GameLog.error(LogModule.MagicSecret, userId, String.format("provideNextSelectalbeBuff, 由副本id[%s]获得的章节[%s]信息为空", currentDungeonID, chapterID), null);
			return;
		}
		String nextDungeonID = (stageID + 1) + "_1";
		DungeonsDataCfg dungDataCfg = DungeonsDataCfgDAO.getInstance().getCfgById(nextDungeonID);
		if(dungDataCfg != null) {
			String[] strLayerArr = dungDataCfg.getBuffBonus().split(",");
			for(String layerID : strLayerArr){
				BuffBonusCfg buffCfg = BuffBonusCfgDAO.getInstance().getRandomBuffByLayerID(Integer.parseInt(layerID));
				mcInfo.getUnselectedBuff().add(Integer.parseInt(buffCfg.getKey()));
			}
		}else{
			GameLog.info(LogModule.MagicSecret.getName(), userId, String.format("provideNextSelectalbeBuff, 由副本id[%s]已经是本章节最后一个章节", currentDungeonID), null);
		}
	}
	
	/**
	 * 生成下一个stage的三个关卡数据
	 * @param currentDungeonID
	 */
	public void createDungeonsDataForNextStage(String currentDungeonID){
		int stageID = fromDungeonIDToStageID(currentDungeonID);
		int chapterID = fromStageIDToChapterID(stageID);
		MagicChapterInfo mcInfo = mChapterHolder.getItem(userId, String.valueOf(chapterID));
		if(mcInfo == null){
			GameLog.error(LogModule.MagicSecret, userId, String.format("provideNextSelectalbeBuff, 由副本id[%s]获得的章节[%s]信息为空", currentDungeonID, chapterID), null);
			return;
		}
		mcInfo.setSelectedDungeonIndex(-1);  //-1表示未选择
		List<MSDungeonInfo> selectableDungeons = new ArrayList<MSDungeonInfo>();
		int nextStageID = stageID + 1;	
		for(int i = 1; i <= DUNGEON_MAX_LEVEL; i++){
			String dungID = nextStageID + "_" + i;
			DungeonsDataCfg dungDataCfg = DungeonsDataCfgDAO.getInstance().getCfgById(dungID);
			if(dungDataCfg == null) continue;
			MSDungeonInfo msdInfo = new MSDungeonInfo(dungID, provideNextFabaoBuff(dungDataCfg.getFabaoBuff()), 
					generateEnimyForDungeon(dungDataCfg.getEnimy()), generateDropItem(dungDataCfg.getDrop()));
			selectableDungeons.add(msdInfo);
		}
		mcInfo.setSelectableDungeons(selectableDungeons);
	}
	
	/**
	 * 为下个阶段生成怪物组
	 * @param enimyStr
	 * @return
	 */
	private int generateEnimyForDungeon(String enimyStr){
		return Integer.parseInt(enimyStr);
	}

	/**
	 * 提供下个stage的怪物法宝buff
	 * @param fabaoBuffStr
	 * @return
	 */
	private ArrayList<Integer> provideNextFabaoBuff(String fabaoBuffStr){
		ArrayList<Integer> resultBuff = new ArrayList<Integer>();
		String[] strLayerArr = fabaoBuffStr.split(",");
		for(String layerID : strLayerArr){
			BuffBonusCfg buffCfg = BuffBonusCfgDAO.getInstance().getRandomBuffByLayerID(Integer.parseInt(layerID));
			resultBuff.add(Integer.parseInt(buffCfg.getKey()));
		}
		return resultBuff;
	}
	
	/**
	 * 根据掉落字符串计算物品掉落
	 * @param dropStr
	 * @return
	 */
	protected List<? extends ItemInfo> generateDropItem(String dropStr){
		List<Integer> dropList = new ArrayList<Integer>();
		for(String str : dropStr.split(",")){
			try{
				dropList.add(Integer.parseInt(str));
			}catch(Exception ex){
				GameLog.error(LogModule.MagicSecret, userId, String.format("generateDropItem, 由掉落字符串[%s]转整数的时候出错", dropStr), ex);
			}
		}
		ArrayList<ItemInfo> itemList = new ArrayList<ItemInfo>();
		try {
			return DropItemManager.getInstance().pretreatDrop(m_pPlayer, dropList, -1, false);
		} catch (DataAccessTimeoutException e) {
			GameLog.error(LogModule.MagicSecret, userId, String.format("generateDropItem, 由掉落字符串[%s]计算掉落时出错", dropStr), e);
		}
		return itemList;
	}
	
	public void resetDailyMSInfo(){
		mChapterHolder.resetAllItem(m_pPlayer);
		userMSHolder.get().saveDailyScoreData();
		mChapterHolder.synAllData(m_pPlayer);
	}
}
