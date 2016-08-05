package com.gm.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bm.serverStatus.ServerStatus;
import com.bm.serverStatus.ServerStatusMgr;
import com.gm.GmRequest;
import com.gm.GmResponse;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.service.http.HttpServer;
import com.rw.service.http.platformResponse.PlatformNoticeBaseDataResponse;
import com.rw.service.http.platformResponse.WhiteListBaseDataResponse;
import com.rw.service.platformService.PlatformService;
import com.rwbase.dao.user.platformwhitelist.TablePlatformWhiteList;
import com.rwbase.dao.user.platformwhitelist.TablePlatformWhiteListDataHolder;

public class GmWhiteListSwitch implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		boolean blnClose = false;
		int status = (Integer)request.getArgs().get("value");
		if(status == 0){//开启
			open();
			blnClose = false;
		}else{ //关闭
			close();
			blnClose = true;
		}

		List<String> whiteList = ServerStatusMgr.getWhiteList();
		List<String> accountList = new ArrayList<String>();
		WhiteListBaseDataResponse whiteListBaseDataResponse = new WhiteListBaseDataResponse();
		whiteListBaseDataResponse.setBlnClose(blnClose);
		for (String accountId : whiteList) {
			accountList.add(accountId);
		}
		
		for (String accountId : accountList) {
			TablePlatformWhiteListDataHolder holder = new TablePlatformWhiteListDataHolder(accountId);
			TablePlatformWhiteList tablePlatformWhiteList = holder.getTablePlatformWhiteList();
			tablePlatformWhiteList.setAccountId(accountId);
			tablePlatformWhiteList.setClose(blnClose);
			holder.saveItem(tablePlatformWhiteList);
		}
		
		whiteListBaseDataResponse.setAccountList(accountList);
		whiteListBaseDataResponse.setProcess("close");
		
		PlatformService.SendResponse("com.rw.netty.http.requestHandler.WhiteListHandler", "updateWhiteList", whiteListBaseDataResponse, WhiteListBaseDataResponse.class);
		
		response.setStatus(0);
		response.setCount(1);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("value", 0);
		response.addResult(resultMap );
		return response;
	}
	
	private void open(){
		ServerStatusMgr.switchWhiteList(true);
	}
	private void close(){
		ServerStatusMgr.switchWhiteList(false);
	}

}
