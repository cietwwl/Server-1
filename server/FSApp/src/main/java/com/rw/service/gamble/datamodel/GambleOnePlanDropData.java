package com.rw.service.gamble.datamodel;

import com.log.GameLog;

public class GambleOnePlanDropData {
	private GamblePlanCfg planCfg;
	private GambleDropHistory historyRecord;
	private GambleHistoryRecord groupRec;

	public GambleOnePlanDropData(GambleDropHistory historyRecord, GamblePlanCfg planCfg,GambleHistoryRecord groupRec) {
		this.historyRecord=historyRecord;
		this.planCfg = planCfg;
		this.groupRec = groupRec;
		if (planCfg == null){
			GameLog.error("钓鱼台", "无效参数", "找不到配置");
		}
		if (historyRecord == null){
			GameLog.error("钓鱼台", "无效参数", "无法从数据库找到历史纪录");
		}
	}
	
	/**
	 * 仅对单抽有效
	 * @return
	 */
	public int getGuaranteeLeftCount(){
		if (planCfg.getDropItemCount() == 1){
			//int size = groupRec.getChargeGambleHistory().size();
			int number = historyRecord.getLookbackNumber();
			int guaranteePlanIndex = historyRecord.getChargeGuaranteePlanIndex();
			IDropGambleItemPlan dropPlan = planCfg.getChargePlan();
			//特殊规则，钻石单抽的首抽不能记录在保底次数中
			if (planCfg.getKey() == 5 && historyRecord.isFirstChargeGamble()){
				return 0;
			}
			int guaranteeCheckNum = dropPlan.getCheckNum(guaranteePlanIndex);
			return guaranteeCheckNum > number ? guaranteeCheckNum - number - 1 : 0;
			//return guaranteeCheckNum > size ? guaranteeCheckNum - size - 1 : 0;
		}else{
			return -1;
		}
	}

	public int getMaxFreeCount() {
		return planCfg.getFreeCountPerDay();
	}
	
	public boolean canGambleFree(){
		return historyRecord.canUseFree(planCfg);
	}
	
	public int getFreeCount() {
		return historyRecord.getFreeCount();
	}

	// 免费时间倒计时，单位为秒
	public int getLeftTime() {
		return historyRecord.getFreeLeftTime(planCfg);
	}

}
