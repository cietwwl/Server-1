package com.rw.dataaccess.mapitem;

import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.dailyCharge.data.ActivityDailyRechargeTypeItem;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItem;
import com.playerdata.activity.dailyDiscountType.data.ActivityDailyDiscountTypeItem;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItem;
import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatTypeItem;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItem;
import com.playerdata.activity.rankType.data.ActivityRankTypeItem;
import com.playerdata.activity.rateType.data.ActivityRateTypeItem;
import com.playerdata.activity.redEnvelopeType.data.ActivityRedEnvelopeTypeItem;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeItem;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeItem;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;

public enum MapItemType {

	// 每日充值
	RECRAHGE_TYPE(1, ActivityDailyRechargeTypeItem.class, ActivityRechargeTypeCreator.class),
	// 限时英雄
	LIMIT_HERO(2, ActivityLimitHeroTypeItem.class, LimitHeroCreator.class),
	// 招财猫
	FOUTUNE_CAT(3, ActivityFortuneCatTypeItem.class, FortuneCatCreator.class),
	// 红包
	RED_ENVELOP(4, ActivityRedEnvelopeTypeItem.class, RedEnvolopeCreator.class), 

	COUNT_TYPE(5,ActivityCountTypeItem.class,CountTypeCreator.class),
	
	DAILY_TYPE(6,ActivityDailyTypeItem.class,DailyCountCreator.class),
	
	DAILY_DISCOUNT(7,ActivityDailyDiscountTypeItem.class,DailyDisCountCreator.class),
	
	EXCHANGE(8,ActivityExchangeTypeItem.class,ExchangeCreator.class),
	
	RANK_TYPE(9,ActivityRankTypeItem.class,RankTypeCreator.class),
	
	RATE_TEYP(10,ActivityRateTypeItem.class,RateTypeCreator.class),
	
	TIME_CARD(11,ActivityTimeCardTypeItem.class,TimeCardCreator.class),
	
	TIME_COUNT(12,ActivityTimeCountTypeItem.class,TimeCountCreator.class),
	
	VITAITY(13,ActivityVitalityTypeItem.class,VitalityCreator.class)
	;
	
	private final int type;
	private final Class<? extends IMapItem> mapItemClass;
	private final Class<? extends MapItemCreator<? extends IMapItem>> creatorClass;

	private MapItemType(int type, Class<? extends IMapItem> clazz, Class<? extends MapItemCreator<? extends IMapItem>> createClass) {
		this.type = type;
		this.mapItemClass = clazz;
		this.creatorClass = createClass;
	}

	public Class<? extends MapItemCreator<? extends IMapItem>> getCreatorClass() {
		return creatorClass;
	}

	public Class<? extends IMapItem> getMapItemClass() {
		return mapItemClass;
	}

	public int getType() {
		return type;
	}
}
