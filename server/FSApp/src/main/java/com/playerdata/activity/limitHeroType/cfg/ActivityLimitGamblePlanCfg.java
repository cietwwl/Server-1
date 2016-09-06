package com.playerdata.activity.limitHeroType.cfg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ActivityLimitGamblePlanCfg {

	private String id;	
	
	private int dropType;	
	
	private String levelSegment;
	
	private int levelMin;
	
	private int levelMax;
	
	private int moneyNum;
	
	private int recoverTime;//免费时间cd
	
	private int dropItemCount;//个数
	
	private String guaranteeCheckNum;//保底次数
	
	private String ordinaryFreePlan;//免费方案，单个
	
	private String ordinaryPlan ;//收费方案	
	
	private String guaranteePlan;//保底方案

	private Map<Integer, Integer> ordinaryFreePlanMap = new HashMap<Integer, Integer>();
	
	private Map<Integer, Integer> ordinaryPlanMap = new HashMap<Integer, Integer>();
	
	private Map<Integer, Integer> guaranteePlanMap = new HashMap<Integer, Integer>();
	
	private List<Integer> guaranteeList;//保底list
	
	private int maxGuarantee;//循环保底

	public String getId() {
		return id;
	}	

	
	
	public int getMaxGuarantee() {
		return maxGuarantee;
	}



	public void setMaxGuarantee(int maxGuarantee) {
		this.maxGuarantee = maxGuarantee;
	}



	public List<Integer> getGuaranteeList() {
		return guaranteeList;
	}
	
	public void setGuaranteeList(List<Integer> guaranteeList) {
		this.guaranteeList = guaranteeList;
	}

	public int getLevelMin() {
		return levelMin;
	}

	public void setLevelMin(int levelMin) {
		this.levelMin = levelMin;
	}

	public int getLevelMax() {
		return levelMax;
	}

	public void setLevelMax(int levelMax) {
		this.levelMax = levelMax;
	}



	public int getDropType() {
		return dropType;
	}



	public void setDropType(int dropType) {
		this.dropType = dropType;
	}



	public String getLevelSegment() {
		return levelSegment;
	}

	public void setLevelSegment(String levelSegment) {
		this.levelSegment = levelSegment;
	}

	public int getMoneyNum() {
		return moneyNum;
	}

	public void setMoneyNum(int moneyNum) {
		this.moneyNum = moneyNum;
	}

	public int getRecoverTime() {
		return recoverTime;
	}

	public void setRecoverTime(int recoverTime) {
		this.recoverTime = recoverTime;
	}

	public int getDropItemCount() {
		return dropItemCount;
	}

	public void setDropItemCount(int dropItemCount) {
		this.dropItemCount = dropItemCount;
	}

	public String getOrdinaryFreePlan() {
		return ordinaryFreePlan;
	}

	public void setOrdinaryFreePlan(String ordinaryFreePlan) {
		this.ordinaryFreePlan = ordinaryFreePlan;
	}

	public String getOrdinaryPlan() {
		return ordinaryPlan;
	}

	public void setOrdinaryPlan(String ordinaryPlan) {
		this.ordinaryPlan = ordinaryPlan;
	}

	public String getGuaranteeCheckNum() {
		return guaranteeCheckNum;
	}

	public void setGuaranteeCheckNum(String guaranteeCheckNum) {
		this.guaranteeCheckNum = guaranteeCheckNum;
	}


	public String getGuaranteePlan() {
		return guaranteePlan;
	}


	public void setGuaranteePlan(String guaranteePlan) {
		this.guaranteePlan = guaranteePlan;
	}


	public void setId(String id) {
		this.id = id;
	}



	public Map<Integer, Integer> getOrdinaryFreePlanMap() {
		return ordinaryFreePlanMap;
	}



	public void setOrdinaryFreePlanMap(Map<Integer, Integer> ordinaryFreePlanMap) {
		this.ordinaryFreePlanMap = ordinaryFreePlanMap;
	}



	public Map<Integer, Integer> getOrdinaryPlanMap() {
		return ordinaryPlanMap;
	}



	public void setOrdinaryPlanMap(Map<Integer, Integer> ordinaryPlanMap) {
		this.ordinaryPlanMap = ordinaryPlanMap;
	}



	public Map<Integer, Integer> getGuaranteePlanMap() {
		return guaranteePlanMap;
	}



	public void setGuaranteePlanMap(Map<Integer, Integer> guaranteePlanMap) {
		this.guaranteePlanMap = guaranteePlanMap;
	}

	

}
