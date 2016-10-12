package com.rwbase.dao.store.pojo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 觉醒抽奖配置表
 * @author lida
 *
 */
public class WakenLotteryDrawCfg {
	private int ID;
	private int lotteryDrawType;     		//抽奖类型
	private String lvPeriod;          		//等级分段
	private String consume;          		//消耗
	private String firstFreeRewardPlan;  	//首抽免费奖励方案
	private String firstPayRewardPlan;      //首抽付费奖励方案
	private String payRewardPlan;			//付费抽取奖励方案
	private String freeRewardPlan;			//免费抽取奖励方案
	private String guaranteeRewardPlan;		//保底奖励方案
	private int guaranteeeTime;				//保底次数
	private String resetTime;				//重置时间
	private int freeTime;                   //免费次数
	private int drawTime;                   //抽奖次数
	private int minLevel;                   //最小等级
	private int maxLevel;                   //最大等级
	private String firstConsume;            //优先消耗
	private String secondConsume;           //次要消耗
	
	private HashMap<Integer, Integer> consumeMap = new HashMap<Integer, Integer>();
	private ArrayList<Integer> firstFreeRewardPlanList = new ArrayList<Integer>();
	private ArrayList<Integer> firstPayRewardPlanList = new ArrayList<Integer>();
	private ArrayList<Integer> payRewardPlanList = new ArrayList<Integer>();
	private ArrayList<Integer> freeRewardPlanList = new ArrayList<Integer>();
	private ArrayList<Integer> guaranteeRewardPlanList = new ArrayList<Integer>();
	public int getLotteryDrawType() {
		return lotteryDrawType;
	}
	public void setLotteryDrawType(int lotteryDrawType) {
		this.lotteryDrawType = lotteryDrawType;
	}
	public String getLvPeriod() {
		return lvPeriod;
	}
	public void setLvPeriod(String lvPeriod) {
		this.lvPeriod = lvPeriod;
	}
	public String getConsume() {
		return consume;
	}
	public void setConsume(String consume) {
		this.consume = consume;
	}
	public String getPayRewardPlan() {
		return payRewardPlan;
	}
	public void setPayRewardPlan(String payRewardPlan) {
		this.payRewardPlan = payRewardPlan;
	}
	public String getFreeRewardPlan() {
		return freeRewardPlan;
	}
	public void setFreeRewardPlan(String freeRewardPlan) {
		this.freeRewardPlan = freeRewardPlan;
	}
	public String getGuaranteeRewardPlan() {
		return guaranteeRewardPlan;
	}
	public void setGuaranteeRewardPlan(String guaranteeRewardPlan) {
		this.guaranteeRewardPlan = guaranteeRewardPlan;
	}
	public int getGuaranteeeTime() {
		return guaranteeeTime;
	}
	public void setGuaranteeeTime(int guaranteeeTime) {
		this.guaranteeeTime = guaranteeeTime;
	}
	public String getResetTime() {
		return resetTime;
	}
	public void setResetTime(String resetTime) {
		this.resetTime = resetTime;
	}
	public ArrayList<Integer> getPayRewardPlanList() {
		return payRewardPlanList;
	}
	public void setPayRewardPlanList(ArrayList<Integer> payRewardPlanList) {
		this.payRewardPlanList = payRewardPlanList;
	}
	public ArrayList<Integer> getFreeRewardPlanList() {
		return freeRewardPlanList;
	}
	public void setFreeRewardPlanList(ArrayList<Integer> freeRewardPlanList) {
		this.freeRewardPlanList = freeRewardPlanList;
	}
	public ArrayList<Integer> getGuaranteeRewardPlanList() {
		return guaranteeRewardPlanList;
	}
	public void setGuaranteeRewardPlanList(
			ArrayList<Integer> guaranteeRewardPlanList) {
		this.guaranteeRewardPlanList = guaranteeRewardPlanList;
	}
	public HashMap<Integer, Integer> getConsumeMap() {
		return consumeMap;
	}
	public void setConsumeMap(HashMap<Integer, Integer> consumeMap) {
		this.consumeMap = consumeMap;
	}
	public String getFirstFreeRewardPlan() {
		return firstFreeRewardPlan;
	}
	public void setFirstFreeRewardPlan(String firstFreeRewardPlan) {
		this.firstFreeRewardPlan = firstFreeRewardPlan;
	}
	public String getFirstPayRewardPlan() {
		return firstPayRewardPlan;
	}
	public void setFirstPayRewardPlan(String firstPayRewardPlan) {
		this.firstPayRewardPlan = firstPayRewardPlan;
	}
	public int getFreeTime() {
		return freeTime;
	}
	public void setFreeTime(int freeTime) {
		this.freeTime = freeTime;
	}
	public ArrayList<Integer> getFirstFreeRewardPlanList() {
		return firstFreeRewardPlanList;
	}
	public void setFirstFreeRewardPlanList(
			ArrayList<Integer> firstFreeRewardPlanList) {
		this.firstFreeRewardPlanList = firstFreeRewardPlanList;
	}
	public ArrayList<Integer> getFirstPayRewardPlanList() {
		return firstPayRewardPlanList;
	}
	public void setFirstPayRewardPlanList(ArrayList<Integer> firstPayRewardPlanList) {
		this.firstPayRewardPlanList = firstPayRewardPlanList;
	}
	public int getDrawTime() {
		return drawTime;
	}
	public void setDrawTime(int drawTime) {
		this.drawTime = drawTime;
	}
	public int getMinLevel() {
		return minLevel;
	}
	public void setMinLevel(int minLevel) {
		this.minLevel = minLevel;
	}
	public int getMaxLevel() {
		return maxLevel;
	}
	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}
	public String getFirstConsume() {
		return firstConsume;
	}
	public void setFirstConsume(String firstConsume) {
		this.firstConsume = firstConsume;
	}
	public String getSecondConsume() {
		return secondConsume;
	}
	public void setSecondConsume(String secondConsume) {
		this.secondConsume = secondConsume;
	}
}
