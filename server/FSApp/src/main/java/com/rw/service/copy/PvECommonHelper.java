package com.rw.service.copy;

import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.common.RefInt;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Hero;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.activity.rateType.ActivityRateTypeMgr;
import com.playerdata.fightinggrowth.FSuserFightingGrowthMgr;
import com.playerdata.readonly.CopyLevelRecordIF;
import com.playerdata.readonly.ItemInfoIF;
import com.rw.fsutil.common.DataAccessTimeoutException;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rw.service.dropitem.DropItemManager;
import com.rw.service.pve.PveHandler;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.common.userEvent.UserEventMgr;
import com.rwbase.dao.copy.cfg.BuyLevelCfg;
import com.rwbase.dao.copy.cfg.BuyLevelCfgDAO;
import com.rwbase.dao.copy.cfg.CopyCfg;
import com.rwbase.dao.copy.cfg.CopyCfgDAO;
import com.rwbase.dao.copy.itemPrivilege.ItemPrivilegeFactory;
import com.rwbase.dao.copy.itemPrivilege.PrivilegeDescItem;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.copypve.CopyType;
import com.rwbase.dao.copypve.pojo.CopyData;
import com.rwbase.dao.copypve.pojo.CopyInfoCfg;
import com.rwproto.CopyServiceProtos.ERequestType;
import com.rwproto.CopyServiceProtos.EResultType;
import com.rwproto.CopyServiceProtos.MsgCopyRequest;
import com.rwproto.CopyServiceProtos.MsgCopyResponse;
import com.rwproto.CopyServiceProtos.TagSweepInfo;
import com.rwproto.PrivilegeProtos.CopyPrivilegeNames;

public class PvECommonHelper {

	private PvECommonHelper() {
	}

	final public static int SweepTicketID = 806001; // 扫荡券ID...;

	public static List<String> addHerosExp(Player player, MsgCopyRequest copyRequest, CopyCfg copyCfg) {
		List<String> listUpHero = new ArrayList<String>();
		List<String> heroIdList = copyRequest.getTagBattleData().getHeroIdList();
		for (String heroId : heroIdList) {
			// Hero hero = player.getHeroMgr().getHeroById(heroId);
			Hero hero = player.getHeroMgr().getHeroById(player, heroId);
			// int addHeroExp = hero.addHeroExp(copyCfg.getHeroExp());
			int addHeroExp = player.getHeroMgr().addHeroExp(player, hero, copyCfg.getHeroExp());
			if (hero != null && addHeroExp == 1) {
				listUpHero.add(heroId);
			}
		}
		return listUpHero;
	}

	public static void addCopyRewards(Player player, CopyCfg copyCfg, List<? extends ItemInfo> dropItems) {
		// CopyRecordMgr copyRecordMgr = player.getCopyRecordMgr();
		// CopyRewardsIF copyRewards = copyRecordMgr.getCopyRewards();

		int levelId = copyCfg.getLevelID();

		if (dropItems != null) {
			// for (ItemInfoIF item : dropItems) {
			// player.getItemBagMgr().addItem(item.getItemID(), item.getItemNum());
			// }
			List<ItemInfo> itemInfoList = new ArrayList<ItemInfo>(dropItems);
			ItemBagMgr.getInstance().addItem(player, itemInfoList);

			// ActivityExchangeTypeMgr.getInstance().AddItemOfExchangeActivity(player,copyCfg);

			StringBuilder rewardInfo = new StringBuilder();
			rewardInfo.append("成功获取战斗奖励 levelId=").append(levelId).append(" rewards:").append(JsonUtil.writeValue(dropItems));
			GameLog.info(LogModule.COPY.getName(), player.getUserId(), rewardInfo.toString(), null);
		}
	}

	/** 手动副本经验金币增加 */
	public static void addPlayerAttr4Battle(Player player, CopyCfg copyCfg) {
		// int multiple = ActivityRateTypeMgr.getInstance().checkEnumIsExistAndActivityIsOpen(player,copyCfg.getLevelType(), 1);
		// int multiplecoin = ActivityRateTypeMgr.getInstance().checkEnumIsExistAndActivityIsOpen(player,copyCfg.getLevelType(), 2);

		Map<Integer, Integer> map = ActivityRateTypeMgr.getInstance().getEspecialItemtypeAndEspecialWithTime(player, copyCfg.getLevelType());
		int multiplePower = 1 + ActivityRateTypeMgr.getInstance().getMultiple(map, eSpecialItemId.Power.getValue());
		int multiplePlayerExp = 1 + ActivityRateTypeMgr.getInstance().getMultiple(map, eSpecialItemId.PlayerExp.getValue());
		int multipleCoin = 1 + ActivityRateTypeMgr.getInstance().getMultiple(map, eSpecialItemId.Coin.getValue());

		List<PrivilegeDescItem> totalPriv = new ArrayList<PrivilegeDescItem>();
		if (multipleCoin - 1 >= 0.001f || multipleCoin - 1 <= -0.001f) {
			PrivilegeDescItem privDescItem_Coin = new PrivilegeDescItem(eSpecialItemId.Coin.getValue(), multipleCoin - 1);
			totalPriv.add(privDescItem_Coin);
		}
		if (multiplePlayerExp - 1 >= 0.001f || multiplePlayerExp - 1 <= -0.001f) {
			PrivilegeDescItem privDescItem_Exp = new PrivilegeDescItem(eSpecialItemId.PlayerExp.getValue(), multiplePlayerExp - 1);
			totalPriv.add(privDescItem_Exp);
		}
		if (CopyCfgDAO.getInstance().isNormalOrElite(copyCfg)) {
			List<? extends PrivilegeDescItem> privList = FSuserFightingGrowthMgr.getInstance().getPrivilegeDescItem(player);
			if (null != privList && !privList.isEmpty()) {
				totalPriv.addAll(privList);
			}
		}
		ArrayList<ItemInfo> dropItem = new ArrayList<ItemInfo>();
		ItemInfo exp = new ItemInfo(eSpecialItemId.PlayerExp.getValue(), copyCfg.getPlayerExp());
		ItemInfo coin = new ItemInfo(eSpecialItemId.Coin.getValue(), copyCfg.getCoin());
		dropItem.add(exp);
		dropItem.add(coin);
		if (!totalPriv.isEmpty()) {
			System.currentTimeMillis();
			ArrayList<ItemInfo> dropItemUnderBuff = new ArrayList<ItemInfo>();
			for (ItemInfo iteminfo : dropItem) {
				ItemInfoIF newItemIF = ItemPrivilegeFactory.createPrivilegeItem(iteminfo, totalPriv);
				dropItemUnderBuff.add(ItemPrivilegeFactory.getItemInfo(newItemIF));
			}
			dropItem = dropItemUnderBuff;
		}
		ItemBagMgr.getInstance().addItem(player, dropItem);
		ItemBagMgr.getInstance().addItem(player, eSpecialItemId.Power.getValue(), -(copyCfg.getSuccSubPower() - copyCfg.getFailSubPower()) * multiplePower);
		// player.getItemBagMgr().addItem(eSpecialItemId.PlayerExp.getValue(), copyCfg.getPlayerExp()*multiplePlayerExp);
		// player.getItemBagMgr().addItem(eSpecialItemId.Coin.getValue(), copyCfg.getCoin()*multipleCoin);
	}

	/** 副本扫荡经验增加 */
	public static void addPlayerAttr4Sweep(Player player, CopyCfg copyCfg, int times) {
		// int multiple = ActivityRateTypeMgr.getInstance().checkEnumIsExistAndActivityIsOpen(player,copyCfg.getLevelType(), 1);
		// int multiplecoin = ActivityRateTypeMgr.getInstance().checkEnumIsExistAndActivityIsOpen(player,copyCfg.getLevelType(), 2);

		Map<Integer, Integer> map = ActivityRateTypeMgr.getInstance().getEspecialItemtypeAndEspecialWithTime(player, copyCfg.getLevelType());
		int multiplePower = 1 + ActivityRateTypeMgr.getInstance().getMultiple(map, eSpecialItemId.Power.getValue());
		int multiplePlayerExp = 1 + ActivityRateTypeMgr.getInstance().getMultiple(map, eSpecialItemId.PlayerExp.getValue());
		int multipleCoin = 1 + ActivityRateTypeMgr.getInstance().getMultiple(map, eSpecialItemId.Coin.getValue());

		List<PrivilegeDescItem> totalPriv = new ArrayList<PrivilegeDescItem>();
		if (multipleCoin - 1 >= 0.001f || multipleCoin - 1 <= -0.001f) {
			PrivilegeDescItem privDescItem_Coin = new PrivilegeDescItem(eSpecialItemId.Coin.getValue(), multipleCoin - 1);
			totalPriv.add(privDescItem_Coin);
		}
		if (multiplePlayerExp - 1 >= 0.001f || multiplePlayerExp - 1 <= -0.001f) {
			PrivilegeDescItem privDescItem_Exp = new PrivilegeDescItem(eSpecialItemId.PlayerExp.getValue(), multiplePlayerExp - 1);
			totalPriv.add(privDescItem_Exp);
		}
		if (CopyCfgDAO.getInstance().isNormalOrElite(copyCfg)) {
			List<? extends PrivilegeDescItem> privList = FSuserFightingGrowthMgr.getInstance().getPrivilegeDescItem(player);
			if (null != privList && !privList.isEmpty()) {
				totalPriv.addAll(privList);
			}
		}

		ArrayList<ItemInfo> dropItem = new ArrayList<ItemInfo>();
		// 总的乘以一个倍数吧，现在10连扫都只掉落一次了
		ItemInfo exp = new ItemInfo(eSpecialItemId.PlayerExp.getValue(), copyCfg.getPlayerExp() * times);
		ItemInfo coin = new ItemInfo(eSpecialItemId.Coin.getValue(), copyCfg.getCoin() * times);
		dropItem.add(exp);
		dropItem.add(coin);

		if (!totalPriv.isEmpty()) {
			ArrayList<ItemInfo> dropItemUnderBuff = new ArrayList<ItemInfo>();
			for (ItemInfo iteminfo : dropItem) {
				ItemInfoIF newItemIF = ItemPrivilegeFactory.createPrivilegeItem(iteminfo, totalPriv);
				ItemInfo newItem = ItemPrivilegeFactory.getItemInfo(newItemIF);
				newItem.setItemNum(newItem.getItemNum());
				dropItemUnderBuff.add(newItem);
			}
			dropItem = dropItemUnderBuff;
		}
		ItemBagMgr.getInstance().addItem(player, dropItem);
		ItemBagMgr.getInstance().addItem(player, eSpecialItemId.Power.getValue(), -copyCfg.getSuccSubPower() * times * multiplePower);
		// player.getItemBagMgr().addItem(eSpecialItemId.PlayerExp.getValue(), copyCfg.getPlayerExp() * times*multiplePlayerExp);
		// player.getItemBagMgr().addItem(eSpecialItemId.Coin.getValue(), copyCfg.getCoin() * times*multipleCoin);
	}

	public static List<TagSweepInfo> gainSweepRewards(Player player, int times, CopyCfg copyCfg) {
		List<TagSweepInfo> listSweepInfo = new ArrayList<TagSweepInfo>();
		IntObjectHashMap<RefInt> results = new IntObjectHashMap<RefInt>(23);
		// 物品掉落
		for (int i = 0; i < times; i++) {
			List<ItemInfo> dropItems = null;
			try {
				// 预生成奖励
				DropItemManager.getInstance().pretreatDrop(player, copyCfg);
				// 提取预生成的奖励
				dropItems = new ArrayList<ItemInfo>(DropItemManager.getInstance().extractDropPretreatment(player, copyCfg.getLevelID()));
			} catch (DataAccessTimeoutException e) {
				GameLog.error("生成掉落列表异常：" + player.getUserId() + "," + copyCfg.getLevelID(), e);
			}

			if (dropItems != null) {
				TagSweepInfo.Builder tagsweepInfo = TagSweepInfo.newBuilder();
				tagsweepInfo.setTimes(i + 1);
				List<String> listItem = new ArrayList<String>();
				// TODO 这种拼接的方式浪费性能+不好维护，客户端配合一起改
				for (ItemInfo item : dropItems) {
					// 将奖励放入协议中，发送给客户端
					int itemId = item.getItemID();
					int itemNum = item.getItemNum();
					listItem.add(itemId + "," + itemNum);

					RefInt refInt = results.get(itemId);
					if (refInt == null) {
						results.put(itemId, new RefInt(itemNum));
					} else {
						refInt.value += itemNum;
					}
					// 将奖励放入背包
					// player.getItemBagMgr().addItem(item.getItemID(), item.getItemNum());
				}

				// Map<Integer, Integer> map = ActivityExchangeTypeMgr.getInstance().AddItemOfExchangeActivity(player,copyCfg);
				// for(Map.Entry<Integer, Integer> entry:map.entrySet()){
				// listItem.add(entry.getKey()+","+entry.getValue());
				// }

				tagsweepInfo.addAllTagItemList(listItem);
				listSweepInfo.add(tagsweepInfo.build());

			}
		}
		ArrayList<ItemInfo> infos = new ArrayList<ItemInfo>(results.size());
		for (Entry<RefInt> entry : results.entries()) {
			ItemInfo info = new ItemInfo(entry.key(), entry.value().value);
			infos.add(info);
		}
		if (!ItemBagMgr.getInstance().addItem(player, infos)) {
			GameLog.error("PvECommonHelper", "", "扫荡十次掉落失败：" + player.getUserId() + "," + infos);
		}

		String extraRewards = copyCfg.getExtraRewards();
		if (extraRewards != null && !extraRewards.isEmpty()) {
			try {
				int extraItem = Integer.parseInt(extraRewards);
				int extraCount = Integer.parseInt(copyCfg.getExtraRewardsNum());
				extraCount = extraCount * times;
				ItemBagMgr.getInstance().addItem(player, extraItem, extraCount);
			} catch (Exception e) {
				GameLog.error("PvECommonHelper", "#gainSweepRewards()", "扫荡额外掉落异常：" + player.getUserId(), e);
			}
		}

		return listSweepInfo;
	}

	/*
	 * 购买关卡,今天购买几次已经需要耗费多少钻石统统在客户端计算...
	 */
	public static ByteString buyLevel(MsgCopyRequest copyRequest, Player player) {
		MsgCopyResponse.Builder copyResponse = MsgCopyResponse.newBuilder().setRequestType(ERequestType.BUY_LEVEL);
		int levelId = copyRequest.getLevelId();
		List<String> listLevelRecord = new ArrayList<String>();
		CopyLevelRecordIF copyLevelData = player.getCopyRecordMgr().getLevelRecord(levelId);
		if (copyLevelData == null) {
			player.NotifyCommonMsg(CommonTip.STAGE_NOT_OPEN);
			return copyResponse.setEResultType(EResultType.NOT_OPEN).build().toByteString();// 关卡未开放
		}
		BuyLevelCfg pCfgBuyLevel = (BuyLevelCfg) BuyLevelCfgDAO.getInstance().getCfgById(String.valueOf(copyLevelData.getBuyCount() + 1)); // 购买关卡的配置...
		// PrivilegeCfg pPrivilege = PrivilegeCfgDAO.getInstance().getCfg(player.getVip());
		int resetCopyTimes = player.getPrivilegeMgr().getIntPrivilege(CopyPrivilegeNames.eliteResetCnt);
		if (copyLevelData.getBuyCount() >= resetCopyTimes) {
			player.NotifyCommonMsg(CommonTip.VIP_NOT_ENOUGH);
			copyResponse.setEResultType(EResultType.LOW_VIP); // VIP等级不足...
			return copyResponse.build().toByteString();
		}

		if (player.getUserGameDataMgr().getGold() >= pCfgBuyLevel.getNeedPurse()) // 钻石够的话...
		{
			ItemBagMgr.getInstance().addItem(player, eSpecialItemId.Gold.getValue(), -pCfgBuyLevel.getNeedPurse());

			String buyLevelRecord = player.getCopyRecordMgr().buyLevel(levelId);
			UserEventMgr.getInstance().ResetElityVitality(player, 1);
			listLevelRecord.add(buyLevelRecord);
			copyResponse.setEResultType(EResultType.PURCHASE_SUCCESS);
			copyResponse.addAllTagCopyLevelRecord(listLevelRecord);
			return copyResponse.build().toByteString();
		} else {
			player.NotifyCommonMsg(CommonTip.GOLD_NOT_ENOUGH);
			copyResponse.setEResultType(EResultType.NOT_ENOUGH_DIAMOND);// 钻石不足...
		}
		return copyResponse.build().toByteString();
	}

	public static EResultType checkLimit(Player player, CopyLevelRecordIF copyRecord, CopyCfg copyCfg, int times) {
		if (!player.getCopyRecordMgr().isOpen(copyCfg)) {
			player.NotifyCommonMsg(CommonTip.STAGE_NOT_OPEN);
			return EResultType.NOT_OPEN;
		}
		if (isPowerLimit(player, copyCfg, times)) {
			player.NotifyCommonMsg(CommonTip.POWER_NOT_ENOUGH);
			return EResultType.NOT_ENOUGH_HP;
		}
		if (times <= 0) {
			player.NotifyCommonMsg(CommonTip.TIMES_NOT_ENOUGH);
			return EResultType.NOT_ENOUGH_TIMES;
		}
		if (isTimesLimit(player, copyCfg, copyRecord, times)) {
			player.NotifyCommonMsg(CommonTip.TIMES_NOT_ENOUGH);
			return EResultType.NOT_ENOUGH_TIMES;
		}
		try {
			int levelType = copyCfg.getLevelType();
			CopyInfoCfg infoCfg = player.getCopyDataMgr().getCopyInfoCfgByLevelID(String.valueOf(copyCfg.getLevelID()));
			if (infoCfg != null) {
				CopyData copyData = player.getCopyDataMgr().getByInfoId(infoCfg.getId());
				if (copyData != null && copyData.getResetCount() <= 0 && PveHandler.getInstance().getRemainSeconds(player, copyData.getLastChallengeTime(), System.currentTimeMillis(), levelType) > 0) {
					player.NotifyCommonMsg(CommonTip.COOL_DOWN);
					return EResultType.NOT_ENOUGH_TIMES;
				}
			}

		} catch (Exception e) {
			GameLog.error("PvECommonHelper", "#checkLimit()", "", e);
		}
		return EResultType.NONE;
	}

	private static boolean isPowerLimit(Player player, CopyCfg copyCfg, int times) {
		return (copyCfg.getSuccSubPower() - copyCfg.getFailSubPower()) * times > player.getUserGameDataMgr().getPower();
	}

	private static boolean isTimesLimit(Player player, CopyCfg copyCfg, CopyLevelRecordIF copyRecord, int times) {

		boolean isLimit = false;
		int levelType = copyCfg.getLevelType();
		if (levelType == CopyType.COPY_TYPE_TRIAL_JBZD || levelType == CopyType.COPY_TYPE_TRIAL_LQSG || levelType == CopyType.COPY_TYPE_CELESTIAL) {
			// 历史原因 ， 聚宝之地 练气山谷 生存幻境 的次数保存在copydata,
			isLimit = player.getCopyDataMgr().getCopyCount(String.valueOf(copyCfg.getLevelID())) - times < 0;
		} else if (levelType == CopyType.COPY_TYPE_ELITE) {
			// 精英和普通副本保存在copylevelRecord
			isLimit = copyRecord.getCurrentCount() + times > copyCfg.getResetNum();
		} else {
			// do nothing 另外的pve都有自己的次数判断,普通副本不做限制
		}

		return isLimit;
	}

	public static void deduceSweepCost(Player player, MsgCopyRequest copyRequest, MsgCopyResponse.Builder copyResponse, int times) {
		ERequestType requestType = copyRequest.getRequestType();
		int ticketCount = ItemBagMgr.getInstance().getItemCountByModelId(player.getUserId(), PvECommonHelper.SweepTicketID);
		if (requestType == ERequestType.SWEEP_LEVEL_DIAMOND) { // 钻石扫荡
			if (times > ticketCount) {
				ItemBagMgr.getInstance().useItemByCfgId(player, PvECommonHelper.SweepTicketID, ticketCount);
				player.getUserGameDataMgr().addGold(-(times - ticketCount));
				// UserEventMgr.getInstance().UseSweepTicketVitality(player, ticketCount);
			} else {
				player.getUserGameDataMgr().addGold(-times);
			}
		} else if (copyRequest.getRequestType() == ERequestType.SWEEP_LEVEL_TICKET) { // 扫荡券扫荡
			ItemBagMgr.getInstance().useItemByCfgId(player, PvECommonHelper.SweepTicketID, times);
		}
		// 扫荡时消耗的钻石，也算消耗的扫荡券
		UserEventMgr.getInstance().UseSweepTicketVitality(player, times);
	}

}
