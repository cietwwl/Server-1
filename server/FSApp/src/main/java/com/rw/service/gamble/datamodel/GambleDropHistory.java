package com.rw.service.gamble.datamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.log.GameLog;
import com.rw.service.gamble.GambleLogicHelper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GambleDropHistory {

	private int freeCount;// 当天使用免费抽卡次数，每日重置
	private long lastFreeGambleTime;
	private int hotCount;// 热点英雄抽卡次数，保底时重置
	private int hotCheckRandomThreshold;//出现保留英雄的次数， 会在每次起服的时候进行初始化
	private boolean firstChargeGamble = true;
	
	private int chargeGuaranteePlanIndex=0;//收费保底检索的索引
	private int lookbackNumber;// 历史记录序列中保底检查的位置
	private boolean passFreeExclusiveCheck = false;
	private boolean passChargeExclusiveCheck = false;
	
	public StringBuilder toDebugString(GambleHistoryRecord groupRec){
		StringBuilder b=new StringBuilder();
		b.append("freeCount:").append(freeCount).append("\n");
		b.append("lastFreeGambleTime:").append(lastFreeGambleTime).append("\n");
		b.append("hotCount:").append(hotCount).append("\n");
		b.append("hotCheckRandomThreshold:").append(hotCheckRandomThreshold).append("\n");
		b.append("firstChargeGamble:").append(firstChargeGamble).append("\n");
		b.append("chargeGuaranteePlanIndex:").append(chargeGuaranteePlanIndex).append("\n");
		b.append("passFreeExclusiveCheck:").append(passFreeExclusiveCheck).append("\n");
		b.append("passChargeExclusiveCheck:").append(passChargeExclusiveCheck).append("\n");
		b.append("chargeGambleHistory:").append(toDebugString(groupRec.getChargeGambleHistory())).append("\n");
		b.append("freeExclusiveHistory:").append(toDebugString(groupRec.getFreeExclusiveHistory())).append("\n");
		b.append("chargeExclusiveHistory:").append(toDebugString(groupRec.getChargeExclusiveHistory())).append("\n");
		return b;
	}
	
	private static StringBuilder toDebugString(List<String> lst){
		StringBuilder b=new StringBuilder();
		for (String ele : lst) {
			b.append(ele).append(",");
		}
		return b;
	}
	
	@JsonIgnore
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

	public int getChargeGuaranteePlanIndex() {
		return chargeGuaranteePlanIndex;
	}

	public void setChargeGuaranteePlanIndex(int chargeGuaranteePlanIndex) {
		this.chargeGuaranteePlanIndex = chargeGuaranteePlanIndex;
	}

	public int getLookbackNumber() {
		return lookbackNumber;
	}

	public void setLookbackNumber(int lookbackNumber) {
		this.lookbackNumber = lookbackNumber;
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
	}

	@JsonIgnore
	public int getHotCheckThreshold() {
		return hotCheckRandomThreshold;
	}

	
	@JsonIgnore
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

	@JsonIgnore
	public void resetHotHistory(Random r, int hotCheckMin, int hotCheckMax) {
		hotCount = 0;
		GenerateHotCheckCount(r, hotCheckMin, hotCheckMax);
	}

	@JsonIgnore
	public int getHotHistoryCount() {
		return hotCount;
	}

	@JsonIgnore
	public void reset() {
		freeCount = 0;
		//lastFreeGambleTime = 0;
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
	@JsonIgnore
	public void clearGuaranteeHistory(boolean isGuarantee, IDropGambleItemPlan dropPlan, StringBuilder trace,GambleHistoryRecord groupRec){
		if (isGuarantee){
			GambleLogicHelper.logTrace(trace,"isGuarantee trigger clearHistory");
			clearGuaranteeRecord(groupRec,dropPlan, trace);
			//System.out.println("guarantee trigger!");
		}else{
			List<String> history = groupRec.getChargeGambleHistory();
			int checkNum = dropPlan.getCheckNum(chargeGuaranteePlanIndex);//寻找当前保底次数
			int historySize = history.size();
			if (lookbackNumber < historySize){
				historySize = lookbackNumber;
			}
			if (historySize >= checkNum){// 超出当前保底次数，清理历史并调整保底次数数组的索引
				GambleLogicHelper.logTrace(trace,"exceed check number trigger clearHistory");
				clearGuaranteeRecord(groupRec,dropPlan, trace);
			}
		}
	}

	private void clearGuaranteeRecord(GambleHistoryRecord groupRec,IDropGambleItemPlan dropPlan, StringBuilder trace) {
		GambleLogicHelper.logTrace(trace,this,groupRec);
		List<String> history = groupRec.getChargeGambleHistory();
		if (history.size() >= 20){//TODO 获取最大的历史记录数值
			while (history.size() > 20){
				history.remove(0);
			}
		}
		increaseGuaranteePlanIndex(dropPlan.getLastCheckIndex());
	}	
	/**
	 * 返回true表示需要使用保底方案
	 * @param isFree
	 * @param dropPlan
	 * @return
	 */
	@JsonIgnore
	public boolean checkGuarantee(boolean isFree, IDropGambleItemPlan dropPlan,GambleHistoryRecord groupRec) {
		List<String> history = groupRec.getChargeGambleHistory();
		int checkNum = dropPlan.getCheckNum(chargeGuaranteePlanIndex);
		int historySize = lookbackNumber;
		if (lookbackNumber > history.size()){
			//如果历史记录的大小比需要检查的历史数量要少！
			historySize = history.size();
			System.out.println("lookbackNumber:"+lookbackNumber+",historySize:"+historySize);
		}
		
		if (historySize < checkNum - 1) {
			return false;
		}
		
		//单抽特殊规则，忽略抽卡历史：不管前面抽了多少张，到了设定的数量就必须转到保底组来抽，否则就不用保底组
		if (dropPlan.isSingleGamble()){
			return historySize >= checkNum - 1;
		}
		
		//检查最后(checkNum - 1)个历史!
		historySize = history.size();
		for (int i = historySize-1; i > historySize - checkNum; i--){
			String itemModelId  = history.get(i);
			if (dropPlan.checkInList(itemModelId)) {
				return false;
			}
		}
		return true;
	}
	
	@JsonIgnore
	private void increaseGuaranteePlanIndex(int lastIndex){
		if (chargeGuaranteePlanIndex<lastIndex){
			chargeGuaranteePlanIndex++;
		}
		lookbackNumber = 0;
	}

	@JsonIgnore
	public void add(boolean isFree, String itemModel, int itemCount,GambleHistoryRecord groupRec) {
		add(isFree,itemModel,itemCount,groupRec,false);
	}
	
	public void add(boolean isFree, String itemModel, int itemCount,GambleHistoryRecord groupRec,boolean ignoreThisCount) {
		List<String> history = groupRec.getChargeGambleHistory();
		history.add(itemModel);
		if (isFree) {
			freeCount++;
			lastFreeGambleTime = System.currentTimeMillis();
		}
		
		if (!ignoreThisCount){
			lookbackNumber++;
		}
		
		firstChargeGamble = false;
		addExclusiveHistory(isFree,itemModel,groupRec);
	}

	@JsonIgnore
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

	@JsonIgnore
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
	public void checkDistinctTag(boolean isFree, int exclusiveCount,GambleHistoryRecord groupRec) {
		int historySize = (isFree?groupRec.getFreeExclusiveHistory():groupRec.getChargeExclusiveHistory()).size();
		if (historySize >= exclusiveCount){//假设历史添加成功：historySize+1 > exclusiveCount
			if (isFree) {
				passFreeExclusiveCheck = true;
				groupRec.getFreeExclusiveHistory().clear();
			} else {
				passChargeExclusiveCheck = true;
				groupRec.getChargeExclusiveHistory().clear();
			}
		}
	}
	
	@JsonIgnore
	private void addExclusiveHistory(boolean isFree, String itemId,GambleHistoryRecord groupRec){
		if (isFree) {
			if (!passFreeExclusiveCheck){
				groupRec.getFreeExclusiveHistory().add(itemId);
			}
		} else {
			if (!passChargeExclusiveCheck){
				groupRec.getChargeExclusiveHistory().add(itemId);
			}
		}
	}

	@JsonIgnore
	public List<String> getExculsiveHistory(boolean isFree, IDropGambleItemPlan dropPlan,GambleHistoryRecord groupRec) {
		int checkNum = dropPlan.getExclusiveCount();
		List<String> result = isFree?groupRec.getFreeExclusiveHistory():groupRec.getChargeExclusiveHistory();
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

	/**
	 * 移动 保底检索次数的索引
	 */
	public void increaseCount(IDropGambleItemPlan dropPlan,int incrCount) {
		int checkNum = dropPlan.getCheckNum(chargeGuaranteePlanIndex);//寻找当前保底次数
		for (int i = 0; i < incrCount; i++) {
			lookbackNumber++;
			if (lookbackNumber >= checkNum){
				increaseGuaranteePlanIndex(dropPlan.getLastCheckIndex());
				checkNum = dropPlan.getCheckNum(chargeGuaranteePlanIndex);//寻找当前保底次数
			}
		}
	}
}
