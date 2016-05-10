package com.gm.task;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.gm.GmExecutor;
import com.gm.GmRequest;
import com.gm.GmResponse;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;

public class GmKickOffPlayer implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {		
		
		GmResponse response = new GmResponse();
		
		String userIdList = (String)request.getArgs().get("value");
		if(StringUtils.equals("all", userIdList)){
			GmExecutor.getInstance().submit(new Runnable() {
				
				@Override
				public void run() {
					try {
						PlayerMgr.getInstance().gmKickOffAllPlayer("亲爱的用户，抱歉你已被强制下线，请服务器维护结束再次尝试登录。", false);
					} catch (Throwable e) {
						GameLog.error(LogModule.GM.getName(), "GmKickOffPlayer", "GmKickOffPlayer[doTask] GmExecutor run", e);
					}
				}
			});
			
		}else{
			
			String[] userIdArray = userIdList.split(",");			
			for (String userId : userIdArray) {
				Player target = PlayerMgr.getInstance().findPlayerFromMemory(userId.trim());
				if(target!=null){
					String reason = "亲爱的用户，抱歉你已被强制下线，请5分钟后再次尝试登录。";
					target.KickOffWithCoolTime(reason, true);
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
