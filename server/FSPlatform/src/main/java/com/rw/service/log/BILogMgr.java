package com.rw.service.log;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.rw.common.SynTask;
import com.rw.common.SynTaskExecutor;
import com.rw.fsutil.util.DateUtils;
import com.rw.service.log.eLog.eBILogType;
import com.rw.service.log.infoPojo.ClientInfo;
import com.rw.service.log.template.AccountLoginLogTemplate;
import com.rw.service.log.template.AccountRegLogTemplate;
import com.rw.service.log.template.BILogTemplate;

public class BILogMgr {
	
	private static Logger biLog = Logger.getLogger("biLog");
	private static BILogMgr instance = new BILogMgr();
	
	private Map<eBILogType, BILogTemplate> templateMap;
	
	public static BILogMgr getInstance(){
		return instance;
	}
	
	private BILogMgr(){
		
		templateMap = new HashMap<eBILogType, BILogTemplate>();
		templateMap.put(eBILogType.AccountRegLog, new AccountRegLogTemplate());
		templateMap.put(eBILogType.AccountLoginLog, new AccountLoginLogTemplate());
	
		
	}
	
	
	public void logAccountReg(ClientInfo clientInfo, Long registerTime, boolean success){
		
		Map<String,String> moreInfo = new HashMap<String, String>();
		moreInfo.put("registerTime", registerTime.toString());
		if(success){
			moreInfo.put("result", "1");
		}else{
			moreInfo.put("result", "0");
		}
		
		log(eBILogType.AccountRegLog, clientInfo, moreInfo);
		log(eBILogType.AccountLoginLog, clientInfo, moreInfo);
		
	}
	
	
	public void logAccountLogin(ClientInfo clientInfo, Long registerTime, boolean success){
		
		Map<String,String> moreInfo = new HashMap<String, String>();
		moreInfo.put("registerTime", registerTime.toString());
		if(success){
			moreInfo.put("result", "1");
		}else{
			moreInfo.put("result", "0");
		}
		
		log(eBILogType.AccountLoginLog, clientInfo, moreInfo);
		
	}
	


	
	private void log(final eBILogType logType, ClientInfo clientInfo, Map<String,String> moreInfo){
		
		
		
		final BILogTemplate logTemplate = templateMap.get(logType);
		if(logTemplate!=null){
			long logTime = System.currentTimeMillis();
			if(moreInfo == null){
				moreInfo = new HashMap<String, String>();
			}
			moreInfo.put("logTime", DateUtils.getDateTimeFormatString(logTime, "yyyy-MM-dd HH:mm:ss"));
			final String log = logTemplate.build(clientInfo, moreInfo);
			
			
			SynTaskExecutor.submitTask(new SynTask() {
				
				@Override
				public void dotask() {
					biLog.info(log);
					LogService.getInstance().sendLog(log);
				}
			});
			
		}
		
		
		
	}
	
}
