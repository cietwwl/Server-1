package com.playerdata.mgcsecret.manager;

import java.util.ArrayList;
import java.util.List;

import com.bm.rank.magicsecret.MSScoreRankMgr;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.mgcsecret.cfg.BuffBonusCfg;
import com.playerdata.mgcsecret.cfg.BuffBonusCfgDAO;
import com.playerdata.mgcsecret.cfg.DungeonsDataCfg;
import com.playerdata.mgcsecret.cfg.DungeonsDataCfgDAO;
import com.playerdata.mgcsecret.data.MSDungeonInfo;
import com.playerdata.mgcsecret.data.MagicChapterInfo;
import com.playerdata.mgcsecret.data.MagicChapterInfoHolder;
import com.playerdata.mgcsecret.data.UserMagicSecretData;
import com.playerdata.mgcsecret.data.UserMagicSecretHolder;
import com.playerdata.team.TeamInfo;
import com.rw.fsutil.common.DataAccessTimeoutException;
import com.rw.service.dropitem.DropItemManager;
import com.rwbase.dao.anglearray.pojo.AngleArrayMatchHelper;
import com.rwbase.dao.copy.pojo.ItemInfo;

class MSInnerProcessor extends MSConditionJudger{
	
	/**
	 * 通知排行榜做出排名更改
	 * @param player
	 */
	public static void informRankModule(Player player){
		MSScoreRankMgr.addOrUpdateMSScoreRank(player, UserMagicSecretHolder.getInstance().get(player));
	}
	
	/**
	 * 处理掉落，这里面包括了秘境货币的特殊处理
	 * @param player
	 * @param dropItems
	 */
	public static void handleDropItem(Player player, List<ItemInfo> dropItems){
		ItemBagMgr bagMgr = player.getItemBagMgr();
		for(ItemInfo itm : dropItems){
			GameLog.info(LogModule.MagicSecret.getName(), player.getUserId(), String.format("handleDropItem, 准备添加物品[%s]数量[%s]", itm.getItemID(), itm.getItemNum()), null);
			if(!bagMgr.addItem(itm.getItemID(), itm.getItemNum()))
				GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("handleDropItem, 添加物品[%s]的时候不成功，有[%s]未添加", itm.getItemID(), itm.getItemNum()), null);
		}
	}
	
	/**
	 * 增加可以购买的箱子(普通和高级各一个)
	 * @param player
	 * @param chapterID
	 */
	public static void addCanOpenBoxes(Player player, String chapterID){
		MagicChapterInfo mcInfo = MagicChapterInfoHolder.getInstance().getItem(player.getUserId(), chapterID);
		if(mcInfo == null)
			GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("addCanOpenBoxes, 不合法的章节[%s], 有可能是没开启或不存在", chapterID), null);
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
	 * @param player
	 * @param chapteID
	 */
	public static void dropSelectableBuff(Player player, String chapteID){
		MagicChapterInfo mcInfo = MagicChapterInfoHolder.getInstance().getItem(player.getUserId(), chapteID);
		mcInfo.getUnselectedBuff().clear();
	}
	
	/**
	 * 设置玩家最高闯关纪录
	 * @param player
	 * @param dungeonID
	 */
	public static boolean updateSelfMaxStage(Player player, String dungeonID){
		UserMagicSecretData umsData = UserMagicSecretHolder.getInstance().get(player);
		int paraStageID = fromDungeonIDToStageID(player, dungeonID);
		if(paraStageID > umsData.getMaxStageID()){
			umsData.setMaxStageID(paraStageID);
			return true;
		}
		return false;
	}
	
	/**
	 * 提供可以购买的buff
	 * @param player
	 * @param currentDungeonID
	 */
	public static void provideNextSelectalbeBuff(Player player, String currentDungeonID){
		int stageID = fromDungeonIDToStageID(player, currentDungeonID);
		int chapterID = fromStageIDToChapterID(stageID);
		MagicChapterInfo mcInfo = MagicChapterInfoHolder.getInstance().getItem(player.getUserId(), String.valueOf(chapterID));
		if(mcInfo == null){
			GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("provideNextSelectalbeBuff, 由副本id[%s]获得的章节[%s]信息为空", currentDungeonID, chapterID), null);
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
			GameLog.info(LogModule.MagicSecret.getName(), player.getUserId(), String.format("provideNextSelectalbeBuff, 由副本id[%s]已经是本章节最后一个章节", currentDungeonID), null);
		}
	}
	
	/**
	 * 生成下一个stage的三个关卡数据
	 * @param player
	 * @param currentDungeonID
	 */
	public static void createDungeonsDataForNextStage(Player player, String currentDungeonID){
		int stageID = fromDungeonIDToStageID(player, currentDungeonID);
		int chapterID = fromStageIDToChapterID(stageID);
		MagicChapterInfo mcInfo = MagicChapterInfoHolder.getInstance().getItem(player.getUserId(), String.valueOf(chapterID));
		if(mcInfo == null){
			GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("provideNextSelectalbeBuff, 由副本id[%s]获得的章节[%s]信息为空", currentDungeonID, chapterID), null);
			return;
		}
		mcInfo.setSelectedDungeonIndex(-1);  //-1表示未选择
		List<MSDungeonInfo> selectableDungeons = new ArrayList<MSDungeonInfo>();
		int nextStageID = stageID + 1;	
		for(int i = 1; i <= MagicSecretMgr.DUNGEON_MAX_LEVEL; i++){
			String dungID = nextStageID + "_" + i;
			DungeonsDataCfg dungDataCfg = DungeonsDataCfgDAO.getInstance().getCfgById(dungID);
			if(dungDataCfg == null) continue;
			MSDungeonInfo msdInfo = new MSDungeonInfo(dungID, provideNextFabaoBuff(dungDataCfg.getFabaoBuff()), 
					generateEnimyForDungeon(dungDataCfg.getEnimy()), generateDropItem(player, dungDataCfg.getDrop()));
			selectableDungeons.add(msdInfo);
		}
		mcInfo.setSelectableDungeons(selectableDungeons);
	}
	
	/**
	 * 为下个阶段生成怪物组
	 * @param enimyStr
	 * @return
	 */
	private static TeamInfo generateEnimyForDungeon(String enimyStr){
		return AngleArrayMatchHelper.getRobotTeamInfo(Integer.parseInt(enimyStr));
	}

	/**
	 * 提供下个stage的怪物法宝buff
	 * @param fabaoBuffStr
	 * @return
	 */
	private static ArrayList<Integer> provideNextFabaoBuff(String fabaoBuffStr){
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
	 * @param player
	 * @param dropStr
	 * @return
	 */
	public static List<? extends ItemInfo> generateDropItem(Player player, String dropStr){
		List<Integer> dropList = new ArrayList<Integer>();
		for(String str : dropStr.split(",")){
			try{
				dropList.add(Integer.parseInt(str));
			}catch(Exception ex){
				GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("generateDropItem, 由掉落字符串[%s]转整数的时候出错", dropStr), ex);
			}
		}
		ArrayList<ItemInfo> itemList = new ArrayList<ItemInfo>();
		try {
			return DropItemManager.getInstance().pretreatDrop(player, dropList, -1, false);
		} catch (DataAccessTimeoutException e) {
			GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("generateDropItem, 由掉落字符串[%s]计算掉落时出错", dropStr), e);
		}
		return itemList;
	}
}
