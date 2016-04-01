package com.rwbase.dao.fashion;

import com.log.GameLog;
import com.rwbase.common.enu.eAttrIdDef;
import com.rwbase.common.enu.eSpecialItemId;

public class FashionBuyRenewCfg {
	private String key; // 关键字
	private int id; // 时装id
	private String payType;
	private FashionPayType payTypeField; // 缴费类型
	private int Day; // 天数
	private String CoinType;
	private eSpecialItemId CoinTypeField; // 货币类型
	private int Num; // 货币值

	public void ExtraInit(String key) {
		if (this.key != null) return;
		this.key = key;
		payTypeField = FashionPayType.valueOf(payType);
		CoinTypeField = eSpecialItemId.valueOf(CoinType);
		int cost = Num;
		eAttrIdDef currencyType = CoinTypeField.geteAttrId();
		if (cost <= 0 || currencyType == null) {
			GameLog.error("时装", key+","+id, "货币类型或货币值配置错误:"+Num+","+CoinType);
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
		return payTypeField;
	}

	public int getDay() {
		return Day;
	}

	public eSpecialItemId getCoinType() {
		return CoinTypeField;
	}

	public int getNum() {
		return Num;
	}
}