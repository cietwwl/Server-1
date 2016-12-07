package com.gm.util;

import java.util.Map;

public class HotFixHistoryRecord {

	private String version;
	private Map<String, Long> hotfixHistories;
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public Map<String, Long> getHotfixHistories() {
		return hotfixHistories;
	}
	
	public void setHotfixHistories(Map<String, Long> hotfixHistories) {
		this.hotfixHistories = hotfixHistories;
	}
}
