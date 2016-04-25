package com.rw.service.gamble.datamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.log.GameLog;
import com.rw.service.gamble.GambleLogicHelper;

public class GambleDropHistory {
	//历史纪录队列，越早的越靠前，越迟的越靠后
	private List<String> chargeGambleHistory;
	private List<String> freeGambleHistory;
	private int freeCount;// 免费抽卡次数，每日重置
	private long lastFreeGambleTime;
	private int hotCount;// 热点英雄抽卡次数，保底时重置
	private int hotCheckRandomThreshold;
	private boolean firstFreeGamble = true;
	private boolean firstChargeGamble = true;

	protected GambleDropHistory() {
		chargeGambleHistory = new ArrayList<String>();
		freeGambleHistory = new ArrayList<String>();
	}

	public int getHotCheckThreshold() {
		return hotCheckRandomThreshold;
	}

	public void GenerateHotCheckCount(Random r, int min, int max) {
		if (min <= 0) {
			hotCheckRandomThreshold = 1;// 容错处理
			GameLog.error("钓鱼台", "", "无效配置:热点必掉英雄次数:" + min + "~" + max);
			return;
		}
		if (min >= max) {// 容错处理
			hotCheckRandomThreshold = max;
			return;
		}
		hotCheckRandomThreshold = r.nextInt(max - min) + min;
	}

	public void resetHotHistory(Random r, int hotCheckMin, int hotCheckMax) {
		hotCount = 0;
		GenerateHotCheckCount(r, hotCheckMin, hotCheckMax);
	}

	public int getHotHistoryCount() {
		return hotCount;
	}

	public int getFreeCount() {
		return freeCount;
	}

	public void reset() {
		freeCount = 0;
		lastFreeGambleTime = 0;
	}

	/**
	 * 是否时第一次免费抽卡
	 * 
	 * @return
	 */
	public boolean isFreeGambleFirstTime() {
		return firstFreeGamble;
	}

	/**
	 * 是否第一次给钱抽卡
	 * 
	 * @return
	 */
	public boolean isChargeGambleFirstTime() {
		return firstChargeGamble;
	}

	/**
	 * 返回true表示需要使用保底方案
	 * 
	 * @param isFree
	 * @param dropPlan
	 * @return
	 */
	public boolean checkGuarantee(boolean isFree, IDropGambleItemPlan dropPlan, int maxHistory) {
		List<String> history = checkHistoryNum(isFree, maxHistory);
		if (history.size() < dropPlan.getCheckNum() - 1) {
			return false;
		}
		for (String itemModelId : history) {
			if (dropPlan.checkInList(itemModelId)) {
				return false;
			}
		}
		return true;
	}

	public void add(boolean isFree, String itemModel, int itemCount, int maxHistory) {
		List<String> history = checkHistoryNum(isFree, maxHistory);
		history.add(itemModel);
		if (isFree) {
			firstFreeGamble = false;
			freeCount++;
			lastFreeGambleTime = System.currentTimeMillis();
		} else {
			firstChargeGamble = false;
		}
	}

	private List<String> checkHistoryNum(boolean isFree, int maxHistory) {
		List<String> history = isFree ? freeGambleHistory : chargeGambleHistory;
		int removeCount = history.size() - maxHistory + 1;
		for (int i = 0; i < removeCount; i++) {
			history.remove(0);
		}
		return history;
	}

	public void addHotHistoryCount() {
		hotCount++;
	}

	public int getFreeLeftTime(GamblePlanCfg planCfg) {
		if (lastFreeGambleTime > 0) {
			long now = System.currentTimeMillis();
			return (planCfg.getRecoverTime() * 1000 - (int) (now - lastFreeGambleTime)) / 1000;
		}
		return 0;
	}

	public boolean canUseFree(GamblePlanCfg planCfg) {
		return freeCount < planCfg.getFreeCountPerDay() && GambleLogicHelper.isLeftTimeOver(getFreeLeftTime(planCfg));
	}

	public boolean canUseFree(String gamblePlanId) {
		GamblePlanCfg planCfg = GamblePlanCfgHelper.getInstance().getCfgById(gamblePlanId);
		return canUseFree(planCfg);
	}

	public boolean canUseFree(int gamblePlanId) {
		String planIdStr = String.valueOf(gamblePlanId);
		return canUseFree(planIdStr);
	}
}
