package com.rw.service.gamble.datamodel;

public class GambleOnePlanDropData {
	private GamblePlanCfg planCfg;
	private GambleDropHistory historyRecord;

	public GambleOnePlanDropData(GambleDropHistory historyRecord, GamblePlanCfg planCfg) {
		this.historyRecord=historyRecord;
		this.planCfg = planCfg;
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
