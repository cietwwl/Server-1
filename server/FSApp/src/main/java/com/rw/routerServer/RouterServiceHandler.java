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
		System.out.println("get gift param:" + param);
		RouterRespObject responseObj = new RouterRespObject();
		try {
			
			System.out.println("before json parse~");
			
			ReqestParams paramObj = JsonUtil.readValue(param, ReqestParams.class);	
			if(null != paramObj){
				ResultState result = RouterGiftMgr.getInstance().addGift(paramObj.getRoleId(), paramObj.getGiftId(), paramObj.getDate());
				responseObj.setResult(result);
			}else{
				System.out.println("json is null");
				responseObj.setResult(ResultState.PARAM_ERROR);
			}
			return JsonUtil.writeValue(responseObj);
			
		} catch (Exception e) {
			System.out.println("parse accour exception!!");
			e.printStackTrace();
		}
		responseObj.setResult(ResultState.EXCEPTION);
		return JsonUtil.writeValue(responseObj);
	}
	

}
