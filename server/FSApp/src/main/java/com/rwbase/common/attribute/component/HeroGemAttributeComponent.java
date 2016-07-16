package com.rwbase.common.attribute.component;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.impl.AbstractAttributeCalc;
import com.rwbase.common.attribute.param.GemParam;
import com.rwbase.common.attribute.param.GemParam.GemBuilder;

/*
 * @author HC
 * @date 2016年5月13日 上午9:37:20
 * @Description 
 */
public class HeroGemAttributeComponent extends AbstractAttributeCalc {

	@Override
	protected AttributeSet calcAttribute(Player player, Hero hero) {
		GemParam.GemBuilder builder = new GemBuilder();
		builder.setUserId(player.getUserId());
		builder.setHeroId(hero.getUUId());
		builder.setGemList(hero.getInlayMgr().getInlayGemList(player, hero.getUUId()));
		builder.setHeroModelId(String.valueOf(hero.getModelId()));

		IComponentCalc calc = AttributeBM.getComponentCalc(getComponentTypeEnum());
		if (calc == null) {
//			GameLog.error("计算英雄宝石属性", player.getUserId(), String.format("Id为[%s]的英雄[%s]对应类型的IComponentCacl的实现类为Null", hero.getUUId(), getComponentTypeEnum()));
			return null;
		}
		return calc.calc(builder.build());
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Gem;
	}
}