package com.rwbase.common.attribute.calc;

import java.util.ArrayList;
import java.util.Map;

import com.rw.service.TaoistMagic.datamodel.TaoistMagicCfgHelper;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.param.TaoistParam;

/*
 * @author HC
 * @date 2016年7月14日 上午11:59:05
 * @Description 道术属性计算
 */
public class HeroTaoistAttrCalc implements IComponentCalc {

	@Override
	public AttributeSet calc(Object obj) {
		TaoistParam param = (TaoistParam) obj;
		Map<Integer, Integer> taoistMap = param.getTaoistMap();
		if (taoistMap == null || taoistMap.isEmpty()) {
			return null;
		}

		Map<Integer, AttributeItem> attrMap = TaoistMagicCfgHelper.getInstance().getEffectAttr(taoistMap.entrySet());
		if (attrMap == null || attrMap.isEmpty()) {
			return null;
		}

		return new AttributeSet.Builder().addAttribute(new ArrayList<AttributeItem>(attrMap.values())).build();
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Taoist;
	}
}