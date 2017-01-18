package com.rw.routerServer;

import java.util.List;

import com.bm.login.AccoutBM;
import com.bm.login.ZoneBM;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rw.routerServer.data.ResultState;
import com.rw.routerServer.data.RouterRespObject;
import com.rw.routerServer.data.params.AllAreasInfo;
import com.rw.routerServer.data.params.AllRolesInfo;
import com.rw.routerServer.data.params.ReqestParams;
import com.rwbase.dao.user.accountInfo.TableAccount;
import com.rwbase.dao.zone.TableZoneInfo;

public class RouterServiceHandler {
	
	private static RouterServiceHandler instance = new RouterServiceHandler();
	
	public static RouterServiceHandler getInstance(){
		return instance;
	}
	
	public String getSelfAllRoles(String param){
		ReqestParams paramObj = JsonUtil.readValue(param, ReqestParams.class);		
		RouterRespObject responseObj = new RouterRespObject();
		responseObj.setResult(ResultState.SUCCESS);
		TableAccount tableAccount = AccoutBM.getInstance().getByAccountId(paramObj.getAccountId());
		if(null == tableAccount){
			responseObj.setResult(ResultState.NO_ACCOUNT);
		}else{
			AllRolesInfo roles = new AllRolesInfo();
			roles.setAccountId(paramObj.getAccountId());
			roles.setRoles(tableAccount.getUserZoneInfoList());
			responseObj.setContent(JsonUtil.writeValue(roles));
			responseObj.setResult(ResultState.SUCCESS);
		}
		return JsonUtil.writeValue(responseObj);
	}
	
	public String getAllAreas(){
		List<TableZoneInfo> zoneList = ZoneBM.getInstance().getAllZoneCfg();
		RouterRespObject responseObj = new RouterRespObject();
		if(null == zoneList){
			responseObj.setResult(ResultState.EXCEPTION);
		}else{
			AllAreasInfo areas = new AllAreasInfo();
			areas.setZoneList(zoneList);
			responseObj.setContent(JsonUtil.writeValue(areas));
			responseObj.setResult(ResultState.SUCCESS);
		}
		return JsonUtil.writeValue(responseObj);
	}
}
