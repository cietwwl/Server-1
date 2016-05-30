package com.rwbase.common.attribute.component.robot;

import com.playerdata.team.HeroInfo;
import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.IAttributeComponent;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.param.EquipParam;
import com.rwbase.common.attribute.param.EquipParam.EquipBuilder;

/*
 * @author HC
 * @date 2016年5月14日 下午8:19:26
 * @Description 
 */
public class RobotEquipAttributeComponent implements IAttributeComponent {

	private final HeroInfo heroInfo;

	public RobotEquipAttributeComponent(HeroInfo heroInfo) {
		this.heroInfo = heroInfo;
	}

	@Override
	public AttributeSet convertToAttribute(String userId, String heroId) {
		EquipParam.EquipBuilder builder = new EquipBuilder();
		builder.setUserId(userId);
		builder.setEquipList(heroInfo.getEquip());

		IComponentCalc calc = AttributeBM.getComponentCalc(getComponentTypeEnum());
		if (calc == null) {
			return null;
		}
		return calc.calc(builder.build());
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Equip;
	}
}