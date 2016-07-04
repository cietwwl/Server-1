package com.rw.service.log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.rw.common.SynTask;
import com.rw.common.SynTaskExecutor;
import com.rw.fsutil.util.DateUtils;
import com.rw.service.log.eLog.eBILogType;
import com.rw.service.log.infoPojo.ClientInfo;
import com.rw.service.log.template.AccountLoginLogTemplate;
import com.rw.service.log.template.AccountRegLogTemplate;
import com.rw.service.log.template.BILogTemplate;
import com.rw.service.log.template.ModelRegLogTemplate;

public class BILogMgr {
	
	private static Logger biLog = Logger.getLogger("biLog");
	private static BILogMgr instance = new BILogMgr();
	
	private static Map<eBILogType, Logger> LogMap = new HashMap<eBILogType, Logger>();
	
	private Map<eBILogType, BILogTemplate> templateMap;
	
	public static BILogMgr getInstance(){
		return instance;
	}
	

	private BILogMgr(){
		
		templateMap = new HashMap<eBILogType, BILogTemplate>();
		templateMap.put(eBILogType.AccountRegLog, new AccountRegLogTemplate());
		templateMap.put(eBILogType.AccountLoginLog, new AccountLoginLogTemplate());
		templateMap.put(eBILogType.ModelRegLog, new ModelRegLogTemplate());
	
		
	}
	
	private Logger getLogger(eBILogType type){
		if(LogMap.containsKey(type)){
			return LogMap.get(type);
		}else{
			Logger logger = Logger.getLogger(type.getLogName());
			try {

				logger.removeAllAppenders();
				logger.setAdditivity(false);
				PatternLayout layout = new PatternLayout();
				layout.setConversionPattern("[%-5p] %m%n");
				DailyRollingFileAppender appender;

				appender = new DailyRollingFileAppender(layout, "./log/biLog/" + type.getLogName()+"/"+type.getLogName(), "yyyy-MM-dd");

				logger.addAppender(appender);
				LogMap.put(type, logger);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return logger;
		}
	}
	
	/**手机硬件信息只在极刑注册处有用*/
	public void logAccountReg(ClientInfo clientInfo, Long registerTime, RegLog reglog,boolean success){
		
		Map<String,String> moreInfo = new HashMap<String, String>();
		moreInfo.put("registerTime", DateUtils.getDateTimeFormatString(registerTime, "yyyy-MM-dd HH:mm:ss"));
		if(success){
			moreInfo.put("result", "1");
		}else{
			moreInfo.put("result", "0");
		}
		
		log(eBILogType.AccountRegLog, clientInfo, moreInfo,reglog);
		log(eBILogType.AccountLoginLog, clientInfo, moreInfo,reglog);
//		log(eBILogType.ModelRegLog, clientInfo, moreInfo,reglog);
		
		
	}
	
	
	public void logAccountLogin(ClientInfo clientInfo, Long registerTime, boolean success){
		
		Map<String,String> moreInfo = new HashMap<String, String>();
		moreInfo.put("registerTime", DateUtils.getDateTimeFormatString(registerTime, "yyyy-MM-dd HH:mm:ss"));
		if(success){
			moreInfo.put("result", "1");
		}else{
			moreInfo.put("result", "0");
		}
		
		log(eBILogType.AccountLoginLog, clientInfo, moreInfo,null);
		
	}
	


	
	private void log(final eBILogType logType, ClientInfo clientInfo, Map<String,String> moreInfo,RegLog reglog){
		
		
		
		final BILogTemplate logTemplate = templateMap.get(logType);
		if(logTemplate!=null){
			long logTime = System.currentTimeMillis();
			if(moreInfo == null){
				moreInfo = new HashMap<String, String>();
			}
			moreInfo.put("logTime", DateUtils.getDateTimeFormatString(logTime, "yyyy-MM-dd HH:mm:ss"));
			final String log = logTemplate.build(clientInfo, moreInfo,reglog);
			
			
			SynTaskExecutor.submitTask(new SynTask() {
				
				@Override
				public void dotask() {
//					biLog.info(logType + " " + logTemplate.getTextTemplate());
					Logger logger = getLogger(logType);
					logger.info(log);
					LogService.getInstance().sendLog(log);
				}
			});
			
		}
		
		
		
	}
	
}
