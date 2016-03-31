package com.rwbase.dao.fashion;

import com.log.GameLog;
import com.rwproto.FashionServiceProtos.FashionType;

public class FashionCommonCfg {
	private int id; // 时装id
	private String fashionType;
	private FashionType fashionTypeField;// 时装类型
	private String name;
	private String specialEffect; //特殊效果

	public void ExtraInit() {
		fashionTypeField = FashionType.valueOf(fashionType);
		if (fashionTypeField == null){
			GameLog.error("时装", String.valueOf(id), "无效时装类型："+fashionType);
		}
	}

	public int getId() {
		return id;
	}

	public FashionType getFashionType() {
		return fashionTypeField;
	}

	public String getName() {
		return name;
	}

	public String getSpecialEffect() {
		return specialEffect;
	}
}