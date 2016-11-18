package com.playerdata.activityCommon.activityType;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.activity.countType.ActivityCountTypeMgr;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfgDAO;
import com.playerdata.activity.countType.cfg.ActivityCountTypeSubCfgDAO;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeSubItem;
import com.playerdata.activity.dailyCharge.ActivityDailyRechargeTypeMgr;
import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeCfgDAO;
import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeSubCfgDAO;
import com.playerdata.activity.dailyCharge.data.ActivityDailyRechargeTypeItem;
import com.playerdata.activity.dailyCharge.data.ActivityDailyRechargeTypeSubItem;
import com.playerdata.activity.growthFund.ActivityGrowthFundMgr;
import com.playerdata.activity.growthFund.cfg.GrowthFundBasicCfgDAO;
import com.playerdata.activity.growthFund.cfg.GrowthFundSubCfgDAO;
import com.playerdata.activity.growthFund.data.ActivityGrowthFundItem;
import com.playerdata.activity.growthFund.data.ActivityGrowthFundSubItem;
import com.playerdata.activityCommon.activityType.exception.RepeatedActivityTypeException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ActivityTypeFactory {
	
	public static final ActivityType DailyRecharge;
	public static final ActivityType GrowthFund;
	public static final ActivityType CountType;
	private static List<ActivityType> typeList;
	
	static{
		DailyRecharge = new ActivityType(1001, ActivityDailyChargeCfgDAO.class, ActivityDailyRechargeTypeItem.class, 
				ActivityDailyChargeSubCfgDAO.class, ActivityDailyRechargeTypeSubItem.class, ActivityDailyRechargeTypeMgr.getInstance());
		GrowthFund = new ActivityType(1002, GrowthFundBasicCfgDAO.class, ActivityGrowthFundItem.class,
				GrowthFundSubCfgDAO.class, ActivityGrowthFundSubItem.class, ActivityGrowthFundMgr.getInstance());
		CountType = new ActivityType(1003, ActivityCountTypeCfgDAO.class, ActivityCountTypeItem.class,
				ActivityCountTypeSubCfgDAO.class, ActivityCountTypeSubItem.class, ActivityCountTypeMgr.getInstance());
		
		typeList = new ArrayList<ActivityType>();
		addType(DailyRecharge);
		addType(GrowthFund);
		addType(CountType);
	}
	
	public static List<ActivityType> getAllTypes(){
		return typeList;
	}
	
	public static void addType(ActivityType type){
		for(ActivityType at : typeList){
			if(at.getTypeId() == type.getTypeId()){
				new RepeatedActivityTypeException("活动类型的id重复").printStackTrace();
				return;
			}
		}
		typeList.add(type);
	}
}
