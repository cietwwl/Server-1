package com.playerdata.activity.exChangeType.cfg;

import java.util.HashMap;
import java.util.Map;

public class ActivityExchangeTypeDropCfg {

	private String id;

	// 计数
	private String parentCfg;

	// 计数奖励
	private String itemId;

	private String drop;

	private Map<Integer, Integer[]> dropMap = new HashMap<Integer, Integer[]>();

	public Map<Integer, Integer[]> getDropMap() {
		return dropMap;
	}

	public void setDropMap(Map<Integer, Integer[]> dropMap) {
		this.dropMap = dropMap;
	}

	public String getParentCfg() {
		return parentCfg;
	}

	public void setParentCfg(String parentCfg) {
		this.parentCfg = parentCfg;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getDrop() {
		return drop;
	}

	public void setDrop(String drop) {
		this.drop = drop;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

}
