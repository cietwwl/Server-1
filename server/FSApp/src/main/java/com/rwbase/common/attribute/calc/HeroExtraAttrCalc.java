package com.rwbase.common.attribute.calc;

import java.util.ArrayList;
import java.util.HashMap;

import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.AttributeUtils;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.param.ExtraParam;
import com.rwbase.dao.arena.RobotExtraAttributeCfgDAO;
import com.rwbase.dao.arena.pojo.RobotExtraAttributeTemplate;

/*
 * @author HC
 * @date 2016年7月14日 下午12:16:37
 * @Description 
 */
public class HeroExtraAttrCalc implements IComponentCalc {

	@Override
	public AttributeSet calc(Object obj) {
		ExtraParam param = (ExtraParam) obj;
		int extraAttrId = param.getExtraAttrId();

		RobotExtraAttributeTemplate extraTmp = RobotExtraAttributeCfgDAO.getCfgDAO().getRobotExtraAttributeTemplate(extraAttrId);
		if (extraTmp == null) {
			return null;
		}

		HashMap<Integer, AttributeItem> map = new HashMap<Integer, AttributeItem>();
		AttributeUtils.calcAttribute(extraTmp.getAttrDataMap(), extraTmp.getPrecentAttrDataMap(), map);
		if (map.isEmpty()) {
			return null;
		}

		return AttributeSet.newBuilder().addAttribute(new ArrayList<AttributeItem>(map.values())).build();
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Extra;
	}
}