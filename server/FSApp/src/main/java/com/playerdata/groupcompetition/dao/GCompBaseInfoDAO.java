package com.playerdata.groupcompetition.dao;

import com.playerdata.groupcompetition.holder.data.GCompBaseInfo;
import com.playerdata.groupcompetition.util.GCompStageType;

public class GCompBaseInfoDAO {

	private static final GCompBaseInfoDAO _instance = new GCompBaseInfoDAO();
	
	public static final GCompBaseInfoDAO getInstance() {
		return _instance;
	}
	
	private GCompBaseInfo baseInfoTemplate;
	
	public GCompBaseInfo getBaseInfoTemplate() {
		if(this.baseInfoTemplate == null) {
			synchronized(this) {
				if(this.baseInfoTemplate == null) {
					baseInfoTemplate = new GCompBaseInfo();
					//TODO 从模板拿数据
				}
			}
		}
		return baseInfoTemplate;
	}
	
	public GCompBaseInfo getBaseInfo() {
		getBaseInfoTemplate();
		GCompBaseInfo baseInfo = new GCompBaseInfo();
		baseInfo.setCurrentStageType(baseInfoTemplate.getCurrentStageType());
		baseInfo.setEventStatus(baseInfoTemplate.getEventStatus());
		if (baseInfoTemplate.getStartTime() > System.currentTimeMillis()) {
			baseInfo.setLeftTime(baseInfoTemplate.getStartTime() - System.currentTimeMillis());
		}
		return baseInfo;
	}
	
	public void updateStartTime(long startTime) {
		this.getBaseInfoTemplate().setStartTime(startTime);
	}
	
	public void update(boolean start) {
		this.getBaseInfoTemplate().setStart(start);
	}

	public void updateStage(GCompStageType currentStage) {
		this.getBaseInfoTemplate().setCurrentStageType(currentStage);
	}
}
