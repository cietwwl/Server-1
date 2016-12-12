package com.playerdata.mgcsecret.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bm.rank.magicsecret.MSScoreRankMgr;
import com.bm.rank.teaminfo.AngelArrayTeamInfoHelper;
import com.bm.robot.RobotHeroBuilder;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.army.ArmyHero;
import com.playerdata.army.ArmyInfo;
import com.playerdata.mgcsecret.cfg.BuffBonusCfg;
import com.playerdata.mgcsecret.cfg.BuffBonusCfgDAO;
import com.playerdata.mgcsecret.cfg.DungeonsDataCfg;
import com.playerdata.mgcsecret.cfg.DungeonsDataCfgDAO;
import com.playerdata.mgcsecret.cfg.FabaoBuffCfg;
import com.playerdata.mgcsecret.cfg.FabaoBuffCfgDAO;
import com.playerdata.mgcsecret.data.MSDungeonInfo;
import com.playerdata.mgcsecret.data.MagicChapterInfo;
import com.playerdata.mgcsecret.data.MagicChapterInfoHolder;
import com.playerdata.mgcsecret.data.UserMagicSecretData;
import com.playerdata.mgcsecret.data.UserMagicSecretHolder;
import com.playerdata.team.TeamInfo;
import com.rw.fsutil.common.DataAccessTimeoutException;
import com.rw.fsutil.util.RandomUtil;
import com.rw.service.dropitem.DropItemManager;
import com.rwbase.dao.copy.pojo.ItemInfo;

class MSInnerProcessor extends MSConditionJudger {

	/**
	 * 通知排行榜做出排名更改
	 * 
	 * @param player
	 */
	public static void informRankModule(Player player) {
		MSScoreRankMgr.addOrUpdateMSScoreRank(player, UserMagicSecretHolder.getInstance().get(player));
	}

	/**
	 * 处理掉落，这里面包括了秘境货币的特殊处理
	 * 
	 * @param player
	 * @param dropItems
	 */
	public static void handleDropItem(Player player, List<ItemInfo> dropItems) {
		GameLog.info(LogModule.MagicSecret.getName(), player.getUserId(), String.format("handleDropItem, 准备添加物品：%s", dropItems), null);
		ItemBagMgr.getInstance().addItem(player, dropItems);
	}

	/**
	 * 增加可以购买的箱子(普通和高级各一个)
	 * 
	 * @param player
	 * @param chapterID
	 */
	public static void addCanOpenBoxes(Player player, String chapterID) {
		MagicChapterInfo mcInfo = MagicChapterInfoHolder.getInstance().getItem(player.getUserId(), chapterID);
		if (mcInfo == null)
			GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("addCanOpenBoxes, 不合法的章节[%s], 有可能是没开启或不存在", chapterID), null);
		List<ItemInfo> canOpenBoxList = mcInfo.getCanOpenBoxes();
		if (canOpenBoxList.size() != 2)
			canOpenBoxList.clear();
		if (canOpenBoxList.size() == 0) {
			for (int i = 1; i <= 2; i++) {
				ItemInfo box = new ItemInfo();
				box.setItemID(i);
				box.setItemNum(0);
				canOpenBoxList.add(box);
			}
		}
		for (ItemInfo box : canOpenBoxList) {
			box.setItemNum(box.getItemNum() + 1);
		}
	}

	/**
	 * 清除可选的buff
	 * 
	 * @param player
	 * @param chapteID
	 */
	public static void dropSelectableBuff(Player player, String chapteID) {
		MagicChapterInfo mcInfo = MagicChapterInfoHolder.getInstance().getItem(player.getUserId(), chapteID);
		mcInfo.getUnselectedBuff().clear();
	}

	/**
	 * 设置玩家最高闯关纪录
	 * 
	 * @param player
	 * @param dungeonID
	 */
	public static boolean updateSelfMaxStage(Player player, String dungeonID) {
		UserMagicSecretData umsData = UserMagicSecretHolder.getInstance().get(player);
		int paraStageID = fromDungeonIDToStageID(player, dungeonID);
		if (paraStageID > umsData.getMaxStageID()) {
			umsData.setMaxStageID(paraStageID);
			return true;
		}
		return false;
	}

	/**
	 * 提供可以购买的buff
	 * 
	 * @param player
	 * @param currentDungeonID
	 */
	public static void provideNextSelectalbeBuff(Player player, String currentDungeonID) {
		int stageID = fromDungeonIDToStageID(player, currentDungeonID);
		int chapterID = fromStageIDToChapterID(stageID);
		MagicChapterInfo mcInfo = MagicChapterInfoHolder.getInstance().getItem(player.getUserId(), String.valueOf(chapterID));
		if (mcInfo == null) {
			GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("provideNextSelectalbeBuff, 由副本id[%s]获得的章节[%s]信息为空", currentDungeonID, chapterID), null);
			return;
		}
		String nextDungeonID = (stageID + 1) + "_3";
		DungeonsDataCfg dungDataCfg = DungeonsDataCfgDAO.getInstance().getCfgById(nextDungeonID);
		if (dungDataCfg != null) {
			String[] strLayerArr = dungDataCfg.getBuffBonus().split(",");
			BuffBonusCfgDAO buffBonusCfgDAO = BuffBonusCfgDAO.getInstance();
			for (String layerID : strLayerArr) {
				BuffBonusCfg buffCfg = buffBonusCfgDAO.getRandomBuffByLayerID(Integer.parseInt(layerID));
				mcInfo.getUnselectedBuff().add(Integer.parseInt(buffCfg.getKey()));
			}
		} else {
			GameLog.info(LogModule.MagicSecret.getName(), player.getUserId(), String.format("provideNextSelectalbeBuff, 由副本id[%s]已经是本章节最后一个章节", currentDungeonID), null);
		}
	}

	/**
	 * 生成下一个stage的三个关卡数据
	 * 
	 * @param player
	 * @param currentDungeonID
	 */
	public static void createDungeonsDataForNextStage(Player player, String currentDungeonID) {
		int stageID = fromDungeonIDToStageID(player, currentDungeonID);
		int chapterID = fromStageIDToChapterID(stageID);
		MagicChapterInfo mcInfo = MagicChapterInfoHolder.getInstance().getItem(player.getUserId(), String.valueOf(chapterID));
		if (null == mcInfo) {
			GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("provideNextSelectalbeBuff, 由副本id[%s]获得的章节[%s]信息为空", currentDungeonID, chapterID), null);
			return;
		}
		UserMagicSecretData umsData = UserMagicSecretHolder.getInstance().get(player);
		if (null == umsData) {
			GameLog.error(LogModule.MagicSecret, player.getUserId(), "provideNextSelectalbeBuff，玩家乾坤幻境信息为空", null);
			return;
		}
		mcInfo.setSelectedDungeonIndex(-1); // -1表示未选择
		List<MSDungeonInfo> selectableDungeons = new ArrayList<MSDungeonInfo>();
		int nextStageID = stageID + 1;
		DungeonsDataCfgDAO dungeonsDataCfgDAO = DungeonsDataCfgDAO.getInstance();
		for (int i = 1; i <= MagicSecretMgr.DUNGEON_MAX_LEVEL; i++) {
			String dungID = nextStageID + "_" + i;
			DungeonsDataCfg dungDataCfg = dungeonsDataCfgDAO.getCfgById(dungID);
			if (dungDataCfg == null) {
				continue;
			}
			int fighting = 0;
			int fightArea = dungDataCfg.getFightEnd() - dungDataCfg.getFightStart();
			if (0 != fightArea) {
				fighting = RandomUtil.getRandom().nextInt(fightArea) + dungDataCfg.getFightStart();
			}
			MSDungeonInfo msdInfo = new MSDungeonInfo(dungID, provideNextFabaoBuff(dungDataCfg.getFabaoBuff()), generateEnimyForDungeon(dungDataCfg.getEnimy(), fighting), generateDropItem(player, dungDataCfg.getDrop()));
			if (nextStageID > umsData.getMaxStageID() && StringUtils.isNotBlank(dungDataCfg.getFirstDrop())) {
				// 判断是否首掉，如果首掉（并且有首掉配置），就改成首掉物品
				msdInfo.setDropItem(generateDropItem(player, dungDataCfg.getFirstDrop()));
			}

			selectableDungeons.add(msdInfo);
		}
		mcInfo.setSelectableDungeons(selectableDungeons);
	}

	/**
	 * 为下个阶段生成怪物组
	 * 
	 * @param enimyStr
	 * @return
	 */
	private static TeamInfo generateEnimyForDungeon(String enimyStr, int fighting) {
		int robotId = Integer.parseInt(enimyStr);
		TeamInfo buildOnlyHerosTeamInfo = RobotHeroBuilder.buildOnlyHerosTeamInfo(robotId);
		if (fighting > 0) {
			buildOnlyHerosTeamInfo.setTeamFighting(fighting);
			return buildOnlyHerosTeamInfo;
		}
		// 获取战力
		ArmyInfo armyInfo = AngelArrayTeamInfoHelper.getInstance().parseTeamInfo2ArmyInfo(buildOnlyHerosTeamInfo);
		ArmyHero player = armyInfo.getPlayer();
		if (player != null) {
			fighting += player.getFighting();
		}
		List<ArmyHero> heroList = armyInfo.getHeroList();
		if (heroList != null && !heroList.isEmpty()) {
			for (int i = 0, size = heroList.size(); i < size; i++) {
				ArmyHero armyHero = heroList.get(i);
				if (armyHero == null) {
					continue;
				}

				fighting += armyHero.getFighting();
			}
		}
		buildOnlyHerosTeamInfo.setTeamFighting(fighting);
		return buildOnlyHerosTeamInfo;
	}

	/**
	 * 提供下个stage的怪物法宝buff
	 * 
	 * @param fabaoBuffStr
	 * @return
	 */
	private static ArrayList<Integer> provideNextFabaoBuff(String fabaoBuffStr) {
		ArrayList<Integer> resultBuff = new ArrayList<Integer>();
		String[] strLayerArr = fabaoBuffStr.split(",");
		FabaoBuffCfgDAO fabaoBuffCfgDAO = FabaoBuffCfgDAO.getInstance();
		for (String layerID : strLayerArr) {
			FabaoBuffCfg buffCfg = fabaoBuffCfgDAO.getRandomBuffByLayerID(Integer.parseInt(layerID));
			resultBuff.add(Integer.parseInt(buffCfg.getKey()));
		}
		return resultBuff;
	}

	/**
	 * 根据掉落字符串计算物品掉落
	 * 
	 * @param player
	 * @param dropStr
	 * @return
	 */
	public static List<? extends ItemInfo> generateDropItem(Player player, String dropStr) {
		List<Integer> dropList = new ArrayList<Integer>();
		for (String str : dropStr.split(",")) {
			try {
				dropList.add(Integer.parseInt(str));
			} catch (Exception ex) {
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
