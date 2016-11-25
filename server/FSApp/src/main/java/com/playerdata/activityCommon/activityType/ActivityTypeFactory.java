package com.playerdata.activityCommon.activityType;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.activity.chargeRank.ActivityChargeRankMgr;
import com.playerdata.activity.chargeRank.cfg.ActivityChargeRankCfgDAO;
import com.playerdata.activity.chargeRank.data.ActivityChargeRankItem;
import com.playerdata.activity.consumeRank.ActivityConsumeRankMgr;
import com.playerdata.activity.consumeRank.cfg.ActivityConsumeRankCfgDAO;
import com.playerdata.activity.consumeRank.data.ActivityConsumeRankItem;
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
import com.playerdata.activity.evilBaoArrive.EvilBaoArriveMgr;
import com.playerdata.activity.evilBaoArrive.cfg.EvilBaoArriveCfgDAO;
import com.playerdata.activity.evilBaoArrive.cfg.EvilBaoArriveSubCfgDAO;
import com.playerdata.activity.evilBaoArrive.data.EvilBaoArriveItem;
import com.playerdata.activity.evilBaoArrive.data.EvilBaoArriveSubItem;
import com.playerdata.activity.growthFund.ActivityGrowthFundMgr;
import com.playerdata.activity.growthFund.cfg.GrowthFundBasicCfgDAO;
import com.playerdata.activity.growthFund.cfg.GrowthFundSubCfgDAO;
import com.playerdata.activity.growthFund.data.ActivityGrowthFundItem;
import com.playerdata.activity.growthFund.data.ActivityGrowthFundSubItem;
import com.playerdata.activity.rankType.ActivityRankTypeMgr;
import com.playerdata.activity.rankType.cfg.ActivityRankTypeCfgDAO;
import com.playerdata.activity.rankType.data.ActivityRankTypeItem;
import com.playerdata.activityCommon.activityType.exception.RepeatedActivityTypeException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ActivityTypeFactory {
	
	public static final ActivityType DailyRecharge;
	public static final ActivityType GrowthFund;
	public static final ActivityType CountType;
	public static final ActivityType EvilBaoArrive;
	public static final ActivityType ChargeRank;
	public static final ActivityType ConsumeRank;
	public static final ActivityType ActRankType;	//竞技之王
	private static List<ActivityType> typeList;
	
	static{
		DailyRecharge = new ActivityType(1001, ActivityDailyChargeCfgDAO.class, ActivityDailyRechargeTypeItem.class, 
				ActivityDailyChargeSubCfgDAO.class, ActivityDailyRechargeTypeSubItem.class, ActivityDailyRechargeTypeMgr.getInstance());
		GrowthFund = new ActivityType(1002, GrowthFundBasicCfgDAO.class, ActivityGrowthFundItem.class,
				GrowthFundSubCfgDAO.class, ActivityGrowthFundSubItem.class, ActivityGrowthFundMgr.getInstance());
		CountType = new ActivityType(1003, ActivityCountTypeCfgDAO.class, ActivityCountTypeItem.class,
				ActivityCountTypeSubCfgDAO.class, ActivityCountTypeSubItem.class, ActivityCountTypeMgr.getInstance());
		EvilBaoArrive = new ActivityType(1004, EvilBaoArriveCfgDAO.class, EvilBaoArriveItem.class,
				EvilBaoArriveSubCfgDAO.class, EvilBaoArriveSubItem.class, EvilBaoArriveMgr.getInstance());
		ChargeRank = new ActivityType(1005, ActivityChargeRankCfgDAO.class, ActivityChargeRankItem.class,
				null, null, ActivityChargeRankMgr.getInstance());
		ConsumeRank = new ActivityType(1006, ActivityConsumeRankCfgDAO.class, ActivityConsumeRankItem.class,
				null, null, ActivityConsumeRankMgr.getInstance());
		ActRankType = new ActivityType(1007, ActivityRankTypeCfgDAO.class, ActivityRankTypeItem.class,
				null, null, ActivityRankTypeMgr.getInstance());
		
		typeList = new ArrayList<ActivityType>();
		addType(DailyRecharge);
		addType(GrowthFund);
		addType(CountType);
		addType(EvilBaoArrive);
		addType(ChargeRank);
		addType(ConsumeRank);
		addType(ActRankType);
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
	
	public static ActivityType getType(int typeId){
		for(ActivityType at : typeList){
			if(at.getTypeId() == typeId){
				return at;
			}
		}
		return null;
	}
}
