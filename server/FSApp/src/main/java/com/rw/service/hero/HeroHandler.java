package com.rw.service.hero;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.ItemCfgHelper;
import com.playerdata.Player;
import com.rwbase.common.enu.eTaskFinishDef;
import com.rwbase.common.userEvent.UserEventMgr;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
import com.rwbase.dao.item.pojo.ConsumeCfg;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.user.LevelCfgDAO;
import com.rwbase.dao.user.pojo.LevelCfg;
import com.rwproto.HeroServiceProtos.MsgHeroRequest;
import com.rwproto.HeroServiceProtos.MsgHeroResponse;
import com.rwproto.HeroServiceProtos.TagUseItem;
import com.rwproto.HeroServiceProtos.eHeroResultType;
import com.rwproto.HeroServiceProtos.eHeroType;

public class HeroHandler {
	private static HeroHandler instance = new HeroHandler();

	private HeroHandler() {
	}

	public static HeroHandler getInstance() {
		return instance;
	}

	/**
	 * 召唤佣兵
	 * 
	 * @param player
	 * @param msgHeroRequest
	 * @return
	 */
	public ByteString summonHero(Player player, MsgHeroRequest msgHeroRequest) {
		String userId = player.getUserId();
		MsgHeroResponse.Builder msgHeroResponse = MsgHeroResponse.newBuilder();
		msgHeroResponse.setEventType(eHeroType.SUMMON_HERO);

		String modelId = msgHeroRequest.getHeroModelId();
		RoleCfg pHeroCfg = (RoleCfg) RoleCfgDAO.getInstance().getCfgByModeID(modelId);
		if (pHeroCfg == null) {
			msgHeroResponse.setEHeroResultType(eHeroResultType.DATA_ERROR);
			GameLog.error("召唤佣兵", userId, String.format("客户端传递ModelId为[%s]的佣兵找不到对应的RoleCfg配置", modelId));
			return msgHeroResponse.build().toByteString();
		}

		int summonNumber = pHeroCfg.getSummonNumber();
		int itemCountByModelId = player.getItemBagMgr().getItemCountByModelId(pHeroCfg.getSoulStoneId());
		if (summonNumber > itemCountByModelId) {
			msgHeroResponse.setEHeroResultType(eHeroResultType.NOT_ENOUGH_SOULSTONE);
			GameLog.error("召唤佣兵", userId, String.format("客户端传递ModelId为[%s]的佣兵合成需要[%s],背包中只有[%s],数量不足", modelId, summonNumber, itemCountByModelId));
			return msgHeroResponse.build().toByteString();
		}

		// TODO @modify 2015-08-10 HC
		player.getItemBagMgr().useItemByCfgId(pHeroCfg.getSoulStoneId(), summonNumber);// 减少神魂石
		player.getHeroMgr().addHero(pHeroCfg.getRoleId());// 增加佣兵
		msgHeroResponse.setModerId(modelId);
		msgHeroResponse.setEHeroResultType(eHeroResultType.SUCCESS);

		return msgHeroResponse.build().toByteString();
	}

	/**
	 * 佣兵升星
	 */
	public ByteString upgradeHeroStar(Player player, String roleId) {

		MsgHeroResponse.Builder msgHeroResponse = MsgHeroResponse.newBuilder();
		msgHeroResponse.setEventType(eHeroType.EVOLUTION_HERO);
		Hero role = player.getHeroMgr().getHeroById(roleId);
		int canUpgrade = role.canUpgradeStar();
		RoleCfg heroCfg = role.getHeroCfg();
		switch (canUpgrade) {
		case 0:
			player.getUserGameDataMgr().addCoin(-heroCfg.getUpNeedCoin());
			player.getItemBagMgr().useItemByCfgId(heroCfg.getSoulStoneId(), heroCfg.getRisingNumber());
			RoleCfg nextHeroCfg = (RoleCfg) RoleCfgDAO.getInstance().getCfgById(heroCfg.getNextRoleId());
			// 佣兵升星
			role.setTemplateId(nextHeroCfg.getRoleId());// 佣兵升星
			role.setStarLevel(nextHeroCfg.getStarLevel());
			player.getTaskMgr().AddTaskTimes(eTaskFinishDef.Hero_Star);
			UserEventMgr.getInstance().UpGradeStarDaily(player, 1);
			
			break;
		case -1:
			msgHeroResponse.setEHeroResultType(eHeroResultType.NOT_ENOUGH_SOULSTONE);
			break;
		case -2:
			msgHeroResponse.setEHeroResultType(eHeroResultType.NOT_ENOUGH_COIN);
			break;
		case -3:

			break;
		}
		return msgHeroResponse.build().toByteString();
	}

	public ByteString initHero(Player player, String id) {
		// Hero hero = player.getHeroMgr().initHero(player, id);
		return null;
	}

	/**
	 * 佣兵使用经验丹
	 * 
	 * @param player
	 * @param msgHeroRequest
	 * @return
	 */
	public ByteString useHeroExp(Player player, MsgHeroRequest msgHeroRequest) {
		// 增加使用经验丹等级判断 modify@2015-12-11 by Jamaz
		MsgHeroResponse.Builder msgHeroResponse = MsgHeroResponse.newBuilder();
		msgHeroResponse.setEventType(eHeroType.USE_EXP);

		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.USE_EXP_ITEM, player.getLevel())) {
			GameLog.error("佣兵吃经验卡", player.getUserId(), "还没达到开启的等级", null);
			msgHeroResponse.setEHeroResultType(eHeroResultType.LOW_LEVEL);
			return msgHeroResponse.build().toByteString();
		}

		String heroUUID = msgHeroRequest.getHeroId();
		Hero pHero = player.getHeroMgr().getHeroById(heroUUID);
		if (pHero == null) {
			GameLog.error("佣兵吃经验卡", player.getUserId(), String.format("ID为[%s]的佣兵不存在", heroUUID), null);
			msgHeroResponse.setEHeroResultType(eHeroResultType.HERO_NOT_EXIST);
			return msgHeroResponse.build().toByteString();
		}

		RoleCfg heroCfg = (RoleCfg) RoleCfgDAO.getInstance().getCfgById(pHero.getHeroData().getTemplateId());
		if (heroCfg == null) {
			GameLog.error("佣兵吃经验卡", player.getUserId(), String.format("ID为[%s]模版为[%S]的佣兵模版不存在", heroUUID, pHero.getHeroData().getTemplateId()), null);
			msgHeroResponse.setEHeroResultType(eHeroResultType.DATA_ERROR);
			return msgHeroResponse.build().toByteString();
		}

		int curLevel = pHero.getLevel();
		if (curLevel > player.getLevel()) {
			GameLog.error("佣兵吃经验卡", player.getUserId(), String.format("佣兵等级[%s]角色等级[%s]", curLevel, player.getLevel()), null);
			msgHeroResponse.setEHeroResultType(eHeroResultType.HERO_EXP_FULL);// 佣兵经验已满
			return msgHeroResponse.build().toByteString();
		}

		if (msgHeroRequest.getTagUseItemList().isEmpty()) {
			GameLog.error("佣兵吃经验卡", player.getUserId(), "要使用的道具数量为空", null);
			msgHeroResponse.setEHeroResultType(eHeroResultType.DATA_ERROR);// 没有发送使用任何物品
			return msgHeroResponse.build().toByteString();
		}

		TagUseItem tagUseItem = msgHeroRequest.getTagUseItem(0);
		int itemId = tagUseItem.getSoltId();
		int itemNum = tagUseItem.getNumber();

		int itemCount = player.getItemBagMgr().getItemCountByModelId(itemId);
		if (itemCount <= 0) {
			GameLog.error("佣兵吃经验卡", player.getUserId(), String.format("使用经验卡的模版ID是[%s],背包中的数量是[%s]", itemId, itemCount), null);
			msgHeroResponse.setEHeroResultType(eHeroResultType.EXP_ITEM_NOT_EXIST);// 没有对应的道具
			return msgHeroResponse.build().toByteString();
		}

		ConsumeCfg consumeCfg = ItemCfgHelper.getConsumeCfg(itemId);
		if (consumeCfg == null) {
			GameLog.error("佣兵吃经验卡", player.getUserId(), String.format("使用经验卡的模版ID是[%s]的ConsumeCfg模版不存在", itemId), null);
			msgHeroResponse.setEHeroResultType(eHeroResultType.DATA_ERROR);// 获取到的物品数据不正确
			return msgHeroResponse.build().toByteString();
		}

		int perExpItemAdd = consumeCfg.getValue();// 单个物品增加的经验

		// 当前的角色经验值
		RoleBaseInfo baseInfo = pHero.getRoleBaseInfoMgr().getBaseInfo();
		if (baseInfo == null) {
			GameLog.error("佣兵吃经验卡", player.getUserId(), String.format("佣兵的Id是[%s]不能获取到RoleBaseInfo数据", heroUUID), null);
			msgHeroResponse.setEHeroResultType(eHeroResultType.DATA_ERROR);// 获取到的物品数据不正确
			return msgHeroResponse.build().toByteString();
		}

		long curExp = baseInfo.getExp();// 当前经验
		LevelCfgDAO levelCfgDao = LevelCfgDAO.getInstance();
		LevelCfg levelCfg = levelCfgDao.getByLevel(curLevel);
		if (levelCfg == null) {
			GameLog.error("佣兵吃经验卡", player.getUserId(), String.format("佣兵Id是[%s],当前等级是[%s]", heroUUID, curLevel), null);
			msgHeroResponse.setEHeroResultType(eHeroResultType.DATA_ERROR);// 获取到的物品数据不正确
			return msgHeroResponse.build().toByteString();
		}

		long levelExp = levelCfg.getHeroUpgradeExp();// 升级需要的经验值

		int useCount = 0;// 看下总共需要多少个经验道具
		boolean isAddBtnUse = msgHeroRequest.getIsAddBtnUse();
		if (isAddBtnUse) {// 使用按钮升级
			long needExp = levelExp - curExp;// 当前等级需要的经验值
			// 判断目前是升1级还是10级
			int offLevel = player.getLevel() - curLevel;
			// if (offLevel >= 10) {// 差别大于了10级，肯定是升10级
			// }
			offLevel = offLevel >= 10 ? 10 : 1;
			for (int i = 1; i < offLevel; i++) {
				int tempLevel = curLevel + i;
				LevelCfg cfg = levelCfgDao.getByLevel(tempLevel);
				if (cfg == null) {
					break;
				}

				int levelNeedExp = cfg.getHeroUpgradeExp();
				needExp += levelNeedExp;
			}

			BigDecimal b = new BigDecimal(needExp);
			BigDecimal b0 = new BigDecimal(perExpItemAdd);
			int needCount = b.divide(b0, RoundingMode.CEILING).intValue();
			useCount = needCount >= itemCount ? itemCount : needCount;
			useCount = useCount <= 0 ? 1 : useCount;
		} else {
			useCount = itemNum;
		}

		if (useCount <= 0) {// 使用数量必须大于0
			GameLog.error("佣兵吃经验卡", player.getUserId(), String.format("使用经验卡的模版ID是[%s],使用的数量是[%s]出现了异常", itemId, useCount), null);
			msgHeroResponse.setEHeroResultType(eHeroResultType.DATA_ERROR);// 获取到的物品数据不正确
			return msgHeroResponse.build().toByteString();
		}

		boolean isFull = false;
		int addExp = useCount * perExpItemAdd;
		while (addExp >= (levelExp - curExp)) {
			addExp -= (levelExp - curExp);// 剩余的增加经验
			// 如果下一级是空的，就说明经验已经全部吃满了
			levelCfg = levelCfgDao.getByLevel(curLevel + 1);
			if (levelCfg == null) {
				curExp = levelExp;
				isFull = true;
				break;
			}

			if (curLevel + 1 > player.getLevel()) {// 超过了角色的等级，也直接退出检查
				curExp = levelExp;
				isFull = true;
				break;
			}

			curLevel++;// 增加的等级
			curExp = 0;
			levelExp = levelCfg.getHeroUpgradeExp();
		}

		if (!isFull) {
			curExp += addExp;
			addExp = 0;
		}

		if (player.getItemBagMgr().useItemByCfgId(itemId, useCount)) {// 消耗卡
			pHero.getRoleBaseInfoMgr().setLevelAndExp(curLevel, (int) curExp);

			GameLog.info("佣兵吃经验卡", player.getUserId(), String.format("\n佣兵Id[%s],吞卡后浪费经验值[%s],等级[%s],当前经验[%s]\n吞卡数量[%s][%s]", heroUUID, addExp, curLevel, curExp, itemId, useCount), null);
			msgHeroResponse.setModerId(heroUUID);
			msgHeroResponse.setEHeroResultType(eHeroResultType.SUCCESS);
			return msgHeroResponse.build().toByteString();
		} else {
			GameLog.error("佣兵吃经验卡", player.getUserId(), String.format("\n佣兵Id[%s]需要吞卡的Id是[%s],数量为[%s]失败了", heroUUID, itemId, useCount), null);
			msgHeroResponse.setEHeroResultType(eHeroResultType.DATA_ERROR);
			return msgHeroResponse.build().toByteString();
		}
	}
	/*
	 * public ByteString buyHeroSkill(Player player, MsgHeroRequest msgHeroRequest) { MsgHeroResponse.Builder msgHeroResponse =
	 * MsgHeroResponse.newBuilder().setMsgHeroRequest(msgHeroRequest); PrivilegeCfg privilege = PrivilegeCfgDAO.getInstance().getCfg(player.getVip());
	 * // 未开放购买 if (privilege.getBuySkillPointOpen() == 0) {// 未开放购买getBuySkillPointOpen msgHeroResponse.setEHeroResultType(eHeroResultType.LOW_VIP);
	 * return msgHeroResponse.build().toByteString(); }
	 * 
	 * // 金币不足 int currTimes = player.getHeroBuySkillTimes(); CfgBuySkill cfgBuySkill = CfgBuySkillDAO.getInstance().getCfgBuySkill(currTimes + 1);//
	 * 购买增加技能配置 int leftGoldNum = player.getGold() - cfgBuySkill.getNeedPurse(); if (leftGoldNum < 0) {
	 * msgHeroResponse.setEHeroResultType(eHeroResultType.NOT_ENOUGH_GOLD); return msgHeroResponse.build().toByteString(); }
	 * 
	 * // 技能点上限 player.getSkillMgr().buyHeroSkill(player); msgHeroResponse.setEHeroResultType(eHeroResultType.SUCCESS); return
	 * msgHeroResponse.build().toByteString(); }
	 */
}
