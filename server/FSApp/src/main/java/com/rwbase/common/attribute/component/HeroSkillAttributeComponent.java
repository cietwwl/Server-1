package com.rwbase.common.attribute.component;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.team.SkillInfo;
import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.impl.AbstractAttributeCalc;
import com.rwbase.common.attribute.param.SkillParam;
import com.rwbase.common.attribute.param.SkillParam.SkillBuilder;
import com.rwbase.dao.skill.pojo.SkillItem;

/*
 * @author HC
 * @date 2016年5月13日 下午3:53:40
 * @Description 
 */
public class HeroSkillAttributeComponent extends AbstractAttributeCalc {

	@Override
	protected AttributeSet calcAttribute(Player player, Hero hero) {
		List<SkillItem> skillList = hero.getSkillMgr().getSkillList(hero.getUUId());
		if (skillList == null || skillList.isEmpty()) {
			// GameLog.error("计算英雄技能属性", player.getUserId(), String.format("Id为[%s]的英雄身上的技能是空的", hero.getUUId()));
			return null;
		}

		int size = skillList.size();
		List<SkillInfo> skillInfoList = new ArrayList<SkillInfo>(size);
		for (int i = 0; i < size; i++) {
			SkillItem skill = skillList.get(i);
			if (skill == null) {
				continue;
			}

			int level = skill.getLevel();
			if (level <= 0) {
				continue;
			}

			SkillInfo si = new SkillInfo();
			si.setSkillId(skill.getSkillId());
			si.setSkillLevel(level);

			skillInfoList.add(si);
		}

		SkillParam.SkillBuilder builder = new SkillBuilder();
		builder.setUserId(player.getUserId());
		builder.setSkillList(skillInfoList);
		builder.setHeroTemplateId(hero.getTemplateId());

		IComponentCalc calc = AttributeBM.getComponentCalc(getComponentTypeEnum());
		if (calc == null) {
			// GameLog.error("计算英雄技能属性", player.getUserId(), String.format("Id为[%s]的英雄[%s]对应类型的IComponentCacl的实现类为Null", hero.getUUId(),
			// getComponentTypeEnum()));
			return null;
		}
		return calc.calc(builder.build());
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Skill;
	}
}