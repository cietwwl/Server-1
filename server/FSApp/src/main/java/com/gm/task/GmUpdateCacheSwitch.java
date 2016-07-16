package com.gm.task;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.util.GmUtils;
import com.rw.fsutil.dao.cache.CacheLoggerSwitch;

public class GmUpdateCacheSwitch implements IGmTask {

	private final static int COMMAND_TYPE_SWITCH = 1;   //控制开关
	private final static int COMMAND_TYPE_LIST = 2;		//控制跟踪名单
	
	@Override
	public GmResponse doTask(GmRequest request) {
		// TODO Auto-generated method stub
		GmResponse response = new GmResponse();

		response.setStatus(0);
		response.setCount(1);
		
		Map<String, Object> args = request.getArgs();
		int type = GmUtils.parseInt(args, "type");
		if (type == COMMAND_TYPE_SWITCH) {
			handlerCacheLoggerTrackList(args);
		}
		if (type == COMMAND_TYPE_LIST) {
			handlerCacheLoggerTrackList(args);
		}
		return response;
	}

	private void handlerCacheLoggerTrackList(Map<String, Object> args) {
		String userIds = GmUtils.parseString(args, "userIds");
		String[] ids = userIds.split(",");
		if(StringUtils.isBlank(userIds)){
			CacheLoggerSwitch.getInstance().clearTrackList();
		}else{
			String option = GmUtils.parseString(args, "option");
			if(option.equals("delete")){
				for (String id : ids) {
					CacheLoggerSwitch.getInstance().removeTrackList(id);
				}
			}
			if(option.equals("add")){
				for (String id : ids) {
					CacheLoggerSwitch.getInstance().addTrackList(id);
				}
			}
		}
	}
	
	public void handlerCacheLoggerSwitch(Map<String, Object> args){
		boolean value = GmUtils.parseBoolean(args, "value");
		CacheLoggerSwitch.getInstance().setCacheLoggerSwitch(value);
	}

}
