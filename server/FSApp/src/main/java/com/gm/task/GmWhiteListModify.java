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
import com.rw.service.platformService.PlatformService;
import com.rwbase.dao.user.accountInfo.TableAccount;

public class GmWhiteListModify implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		
		WhiteListBaseDataResponse whiteListBaseDataResponse = new WhiteListBaseDataResponse();
		List<String> accountList = new ArrayList<String>();
		
		String accountIdStr = (String)request.getArgs().get("add");
		if(StringUtils.isNotBlank(accountIdStr)){//开启
			String[] split = accountIdStr.split(",");
			for (String accountId : split) {
				if(StringUtils.isNotBlank(accountId)){
					TableAccount tableAccount = AccoutBM.getInstance().getByAccountId(accountId);
					if (tableAccount != null) {
						ServerStatusMgr.addWhite(accountId.trim());
						accountList.add(accountId);
					}
				}
			}
			whiteListBaseDataResponse.setBlnClose(!ServerStatusMgr.isWhilteListON());
			whiteListBaseDataResponse.setAccountList(accountList);
			whiteListBaseDataResponse.setProcess("add");
		}
		String delAccountIdStr = (String)request.getArgs().get("del");
		
		if(StringUtils.isNotBlank(delAccountIdStr)){ //关闭
			String[] split = delAccountIdStr.split(",");
			for (String accountId : split) {
				if(StringUtils.isNotBlank(accountId)){
					ServerStatusMgr.removeWhite(accountId.trim());
					accountList.add(accountId);
				}
			}
			
			whiteListBaseDataResponse.setBlnClose(!ServerStatusMgr.isWhilteListON());
			whiteListBaseDataResponse.setAccountList(accountList);
			whiteListBaseDataResponse.setProcess("del");
		}
		
		if (accountList.size() > 0) {
			PlatformService.SendResponse("com.rw.netty.http.requestHandler.WhiteListHandler", "updateWhiteList", whiteListBaseDataResponse, WhiteListBaseDataResponse.class);
		}
		
		response.setStatus(0);
		response.setCount(1);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("value", 0);
		response.addResult(resultMap );
		return response;
	}
	

}
