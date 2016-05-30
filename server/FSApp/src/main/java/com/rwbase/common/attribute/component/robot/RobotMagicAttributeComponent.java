package com.rwbase.common.attribute.component.robot;

import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.IAttributeComponent;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.param.MagicParam;

/*
 * @author HC
 * @date 2016年5月14日 下午8:20:25
 * @Description 
 */
public class RobotMagicAttributeComponent implements IAttributeComponent {
	private final MagicParam magicInfo;// 英雄的简略信息

	public RobotMagicAttributeComponent(MagicParam magicInfo) {
		this.magicInfo = magicInfo;
	}

	@Override
	public AttributeSet convertToAttribute(String userId, String heroId) {
		IComponentCalc calc = AttributeBM.getComponentCalc(getComponentTypeEnum());
		if (calc == null) {
			return null;
		}

		return calc.calc(magicInfo);
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Magic;
	}
}