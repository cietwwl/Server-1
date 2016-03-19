package com.gm.task;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.manager.GameManager;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;

public class GmBlockPlayer implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {		
		
		GmResponse response = new GmResponse();
		
		
		
		String accountList =  (String)request.getArgs().get("blockAccount");
		String blockReason =  (String)request.getArgs().get("blockReason");
		long expiresTimeInSecond = (Long)request.getArgs().get("expiresTime");
		if(StringUtils.isNotBlank(accountList)){ 
			String[] accountArray = accountList.split(",");			
			for (String account : accountArray) {
				User user = UserDataDao.getInstance().getByAccoutAndZoneId(account, GameManager.getZoneId());
				Player target = PlayerMgr.getInstance().find(user.getUserId());
				if(target!=null){
					long blockCoolTime = System.currentTimeMillis();
					if(expiresTimeInSecond < 0){
						blockCoolTime = expiresTimeInSecond;
					}else{
						blockCoolTime = blockCoolTime + expiresTimeInSecond*1000;
					}
					target.block(blockReason, blockCoolTime);
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
