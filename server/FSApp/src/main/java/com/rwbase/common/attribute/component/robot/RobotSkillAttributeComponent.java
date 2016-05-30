package com.rwbase.common.attribute.component.robot;

import com.playerdata.team.HeroInfo;
import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.IAttributeComponent;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.param.SkillParam;
import com.rwbase.common.attribute.param.SkillParam.SkillBuilder;

/*
 * @author HC
 * @date 2016年5月14日 下午8:20:10
 * @Description 
 */
public class RobotSkillAttributeComponent implements IAttributeComponent {

	private final HeroInfo heroInfo;

	public RobotSkillAttributeComponent(HeroInfo heroInfo) {
		this.heroInfo = heroInfo;
	}

	@Override
	public AttributeSet convertToAttribute(String userId, String heroId) {
		SkillParam.SkillBuilder builder = new SkillBuilder();
		builder.setUserId(userId);
		builder.setSkillList(heroInfo.getSkill());

		IComponentCalc calc = AttributeBM.getComponentCalc(getComponentTypeEnum());
		if (calc == null) {
			return null;
		}
		return calc.calc(builder.build());
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Skill;
	}
}