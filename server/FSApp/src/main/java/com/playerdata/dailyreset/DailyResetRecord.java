package com.playerdata.dailyreset;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DailyResetRecord {

	@Id
	private String userId;
	private Map<Integer, Integer> dailyResetReccord;
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public Map<Integer, Integer> getDailyResetReccord() {
		return dailyResetReccord;
	}
	
	public void updateDailyResetDay(int type, int dayOfYear) {
		dailyResetReccord.put(type, dayOfYear);
	}
	
	public void setDailyResetReccord(Map<Integer, Integer> dailyResetReccord) {
		this.dailyResetReccord = new HashMap<Integer, Integer>(dailyResetReccord);
	}
	
	@JsonIgnore
	public int getLastResetDay(int type) {
		Integer day = dailyResetReccord.get(type);
		if (day == null) {
			return 0;
		}
		return day.intValue();
	}
}
