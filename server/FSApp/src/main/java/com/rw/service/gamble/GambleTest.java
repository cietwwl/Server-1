package com.rw.service.gamble;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import com.common.HPCUtil;
import com.common.RefBool;
import com.rw.service.gamble.datamodel.GambleAdwardItem;
import com.rw.service.gamble.datamodel.GambleDropHistory;
import com.rw.service.gamble.datamodel.GambleHistoryRecord;
import com.rw.service.gamble.datamodel.GamblePlanCfg;
import com.rw.service.gamble.datamodel.GamblePlanCfgHelper;
import com.rw.service.gamble.datamodel.IDropGambleItemPlan;

public class GambleTest {
	private static boolean TestGamble = false;
	/**
	 * 仅用于概率测试，请不要调用 测试概率，统计一百万次的情况下，免费/收费的掉落情况，存放在一个文本里面，需要纪录测试时间和配置版本
	 */
	public static void Test() {
		if (!TestGamble) return;
		System.out.println("Gamble Test Start...");

		isTesting = true;
		Random ranGen = HPCUtil.getRandom();
		String userId = "测试概率脚本";

		GambleHandler coreLogic = GambleHandler.getInstance();
		ArrayList<GambleAdwardItem> dropList = new ArrayList<GambleAdwardItem>(10);

		findBug(5, 1, userId, 10000 * 10000, ranGen, 6, coreLogic,dropList);

		int[] gambleTimes = { 10, 10, 10, 20, 30, 50, 100, 100, 100 * 10000, 1000 * 10000 };
		Iterable<GamblePlanCfg> allCfgs = GamblePlanCfgHelper.getInstance().getIterateAllCfg();
		for (GamblePlanCfg cfg : allCfgs) {
			int cfgKey = cfg.getKey();
			int gamblePlanId = cfg.getDropType();// gamblePlanId
			int playerLevel = cfg.getLevelStart();// playerLevel
			for (int index = 0; index < gambleTimes.length; index++) {
				int gambleTime = gambleTimes[index];
				testOneCfg(gamblePlanId, playerLevel, userId, gambleTime, true, ranGen, cfgKey, coreLogic,dropList);
				testOneCfg(gamblePlanId, playerLevel, userId, gambleTime, false, ranGen, cfgKey, coreLogic,dropList);
			}
		}
		isTesting = false;

		System.out.println("Gamble Test End");
	}

	private static boolean isTesting = false;

	public static boolean isGambleTesting() {
		return isTesting;
	}

	private static void findBug(int gamblePlanId, int playerLevel, String userId, int gambleTime, Random ranGen,
			int cfgKey, GambleHandler coreLogic, ArrayList<GambleAdwardItem> dropList) {
		GambleDropHistory historyRecord = new GambleDropHistory();// 临时数据不写入数据库
		GambleHistoryRecord historyByGroup = new GambleHistoryRecord();
		for (int i = 0; i < gambleTime; i++) {
			StringBuilder trace = new StringBuilder();
			HashMap<String, Integer> result = simulateGamble(userId, gamblePlanId, playerLevel, ranGen, historyRecord,
					trace, coreLogic, historyByGroup,dropList);
			test(result, trace);
		}
	}

	// 检查十连抽是否有英雄
	private static void test(HashMap<String, Integer> result, StringBuilder trace) {
		Set<String> heroIdList = result.keySet();
		boolean hasHero = false;
		for (String heroId : heroIdList) {
			if (GambleLogicHelper.isValidHeroId(heroId)) {
				hasHero = true;
				break;
			}
		}
		if (!hasHero) {
			System.out.println("bug: no hero in ten continue gamble");
			System.out.println(trace.toString());
		}
	}

	private static void testOneCfg(int gamblePlanId, int playerLevel, String userId, int gambleTime, boolean isFree,
			Random ranGen, int cfgKey, GambleHandler coreLogic, ArrayList<GambleAdwardItem> dropList) {
		GambleDropHistory historyRecord = new GambleDropHistory();// 临时数据不写入数据库
		GambleHistoryRecord historyByGroup = new GambleHistoryRecord();
		HashMap<String, Integer> collector = new HashMap<String, Integer>();
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < gambleTime; i++) {
			if (!isFree) {
				historyRecord.setFreeCount(100);
				historyRecord.setFirstChargeGamble(false);
			} else {
				historyRecord.setFreeCount(0);
				historyRecord.setLastFreeGambleTime(0);
			}

			HashMap<String, Integer> result = simulateGamble(userId, gamblePlanId, playerLevel, ranGen, historyRecord,
					null, coreLogic, historyByGroup,dropList);
			merge(collector, result);
		}
		long endTime = System.currentTimeMillis();

		FileOutputStream fos;
		try {
			String freeTip = isFree ? "free" : "charge";
			File resultFile = new File("GambleProbability/GambleTest_" + gambleTime + "_" + cfgKey + "_" + freeTip + ".txt");
			resultFile.getParentFile().mkdirs();

			fos = new FileOutputStream(resultFile);
			PrintWriter logger = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"));
			logger.println("use time:" + (endTime - startTime) + "ms");
			logger.println("gamble times:" + gambleTime);
			logger.println("GamblePlanCfg.csv Key:" + cfgKey);
			logger.println("gamblePlanId:" + gamblePlanId);
			logger.println("playerLevel:" + playerLevel);
			logger.println("isFree:" + isFree);

			Set<Entry<String, Integer>> entryList = collector.entrySet();
			for (Entry<String, Integer> entry : entryList) {
				logger.println(entry.getKey() + ":" + entry.getValue());
			}
			logger.close();
			fos.close();
			System.out.println("write log success");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static RefBool hasFirst = new RefBool();
	private static HashMap<String, Integer> simulateGamble(String userId, int gamblePlanId, int playerLevel,
			Random ranGen, GambleDropHistory historyRecord, StringBuilder trace, GambleHandler coreLogic,
			GambleHistoryRecord historyByGroup, ArrayList<GambleAdwardItem> dropList) {
		String planIdStr = String.valueOf(gamblePlanId);
		GamblePlanCfg planCfg = GamblePlanCfgHelper.getInstance().getConfig(gamblePlanId, playerLevel);
		boolean isFree = historyRecord.canUseFree(planCfg);
		String defaultItem = String.valueOf(planCfg.getGoods());
		IDropGambleItemPlan dropPlan;// 免费或者收费方案组
		if (isFree) {// 使用免费方案
			dropPlan = planCfg.getFreePlan();
		} else {// 使用收费方案
			dropPlan = planCfg.getChargePlan();
		}

		HashMap<String, Integer> result = new HashMap<String, Integer>();
		dropList.clear();
		coreLogic.coreLogic(gamblePlanId, planIdStr, planCfg, dropList, userId, historyRecord, historyByGroup, dropPlan,
				isFree, ranGen, defaultItem, trace,hasFirst);
		for (GambleAdwardItem item : dropList) {
			result.put(item.getItemId(), item.getItemNum());
		}

		return result;
	}

	private static void merge(HashMap<String, Integer> collector, HashMap<String, Integer> result) {
		Set<Entry<String, Integer>> added = result.entrySet();
		for (Entry<String, Integer> entry : added) {
			String id = entry.getKey();
			Integer add = entry.getValue();
			Integer old = collector.get(id);
			if (old != null) {
				collector.put(id, old + add);
			} else {
				collector.put(id, add);
			}
		}
	}

	/*
	private static HashMap<String, Integer> coreLogic(String userId, Random ranGen, GambleDropHistory historyRecord,
			StringBuilder trace, String planIdStr, GamblePlanCfg planCfg, boolean isFree, String defaultItem,
			IDropGambleItemPlan dropPlan) {
		RefInt slotCount = new RefInt();

		logTrace(trace, "isFree:" + isFree);
		// 保证热点随机种子初始化
		if (planCfg.getHotCount() > 0 && historyRecord.getHotCheckThreshold() <= 0) {
			historyRecord.GenerateHotCheckCount(ranGen, planCfg.getHotCheckMin(), planCfg.getHotCheckMax());
		}

		RefInt dropListCount = new RefInt();

		GambleDropCfgHelper gambleDropConfig = GambleDropCfgHelper.getInstance();
		int firstDropItemId = isFree ? planCfg.getFreeFirstDrop() : planCfg.getChargeFirstDrop();
		boolean isFirstTime = historyRecord.isChargeGambleFirstTime();

		if (isFirstTime && firstDropItemId > 0) {// firstDropItemId配置为0表示不想搞首抽必掉
			// 计算首次必掉
			String itemModel = gambleDropConfig.getRandomDrop(ranGen, firstDropItemId, slotCount);
			if (StringUtils.isBlank(itemModel) || slotCount.value <= 0) {
				// 首抽配置错误，在日志纪录并跳过首抽
				// return SetError(response,player,String.format("首抽配置无效，配置:%s",
				// planIdStr),"首抽未配置");
				GameLog.error("钓鱼台", userId, String.format("首抽配置无效，配置:%s", planIdStr));
			} else if (add2DropList(dropList, slotCount.value, itemModel, userId, planIdStr, defaultItem,
					dropListCount)) {
				historyRecord.add(isFree, itemModel, slotCount.value);
			}
		}

		// 判断是否需要使用热点方案
		if (planCfg.getHotCount() > 0) {
			HotGambleCfgHelper hotGambleConfig = HotGambleCfgHelper.getInstance();
			String heroId = hotGambleConfig.getTodayGuanrateeHotHero(slotCount);
			// 特殊容错处理：如果保底英雄有效就作为容错默认值，否则使用必送丹药作为默认值
			String errDefaultModelId = GambleLogicHelper.isValidHeroOrItemId(heroId) ? heroId : defaultItem;

			// 热点是否也需要考虑去重？
			if (historyRecord.getHotHistoryCount() < historyRecord.getHotCheckThreshold() - 1) {
				// 用热点组生成N个英雄
				int hotPlanId = hotGambleConfig.getTodayHotPlanId();
				GambleHotHeroPlan hotPlan = GambleHotHeroPlan.getTodayHotHeroPlan(ranGen, hotPlanId,
						GambleHandler.HotHeroPoolSize, errDefaultModelId);
				int hotCount = 0;
				while (hotCount < planCfg.getHotCount()) {
					String itemModel = hotPlan.getRandomDrop(ranGen, slotCount);
					if (!add2DropList(dropList, slotCount.value, itemModel, userId, planIdStr, errDefaultModelId,
							dropListCount)) {
						GameLog.error("钓鱼台", userId, "热点英雄配置有问题");
					}
					hotCount++;
				}
			} else {
				// 使用热点保底英雄
				if (add2DropList(dropList, slotCount.value, heroId, userId, planIdStr, errDefaultModelId,
						dropListCount)) {
					historyRecord.resetHotHistory(ranGen, planCfg.getHotCheckMin(), planCfg.getHotCheckMax());
				}
			}

			// 每次热点英雄的抽取，只要纪录次数即可
			historyRecord.addHotHistoryCount();
		}

		int maxCount = planCfg.getDropItemCount();
		RefInt selectedDropGroupIndex = new RefInt();
		while (dropListCount.value < maxCount) {
			logTrace(trace, "dropListCount=" + dropListCount.value);
			int dropGroupId;
			if (historyRecord.passExclusiveCheck(isFree)) {// 前面N次的抽卡必须不一样，之后的就不需要唯一性检查
				logTrace(trace, "passExclusiveCheck:true");
				if (historyRecord.checkGuarantee(isFree, dropPlan)) {
					dropGroupId = dropPlan.getGuaranteeGroup(ranGen);
					logTrace(trace, "checkGuarantee:true,dropGroupId=" + dropGroupId);
				} else {
					dropGroupId = dropPlan.getOrdinaryGroup(ranGen);
					logTrace(trace, "checkGuarantee:false,dropGroupId=" + dropGroupId);
				}
				String itemModel = gambleDropConfig.getRandomDrop(ranGen, dropGroupId, slotCount);
				logTrace(trace, "random generate itemModel=" + itemModel + ",slotCount=" + slotCount.value);
				if (add2DropList(dropList, slotCount.value, itemModel, userId, planIdStr, defaultItem, dropListCount)) {
					historyRecord.add(isFree, itemModel, slotCount.value);
				} else {
					// 有错误，减少最大抽卡数量
					maxCount--;
					GameLog.error("钓鱼台", userId,
							"严重错误,抽卡失败,itemModel:" + itemModel + ",isFree:" + isFree + ",plan key:" + planCfg.getKey());
				}

			} else {
				logTrace(trace, "passExclusiveCheck:false");
				List<String> checkHistory = historyRecord.getExculsiveHistory(isFree, dropPlan);
				logTrace(trace, "checkHistory:", checkHistory);
				GambleDropGroup tmpGroup = null;
				if (historyRecord.checkGuarantee(isFree, dropPlan)) {
					tmpGroup = dropPlan.getGuaranteeGroup(ranGen, checkHistory, selectedDropGroupIndex);
					logTrace(trace, "checkGuarantee:true,tmpGroup=", tmpGroup);
				} else {
					tmpGroup = dropPlan.getOrdinaryGroup(ranGen, checkHistory, selectedDropGroupIndex);
					logTrace(trace, "checkGuarantee:false,tmpGroup=", tmpGroup);
				}

				if (tmpGroup == null) {
					GameLog.error("钓鱼台", userId, "严重错误,无法去重,history:" + checkHistory + ",isFree:" + isFree
							+ ",plan key:" + planCfg.getKey());
					maxCount--;
					continue;
				}

				RefInt tmpWeight = null;
				String itemModel = tmpGroup.getRandomGroup(ranGen, slotCount, tmpWeight);
				logTrace(trace, "random generate itemModel=" + itemModel + ",slotCount=" + slotCount.value);
				if (add2DropList(dropList, slotCount.value, itemModel, userId, planIdStr, defaultItem, dropListCount)) {
					historyRecord.add(isFree, itemModel, slotCount.value);
					historyRecord.checkDistinctTag(isFree, dropPlan.getExclusiveCount());
				} else {
					// 有错误，减少最大抽卡数量
					maxCount--;
					GameLog.error("钓鱼台", userId,
							"严重错误,抽卡失败,itemModel:" + itemModel + ",isFree:" + isFree + ",plan key:" + planCfg.getKey());
				}
			}

		}

		return dropList;
	}

	private static void logTrace(StringBuilder trace, String log) {
		GambleLogicHelper.logTrace(trace, log);
	}

	private static void logTrace(StringBuilder trace, String log, List<String> checkHistory) {
		GambleLogicHelper.logTrace(trace, log, checkHistory);
	}

	private static void logTrace(StringBuilder trace, String log, GambleDropGroup tmpGroup) {
		GambleLogicHelper.logTrace(trace, log, tmpGroup);
	}

	private static boolean add2DropList(HashMap<String, Integer> dropList, int slotCount, String itemModelId,
			String uid, String planIdStr, String defaultModelId, RefInt dropListCount) {
		if (StringUtils.isBlank(itemModelId)) {
			GameLog.error("钓鱼台", uid, String.format("配置物品ID无效，配置:%s", planIdStr));
			itemModelId = defaultModelId;
			// return false;
		}
		if (slotCount <= 0) {
			GameLog.error("钓鱼台", uid, String.format("配置叠加数量无效，配置:%s", planIdStr));
			slotCount = 1;
			// return false;
		}

		if (itemModelId.indexOf("_") != -1) {
			String[] arr = itemModelId.split("_");
			if (arr == null) {
				itemModelId = defaultModelId;
				// return false;
			} else {
				RoleCfg roleCfg = RoleCfgDAO.getInstance().getConfig(itemModelId);
				if (roleCfg == null) {
					itemModelId = defaultModelId;
					GameLog.error("钓鱼模块", uid, "钓鱼随机到了模版Id为：" + itemModelId + "的英雄，配置不存在,ID=" + planIdStr);
					// return false;
				}
			}
		}

		Integer old = dropList.get(itemModelId);
		if (old != null) {
			dropList.put(itemModelId, old + slotCount);
		} else {
			dropList.put(itemModelId, slotCount);
		}
		dropListCount.value++;
		return true;
	}
*/
}
