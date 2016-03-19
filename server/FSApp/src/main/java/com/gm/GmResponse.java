package com.gm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GmResponse {
	
	private int status;
	private int count;
	private List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	public void addResult(Map<String,Object> resultMap){
		result.add(resultMap);
	}
	public List<Map<String, Object>> getResult() {
		return result;
	}
	public void setResult(List<Map<String, Object>> result) {
		this.result = result;
	}

	
	
	

}
