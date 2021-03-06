package com.rw.service.gamble.datamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Table(name = "gamble_record")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class GambleRecord {
	@Id
	private String userId;
	private Map<Integer, GambleDropHistory> dropPlanHistoryMapping;

	// 根据保底次数进行分组记录
	private Map<Integer, GambleHistoryRecord> historyByGroupMapping;

	// // set方法仅仅用于Json库反射使用，其他类不要调用！
	// public Map<Integer, GambleDropHistory> getDropPlanHistoryMapping() {
	// return dropPlanHistoryMapping;
	// }
	//
	// public void setDropPlanHistoryMapping(Map<Integer, GambleDropHistory> dropPlanHistoryMapping) {
	// this.dropPlanHistoryMapping = dropPlanHistoryMapping;
	// }

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	// for json and ORMapping
	private GambleRecord() {
		dropPlanHistoryMapping = new HashMap<Integer, GambleDropHistory>();
		historyByGroupMapping = new HashMap<Integer, GambleHistoryRecord>();
	}

	protected GambleRecord(String uid) {
		this();
		this.userId = uid;
	}

	public boolean resetHistory() {
		boolean changed = false;
		Collection<GambleDropHistory> histories = dropPlanHistoryMapping.values();
		for (GambleDropHistory gambleDropHistory : histories) {
			if (gambleDropHistory.reset()) {
				changed = true;
			}
		}
		return changed;
	}

	public GambleDropHistory getHistory(int planId) {
		GambleDropHistory history = dropPlanHistoryMapping.get(planId);
		if (history == null) {
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
		if (result == null) {
			result = new GambleHistoryRecord();
			historyByGroupMapping.put(groupIndex, result);
		}
		return result;
	}

	/**
	 * 相同分组的其他记录需要同步抽卡数量，移动 保底检索次数的索引
	 * 
	 * @param planCfg
	 * @param count
	 */
	public void adjustCountOfSameGroup(GamblePlanCfg planCfg, IDropGambleItemPlan dropPlan, int incrCount) {
		if (incrCount <= 0)
			return;
//		int mainKey = planCfg.getKey();
		int dropType = planCfg.getDropType();
		List<GamblePlanCfg> lst = GamblePlanCfgHelper.getInstance().getCfgOfSameGroup(planCfg);
		List<Integer> processDropTypes = new ArrayList<Integer>();
		processDropTypes.add(dropType);
		for (GamblePlanCfg cfg : lst) {
			// if (cfg.getKey() == mainKey){//这里用key判断，但是获取GambleDropHistory 用dropType,配置表内存在key不同但dropType相同的配置，所以这里判断会出错
//			if (cfg.getDropType() == dropType) {
//				continue;
//			}
			int nowDropType = cfg.getDropType();
			if (!processDropTypes.contains(nowDropType)) {
				processDropTypes.add(nowDropType);
				GambleDropHistory his = getHistory(nowDropType);
				his.increaseCount(dropPlan, incrCount);
			}
		}
	}
}
