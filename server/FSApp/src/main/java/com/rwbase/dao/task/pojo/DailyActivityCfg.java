package com.rwbase.dao.task.pojo;

public class DailyActivityCfg {
	private int id; // 任务id
	private int sortNum; // 排序序号，从1开始
	private int taskType; // 任务类型(具体的任务对应的类型)1时间类，2功能类
	private int taskFinishType; // 任务完成类型(一次性或者叠加性)
	private String finishCondition; // 任务完成条件(时间或者数量)
	private String title; // 任务标题
	private String taskIcon; // 任务图标
	private String description; // 任务描述
	private String reward; // 任务奖励
	private int gotoType; // 跳转的任务类型
	private String startCondition; // 开启条件(时间或者等级)
	private int taskClassify; // 任务分类类型
	private int maxLevel; // 最大生效等级
	private int vip; // vip等级
	private int maxVip; // 最大vip等级
	private String mapId;// 开启关卡
	private int taskInitNum;// 任务初始化数量
	
	private int BICode;//对应的日志code

	public int getTaskType() {
		return taskType;
	}

	public void setTaskType(int taskType) {
		this.taskType = taskType;
	}

	public int getSortNum() {
		return sortNum;
	}

	public void setSortNum(int sortNum) {
		this.sortNum = sortNum;
	}

	public int getTaskFinishType() {
		return taskFinishType;
	}

	public void setTaskFinishType(int taskFinishType) {
		this.taskFinishType = taskFinishType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTaskIcon() {
		return taskIcon;
	}

	public void setTaskIcon(String taskIcon) {
		this.taskIcon = taskIcon;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getReward() {
		return reward;
	}

	public void setReward(String reward) {
		this.reward = reward;
	}

	public int getGotoType() {
		return gotoType;
	}

	public void setGotoType(int gotoType) {
		this.gotoType = gotoType;
	}

	public String getStartCondition() {
		return startCondition;
	}

	public void setStartCondition(String startCondition) {
		this.startCondition = startCondition;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTaskClassify() {
		return taskClassify;
	}

	public void setTaskClassify(int taskClassify) {
		this.taskClassify = taskClassify;
	}

	public String getFinishCondition() {
		return finishCondition;
	}

	public void setFinishCondition(String finishCondition) {
		this.finishCondition = finishCondition;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}

	public int getVip() {
		return vip;
	}

	public void setVip(int vip) {
		this.vip = vip;
	}

	public int getMaxVip() {
		return maxVip;
	}

	public void setMaxVip(int maxVip) {
		this.maxVip = maxVip;
	}

	public String getMapId() {
		return mapId;
	}

	public void setMapId(String mapId) {
		this.mapId = mapId;
	}

	public int getTaskInitNum() {
		return taskInitNum;
	}

	public void setTaskInitNum(int taskInitNum) {
		this.taskInitNum = taskInitNum;
	}

	public int getBICode() {
		return BICode;
	}

	
}
