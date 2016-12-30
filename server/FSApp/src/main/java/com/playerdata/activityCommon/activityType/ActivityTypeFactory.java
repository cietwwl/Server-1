package com.playerdata.activityCommon.activityType;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.activity.VitalityType.ActivityVitalityTypeMgr;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityCfgDAO;
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
import com.playerdata.activity.dailyCountType.ActivityDailyTypeMgr;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeCfgDAO;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeSubCfgDAO;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItem;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeSubItem;
import com.playerdata.activity.dailyDiscountType.ActivityDailyDiscountTypeMgr;
import com.playerdata.activity.dailyDiscountType.cfg.ActivityDailyDiscountTypeCfgDAO;
import com.playerdata.activity.dailyDiscountType.cfg.ActivityDailyDiscountTypeSubCfgDAO;
import com.playerdata.activity.dailyDiscountType.data.ActivityDailyDiscountTypeItem;
import com.playerdata.activity.dailyDiscountType.data.ActivityDailyDiscountTypeSubItem;
import com.playerdata.activity.evilBaoArrive.EvilBaoArriveMgr;
import com.playerdata.activity.evilBaoArrive.cfg.EvilBaoArriveCfgDAO;
import com.playerdata.activity.evilBaoArrive.cfg.EvilBaoArriveSubCfgDAO;
import com.playerdata.activity.evilBaoArrive.data.EvilBaoArriveItem;
import com.playerdata.activity.evilBaoArrive.data.EvilBaoArriveSubItem;
import com.playerdata.activity.exChangeType.ActivityExchangeTypeMgr;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfgDAO;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeSubCfgDAO;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItem;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeSubItem;
import com.playerdata.activity.fortuneCatType.ActivityFortuneCatTypeMgr;
import com.playerdata.activity.fortuneCatType.cfg.ActivityFortuneCatTypeCfgDAO;
import com.playerdata.activity.fortuneCatType.cfg.ActivityFortuneCatTypeSubCfgDAO;
import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatTypeItem;
import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatTypeSubItem;
import com.playerdata.activity.growthFund.ActivityGrowthFundMgr;
import com.playerdata.activity.growthFund.cfg.GrowthFundBasicCfgDAO;
import com.playerdata.activity.growthFund.cfg.GrowthFundSubCfgDAO;
import com.playerdata.activity.growthFund.data.ActivityGrowthFundItem;
import com.playerdata.activity.growthFund.data.ActivityGrowthFundSubItem;
import com.playerdata.activity.limitHeroType.ActivityLimitHeroTypeMgr;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroCfgDAO;
import com.playerdata.activity.rankType.ActivityRankTypeMgr;
import com.playerdata.activity.rankType.cfg.ActivityRankTypeCfgDAO;
import com.playerdata.activity.rankType.data.ActivityRankTypeItem;
import com.playerdata.activity.rateType.ActivityRateTypeMgr;
import com.playerdata.activity.rateType.cfg.ActivityRateTypeCfgDAO;
import com.playerdata.activity.redEnvelopeType.ActivityRedEnvelopeTypeMgr;
import com.playerdata.activity.redEnvelopeType.cfg.ActivityRedEnvelopeTypeCfgDAO;
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
	public static final ActivityType DailyDiscount;	//折扣活动
	public static final ActivityType FortuneCat;	//招财猫
	public static final ActivityType DailyCount;	//每日福利
	public static final ActivityType ExChangeType;	//交换活动
	public static final ActivityType LimitHeroType;	//限时英雄
	public static final ActivityType RateType;	//双倍活动
	public static final ActivityType RedEnvelopeType;	//红包活动
	public static final ActivityType VitalityType;	//活跃之王
	
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
		DailyDiscount = new ActivityType(1008, ActivityDailyDiscountTypeCfgDAO.class, ActivityDailyDiscountTypeItem.class,
				ActivityDailyDiscountTypeSubCfgDAO.class, ActivityDailyDiscountTypeSubItem.class, ActivityDailyDiscountTypeMgr.getInstance());
		FortuneCat = new ActivityType(1009, ActivityFortuneCatTypeCfgDAO.class, ActivityFortuneCatTypeItem.class,
				ActivityFortuneCatTypeSubCfgDAO.class, ActivityFortuneCatTypeSubItem.class, ActivityFortuneCatTypeMgr.getInstance());
		DailyCount = new ActivityType(1010, ActivityDailyTypeCfgDAO.class, ActivityDailyTypeItem.class,
				ActivityDailyTypeSubCfgDAO.class, ActivityDailyTypeSubItem.class, ActivityDailyTypeMgr.getInstance());
		ExChangeType = new ActivityType(1011, ActivityExchangeTypeCfgDAO.class, ActivityExchangeTypeItem.class, 
				ActivityExchangeTypeSubCfgDAO.class, ActivityExchangeTypeSubItem.class, ActivityExchangeTypeMgr.getInstance());
		
		LimitHeroType = new ActivityType(1012, ActivityLimitHeroCfgDAO.class, ActivityLimitHeroTypeMgr.getInstance());
		RateType = new ActivityType(1013, ActivityRateTypeCfgDAO.class, ActivityRateTypeMgr.getInstance());
		RedEnvelopeType = new ActivityType(1014, ActivityRedEnvelopeTypeCfgDAO.class, ActivityRedEnvelopeTypeMgr.getInstance());
		VitalityType = new ActivityType(1015, ActivityVitalityCfgDAO.class, ActivityVitalityTypeMgr.getInstance());
		
		
		typeList = new ArrayList<ActivityType>();
		addType(DailyRecharge);
		addType(GrowthFund);
		addType(CountType);
		addType(EvilBaoArrive);
		addType(ChargeRank);
		addType(ConsumeRank);
		addType(ActRankType);
		addType(DailyDiscount);
		addType(FortuneCat);
		addType(DailyCount);
		addType(ExChangeType);
		addType(LimitHeroType);
		addType(RateType);
		addType(RedEnvelopeType);
		addType(VitalityType);
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
	
	/**
	 * 通过配置id找到属于的类型
	 * @param cfgId
	 * @return
	 */
	public static ActivityType getByCfgId(int cfgId){
		for(ActivityType actType : typeList){
			if(actType.getActivityJudgeMgr().isThisActivityIndex(cfgId)){
				return actType;
			}
		}
		return null;
	}
}
