package com.rw.dataaccess.mapitem;

import com.playerdata.activity.dailyCharge.data.ActivityDailyRechargeTypeItem;
import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatTypeItem;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItem;
import com.playerdata.activity.redEnvelopeType.data.ActivityRedEnvelopeTypeItem;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;

public enum MapItemType {

	//每日充值
	RECRAHGE_TYPE(1, ActivityDailyRechargeTypeItem.class, ActivityRechargeTypeCreator.class), 
	//限时英雄
	LIMIT_HERO(2, ActivityLimitHeroTypeItem.class, LimitHeroCreator.class),
	//招财猫
	FOUTUNE_CAT(3, ActivityFortuneCatTypeItem.class,FortuneCatCreator.class),
	//红包
	RED_ENVELOP(4, ActivityRedEnvelopeTypeItem.class,RedEnvelopeCreator.class),
	;
	private MapItemType(int type, Class<? extends IMapItem> clazz, Class<? extends MapItemCreator<? extends IMapItem>> createClass) {
	
	}

}
