package com.rwbase.common.attribute.component.robot;

import java.util.Map;

import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.IAttributeComponent;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.param.GroupSkillParam;
import com.rwbase.common.attribute.param.GroupSkillParam.GroupSkillBuilder;

/*
 * @author HC
 * @date 2016年7月14日 下午3:43:45
 * @Description 
 */
public class RobotGroupSkillAttributeComponent implements IAttributeComponent {

	private final Map<Integer, Integer> skillMap;

	public RobotGroupSkillAttributeComponent(Map<Integer, Integer> skillMap) {
		this.skillMap = skillMap;
	}

	@Override
	public AttributeSet convertToAttribute(String userId, String heroId) {
		GroupSkillBuilder groupSkillBuilder = new GroupSkillBuilder();
		groupSkillBuilder.setGroupSkillMap(skillMap);
		groupSkillBuilder.setUserId(userId);
		groupSkillBuilder.setHeroId(heroId);

		GroupSkillParam param = groupSkillBuilder.build();
		IComponentCalc calc = AttributeBM.getComponentCalc(getComponentTypeEnum());
		if (calc == null) {
			return null;
		}

		return calc.calc(param);
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Group_Skill;
	}
}