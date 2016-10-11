package com.gm.task;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.util.GmUtils;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.manager.GameManager;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;

public class GmBlockRelease implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {		
		
		GmResponse response = new GmResponse();
		Map<String, Object> args = request.getArgs();
		String accountList = GmUtils.parseString(args, "blockAccount");
		if(StringUtils.isNotBlank(accountList)){ 
			String[] accountArray = accountList.split(",");			
			for (String account : accountArray) {
				String[] split = account.split("_");
				String accountValue = split[1];
				User user = UserDataDao.getInstance().getByAccoutAndZoneId(accountValue, GameManager.getZoneId());
				Player target = PlayerMgr.getInstance().find(user.getUserId());
				if(target!=null){
					long blockCoolTime = 0;
					target.block("", blockCoolTime);
				}
			}
			
		}


		response.setStatus(0);
		response.setCount(1);
		return response;
	}
	

}
