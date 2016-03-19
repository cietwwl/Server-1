package com.gm.task;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.util.SocketHelper;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rw.manager.GameManager;
import com.rw.service.gamenotice.GameNoticeService;
import com.rwbase.dao.gameNotice.TableGameNotice;

public class GmViewGameNotice implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {
		// TODO Auto-generated method stub
		GmResponse response = new GmResponse();
		try{
			//int serverId = Integer.parseInt(request.getArgs().get("serverId").toString());
			
			HashMap<Integer,TableGameNotice> gameNotices = GameManager.getGameNotice().getGameNotices();
			
			for (Iterator<Entry<Integer, TableGameNotice>> iterator = gameNotices.entrySet().iterator(); iterator.hasNext();) {
				Entry<Integer, TableGameNotice> entry = iterator.next();
				TableGameNotice tableGameNotice = entry.getValue();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("serverId", GameManager.getZoneId());
				map.put("noticeId", tableGameNotice.getNoticeId());
				map.put("title", tableGameNotice.getTitle());
				map.put("content", tableGameNotice.getContent());
				map.put("startTime", tableGameNotice.getStartTime());
				map.put("endTime", tableGameNotice.getEndTime());
				response.addResult(map);
			}
			
			response.setStatus(0);
			response.setCount(1);
		}catch(Exception ex){
			SocketHelper.processException(ex, response);
		}
		return response;
	}
}
