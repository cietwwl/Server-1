package com.rw.dataaccess.attachment;

import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItem;
import com.playerdata.activity.dailyDiscountType.data.ActivityDailyDiscountTypeItem;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItem;
import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatTypeItem;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItem;
import com.playerdata.activity.rankType.data.ActivityRankTypeItem;
import com.playerdata.activity.rateType.data.ActivityRateTypeItem;
import com.playerdata.activity.redEnvelopeType.data.ActivityRedEnvelopeTypeItem;
import com.playerdata.activity.retrieve.data.RewardBackItem;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeItem;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeItem;
import com.rw.dataaccess.attachment.creator.ActivityCountTypeCreator;
import com.rw.dataaccess.attachment.creator.ActivityDailyDiscountCreator;
import com.rw.dataaccess.attachment.creator.ActivityDailyTypeCreator;
import com.rw.dataaccess.attachment.creator.ActivityExchangeCreator;
import com.rw.dataaccess.attachment.creator.ActivityFortuneCatCreator;
import com.rw.dataaccess.attachment.creator.ActivityLimitHeroCreator;
import com.rw.dataaccess.attachment.creator.ActivityRankTypeCreator;
import com.rw.dataaccess.attachment.creator.ActivityRateCreator;
import com.rw.dataaccess.attachment.creator.ActivityRedEnvelopeCreator;
import com.rw.dataaccess.attachment.creator.ActivityRetrieveCreator;
import com.rw.dataaccess.attachment.creator.ActivityTimeCardCreator;
import com.rw.dataaccess.attachment.creator.ActivityTimeCountCreator;
import com.rw.dataaccess.attachment.creator.ActivityVitalityCreator;
import com.rw.fsutil.dao.cache.CacheKey;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityBigItem;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityCreator;

public enum PlayerExtPropertyType implements RoleExtPropertyType{

	ACTIVITY_COUNTTYPE(1,ActivityCountTypeItem.class,ActivityCountTypeCreator.class),	
	ACTIVITY_DAILYTYPE(2,ActivityDailyTypeItem.class,ActivityDailyTypeCreator.class),
	ACTIVITY_RATE(3,ActivityRateTypeItem.class,ActivityRateCreator.class),
	ACTIVITY_TIMECOUNT(4,ActivityTimeCountTypeItem.class,ActivityTimeCountCreator.class),
	ACTIVITY_REDENVELOPE(5,ActivityRedEnvelopeTypeItem.class,ActivityRedEnvelopeCreator.class),
	ACTIVITY_VITALITY(6,ActivityVitalityTypeItem.class,ActivityVitalityCreator.class),
	ACTIVITY_EXCHANGE(7,ActivityExchangeTypeItem.class,ActivityExchangeCreator.class),
	ACTIVITY_RANK(8,ActivityRankTypeItem.class,ActivityRankTypeCreator.class),
	ACTIVITY_DAILYDISCOUNT(9,ActivityDailyDiscountTypeItem.class,ActivityDailyDiscountCreator.class),
	ACTIVITY_FORTUNECAT(10,ActivityFortuneCatTypeItem.class,ActivityFortuneCatCreator.class),
	ACTIVITY_TIMECARD(11,ActivityTimeCardTypeItem.class,ActivityTimeCardCreator.class),
	ACTIVITY_LIMITHERO(13,ActivityLimitHeroTypeItem.class,ActivityLimitHeroCreator.class),	
	ACTIVITY_RETRIEVE(14,RewardBackItem.class,ActivityRetrieveCreator.class),
	FRESHER_ACTIVITY(15, FresherActivityBigItem.class, FresherActivityCreator.class),
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
