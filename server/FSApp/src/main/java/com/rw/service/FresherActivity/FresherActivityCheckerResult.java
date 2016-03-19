package com.rw.service.FresherActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FresherActivityCheckerResult {
	
	public FresherActivityCheckerResult(){
		
	}
	private Map<Integer, String> currentProgress = new HashMap<Integer, String>();
	private List<Integer> completeList = new ArrayList<Integer>();

	public List<Integer> getCompleteList() {
		return completeList;
	}
	public void setCompleteList(List<Integer> completeList) {
		this.completeList = completeList;
	}
	public Map<Integer, String> getCurrentProgress() {
		return currentProgress;
	}
	public void setCurrentProgress(Map<Integer, String> currentProgress) {
		this.currentProgress = currentProgress;
	}
}
