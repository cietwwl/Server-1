package com.playerdata;

import java.util.List;
import java.util.Map;

import com.rw.service.redpoint.RedPointType;

public class RedPointMgr {

	private volatile int version;//客户端存
	private volatile Map<RedPointType, List<String>> map;//红点集合

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Map<RedPointType, List<String>> getMap() {
		return map;
	}

	public void setMap(Map<RedPointType, List<String>> map) {
		this.map = map;
	}

}
