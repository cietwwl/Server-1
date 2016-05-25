package com.rwbase.common.attribute.component.robot;

import com.playerdata.team.HeroInfo;
import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.IAttributeComponent;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.param.GemParam;
import com.rwbase.common.attribute.param.GemParam.GemBuilder;

/*
 * @author HC
 * @date 2016年5月14日 下午8:19:44
 * @Description 
 */
public class RobotGemAttributeComponent implements IAttributeComponent {
	private final HeroInfo heroInfo;

	public RobotGemAttributeComponent(HeroInfo heroInfo) {
		this.heroInfo = heroInfo;
	}

	@Override
	public AttributeSet convertToAttribute(String userId, String heroId) {
		GemParam.GemBuilder builder = new GemBuilder();
		builder.setUserId(userId);
		builder.setGemList(heroInfo.getGem());
		builder.setHeroModelId(heroId);

		IComponentCalc calc = AttributeBM.getComponentCalc(getComponentTypeEnum());
		if (calc == null) {
			return null;
		}
		return calc.calc(builder.build());
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Gem;
	}
}