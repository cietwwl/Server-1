package com.gm.task;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.UserGameDataMgr;

public class GmOpCoin implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {		
		
		GmResponse response = new GmResponse();
		
		
		
		String roleIdList =  (String)request.getArgs().get("roleId");
		long value =  Long.parseLong(request.getArgs().get("value").toString());
		if(StringUtils.isNotBlank(roleIdList)){ 
			String[] roleIdArray = roleIdList.split(",");			
			for (String roleId : roleIdArray) {
				Player target = PlayerMgr.getInstance().find(roleId);
				if(target!=null){
					UserGameDataMgr userGameDataMgr = target.getUserGameDataMgr();
					long currentCoin = userGameDataMgr.getCoin();
					if(currentCoin + value < 0){
						value = -currentCoin;
					}
					
					userGameDataMgr.addCoin((int)value);
				}
			}
			
		}


		response.setStatus(0);
		response.setCount(1);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("value", 0);
		response.addResult(resultMap );
		return response;
	}
	

}
