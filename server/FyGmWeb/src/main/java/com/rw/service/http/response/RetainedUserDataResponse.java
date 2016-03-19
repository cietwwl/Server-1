package com.rw.service.http.response;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.springframework.util.LinkedMultiValueMap;

public class RetainedUserDataResponse implements Serializable{
	private static final long serialVersionUID = -5182532647273100002L;
	private long recordTime;
	private int createNum;
	private Map<Long, Integer> retainedMap= new HashMap<Long, Integer>();
	
	
	public long getRecordTime() {
		return recordTime;
	}
	public void setRecordTime(long recordTime) {
		this.recordTime = recordTime;
	}
	public int getCreateNum() {
		return createNum;
	}
	public void setCreateNum(int createNum) {
		this.createNum = createNum;
	}
	public Map<Long, Integer> getRetainedMap() {
		return retainedMap;
	}
	public void setRetainedMap(Map<Long, Integer> retainedMap) {
		this.retainedMap = retainedMap;
	}
	
	public void addRetainedCount(long time){
		if(retainedMap.containsKey(time)){
			Integer value = retainedMap.get(time);
			retainedMap.put(time, ++value);
		}else{
			retainedMap.put(time, 1);
		}
	}
}
