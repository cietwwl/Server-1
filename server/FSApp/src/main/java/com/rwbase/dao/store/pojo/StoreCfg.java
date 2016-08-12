package com.rwbase.dao.store.pojo;

import java.util.ArrayList;
import java.util.List;

public class StoreCfg {

	private int id;
	private String name;
	private int type;
	private String colType;
	private int isAutoRefresh;
	private String autoRetime;
	private int refreshCount;
	private String refreshCost;
	private int prob;
	private int costType;
	private int showType;
	private int vipLimit;
	private int condition;
	private int levelLimit;
	private int existMin;
	private int version;
	private String refreshDay;
	private List<Integer> refreshDayList = new ArrayList<Integer>();
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getColType() {
		return colType;
	}
	public void setColType(String colType) {
		this.colType = colType;
	}
	public int getIsAutoRefresh() {
		return isAutoRefresh;
	}
	public void setIsAutoRefresh(int isAutoRefresh) {
		this.isAutoRefresh = isAutoRefresh;
	}
	public String getAutoRetime() {
		return autoRetime;
	}
	public void setAutoRetime(String autoRetime) {
		this.autoRetime = autoRetime;
	}
	public int getRefreshCount() {
		return refreshCount;
	}
	public void setRefreshCount(int refreshCount) {
		this.refreshCount = refreshCount;
	}
	public int getProb() {
		return prob;
	}
	public void setProb(int prob) {
		this.prob = prob;
	}
	public int getCostType() {
		return costType;
	}
	public void setCostType(int costType) {
		this.costType = costType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getVipLimit() {
		return vipLimit;
	}
	public void setVipLimit(int vipLimit) {
		this.vipLimit = vipLimit;
	}
	public int getCondition() {
		return condition;
	}
	public void setCondition(int condition) {
		this.condition = condition;
	}
	public int getLevelLimit() {
		return levelLimit;
	}
	public void setLevelLimit(int levelLimit) {
		this.levelLimit = levelLimit;
	}
	public String getRefreshCost() {
		return refreshCost;
	}
	public void setRefreshCost(String refreshCost) {
		this.refreshCost = refreshCost;
	}
	public int getExistMin() {
		return existMin;
	}
	public void setExistMin(int existMin) {
		this.existMin = existMin;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public int getShowType() {
		return showType;
	}
	public void setShowType(int showType) {
		this.showType = showType;
	}
	public String getRefreshDay() {
		return refreshDay;
	}
	public void setRefreshDay(String refreshDay) {
		this.refreshDay = refreshDay;
	}
	public List<Integer> getRefreshDayList() {
		return refreshDayList;
	}
	public void setRefreshDayList(List<Integer> refreshDayList) {
		this.refreshDayList = refreshDayList;
	}
	
}
