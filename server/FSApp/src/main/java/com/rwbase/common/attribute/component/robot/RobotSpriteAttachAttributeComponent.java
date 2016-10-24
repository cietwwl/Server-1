package com.rwbase.common.attribute.component.robot;

import com.playerdata.team.HeroInfo;
import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.IAttributeComponent;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.param.SpriteAttachParam;
import com.rwbase.common.attribute.param.SpriteAttachParam.SpriteAttachBuilder;;

public class RobotSpriteAttachAttributeComponent implements IAttributeComponent{

	private final HeroInfo heroInfo;
	
	public RobotSpriteAttachAttributeComponent(HeroInfo heroInfo) {
		this.heroInfo = heroInfo;
	}
	
	@Override
	public AttributeSet convertToAttribute(String userId, String heroId) {
		SpriteAttachParam.SpriteAttachBuilder builder = new SpriteAttachBuilder();
		builder.setUserId(userId);
		builder.setHeroId(heroId);
		builder.setItems(heroInfo.getSpriteAttach());
		IComponentCalc calc = AttributeBM.getComponentCalc(getComponentTypeEnum());
		if (calc == null) {
			return null;
		}
		return calc.calc(builder.build());
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		
		return AttributeComponentEnum.Hero_SpriteAttach;
	}

}
