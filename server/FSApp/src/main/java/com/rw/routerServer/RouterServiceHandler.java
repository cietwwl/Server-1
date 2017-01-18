package com.rw.routerServer;

import com.bm.login.AccoutBM;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rw.routerServer.data.params.AllRolesInfo;
import com.rw.routerServer.data.params.ReqestParams;
import com.rw.service.http.request.ResponseObject;
import com.rwbase.dao.user.accountInfo.TableAccount;

public class RouterServiceHandler {
	
	private static RouterServiceHandler instance = new RouterServiceHandler();
	
	public static RouterServiceHandler getInstance(){
		return instance;
	}
	
	public String getSelfAllRoles(String param){
		ReqestParams paramObj = JsonUtil.readValue(param, ReqestParams.class);		
		ResponseObject responseObj = new ResponseObject();
		responseObj.setSuccess(true);
		TableAccount tableAccount = AccoutBM.getInstance().getByAccountId(paramObj.getAccountId());
		AllRolesInfo roles = new AllRolesInfo();
		roles.setAccountId(paramObj.getAccountId());
		roles.setRoles(tableAccount.getUserZoneInfoList());
		responseObj.setResult(JsonUtil.writeValue(roles));
		return JsonUtil.writeValue(responseObj);
	}
}
