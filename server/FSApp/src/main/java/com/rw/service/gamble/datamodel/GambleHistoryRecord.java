package com.rw.service.gamble.datamodel;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GambleHistoryRecord {
	// 历史纪录队列，越早的越靠前，越迟的越靠后
	private List<String> chargeGambleHistory;
	private List<String> freeExclusiveHistory;
	private List<String> chargeExclusiveHistory;

	public GambleHistoryRecord() {
		chargeGambleHistory = new ArrayList<String>();
		freeExclusiveHistory = new ArrayList<String>();
		chargeExclusiveHistory = new ArrayList<String>();
	}

	public List<String> getChargeGambleHistory() {
		return chargeGambleHistory;
	}

	public void setChargeGambleHistory(List<String> chargeGambleHistory) {
		this.chargeGambleHistory = chargeGambleHistory;
	}

	public List<String> getFreeExclusiveHistory() {
		return freeExclusiveHistory;
	}

	public void setFreeExclusiveHistory(List<String> freeExclusiveHistory) {
		this.freeExclusiveHistory = freeExclusiveHistory;
	}

	public List<String> getChargeExclusiveHistory() {
		return chargeExclusiveHistory;
	}

	public void setChargeExclusiveHistory(List<String> chargeExclusiveHistory) {
		this.chargeExclusiveHistory = chargeExclusiveHistory;
	}

}
