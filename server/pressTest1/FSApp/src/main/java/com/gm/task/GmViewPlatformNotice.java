package com.gm.task;

import java.util.HashMap;
import java.util.Map;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.util.SocketHelper;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rw.manager.GameManager;
import com.rwbase.dao.platformNotice.TablePlatformNotice;
import com.rwbase.dao.platformNotice.TablePlatformNoticeDAO;

public class GmViewPlatformNotice implements IGmTask {

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		try{
			response.setStatus(0);
			response.setCount(1);
			TablePlatformNotice platformNotice = TablePlatformNoticeDAO.getInstance().getPlatformNotice();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("serverId", GameManager.getZoneId());
			map.put("title", platformNotice.getTitle());
			map.put("content", platformNotice.getContent());
			map.put("startTime", platformNotice.getStartTime());
			map.put("endTime", platformNotice.getEndTime());
			response.addResult(map);
			
		}catch(Exception ex){
			SocketHelper.processException(ex, response);
		}
		return response;
	}	
}
