package com.rw.service.gamble.datamodel;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
	
	//根据保底次数进行分组记录
	private Map<Integer,GambleHistoryRecord> historyByGroupMapping;

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
		historyByGroupMapping = new HashMap<Integer, GambleHistoryRecord>();
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
	
	public GambleHistoryRecord getByGroup(GamblePlanCfg planCfg) {
		int groupIndex = planCfg.getGuaranteeGroupIndex();
		GambleHistoryRecord result = historyByGroupMapping.get(groupIndex);
		if (result == null){
			result = new GambleHistoryRecord();
			historyByGroupMapping.put(groupIndex, result);
		}
		return result;
	}
	
	/**
	 * 相同分组的其他记录需要同步抽卡数量，移动 保底检索次数的索引
	 * @param planCfg
	 * @param count
	 */
	public void adjustCountOfSameGroup(GamblePlanCfg planCfg,IDropGambleItemPlan dropPlan,int incrCount){
		if (incrCount <= 0) return;
		int mainKey = planCfg.getKey();
		List<GamblePlanCfg> lst = GamblePlanCfgHelper.getInstance().getCfgOfSameGroup(planCfg);
		for (GamblePlanCfg cfg : lst) {
			if (cfg.getKey() == mainKey){
				continue;
			}
			GambleDropHistory his = getHistory(cfg.getDropType());
			his.increaseCount(dropPlan,incrCount);
		}
	}
}
