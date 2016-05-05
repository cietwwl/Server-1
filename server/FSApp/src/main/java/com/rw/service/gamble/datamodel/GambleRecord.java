package com.rw.service.gamble.datamodel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "gamble_record")
public class GambleRecord {
	@Id
	private String userId;
	private Map<Integer, GambleDropHistory> dropPlanHistoryMapping;

	// set方法仅仅用于Json库反射使用，其他类不要调用！
	public Map<Integer, GambleDropHistory> getDropPlanHistoryMapping() {
		return dropPlanHistoryMapping;
	}
	public void setDropPlanHistoryMapping(Map<Integer, GambleDropHistory> dropPlanHistoryMapping) {
		this.dropPlanHistoryMapping = dropPlanHistoryMapping;
	}
	public String getUserId(){
		return userId;
	}
	public void setUserId(String userId){
		this.userId = userId;
	}
	
	//for json and ORMapping
	private GambleRecord() {
		dropPlanHistoryMapping = new HashMap<Integer, GambleDropHistory>();
	}

	protected GambleRecord(String uid) {
		this();
		this.userId = uid;
	}
	
	public void resetHistory(){
		Collection<GambleDropHistory> histories = dropPlanHistoryMapping.values();
		for (GambleDropHistory gambleDropHistory : histories) {
			gambleDropHistory.reset();
		}
	}
	
	public GambleDropHistory getHistory(int planId){
		GambleDropHistory history = dropPlanHistoryMapping.get(planId);
		if (history == null){
			history = new GambleDropHistory();
			dropPlanHistoryMapping.put(planId, history);
		}
		return history;
	}
	
	public static GambleRecord Create(String userId) {
		GambleRecord result = new GambleRecord(userId);
		return result;
	}
}
