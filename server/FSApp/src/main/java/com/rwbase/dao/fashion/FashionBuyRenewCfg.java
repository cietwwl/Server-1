package com.rwbase.dao.fashion;

import com.common.BaseConfig;
import com.log.GameLog;
import com.rwbase.common.enu.eAttrIdDef;
import com.rwbase.common.enu.eSpecialItemId;

public class FashionBuyRenewCfg extends BaseConfig{
	private String key; // 关键字
	private int id; // 时装id
	private FashionPayType payType; // 缴费类型
	private int Day; // 天数
	private eSpecialItemId CoinType; // 货币类型
	private int Num; // 货币值

	@Override
	public void ExtraInitAfterLoad() {
		if (payType == null || CoinType == null){
			GameLog.error("时装", key+","+id, "缴费类型或货币类型配置错误");
		}
		eAttrIdDef currencyType = CoinType.geteAttrId();
		int cost = Num;
		if (cost <= 0 || currencyType == null) {
			GameLog.error("时装", key+","+id, "货币类型或货币值配置错误");
		}
		if (Day <= 0){
			GameLog.info("时装", key+","+id, "有效期配置为负数或零表示永久有效:"+Day,null);
		}
	}

	public String getKey() {
		return key;
	}

	public int getId() {
		return id;
	}

	public FashionPayType getPayType() {
		return payType;
	}

	public int getDay() {
		return Day;
	}

	public eSpecialItemId getCoinType() {
		return CoinType;
	}

	public int getNum() {
		return Num;
	}
}