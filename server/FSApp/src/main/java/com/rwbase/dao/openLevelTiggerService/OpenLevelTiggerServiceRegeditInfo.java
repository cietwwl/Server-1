package com.rwbase.dao.openLevelTiggerService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 
 * @author Administrator 等级开放时的注册数据；登陆时会把已开启未完成的数据注册；时效到时间后则激活；完成后移除并纪录；这样可以不至于时效任务无限制的空转
 *
 */
public class OpenLevelTiggerServiceRegeditInfo {
	
	
	private Map<Integer, List<Integer>> eventListByType = new HashMap<Integer, List<Integer>>();//type,list(event)的map，目前只有机器人加好友一个type

	public Map<Integer, List<Integer>> getEventListByType() {
		return eventListByType;
	}

	public void setEventListByType(Map<Integer, List<Integer>> eventListByType) {
		this.eventListByType = eventListByType;
	}
	
	
	
	
}
