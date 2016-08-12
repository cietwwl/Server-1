package com.rwbase.common.attribute.calc;

import java.util.ArrayList;
import java.util.Map;

import com.playerdata.FashionMgr;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.param.FashionParam;

/*
 * @author HC
 * @date 2016年7月14日 下午12:13:22
 * @Description 
 */
public class HeroFashionAttrCalc implements IComponentCalc {

	@Override
	public AttributeSet calc(Object obj) {
		FashionParam param = (FashionParam) obj;
		int[] fashionId = param.getFashionId();
		int career = param.getCareer();

		Map<Integer, AttributeItem> attrMap = FashionMgr.getAttrMap(fashionId, career, param.getVaildCount());
		if (attrMap == null || attrMap.isEmpty()) {
			return null;
		}

		return AttributeSet.newBuilder().addAttribute(new ArrayList<AttributeItem>(attrMap.values())).build();
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Fashion;
	}
}