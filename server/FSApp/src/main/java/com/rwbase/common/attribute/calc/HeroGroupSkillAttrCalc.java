package com.rwbase.common.attribute.calc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.playerdata.group.UserGroupAttributeDataMgr;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.param.GroupSkillParam;

/*
 * @author HC
 * @date 2016年7月14日 下午12:15:36
 * @Description 
 */
public class HeroGroupSkillAttrCalc implements IComponentCalc {

	@Override
	public AttributeSet calc(Object obj) {
		GroupSkillParam param = (GroupSkillParam) obj;
		Map<Integer, Integer> groupSkillMap = param.getGroupSkillMap();
		if (groupSkillMap == null || groupSkillMap.isEmpty()) {
			return null;
		}

		HashMap<Integer, AttributeItem> groupSkillAttrMap = UserGroupAttributeDataMgr.getGroupSkillAttrMap(groupSkillMap);
		if (groupSkillAttrMap == null || groupSkillAttrMap.isEmpty()) {
			return null;
		}

		return AttributeSet.newBuilder().addAttribute(new ArrayList<AttributeItem>(groupSkillAttrMap.values())).build();
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Group_Skill;
	}
}