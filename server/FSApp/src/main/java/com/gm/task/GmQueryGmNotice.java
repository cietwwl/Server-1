package com.gm.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bm.serverStatus.ServerStatusMgr;
import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.util.SocketHelper;
import com.rw.manager.GameManager;
import com.rwbase.dao.platformNotice.TablePlatformNotice;
import com.rwbase.dao.platformNotice.TablePlatformNoticeDAO;
import com.rwbase.dao.serverData.GmNoticeInfo;
import com.rwbase.dao.serverData.ServerGmNotice;

public class GmQueryGmNotice implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {
		// TODO Auto-generated method stub
		GmResponse response = new GmResponse();
		try {
			response.setStatus(0);
			response.setCount(1);
			List<ServerGmNotice> allGmNotice = ServerStatusMgr.getAllGmNotice();
			for (ServerGmNotice serverGmNotice : allGmNotice) {

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("noticeId", serverGmNotice.getId());
				GmNoticeInfo noticeInfo = serverGmNotice.getNoticeInfo();
				map.put("serverId", GameManager.getServerId());
				map.put("title", noticeInfo.getTitle());
				map.put("content", noticeInfo.getContent());
				map.put("startTime", noticeInfo.getStartTime() / 1000);
				map.put("endTime", noticeInfo.getEndTime() / 1000);
				map.put("cycleInterval", noticeInfo.getCycleInterval());
				map.put("priority", noticeInfo.getPriority());
				response.addResult(map);
			}
			
			
		} catch (Exception ex) {
			SocketHelper.processException(ex, response);
		}
		return response;
	}
	
}
