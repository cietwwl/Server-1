package com.rwbase.dao.fashion;

import com.rwproto.FashionServiceProtos.FashionType;

public class FashionCommonCfg {
	private int id; // 时装id
	private String fashionType;
	private FashionType fashionTypeField;// 时装类型
	private String name;
	private String specialEffect; //特殊效果

	public void ExtraInit() {
		fashionTypeField = FashionType.valueOf(fashionType);
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