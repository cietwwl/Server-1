package com.rwbase.common.attribute.calc;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.fixEquip.FixEquipHelper;
import com.playerdata.fixEquip.norm.data.FixNormEquipDataItem;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.param.FixNormEquipParam;

/*
 * @author HC
 * @date 2016年7月14日 下午12:20:31
 * @Description 
 */
public class HeroFixNormEquipAttrCalc implements IComponentCalc {

	@Override
	public AttributeSet calc(Object obj) {
		FixNormEquipParam param = (FixNormEquipParam) obj;
		String userId = param.getUserId();
		List<FixNormEquipDataItem> fixNormEquipList = param.getFixNormEquipList();
		if (fixNormEquipList == null || fixNormEquipList.isEmpty()) {
			return null;
		}

		AttributeSet.Builder attrSetBuilder = AttributeSet.newBuilder();
		attrSetBuilder.addAttribute(new ArrayList<AttributeItem>(FixEquipHelper.parseFixNormEquipLevelAttr(userId, fixNormEquipList).values()));
		attrSetBuilder.addAttribute(new ArrayList<AttributeItem>(FixEquipHelper.parseFixNormEquipLevelAttr(userId, fixNormEquipList).values()));
		attrSetBuilder.addAttribute(new ArrayList<AttributeItem>(FixEquipHelper.parseFixNormEquipLevelAttr(userId, fixNormEquipList).values()));
		return attrSetBuilder.build();
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Fix_Norm_Equip;
	}
}