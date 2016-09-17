package com.rw.fsutil.dao.cache;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存日志开关管理类
 * @author lida
 *
 */
public class CacheLoggerSwitch {
	
	private boolean cacheLoggerSwitch = true;
	private List<String> TrackList = new ArrayList<String>(); 
	
	public static CacheLoggerSwitch instance = new CacheLoggerSwitch();
	
	public static CacheLoggerSwitch getInstance(){
		return instance;
	}

	public boolean isCacheLoggerSwitch() {
		return cacheLoggerSwitch;
	}

	public void setCacheLoggerSwitch(boolean cacheLoggerSwitch) {
		this.cacheLoggerSwitch = cacheLoggerSwitch;
	}

	public List<String> getTrackList() {
		return TrackList;
	}

	public void setTrackList(List<String> trackList) {
		TrackList = trackList;
	}
	
	public void clearTrackList(){
		TrackList.clear();
	}
	
	public void removeTrackList(String userId){
		TrackList.remove(userId);
	}
	
	public void addTrackList(String userId){
		TrackList.add(userId);
	}
}
