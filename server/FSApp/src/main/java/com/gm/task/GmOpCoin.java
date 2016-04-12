package com.gm.task;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.util.GmUtils;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.UserGameDataMgr;

public class GmOpCoin implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {		
		
		GmResponse response = new GmResponse();
		
		Map<String, Object> args = request.getArgs();
		
		String roleIdList = GmUtils.parseString(args, "roleId");
		long value = GmUtils.parseLong(args, "value");
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
		return response;
	}
	

}
