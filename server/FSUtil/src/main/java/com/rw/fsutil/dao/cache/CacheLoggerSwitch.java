package com.rw.fsutil.dao.cache;

import java.util.HashMap;

/**
 * 缓存日志开关管理类
 * 
 * @author lida
 *
 */
public class CacheLoggerSwitch {

	private boolean cacheLoggerSwitch = true;
	private HashMap<String, Object> TrackList = new HashMap<String, Object>();

	public static CacheLoggerSwitch instance = new CacheLoggerSwitch();

	public static CacheLoggerSwitch getInstance() {
		return instance;
	}

	public boolean isCacheLoggerSwitch() {
		return cacheLoggerSwitch;
	}

	public boolean isCacheLogger(String name) {
		if (cacheLoggerSwitch) {
			return true;
		}
		return TrackList.containsKey(name);
	}

	public void setCacheLoggerSwitch(boolean cacheLoggerSwitch) {
		this.cacheLoggerSwitch = cacheLoggerSwitch;
	}

	public HashMap<String, Object> getTrackList() {
		return TrackList;
	}

	public void setTrackList(HashMap<String, Object> trackList) {
		TrackList = trackList;
	}

	public void clearTrackList() {
		TrackList.clear();
	}

	public void removeTrackList(String userId) {
		TrackList.remove(userId);
	}

	public void addTrackList(String userId) {
		TrackList.put(userId, userId);
	}
}
