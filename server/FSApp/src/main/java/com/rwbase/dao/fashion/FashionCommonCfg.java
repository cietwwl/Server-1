package com.rwbase.dao.fashion;

import com.common.BaseConfig;
import com.log.GameLog;
import com.rwproto.FashionServiceProtos.FashionType;

public class FashionCommonCfg extends BaseConfig {
	private int id; // 时装id
	private FashionType fashionType;// 时装类型
	private String name;
	private String specialEffect; // 特殊效果
	private int frameIconId;// 头像框ID从settings.xslx的headBoxCfg表读取
	private boolean notAllowBuy; // 不可购买
	private boolean notAllowRenew; // 不可续费

	@Override
	public void ExtraInitAfterLoad() {
		if (fashionType == null) {
			GameLog.error("时装", String.valueOf(id), "无效时装类型");
		}
	}

	public int getFrameIconId() {
		return frameIconId;
	}

	public int getId() {
		return id;
	}

	public FashionType getFashionType() {
		return fashionType;
	}

	public String getName() {
		return name;
	}

	public String getSpecialEffect() {
		return specialEffect;
	}

	public boolean getNotAllowBuy() {
		return notAllowBuy;
	}

	public boolean getNotAllowRenew() {
		return notAllowRenew;
	}
}