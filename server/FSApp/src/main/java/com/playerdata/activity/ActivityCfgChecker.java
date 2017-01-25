package com.playerdata.activity;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityCfg;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityCfgDAO;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfg;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfgDAO;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeCfg;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeCfgDAO;
import com.playerdata.activity.dailyDiscountType.cfg.ActivityDailyDiscountTypeCfg;
import com.playerdata.activity.dailyDiscountType.cfg.ActivityDailyDiscountTypeCfgDAO;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfg;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfgDAO;
import com.playerdata.activity.fortuneCatType.cfg.ActivityFortuneCatTypeCfg;
import com.playerdata.activity.fortuneCatType.cfg.ActivityFortuneCatTypeCfgDAO;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroCfg;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroCfgDAO;
import com.playerdata.activity.rankType.cfg.ActivityRankTypeCfg;
import com.playerdata.activity.rankType.cfg.ActivityRankTypeCfgDAO;
import com.playerdata.activity.rateType.cfg.ActivityRateTypeCfg;
import com.playerdata.activity.rateType.cfg.ActivityRateTypeCfgDAO;
import com.playerdata.activity.redEnvelopeType.cfg.ActivityRedEnvelopeTypeCfg;
import com.playerdata.activity.redEnvelopeType.cfg.ActivityRedEnvelopeTypeCfgDAO;

public class ActivityCfgChecker {

	public static void checkAll(){
		checkCount();//0
		checkDailyCount();//1
		checkRate();//2
//		checkTimeCount();
		checkRedEnvelope();
		checkVitality();
		checkExchange();//6
		checkRank();//
		checkDailyDiscount();//8
		checkFortuneCat();//9
		checkLimitHero();//10
	}

	private static void checkCount() {
		List<ActivityCountTypeCfg> allCfg = ActivityCountTypeCfgDAO.getInstance().getAllCfg();
		for(ActivityCountTypeCfg cfg:allCfg){
			if(cfg.getStartTime() >= cfg.getEndTime()){
				GameLog.cfgError(LogModule.ComActivityCount, null, "时间开启关闭冲突；id =" + cfg.getCfgId());
			}
			for(ActivityCountTypeCfg cfgTmp: allCfg){
				if(cfg.getCfgId() == cfgTmp.getCfgId()){
					continue;
				}
				if(!StringUtils.equals(cfg.getEnumId(), cfgTmp.getEnumId())){
					continue;
				}
				if(cfg.getStartTime()>=cfgTmp.getEndTime()||cfgTmp.getStartTime()>=cfg.getEndTime()){
					continue;
				}
				GameLog.cfgError(LogModule.ComActivityCount, null, "时间冲突；A.id =" + cfg.getCfgId() + " ,B.id = " + cfgTmp.getCfgId());
			}
		}
	}

	private static void checkDailyCount() {
		List<ActivityDailyTypeCfg> allCfg = ActivityDailyTypeCfgDAO.getInstance().getAllCfg();
		for(ActivityDailyTypeCfg cfg:allCfg){
			if(cfg.getStartTime() >= cfg.getEndTime()){
				GameLog.cfgError(LogModule.ComActivityDailyCount, null, "时间开启关闭冲突；id =" + cfg.getId() );
			}
			for(ActivityDailyTypeCfg cfgTmp:allCfg){
				if(cfg.getId() == cfgTmp.getId()){
					continue;
				}
				if(cfg.getEnumId() != cfgTmp.getEnumId()){
					continue;
				}
				if(cfg.getStartTime()>=cfgTmp.getEndTime()||cfgTmp.getStartTime()>=cfg.getEndTime()){
					continue;
				}
				GameLog.cfgError(LogModule.ComActivityDailyCount, null, "时间冲突；A.id =" + cfg.getId() + " ,B。id = " + cfgTmp.getId());
			}
		}
	}
	
	private static void checkRate() {
		List<ActivityRateTypeCfg> allCfg = ActivityRateTypeCfgDAO.getInstance().getAllCfg();
		for(ActivityRateTypeCfg cfg:allCfg){
			if(cfg.getStartTime() >= cfg.getEndTime()){
				GameLog.cfgError(LogModule.ComActivityRate, null, "时间开启关闭冲突；id =" + cfg.getId() );
			}
			for(ActivityRateTypeCfg cfgTmp:allCfg){
				if(cfg.getId() == cfgTmp.getId()){
					continue;
				}
				if(cfg.getEnumId() != cfgTmp.getEnumId()){
					continue;
				}
				if(cfg.getStartTime()>=cfgTmp.getEndTime()||cfgTmp.getStartTime()>=cfg.getEndTime()){
					continue;
				}
				GameLog.cfgError(LogModule.ComActivityRate, null, "时间冲突；A.id =" + cfg.getId() + " ,B。id = " + cfgTmp.getId());
			}
			
		}
	}
	
	private static void checkRedEnvelope() {
		List<ActivityRedEnvelopeTypeCfg> allCfg = ActivityRedEnvelopeTypeCfgDAO.getInstance().getAllCfg();
		for(ActivityRedEnvelopeTypeCfg cfg:allCfg){
			if(cfg.getStartTime() >= cfg.getEndTime()){
				GameLog.cfgError(LogModule.ComActivityRedEnvelope, null, "时间开启关闭冲突；id =" + cfg.getId() );
			}
			for(ActivityRedEnvelopeTypeCfg cfgTmp:allCfg){
				if(cfg.getId() == cfgTmp.getId()){
					continue;
				}
//				if(!StringUtils.equals(cfg.getEnumId(), cfgTmp.getEnumId())){
//					continue;
//				}
				if(cfg.getStartTime()>=cfgTmp.getEndTime()||cfgTmp.getStartTime()>=cfg.getEndTime()){
					continue;
				}
				GameLog.cfgError(LogModule.ComActivityRedEnvelope, null, "时间冲突；A.id =" + cfg.getId() + " ,B。id = " + cfgTmp.getId());
			}
			
		}
	}

	private static void checkVitality() {
		// TODO Auto-generated method stub
		List<ActivityVitalityCfg> allCfg = ActivityVitalityCfgDAO.getInstance().getAllCfg();
		for(ActivityVitalityCfg cfg:allCfg){
			if(cfg.getStartTime() >= cfg.getEndTime()){
				GameLog.cfgError(LogModule.ComActivityVitality, null, "时间开启关闭冲突；id =" + cfg.getId() );
			}
			for(ActivityVitalityCfg cfgTmp:allCfg){
				if(cfg.getId() == cfgTmp.getId()){
					continue;
				}
				if(cfg.getEnumID() != cfgTmp.getEnumID()){
					continue;
				}
				if(cfg.getStartTime()>=cfgTmp.getEndTime()||cfgTmp.getStartTime()>=cfg.getEndTime()){
					continue;
				}
				GameLog.cfgError(LogModule.ComActivityVitality, null, "时间冲突；A.id =" + cfg.getId() + " ,B。id = " + cfgTmp.getId());
			}
			
		}
	}
	
	private static void checkExchange() {
		List<ActivityExchangeTypeCfg> allCfg = ActivityExchangeTypeCfgDAO.getInstance().getAllCfg();
		for(ActivityExchangeTypeCfg cfg:allCfg){
			if(cfg.getChangeStartTime() >= cfg.getChangeEndTime()){
				GameLog.cfgError(LogModule.ComActivityExchange, null, "时间开启关闭冲突；id =" + cfg.getId() );
			}
			for(ActivityExchangeTypeCfg cfgTmp:allCfg){
				if(cfg.getId() == cfgTmp.getId()){
					continue;
				}
				if(!StringUtils.equals(cfg.getEnumId(), cfgTmp.getEnumId())){
					continue;
				}
				if(cfg.getChangeStartTime()>=cfgTmp.getChangeEndTime()||cfgTmp.getChangeStartTime()>=cfg.getChangeEndTime()){
					continue;
				}
				GameLog.cfgError(LogModule.ComActivityExchange, null, "时间冲突；A.id =" + cfg.getId() + " ,B。id = " + cfgTmp.getId());
			}
			
		}
	}
	
	private static void checkRank() {
		List<ActivityRankTypeCfg> allCfg = ActivityRankTypeCfgDAO.getInstance().getAllCfg();
		for(ActivityRankTypeCfg cfg:allCfg){
			if(cfg.getStartTime() >= cfg.getEndTime()){
				GameLog.cfgError(LogModule.ComActivityRank, null, "时间开启关闭冲突；id =" + cfg.getId() );
			}
			for(ActivityRankTypeCfg cfgTmp:allCfg){
				if(cfg.getId() == cfgTmp.getId()){
					continue;
				}
				if(cfg.getEnumId() != cfgTmp.getEnumId()){
					continue;
				}
				if(cfg.getStartTime()>=cfgTmp.getEndTime()||cfgTmp.getStartTime()>=cfg.getEndTime()){
					continue;
				}
				GameLog.cfgError(LogModule.ComActivityRank, null, "时间冲突；A.id =" + cfg.getId() + " ,B。id = " + cfgTmp.getId());
			}
		}
	}
	
	private static void checkDailyDiscount() {
		List<ActivityDailyDiscountTypeCfg>  allCfg = ActivityDailyDiscountTypeCfgDAO.getInstance().getAllCfg();
		for(ActivityDailyDiscountTypeCfg cfg:allCfg){
			if(cfg.getStartTime() >= cfg.getEndTime()){
				GameLog.cfgError(LogModule.ComActivityDailyDisCount, null, "时间开启关闭冲突；id =" + cfg.getId() );
			}
			for(ActivityDailyDiscountTypeCfg cfgTmp:allCfg){
				if(cfg.getId() == cfgTmp.getId()){
					continue;
				}
				if(cfg.getEnumId() != cfgTmp.getEnumId()){
					continue;
				}
				if(cfg.getStartTime()>=cfgTmp.getEndTime()||cfgTmp.getStartTime()>=cfg.getEndTime()){
					continue;
				}
				GameLog.cfgError(LogModule.ComActivityDailyDisCount, null, "时间冲突；A.id =" + cfg.getId() + " ,B。id = " + cfgTmp.getId());
			}
			
		}
	}
	
	private static void checkFortuneCat() {
		List<ActivityFortuneCatTypeCfg>  allCfg = ActivityFortuneCatTypeCfgDAO.getInstance().getAllCfg();
		for(ActivityFortuneCatTypeCfg cfg:allCfg){
			if(cfg.getStartTime() >= cfg.getEndTime()){
				GameLog.cfgError(LogModule.ComActivityFortuneCat, null, "时间开启关闭冲突；id =" + cfg.getId() );
			}
			for(ActivityFortuneCatTypeCfg cfgTmp:allCfg){
				if(cfg.getId() == cfgTmp.getId()){
					continue;
				}
				if(cfg.getStartTime()>=cfgTmp.getEndTime()||cfgTmp.getStartTime()>=cfg.getEndTime()){
					continue;
				}
				GameLog.cfgError(LogModule.ComActivityFortuneCat, null, "时间冲突；A.id =" + cfg.getId() + " ,B。id = " + cfgTmp.getId());
			}
		}		
	}
	
	private static void checkLimitHero() {
		List<ActivityLimitHeroCfg>  allCfg = ActivityLimitHeroCfgDAO.getInstance().getAllCfg();
		for(ActivityLimitHeroCfg cfg:allCfg){
			if(cfg.getStartTime() >= cfg.getEndTime()){
				GameLog.cfgError(LogModule.ComActivityLimitHero, null, "时间开启关闭冲突；id =" + cfg.getId() );
			}
			for(ActivityLimitHeroCfg cfgTmp:allCfg){
				if(cfg.getId() == cfgTmp.getId()){
					continue;
				}
				if(cfg.getStartTime()>=cfgTmp.getEndTime()||cfgTmp.getStartTime()>=cfg.getEndTime()){
					continue;
				}
				GameLog.cfgError(LogModule.ComActivityLimitHero, null, "时间冲突；A.id =" + cfg.getId() + " ,B。id = " + cfgTmp.getId());
			}			
		}	
		
	}
}
