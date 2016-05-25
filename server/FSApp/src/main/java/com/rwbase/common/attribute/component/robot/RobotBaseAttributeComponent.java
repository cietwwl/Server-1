package com.rwbase.common.attribute.component.robot;

import com.playerdata.team.HeroBaseInfo;
import com.playerdata.team.HeroInfo;
import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.IAttributeComponent;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.param.HeroBaseParam;
import com.rwbase.common.attribute.param.HeroBaseParam.HeroBaseBuilder;

/*
 * @author HC
 * @date 2016年5月14日 下午8:19:07
 * @Description 
 */
public class RobotBaseAttributeComponent implements IAttributeComponent {

	private final HeroInfo heroInfo;

	public RobotBaseAttributeComponent(HeroInfo heroInfo) {
		this.heroInfo = heroInfo;
	}

	@Override
	public AttributeSet convertToAttribute(String userId, String heroId) {
		HeroBaseInfo baseInfo = heroInfo.getBaseInfo();

		HeroBaseParam.HeroBaseBuilder builder = new HeroBaseBuilder();
		builder.setUserId(userId);
		builder.setHeroTmpId(baseInfo.getTmpId());
		builder.setLevel(baseInfo.getLevel());
		builder.setQualityId(baseInfo.getQuality());

		IComponentCalc calc = AttributeBM.getComponentCalc(getComponentTypeEnum());
		if (calc == null) {
			return null;
		}
		return calc.calc(builder.build());
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Base;
	}
}