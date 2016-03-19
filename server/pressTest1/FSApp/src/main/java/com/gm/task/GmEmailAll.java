package com.gm.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.common.playerFilter.PlayerFilterCondition;
import com.gm.GmExecutor;
import com.gm.GmRequest;
import com.gm.GmResponse;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.PlayerMgr;
import com.rwbase.dao.email.EmailData;

public class GmEmailAll implements IGmTask{

	
	
	
	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		
		response.setCount(1);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		final EmailData emailData = GmEmailHelper.getSendToAllEmailData();		
		final List<PlayerFilterCondition> conditionList = GmEmailHelper.getConditionList();
		if(emailData == null){
			response.setStatus(1);
			resultMap.put("reason", "没有待发邮件。");
		}else{
			response.setStatus(0);
			GmExecutor.getInstance().submit(new Runnable() {
				
				@Override
				public void run() {
					try {
						PlayerMgr.getInstance().sendEmailToAll(emailData, conditionList);
					} catch (Throwable e) {
						GameLog.error(LogModule.GM.getName(), "GmEmailAll", "GmEmailAll[doTask] GmExecutor run", e);
					}
				}
			});
		}

		response.addResult(resultMap );
		return response;
	}


}
