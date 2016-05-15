package com.rwbase.common.attribute.component;

import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.impl.AbstractAttributeCalc;
import com.rwbase.common.attribute.param.HeroBaseParam;
import com.rwbase.common.attribute.param.HeroBaseParam.HeroBaseBuilder;

/*
 * @author HC
 * @date 2016年5月13日 下午2:54:44
 * @Description 
 */
public class HeroBaseAttributeComponent extends AbstractAttributeCalc {

	@Override
	protected AttributeSet calcAttribute(Player player, Hero hero) {
		HeroBaseParam.HeroBaseBuilder builder = new HeroBaseBuilder();
		builder.setUserId(player.getUserId());
		builder.setHeroTmpId(hero.getTemplateId());
		builder.setLevel(hero.getLevel());
		builder.setQualityId(hero.getQualityId());

		IComponentCalc calc = AttributeBM.getComponentCalc(getComponentTypeEnum());
		if (calc == null) {
			GameLog.error("计算英雄基础属性", player.getUserId(), String.format("[%s]对应类型的IComponentCacl的实现类为Null", getComponentTypeEnum()));
			return null;
		}
		return calc.calc(builder.build());
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Base;
	}
}