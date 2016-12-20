package com.rw.routerServer;

import java.util.HashMap;
import java.util.Map;

import com.bm.login.AccoutBM;
import com.playerdata.PlayerMgr;
import com.playerdata.readonly.PlayerIF;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rw.manager.GameManager;
import com.rw.routerServer.data.ResultState;
import com.rw.routerServer.data.RouterRespObject;
import com.rw.routerServer.data.params.ReqestParams;
import com.rw.routerServer.giftManger.RouterGiftMgr;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;
import com.rwbase.dao.user.accountInfo.TableAccount;

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

	public String getRoleInfo(String content) {
		RouterRespObject resObj = new RouterRespObject();
		try {
			ReqestParams paramObj = JsonUtil.readValue(content, ReqestParams.class);	
			if(null != paramObj){
				String userID = paramObj.getRoleId();
				User user = UserDataDao.getInstance().getByUserId(userID);
				if(user != null){
					Map<String, Object> dataMap = new HashMap<String, Object>();
					dataMap.put("zoneId", GameManager.getZoneId());
					dataMap.put("userName", user.getUserName());
					dataMap.put("userId", userID);
					dataMap.put("level", user.getLevel());
					dataMap.put("vipLevel", user.getVip());
					String value = JsonUtil.writeValue(dataMap);
					resObj.setContent(value);
					resObj.setResult(ResultState.SUCCESS);
				}else{
					resObj.setResult(ResultState.PARAM_ERROR);
				}
			}else{
				resObj.setResult(ResultState.PARAM_ERROR);
			}
			return JsonUtil.writeValue(resObj);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		resObj.setResult(ResultState.EXCEPTION);
		return JsonUtil.writeValue(resObj);
	}
	

}
