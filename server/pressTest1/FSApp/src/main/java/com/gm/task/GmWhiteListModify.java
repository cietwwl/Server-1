package com.gm.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.bm.login.AccoutBM;
import com.bm.serverStatus.ServerStatusMgr;
import com.gm.GmRequest;
import com.gm.GmResponse;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.readonly.PlayerIF;
import com.rw.service.http.HttpServer;
import com.rw.service.http.platformResponse.WhiteListBaseDataResponse;

public class GmWhiteListModify implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		
		WhiteListBaseDataResponse whiteListBaseDataResponse = new WhiteListBaseDataResponse();
		List<String> accountList = new ArrayList<String>();
		
		String addUserIdStr = (String)request.getArgs().get("add");
		if(StringUtils.isNotBlank(addUserIdStr)){//开启
			String[] split = addUserIdStr.split(",");
			for (String userId : split) {
				if(StringUtils.isNotBlank(userId)){
					ServerStatusMgr.addWhite(userId.trim());
					
					Player player = PlayerMgr.getInstance().find(userId.trim());
					String account = player.getTableUser().getAccount();
					accountList.add(account);
				}
			}
			
			
			whiteListBaseDataResponse.setBlnClose(ServerStatusMgr.isWhilteListON());
			whiteListBaseDataResponse.setAccountList(accountList);
			whiteListBaseDataResponse.setProcess("add");
		}
		String delUserIdStr = (String)request.getArgs().get("del");
		
		if(StringUtils.isNotBlank(delUserIdStr)){ //关闭
			String[] split = delUserIdStr.split(",");
			for (String userId : split) {
				if(StringUtils.isNotBlank(userId)){
					ServerStatusMgr.removeWhite(userId.trim());
					
					Player player = PlayerMgr.getInstance().find(userId.trim());
					String account = player.getTableUser().getAccount();
					accountList.add(account);
				}
			}
			
			whiteListBaseDataResponse.setBlnClose(ServerStatusMgr.isWhilteListON());
			whiteListBaseDataResponse.setAccountList(accountList);
			whiteListBaseDataResponse.setProcess("del");
		}
		
		if (accountList.size() > 0) {
			HttpServer.SendResponse("com.rw.netty.http.requestHandler.WhiteListHandler", "updateWhiteList", whiteListBaseDataResponse, WhiteListBaseDataResponse.class);
		}
		
		response.setStatus(0);
		response.setCount(1);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("value", 0);
		response.addResult(resultMap );
		return response;
	}
	

}
