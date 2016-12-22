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
import com.rwbase.dao.user.accountInfo.UserMappingDAO;
import com.rwbase.dao.user.accountInfo.UserMappingInfo;
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
		//System.out.println("apply role info, openAccountId:"+ paramObj.getAccountId());
//		TableAccount tableAccount = AccoutBM.getInstance().getByOpenAccount(paramObj.getAccountId());
//		if(null == tableAccount){
//			//System.out.println("-------no role data!!!");
//			responseObj.setResult(ResultState.NO_ACCOUNT);
//		}else{
//			AllRolesInfo roles = new AllRolesInfo();
//			roles.setAccountId(paramObj.getAccountId());
//			roles.setRoles(tableAccount.getUserZoneInfoList());
//			responseObj.setContent(JsonUtil.writeValue(roles));
//			responseObj.setResult(ResultState.SUCCESS);
//		}
		
		
		List<UserMappingInfo> userZone = UserMappingDAO.getInstance().getUserZone(paramObj.getAccountId());
		if(userZone == null || userZone.isEmpty()){
			responseObj.setResult(ResultState.NO_ACCOUNT);
		}else{
			AllRolesInfo roles = new AllRolesInfo();
			roles.setAccountId(paramObj.getAccountId());
			roles.setRoles(userZone);
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
