package com.gm.task;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.bm.login.ZoneBM;
import com.bm.serverStatus.ServerStatusMgr;
import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.util.GmUtils;
import com.rw.manager.GameManager;
import com.rw.manager.ServerVersionConfig;
import com.rw.netty.ServerConfig;
import com.rw.netty.UserChannelMgr;
import com.rwbase.dao.zone.TableZoneInfo;

public class GmServerStatus implements IGmTask {

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();

		Map<String, Object> args = request.getArgs();
		String serverId = GmUtils.parseString(args, "serverId");

		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		TableZoneInfo tableZoneInfo = ZoneBM.getInstance().getTableZoneInfo(GameManager.getZoneId());
		resultMap.put("serverId", GameManager.getServerId());
		resultMap.put("serverName", tableZoneInfo.getZoneName());
		int onlineCount = UserChannelMgr.getCount();
		resultMap.put("onlineTotal", onlineCount);
		resultMap.put("limitTotal", ServerStatusMgr.getOnlineLimit());
		resultMap.put("version", ServerVersionConfig.getInstance().getVersion());
		int status = ServerStatusMgr.getStatus().ordinal();
		resultMap.put("type", status);
		
		response.addResult(resultMap);
		
		response.setStatus(0);
		response.setCount(1);
		return response;
	}

}
