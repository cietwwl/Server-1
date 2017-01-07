package com.rw.dataaccess.attachment;

import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeItem;
import com.playerdata.activity.chargeRank.data.ActivityChargeRankItem;
import com.playerdata.activity.consumeRank.data.ActivityConsumeRankItem;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.dailyCharge.data.ActivityDailyRechargeTypeItem;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItem;
import com.playerdata.activity.dailyDiscountType.data.ActivityDailyDiscountTypeItem;
import com.playerdata.activity.evilBaoArrive.data.EvilBaoArriveItem;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItem;
import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatTypeItem;
import com.playerdata.activity.growthFund.data.ActivityGrowthFundItem;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItem;
import com.playerdata.activity.rankType.data.ActivityRankTypeItem;
import com.playerdata.activity.rateType.data.ActivityRateTypeItem;
import com.playerdata.activity.redEnvelopeType.data.ActivityRedEnvelopeTypeItem;
import com.playerdata.activity.retrieve.data.RewardBackItem;
import com.playerdata.activity.shakeEnvelope.data.ActivityShakeEnvelopeItem;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeItem;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeItem;
import com.rw.dataaccess.attachment.creator.ActivityChargeRankCreator;
import com.rw.dataaccess.attachment.creator.ActivityConsumeRankCreator;
import com.rw.dataaccess.attachment.creator.ActivityCountTypeCreator;
import com.rw.dataaccess.attachment.creator.ActivityDailyDiscountCreator;
import com.rw.dataaccess.attachment.creator.ActivityDailyRechargeCreator;
import com.rw.dataaccess.attachment.creator.ActivityDailyTypeCreator;
import com.rw.dataaccess.attachment.creator.ActivityExchangeCreator;
import com.rw.dataaccess.attachment.creator.ActivityFortuneCatCreator;
import com.rw.dataaccess.attachment.creator.ActivityGrowthFundCreator;
import com.rw.dataaccess.attachment.creator.ActivityLimitHeroCreator;
import com.rw.dataaccess.attachment.creator.ActivityRankTypeCreator;
import com.rw.dataaccess.attachment.creator.ActivityRateCreator;
import com.rw.dataaccess.attachment.creator.ActivityRedEnvelopeCreator;
import com.rw.dataaccess.attachment.creator.ActivityRetrieveCreator;
import com.rw.dataaccess.attachment.creator.ActivityShakeEnvelopeCreator;
import com.rw.dataaccess.attachment.creator.ActivityTimeCardCreator;
import com.rw.dataaccess.attachment.creator.ActivityTimeCountCreator;
import com.rw.dataaccess.attachment.creator.ActivityVitalityCreator;
import com.rw.dataaccess.attachment.creator.EvilBaoArriveCreator;
import com.rw.dataaccess.attachment.creator.RouterGiftDataCreator;
import com.rw.dataaccess.hero.FashionCreator;
import com.rw.dataaccess.hero.GiveItemHistoryCreator;
import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;
import com.rw.fsutil.dao.cache.CacheKey;
import com.rw.routerServer.giftManger.RouterGiftDataItem;
import com.rw.service.PeakArena.PeakRecordCreator;
import com.rw.service.PeakArena.datamodel.PeakRecordInfo;
import com.rw.service.guide.datamodel.GiveItemHistory;
import com.rwbase.dao.fashion.FashionItem;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityBigItem;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityCreator;

public enum PlayerExtPropertyType implements RoleExtPropertyType{
	/**通用活动一，计数活动；不一定触发*/
	ACTIVITY_COUNTTYPE(1, ActivityCountTypeItem.class, ActivityCountTypeCreator.class),//
	/**通用活动二，每日福利，不一定触发*/
	ACTIVITY_DAILYTYPE(2, ActivityDailyTypeItem.class, ActivityDailyTypeCreator.class),//通用活动二，每日福利，不一定触发
	/**通用活动三，双倍；不一定触发*/
	ACTIVITY_RATE(3, ActivityRateTypeItem.class, ActivityRateCreator.class),//通用活动三，双倍；不一定触发
	
	/**通用活动，在线礼包；一定触发*/
	ACTIVITY_TIMECOUNT(4, ActivityTimeCountTypeItem.class, ActivityTimeCountCreator.class),
	/**通用活动，开服红包；不一定触发*/
	ACTIVITY_REDENVELOPE(5, ActivityRedEnvelopeTypeItem.class, ActivityRedEnvelopeCreator.class),
	/**通用活动，活跃之王；不一定触发*/
	ACTIVITY_VITALITY(6, ActivityVitalityTypeItem.class, ActivityVitalityCreator.class),
	
	/**通用活动，兑换活动；不一定触发*/
	ACTIVITY_EXCHANGE(7, ActivityExchangeTypeItem.class, ActivityExchangeCreator.class),
	/**通用活动，排行榜-竞技之王，战力比拼；不一定触发*/
	ACTIVITY_RANK(8, ActivityRankTypeItem.class, ActivityRankTypeCreator.class),
	/**通用活动，超值欢乐购，战力比拼；不一定触发*/
	ACTIVITY_DAILYDISCOUNT(9, ActivityDailyDiscountTypeItem.class, ActivityDailyDiscountCreator.class),
	
	/**通用活动，招财猫；不一定触发*/
	ACTIVITY_FORTUNECAT(10, ActivityFortuneCatTypeItem.class, ActivityFortuneCatCreator.class),	
	/**通用活动，月卡；一定触发*/
	ACTIVITY_TIMECARD(11, ActivityTimeCardTypeItem.class, ActivityTimeCardCreator.class),
	/**通用活动，每日充值；不一定触发，但为了达到优化效果又不大概逻辑流程，创建即生成空数据*/
	ACTIVITY_DAILYCHARGE(12, ActivityDailyRechargeTypeItem.class, ActivityDailyRechargeCreator.class),
	
	/**通用活动，限时神将；不一定触发*/
	ACTIVITY_LIMITHERO(13, ActivityLimitHeroTypeItem.class, ActivityLimitHeroCreator.class),	
	/**通用活动，每日找回；一定触发*/
	ACTIVITY_RETRIEVE(14, RewardBackItem.class, ActivityRetrieveCreator.class),
	/**开服活动；一定触发*/
	FRESHER_ACTIVITY(15, FresherActivityBigItem.class, FresherActivityCreator.class),
	
	/**时装；一定触发*/
	FISHION(16, FashionItem.class, FashionCreator.class),
	/**新手引导赠送；一定触发*/
	GIVEITEM_HISTORY(17, GiveItemHistory.class, GiveItemHistoryCreator.class),
	/**巅峰竞技场战报*/
	PEAK_ARENA_RECORD(18, PeakRecordInfo.class, PeakRecordCreator.class),
//	OPENLEVEL_TIGGERSERVICE(19,OpenLevelTiggerServiceItem.class,OpenLevelTiggerServiceCreator.class),//暂时将等级开放推送的辅助数据存在各功能模块
	/**通用活动，成长基金；不一定触发，但为了达到优化效果又不大概逻辑流程，创建即生成空数据*/
	ACTIVITY_GROWTHFUND(19, ActivityGrowthFundItem.class, ActivityGrowthFundCreator.class),
	/**通用活动，申公豹驾到；不一定触发，创建即生成空数据*/
	ACTIVITY_EVILBAOARRIVE(20, EvilBaoArriveItem.class, EvilBaoArriveCreator.class),
	/**通用活动，充值排行榜；不一定触发，但为了达到优化效果又不大概逻辑流程，创建即生成空数据*/
	ACTIVITY_CHARGE_RANK(21, ActivityChargeRankItem.class, ActivityChargeRankCreator.class),
	/**通用活动，消费排行榜；不一定触发，但为了达到优化效果又不大概逻辑流程，创建即生成空数据*/
	ACTIVITY_CONSUME_RANK(22, ActivityConsumeRankItem.class, ActivityConsumeRankCreator.class),
	/**直通车礼包*/
	ROUTER_GIFT(23, RouterGiftDataItem.class, RouterGiftDataCreator.class),
	/**通用活动，摇一摇红包；不一定触发，但为了达到优化效果又不大概逻辑流程，创建即生成空数据*/
	ACTIVITY_SHAKE_ENVELOPE(24, ActivityShakeEnvelopeItem.class, ActivityShakeEnvelopeCreator.class),
	;

	private final Class<? extends RoleExtProperty> propertyClass;
	private final Class<? extends PlayerExtPropertyCreator<?>> creatorClass;
	private final String propertyName;
	private final CacheKey cacheKey;
	private final short type;
	private final int capacity;

	<T extends RoleExtProperty> PlayerExtPropertyType(int type, Class<T> attachmentClass, String name, Class<? extends PlayerExtPropertyCreator<T>> creatorClass, int capacity) {
		if (type > Short.MAX_VALUE) {
			throw new ExceptionInInitializerError("out of range:" + type + ",max=" + Short.MAX_VALUE);
		}
		this.type = (short) type;
		this.propertyClass = attachmentClass;
		this.propertyName = name;
		this.creatorClass = creatorClass;
		this.capacity = capacity;
		this.cacheKey = new CacheKey(attachmentClass, name);
	}

	<T extends RoleExtProperty> PlayerExtPropertyType(int type, Class<T> attachmentClass, Class<? extends PlayerExtPropertyCreator<T>> creatorClass, int capacity) {
		this(type, attachmentClass, attachmentClass.getSimpleName(), creatorClass, capacity);
	}

	<T extends RoleExtProperty> PlayerExtPropertyType(int type, Class<T> attachmentClass, Class<? extends PlayerExtPropertyCreator<T>> creatorClass) {
		this(type, attachmentClass, attachmentClass.getSimpleName(), creatorClass, 0);
	}

	public Class<? extends RoleExtProperty> getPropertyClass() {
		return propertyClass;
	}

	public CacheKey getCacheKey() {
		return cacheKey;
	}

	public Class<? extends PlayerExtPropertyCreator<?>> getCreatorClass() {
		return creatorClass;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public short getType() {
		return type;
	}

	public int getCapacity() {
		return capacity;
	}
	
}
