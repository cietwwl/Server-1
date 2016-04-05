package com.gm.task;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.util.GmUtils;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;

public class GmChatBanRelease implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {		
		
		GmResponse response = new GmResponse();
		
		Map<String, Object> args = request.getArgs();
		
		String roleIdList =  GmUtils.parseString(args, "roleId");
		if(StringUtils.isNotBlank(roleIdList)){ 
			String[] roleIdArray = roleIdList.split(",");			
			for (String roleId : roleIdArray) {
				Player target = PlayerMgr.getInstance().find(roleId);
				if(target!=null){
					long coolTime = 0;
					target.chatBan("", coolTime);
				}
			}
			
		}


		response.setStatus(0);
		response.setCount(1);
		return response;
	}
	

}
