package com.playerdata.activity.growthFund;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum GrowthFundType {

	/**
	 * 成长基金礼包
	 */
	GIFT(1, new GrowthFundGiftConditionCheckStrage()), 
	/**
	 * 成长基金福利
	 */
	REWARD(2, new GrowthFundRewardConditionCheckStrage()),
	;

	public final int sign;
	private IConditionCheckStrage _strage;
	
	private static final Map<Integer, GrowthFundType> _mapBySign;
	
	static {
		Map<Integer, GrowthFundType> map = new HashMap<Integer, GrowthFundType>();
		GrowthFundType[] all = GrowthFundType.values();
		for(GrowthFundType fundType : all) {
			map.put(fundType.sign, fundType);
		}
		_mapBySign = Collections.unmodifiableMap(map);
	}

	private GrowthFundType(int pSign, IConditionCheckStrage strage) {
		this.sign = pSign;
		this._strage = strage;
	}
	
	public static GrowthFundType getBySign(int sign) {
		return _mapBySign.get(sign);
	}

	public IConditionCheckStrage getConditionCheckStrage() {
		return _strage;
	}
}
