package com.gm.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bm.serverStatus.ServerStatusMgr;
import com.common.playerFilter.FilterType;
import com.common.playerFilter.PlayerFilterCondition;
import com.gm.GmExecutor;
import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.GmResultStatusCode;
import com.gm.util.GmUtils;
import com.gm.util.SocketHelper;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.PlayerMgr;
import com.rwbase.dao.email.EmailData;
import com.rwbase.dao.serverData.ServerGmEmail;

public class GmEmailAll implements IGmTask {

	public final static int STATUS_SEND = 1;
	public final static int STATUS_CLOSE = 2;
	public final static int STATUS_DELETE = 3;

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		try {
			long taskId = GmUtils.parseLong(request.getArgs(), "taskId");
			int status = GmUtils.parseInt(request.getArgs(), "status");
			boolean blnUpdate = false;
			ServerGmEmail gmMail = ServerStatusMgr.getGmMail(taskId);
			if (gmMail == null) {
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_NOT_FIND_GMMAIL.getStatus()));
			}
			if (status == STATUS_CLOSE) {
				gmMail.setStatus(STATUS_CLOSE);

			}
			if (status == STATUS_SEND) {
				if(gmMail.getStatus() == STATUS_CLOSE || gmMail.getStatus() == STATUS_DELETE){
					throw new Exception(String.valueOf(GmResultStatusCode.STATUS_GMMAIL_CLOSE.getStatus()));
				}
				final EmailData emailData = gmMail.getSendToAllEmailData();
				final List<PlayerFilterCondition> conditionList = gmMail.getConditionList();

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
				for (PlayerFilterCondition condition : conditionList) {
					if(condition.getType() == FilterType.CREATE_TIME.getValue()){
						long endTime = condition.getMaxValue() * 1000;
						if(endTime <= System.currentTimeMillis()){
							gmMail.setStatus(STATUS_CLOSE);
						}
					}else{
						gmMail.setStatus(STATUS_CLOSE);
					}
				}
				
				blnUpdate = true;
			}
			if(status == STATUS_DELETE){
				final EmailData emailData = gmMail.getSendToAllEmailData();
				GmExecutor.getInstance().submit(new Runnable() {

					@Override
					public void run() {
						try {
							PlayerMgr.getInstance().callbackEmail(emailData);
						} catch (Throwable e) {
							GameLog.error(LogModule.GM.getName(), "GmEmailAll", "GmEmailAll[doTask] GmExecutor run", e);
						}
					}
				});
				gmMail.setStatus(STATUS_DELETE);
				blnUpdate = true;
			}
			if(blnUpdate){
				ServerStatusMgr.updateGmMail(gmMail);
			}
			
			response.setStatus(0);
			response.setCount(1);
			Map<String, Object> resultMap = new HashMap<String, Object>();
			response.addResult(resultMap);
		} catch (Exception ex) {
			SocketHelper.processException(ex, response);
		}
		return response;
	}

	/**
	 * 回收邮件
	 */
	public void callBackMail(){
		
	}
}
