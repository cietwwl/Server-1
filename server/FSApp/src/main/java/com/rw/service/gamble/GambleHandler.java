package com.rw.service.gamble;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import com.common.HPCUtil;
import com.common.RefBool;
import com.common.RefInt;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.UserGameDataMgr;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rw.service.gamble.datamodel.GambleAdwardItem;
import com.rw.service.gamble.datamodel.GambleDropCfgHelper;
import com.rw.service.gamble.datamodel.GambleDropGroup;
import com.rw.service.gamble.datamodel.GambleDropHistory;
import com.rw.service.gamble.datamodel.GambleHistoryRecord;
import com.rw.service.gamble.datamodel.GambleHotHeroPlan;
import com.rw.service.gamble.datamodel.GamblePlanCfg;
import com.rw.service.gamble.datamodel.GamblePlanCfgHelper;
import com.rw.service.gamble.datamodel.GambleRecord;
import com.rw.service.gamble.datamodel.GambleRecordDAO;
import com.rw.service.gamble.datamodel.HotGambleCfgHelper;
import com.rw.service.gamble.datamodel.IDropGambleItemPlan;
import com.rw.service.role.MainMsgHandler;
import com.rwbase.common.userEvent.UserEventMgr;
import com.rwproto.GambleServiceProtos.EGambleResultType;
import com.rwproto.GambleServiceProtos.EGambleType;
import com.rwproto.GambleServiceProtos.GambleRequest;
import com.rwproto.GambleServiceProtos.GambleResponse;
import com.rwproto.GambleServiceProtos.GambleRewardData;

public class GambleHandler {
	public static final int HotHeroPoolSize = 3;

	public Random getRandom() {
		return HPCUtil.getRandom();
	}

	protected GambleHandler() {
	}

	private static GambleHandler instance = new GambleHandler();

	public static GambleHandler getInstance() {
		return instance;
	}

	public ByteString gamble(GambleRequest request, Player player) {
		GambleResponse.Builder response = GambleResponse.newBuilder();
		response.setRequest(request);

		int gamblePlanId = request.getGamblePlanId();
		String planIdStr = String.valueOf(gamblePlanId);
		GamblePlanCfg planCfg = GamblePlanCfgHelper.getInstance().getConfig(gamblePlanId, player.getLevel());
		if (planCfg == null) {
			return GambleLogicHelper.SetError(response, player, String.format("找不到配置:ID=%s,level=%d", planIdStr, player.getLevel()), "没有配置");
		}

		if (player.getLevel() < planCfg.getOpenLevel()) {
			return GambleLogicHelper.SetError(response, player, String.format("等级不足，配置:%s", planIdStr), String.format("%d级开放", planCfg.getOpenLevel()));
		}

		if (player.getVip() < planCfg.getOpenVipLevel()) {
			return GambleLogicHelper.SetError(response, player, String.format("VIP等级不足，配置:%s", planIdStr), String.format("VIP%d级开放", planCfg.getOpenVipLevel()));
		}

		if (planCfg.getDropItemCount() <= 0) {
			return GambleLogicHelper.SetError(response, player, String.format("无效掉落物品数量，配置:%s", planIdStr), "无法抽卡");
		}

		ArrayList<GambleAdwardItem> dropList = new ArrayList<GambleAdwardItem>(10);
		UserGameDataMgr userGameDataMgr = player.getUserGameDataMgr();
		GambleRecordDAO gambleRecords = GambleRecordDAO.getInstance();
		String userId = player.getUserId();
		GambleRecord record = gambleRecords.getOrCreate(userId);
		GambleDropHistory historyRecord = record.getHistory(gamblePlanId);
		GambleHistoryRecord historyByGroup = record.getByGroup(planCfg);
		// final int oldCount = historyByGroup.getChargeGambleHistory().size();
		IDropGambleItemPlan dropPlan;// 免费或者收费方案组
		boolean isFree = historyRecord.canUseFree(planCfg);

		if (isFree) {// 使用免费方案
			dropPlan = planCfg.getFreePlan();
		} else {// 使用收费方案
			if (!userGameDataMgr.isEnoughCurrency(planCfg.getMoneyType(), planCfg.getMoneyNum())) {
				return GambleLogicHelper.SetError(response, player, String.format("金钱不足，配置:%s", planIdStr), "金钱不足");
			}
			dropPlan = planCfg.getChargePlan();
		}

		Random ranGen = getRandom();
		String defaultItem = String.valueOf(planCfg.getGoods());

		RefBool hasFirst = new RefBool();
		StringBuilder trace = gamblePlanId == 5 ? new StringBuilder() : null;

		dropPlan = coreLogic(player, gamblePlanId, planIdStr, planCfg, dropList, historyRecord, historyByGroup, dropPlan, isFree, ranGen, defaultItem, trace, hasFirst);

		// 扣钱
		if (!isFree) {// 使用收费方案
			if (!userGameDataMgr.deductCurrency(planCfg.getMoneyType(), planCfg.getMoneyNum())) {
				return GambleLogicHelper.SetError(response, player, String.format("金钱不足，配置:%s", planIdStr), "金钱不足");
			}
		}

		int adjustCount = dropList.size();
		if (hasFirst.value && adjustCount > 0) {
			adjustCount--;
		}
		// 调整同类型的保底次数
		record.adjustCountOfSameGroup(planCfg, dropPlan, adjustCount);

		// 必掉经验丹，个数跟掉落物品个数一样
		ItemBagMgr itemBagMgr = ItemBagMgr.getInstance();
		itemBagMgr.addItem(player, planCfg.getGoods(), planCfg.getDropItemCount());
		// 保存到背包，并发送跑马灯数据
		{
			String reward = "";
			for (int i = 0; i < dropList.size(); i++) {
				GambleAdwardItem rewardData = dropList.get(i);
				if (rewardData.getItemId().indexOf("_") != -1) {// 佣兵
					player.getHeroMgr().addHero(player, rewardData.getItemId());
					MainMsgHandler.getInstance().sendPmdJtYb(player, rewardData.getItemId());
				} else {
					reward += "," + rewardData.getItemId() + "~" + rewardData.getItemNum();
					MainMsgHandler.getInstance().sendPmdJtGoods(player, rewardData.getItemId());
				}
			}
			itemBagMgr.addItemByPrizeStr(player, reward);
		}

		// 保存到历史
		if (!gambleRecords.commit(record)) {
			GameLog.error("钓鱼台", userId, "更新历史纪录失败,table:gamble_record");
		}

		ArrayList<GambleRewardData> tmpDropList = new ArrayList<GambleRewardData>(dropList.size());
		for (int i = 0; i < dropList.size(); i++) {
			GambleAdwardItem tmpItem = dropList.get(i);
			GambleRewardData.Builder b = GambleRewardData.newBuilder();
			b.setItemId(tmpItem.getItemId());
			b.setItemNum(tmpItem.getItemNum());
			tmpDropList.add(b.build());
		}
		response.addAllItemList(tmpDropList);

		GambleLogicHelper.pushGambleItem(player, defaultItem);

		response.setResultType(EGambleResultType.SUCCESS);
		// 魂匣抽不算入通用活动
		if (request.getGambleType() != EGambleType.ADVANCED) {
			UserEventMgr.getInstance().Gamble(player, planCfg.getDropItemCount(), planCfg.getMoneyType());
		}

		// 抽卡完成后通知任务系统抽卡获得奖品的次数
		player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.Altar, dropList.size());

		return response.build().toByteString();
	}

	/**
	 * 为了给GambleTest调用而开放，其他地方不要调用
	 * 
	 * @param gamblePlanId
	 * @param planIdStr
	 * @param planCfg
	 * @param dropList
	 * @param userId
	 * @param historyRecord
	 * @param historyByGroup
	 * @param dropPlan
	 * @param isFree
	 * @param ranGen
	 * @param defaultItem
	 * @param trace
	 * @return
	 */
	public IDropGambleItemPlan coreLogic(Player player, int gamblePlanId, String planIdStr, GamblePlanCfg planCfg, ArrayList<GambleAdwardItem> dropList, GambleDropHistory historyRecord, GambleHistoryRecord historyByGroup, IDropGambleItemPlan dropPlan, boolean isFree, Random ranGen,
			String defaultItem, StringBuilder trace, RefBool hasFirst) {
		String userId = player.getUserId();

		// 保证热点随机种子初始化
		if (planCfg.getHotCount() > 0 && historyRecord.getHotCheckThreshold() <= 0) {
			historyRecord.GenerateHotCheckCount(getRandom(), planCfg.getHotCheckMin(), planCfg.getHotCheckMax());
		}

		GambleLogicHelper.logTrace(trace, historyRecord, historyByGroup);
		GambleLogicHelper.logTrace(trace, "isFree:" + isFree);

		RefInt slotCount = new RefInt();

		GambleDropCfgHelper gambleDropConfig = GambleDropCfgHelper.getInstance();
		int firstDropItemId = isFree ? planCfg.getFreeFirstDrop() : planCfg.getChargeFirstDrop();
		boolean isFirstTime = historyRecord.isChargeGambleFirstTime();

		if (isFirstTime && firstDropItemId > 0) {// firstDropItemId配置为0表示不想搞首抽必掉
			// 计算首次必掉
			String itemModel = gambleDropConfig.getRandomDrop(player, firstDropItemId, slotCount);
			if (StringUtils.isBlank(itemModel) || slotCount.value <= 0) {
				// 首抽配置错误，在日志纪录并跳过首抽
				// return SetError(response,player,String.format("首抽配置无效，配置:%s", planIdStr),"首抽未配置");
				GameLog.error("钓鱼台", userId, String.format("首抽配置无效，配置:%s", planIdStr));
			} else if (GambleLogicHelper.add2DropList(dropList, slotCount.value, itemModel, userId, planIdStr, defaultItem)) {
				historyRecord.add(isFree, itemModel, slotCount.value, historyByGroup, true);
				historyRecord.clearGuaranteeHistory(false, dropPlan, trace, historyByGroup);
				hasFirst.value = true;
			}
		}

		// 判断是否需要使用热点方案
		if (planCfg.getHotCount() > 0) {
			HotGambleCfgHelper hotGambleConfig = HotGambleCfgHelper.getInstance();
			String heroId = hotGambleConfig.getTodayGuanrateeHotHero(slotCount);
			// 特殊容错处理：如果保底英雄有效就作为容错默认值，否则使用必送丹药作为默认值
			String errDefaultModelId = GambleLogicHelper.isValidHeroOrItemId(heroId) ? heroId : defaultItem;

			// TODO 热点是否也需要考虑去重？
			if (historyRecord.getHotHistoryCount() < historyRecord.getHotCheckThreshold() - 1) {
				// 用热点组生成N个英雄
				int hotPlanId = hotGambleConfig.getTodayHotPlanId();
				GambleHotHeroPlan hotPlan = GambleHotHeroPlan.getTodayHotHeroPlan(hotPlanId, HotHeroPoolSize, errDefaultModelId);
				int hotCount = 0;
				while (hotCount < planCfg.getHotCount()) {
					// String itemModel = hotPlan.getRandomDrop(player, slotCount);
					String itemModel = GambleLogicHelper.getRandomGroup(player, hotPlan.getHotPlan(), slotCount);
					if (!GambleLogicHelper.add2DropList(dropList, slotCount.value, itemModel, userId, planIdStr, errDefaultModelId)) {
						GameLog.error("钓鱼台", userId, "热点英雄配置有问题");
					}
					hotCount++;
				}
			} else {
				// 使用热点保底英雄
				if (GambleLogicHelper.add2DropList(dropList, slotCount.value, heroId, userId, planIdStr, errDefaultModelId)) {
					historyRecord.resetHotHistory(ranGen, planCfg.getHotCheckMin(), planCfg.getHotCheckMax());
				}
			}

			// 每次热点英雄的抽取，只要纪录次数即可
			historyRecord.addHotHistoryCount();
		}

		final int maxCount = planCfg.getDropItemCount();
		RefInt selectedDropGroupIndex = new RefInt();
		while (dropList.size() < maxCount) {
			GambleLogicHelper.logTrace(trace, "dropListCount=" + dropList.size());
			if (dropPlan == null) {
				// 最后容错：20个经验单
				GambleLogicHelper.add2DropList(dropList, 20, defaultItem, userId, planIdStr, defaultItem);
				historyRecord.add(isFree, defaultItem, 20, historyByGroup);
				GambleLogicHelper.logTrace(trace, "最后容错：20个经验单,ID=" + defaultItem);
				continue;
			}
			int dropGroupId;
			boolean isGuarantee = false;
			if (historyRecord.passExclusiveCheck(isFree)) {// 前面N次的抽卡必须不一样，之后的就不需要唯一性检查
				GambleLogicHelper.logTrace(trace, "passExclusiveCheck:true");
				if (historyRecord.checkGuarantee(isFree, dropPlan, historyByGroup)) {
					dropGroupId = dropPlan.getGuaranteeGroup(ranGen, selectedDropGroupIndex);
					isGuarantee = true;
					GambleLogicHelper.logTrace(trace, "checkGuarantee:true,dropGroupId=" + dropGroupId);
				} else {
					dropGroupId = dropPlan.getOrdinaryGroup(ranGen, selectedDropGroupIndex);
					GambleLogicHelper.logTrace(trace, "checkGuarantee:false,dropGroupId=" + dropGroupId);
				}
				String itemModel = gambleDropConfig.getRandomDrop(player, dropGroupId, slotCount);
				GambleLogicHelper.logTrace(trace, "random generate itemModel=" + itemModel + ",slotCount=" + slotCount.value);
				if (GambleLogicHelper.add2DropList(dropList, slotCount.value, itemModel, userId, planIdStr, defaultItem)) {
					historyRecord.add(isFree, itemModel, slotCount.value, historyByGroup);
				} else {
					// 有错误，减少最大抽卡数量
					// maxCount --;
					GambleLogicHelper.logTrace(trace, "removeHistoryFromOrdinaryGroup:" + selectedDropGroupIndex.value);
					dropPlan = dropPlan.removeHistoryFromOrdinaryGroup(selectedDropGroupIndex.value);
					GameLog.error("钓鱼台", userId, "严重错误,抽卡失败,itemModel:" + itemModel + ",isFree:" + isFree + ",plan key:" + planCfg.getKey());
				}

			} else {
				GambleLogicHelper.logTrace(trace, "passExclusiveCheck:false");
				List<String> checkHistory = historyRecord.getExculsiveHistory(isFree, dropPlan, historyByGroup);
				GambleLogicHelper.logTrace(trace, "checkHistory:", checkHistory);
				GambleDropGroup tmpGroup = null;
				if (historyRecord.checkGuarantee(isFree, dropPlan, historyByGroup)) {
					tmpGroup = dropPlan.getGuaranteeGroup(ranGen, checkHistory, selectedDropGroupIndex);
					isGuarantee = true;
					GambleLogicHelper.logTrace(trace, "checkGuarantee:true,tmpGroup=", tmpGroup);
				} else {
					tmpGroup = dropPlan.getOrdinaryGroup(ranGen, checkHistory, selectedDropGroupIndex);
					GambleLogicHelper.logTrace(trace, "checkGuarantee:false,tmpGroup=", tmpGroup);
				}

				if (tmpGroup == null) {
					GameLog.error("钓鱼台", userId, "严重错误,无法去重,history:" + checkHistory + ",isFree:" + isFree + ",plan key:" + planCfg.getKey());
					GambleLogicHelper.logTrace(trace, "removeHistoryFromOrdinaryGroup:" + selectedDropGroupIndex.value);
					dropPlan = dropPlan.removeHistoryFromOrdinaryGroup(selectedDropGroupIndex.value);
					continue;
				}

				RefInt tmpWeight = null;
				String itemModel = GambleLogicHelper.getRandomGroup(player, tmpGroup, slotCount, tmpWeight);
				GambleLogicHelper.logTrace(trace, "random generate itemModel=" + itemModel + ",slotCount=" + slotCount.value);
				if (GambleLogicHelper.add2DropList(dropList, slotCount.value, itemModel, userId, planIdStr, defaultItem)) {
					historyRecord.add(isFree, itemModel, slotCount.value, historyByGroup);
					GambleLogicHelper.logTrace(trace, "checkDistinctTag,isFree:" + isFree + ",ExclusiveCount:" + dropPlan.getExclusiveCount());
					historyRecord.checkDistinctTag(isFree, dropPlan.getExclusiveCount(), historyByGroup);
				} else {
					// 有错误，减少最大抽卡数量
					// maxCount --;
					// 删去当前使用的组，该用其他组
					GambleLogicHelper.logTrace(trace, "removeHistoryFromOrdinaryGroup:" + selectedDropGroupIndex.value);
					dropPlan = dropPlan.removeHistoryFromOrdinaryGroup(selectedDropGroupIndex.value);
					GameLog.error("钓鱼台", userId, "严重错误,抽卡失败,itemModel:" + itemModel + ",isFree:" + isFree + ",plan key:" + planCfg.getKey());
				}
			}

			historyRecord.clearGuaranteeHistory(isGuarantee, dropPlan, trace, historyByGroup);
		}

		// System.out.println(trace.toString());
		GambleLogicHelper.testHasHero(dropList, trace, gamblePlanId, userId);
		return dropPlan;
	}

	public ByteString gambleData(GambleRequest request, Player player) {
		// 热点保底默认值 燃灯道人(魂石)
		String defaultItem = "704012";

		GambleResponse.Builder response = GambleLogicHelper.prepareGambleData(request, defaultItem, player);
		return response.build().toByteString();
	}

	public static boolean canGambleFreely(Player player) {
		return GambleLogicHelper.canGambleFreely(player);
	}

	public static boolean isFree(Player player, int dropType) {
		return GambleLogicHelper.isFree(player, dropType);
	}
}
