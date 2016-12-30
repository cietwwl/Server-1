package com.rw.routerServer;

import com.rw.fsutil.util.jackson.JsonUtil;
import com.rw.routerServer.data.ResultState;
import com.rw.routerServer.data.RouterRespObject;
import com.rw.routerServer.data.params.ReqestParams;
import com.rw.routerServer.giftManger.RouterGiftMgr;

public class RouterServiceHandler {
	
	private static RouterServiceHandler instance = new RouterServiceHandler();
	
	public static RouterServiceHandler getInstance(){
		return instance;
	}
	
	public String getGift(String param){
		RouterRespObject responseObj = new RouterRespObject();
		try {
			
			ReqestParams paramObj = JsonUtil.readValue(param, ReqestParams.class);	
			if(null != paramObj){
				ResultState result = RouterGiftMgr.getInstance().addGift(paramObj.getRoleId(), paramObj.getGiftId(), paramObj.getDate());
				responseObj.setResult(result);
			}else{
				responseObj.setResult(ResultState.PARAM_ERROR);
			}
			return JsonUtil.writeValue(responseObj);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		responseObj.setResult(ResultState.EXCEPTION);
		return JsonUtil.writeValue(responseObj);
	}
	

}
