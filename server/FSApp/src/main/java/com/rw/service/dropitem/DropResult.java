package com.rw.service.dropitem;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.dropitem.DropAdjustmentState;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DropResult {

	private List<ItemInfo> itemInfos;
	private Map<Integer, DropAdjustmentState> dropRuleMap;
	private long createTimeMillis; // 记录产生的时间，之后用来做验证
	private boolean firstDrop;	   // 是否首次掉落

	public DropResult(List<ItemInfo> itemInfoList, Map<Integer, DropAdjustmentState> dropRuleSet,boolean firstDrop) {
		this.itemInfos = itemInfoList;
		this.dropRuleMap = dropRuleSet;
		this.firstDrop = firstDrop;
		this.createTimeMillis = System.currentTimeMillis();
	}

	public DropResult() {
	}

	public long getCreateTimeMillis() {
		return createTimeMillis;
	}

	public void setCreateTimeMillis(long createTimeMillis) {
		this.createTimeMillis = createTimeMillis;
	}

	public List<ItemInfo> getItemInfos() {
		return itemInfos;
	}

	public void setItemInfos(List<ItemInfo> itemInfos) {
		this.itemInfos = itemInfos;
	}

	public Map<Integer, DropAdjustmentState> getDropRuleMap() {
		return dropRuleMap;
	}

	public void setDropRuleMap(Map<Integer, DropAdjustmentState> dropRuleMap) {
		this.dropRuleMap = dropRuleMap;
	}

	public boolean isFirstDrop() {
		return firstDrop;
	}

	public void setFirstDrop(boolean firstDrop) {
		this.firstDrop = firstDrop;
	}

}
