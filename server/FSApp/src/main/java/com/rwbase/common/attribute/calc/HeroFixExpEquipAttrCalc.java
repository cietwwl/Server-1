package com.rwbase.common.attribute.calc;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.fixEquip.FixEquipHelper;
import com.playerdata.fixEquip.exp.data.FixExpEquipDataItem;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.param.FixExpEquipParam;

/*
 * @author HC
 * @date 2016年7月14日 下午12:20:16
 * @Description 
 */
public class HeroFixExpEquipAttrCalc implements IComponentCalc {

	@Override
	public AttributeSet calc(Object obj) {
		FixExpEquipParam param = (FixExpEquipParam) obj;
		String userId = param.getUserId();

		List<FixExpEquipDataItem> fixExpEquipList = param.getFixExpEquipList();
		if (fixExpEquipList == null || fixExpEquipList.isEmpty()) {
			return null;
		}

		AttributeSet.Builder attrSetBuilder = AttributeSet.newBuilder();
		attrSetBuilder.addAttribute(new ArrayList<AttributeItem>(FixEquipHelper.parseFixExpEquipLevelAttr(userId, fixExpEquipList).values()));
		attrSetBuilder.addAttribute(new ArrayList<AttributeItem>(FixEquipHelper.parseFixExpEquipLevelAttr(userId, fixExpEquipList).values()));
		attrSetBuilder.addAttribute(new ArrayList<AttributeItem>(FixEquipHelper.parseFixExpEquipLevelAttr(userId, fixExpEquipList).values()));
		return attrSetBuilder.build();
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Fix_Exp_Equip;
	}
}