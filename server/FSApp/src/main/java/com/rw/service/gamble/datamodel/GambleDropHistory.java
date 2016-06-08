package com.rw.service.gamble.datamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.codehaus.jackson.annotate.JsonIgnore;

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
	
	private int freeInitCheckDuplicateCount = 0;//免费保底检索的索引
	private int chargeInitCheckDuplicateCount=0;//收费保底检索的索引
	
	private boolean passFreeExclusiveCheck = false;
	private boolean passChargeExclusiveCheck = false;

	public boolean passExclusiveCheck(boolean isFree){
		return isFree?passFreeExclusiveCheck:passChargeExclusiveCheck;
	}
	
	public boolean isPassFreeExclusiveCheck() {
		return passFreeExclusiveCheck;
	}

	public void setPassFreeExclusiveCheck(boolean passFreeExclusiveCheck) {
		this.passFreeExclusiveCheck = passFreeExclusiveCheck;
	}

	public boolean isPassChargeExclusiveCheck() {
		return passChargeExclusiveCheck;
	}

	public void setPassChargeExclusiveCheck(boolean passChargeExclusiveCheck) {
		this.passChargeExclusiveCheck = passChargeExclusiveCheck;
	}

	public int getFreeInitCheckDuplicateCount() {
		return freeInitCheckDuplicateCount;
	}

	public void setFreeInitCheckDuplicateCount(int freeInitCheckDuplicateCount) {
		this.freeInitCheckDuplicateCount = freeInitCheckDuplicateCount;
	}

	public int getChargeInitCheckDuplicateCount() {
		return chargeInitCheckDuplicateCount;
	}

	public void setChargeInitCheckDuplicateCount(int chargeInitCheckDuplicateCount) {
		this.chargeInitCheckDuplicateCount = chargeInitCheckDuplicateCount;
	}

	// set方法仅仅用于Json库反射使用，其他类不要调用！
	public List<String> getChargeGambleHistory() {
		return chargeGambleHistory;
	}

	public void setChargeGambleHistory(List<String> chargeGambleHistory) {
		this.chargeGambleHistory = chargeGambleHistory;
	}

	public List<String> getFreeGambleHistory() {
		return freeGambleHistory;
	}

	public void setFreeGambleHistory(List<String> freeGambleHistory) {
		this.freeGambleHistory = freeGambleHistory;
	}

	public long getLastFreeGambleTime() {
		return lastFreeGambleTime;
	}

	public void setLastFreeGambleTime(long lastFreeGambleTime) {
		this.lastFreeGambleTime = lastFreeGambleTime;
	}

	public int getHotCount() {
		return hotCount;
	}

	public void setHotCount(int hotCount) {
		this.hotCount = hotCount;
	}

	public int getHotCheckRandomThreshold() {
		return hotCheckRandomThreshold;
	}

	public void setHotCheckRandomThreshold(int hotCheckRandomThreshold) {
		this.hotCheckRandomThreshold = hotCheckRandomThreshold;
	}

	public boolean isFirstFreeGamble() {
		return firstFreeGamble;
	}

	public void setFirstFreeGamble(boolean firstFreeGamble) {
		this.firstFreeGamble = firstFreeGamble;
	}

	public boolean isFirstChargeGamble() {
		return firstChargeGamble;
	}

	public void setFirstChargeGamble(boolean firstChargeGamble) {
		this.firstChargeGamble = firstChargeGamble;
	}

	public void setFreeCount(int freeCount) {
		this.freeCount = freeCount;
	}

	public int getFreeCount() {
		return freeCount;
	}

	public GambleDropHistory() {
		chargeGambleHistory = new ArrayList<String>();
		freeGambleHistory = new ArrayList<String>();
	}

	@JsonIgnore
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

	@JsonIgnore
	public int getHotHistoryCount() {
		return hotCount;
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
	@JsonIgnore
	public boolean isFreeGambleFirstTime() {
		return firstFreeGamble;
	}

	/**
	 * 是否第一次给钱抽卡
	 * 
	 * @return
	 */
	@JsonIgnore
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
		int index = isFree?freeInitCheckDuplicateCount:chargeInitCheckDuplicateCount;
		if (history.size() < dropPlan.getCheckNum(index) - 1) {
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

	@JsonIgnore
	public int getFreeLeftTime(GamblePlanCfg planCfg) {
		if (lastFreeGambleTime > 0) {
			long now = System.currentTimeMillis();
			return (planCfg.getRecoverTime() * 1000 - (int) (now - lastFreeGambleTime)) / 1000;
		}
		return 0;
	}

	public boolean canUseFree(GamblePlanCfg planCfg) {
		if (planCfg == null) return false;
		return freeCount < planCfg.getFreeCountPerDay() && GambleLogicHelper.isLeftTimeOver(getFreeLeftTime(planCfg));
	}

	@JsonIgnore
	public void increseInitDuplicateCheckCount(boolean isFree) {
		if (isFree){
			freeInitCheckDuplicateCount++;
		}else{
			chargeInitCheckDuplicateCount++;
		}
	}

	@JsonIgnore
	public List<String> getHistory(boolean isFree) {
		return isFree?freeGambleHistory:chargeGambleHistory;
	}
}
