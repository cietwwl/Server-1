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
	private int freeCount;// 当天使用免费抽卡次数，每日重置
	private long lastFreeGambleTime;
	private int hotCount;// 热点英雄抽卡次数，保底时重置
	private int hotCheckRandomThreshold;
	private boolean firstFreeGamble = true;
	private boolean firstChargeGamble = true;
	
	private int freeGuaranteePlanIndex = 0;//免费保底检索的索引
	private int chargeGuaranteePlanIndex=0;//收费保底检索的索引
	
	private boolean passFreeExclusiveCheck = false;
	private boolean passChargeExclusiveCheck = false;
	private List<String> freeExclusiveHistory;
	private List<String> chargeExclusiveHistory;
	
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

	public int getFreeGuaranteePlanIndex() {
		return freeGuaranteePlanIndex;
	}

	public void setFreeGuaranteePlanIndex(int freeGuaranteePlanIndex) {
		this.freeGuaranteePlanIndex = freeGuaranteePlanIndex;
	}

	public int getChargeGuaranteePlanIndex() {
		return chargeGuaranteePlanIndex;
	}

	public void setChargeGuaranteePlanIndex(int chargeGuaranteePlanIndex) {
		this.chargeGuaranteePlanIndex = chargeGuaranteePlanIndex;
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

	public List<String> getChargeExclusiveHistory() {
		return chargeExclusiveHistory;
	}

	public void setChargeExclusiveHistory(List<String> chargeExclusiveHistory) {
		this.chargeExclusiveHistory = chargeExclusiveHistory;
	}

	public List<String> getFreeExclusiveHistory() {
		return freeExclusiveHistory;
	}

	public void setFreeExclusiveHistory(List<String> freeExclusiveHistory) {
		this.freeExclusiveHistory = freeExclusiveHistory;
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
		freeExclusiveHistory = new ArrayList<String>();
		chargeExclusiveHistory = new ArrayList<String>();
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
	 * 保底检查次数达到后，需要清除旧的历史纪录，另保底不会过早发生
	 * 还需要调整保底次数数组的索引
	 * @param isFree
	 */
	public void clearGuaranteeHistory(boolean isFree,IDropGambleItemPlan dropPlan){
		int index = isFree?freeGuaranteePlanIndex:chargeGuaranteePlanIndex;
		List<String> history = isFree ? freeGambleHistory : chargeGambleHistory;
		int checkNum = dropPlan.getCheckNum(index);//寻找当前保底次数
		int historySize = history.size();
		if (historySize >= checkNum){// 超出当前保底次数，清理历史并调整保底次数数组的索引
			increaseGuaranteePlanIndex(isFree);
			int removeCount = checkNum;
			for (int i = 0; i < removeCount; i++) {
				history.remove(0);
			}
		}
	}
	
	/**
	 * 返回true表示需要使用保底方案
	 * @param isFree
	 * @param dropPlan
	 * @return
	 */
	public boolean checkGuarantee(boolean isFree, IDropGambleItemPlan dropPlan, int maxHistory) {
		int index = isFree?freeGuaranteePlanIndex:chargeGuaranteePlanIndex;
		List<String> history = isFree ? freeGambleHistory : chargeGambleHistory;
		int checkNum = dropPlan.getCheckNum(index);
		int historySize = history.size();
		if (historySize < checkNum - 1) {
			return false;
		}
		
		for (String itemModelId : history) {
			if (dropPlan.checkInList(itemModelId)) {
				return false;
			}
		}
		return true;
	}
	
	private void increaseGuaranteePlanIndex(boolean isFree){
		if (isFree){
			freeGuaranteePlanIndex++;
		}else{
			chargeGuaranteePlanIndex++;
		}
	}

	public void add(boolean isFree, String itemModel, int itemCount, int maxHistory) {
		//List<String> history = checkHistoryNum(isFree, maxHistory);
		List<String> history = isFree ? freeGambleHistory : chargeGambleHistory;
		history.add(itemModel);
		if (isFree) {
			firstFreeGamble = false;
			freeCount++;
			lastFreeGambleTime = System.currentTimeMillis();
		} else {
			firstChargeGamble = false;
		}
		addExclusiveHistory(isFree,itemModel);
	}

	/**
	 * 保底检查次数达到后，需要清除旧的历史纪录，另保底不会过早发生
	 * @param isFree
	 * @param maxHistory
	 * @return
	 */
	/*
	private List<String> checkHistoryNum(boolean isFree, int maxHistory) {
		List<String> history = isFree ? freeGambleHistory : chargeGambleHistory;
		int removeCount = history.size() - maxHistory + 1;
		for (int i = 0; i < removeCount; i++) {
			history.remove(0);
		}
		return history;
	}*/

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

	/**
	 * 假设历史纪录增加成功，检查是否可以关闭去重检查！
	 * @param isFree
	 * @param exclusiveCount
	 */
	@JsonIgnore
	public void checkDistinctTag(boolean isFree, int exclusiveCount) {
		int historySize = (isFree?freeExclusiveHistory:chargeExclusiveHistory).size();
		if (historySize >= exclusiveCount){//假设历史添加成功：historySize+1 > exclusiveCount
			if (isFree) {
				passFreeExclusiveCheck = true;
				freeExclusiveHistory.clear();
			} else {
				passChargeExclusiveCheck = true;
				chargeExclusiveHistory.clear();
			}
		}
	}
	
	@JsonIgnore
	private void addExclusiveHistory(boolean isFree, String itemId){
		if (isFree) {
			if (!passFreeExclusiveCheck){
				freeExclusiveHistory.add(itemId);
			}
		} else {
			if (!passChargeExclusiveCheck){
				chargeExclusiveHistory.add(itemId);
			}
		}
	}

	@JsonIgnore
	public List<String> getHistory(boolean isFree, IDropGambleItemPlan dropPlan) {
		int index = isFree?freeGuaranteePlanIndex:chargeGuaranteePlanIndex;
		int checkNum = dropPlan.getCheckNum(index);
		List<String> result = isFree?freeGambleHistory:chargeGambleHistory;
		int orgSize = result.size();
		if (orgSize > checkNum){
			List<String> tmp = new ArrayList<String>(checkNum);
			for(int i = orgSize - checkNum; i < orgSize;i++){
				tmp.add(result.get(i));
			}
			result = tmp;
		}
		return result;
	}
}
