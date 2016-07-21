package com.rwbase.common.attribute.component.robot;

import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.IAttributeComponent;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.param.ExtraParam;
import com.rwbase.common.attribute.param.ExtraParam.ExtraBuilder;

/*
 * @author HC
 * @date 2016年7月14日 下午4:08:18
 * @Description 
 */
public class RobotExtraAttributeComponent implements IAttributeComponent {

	private final int extraAttrId;

	public RobotExtraAttributeComponent(int extraAttrId) {
		this.extraAttrId = extraAttrId;
	}

	@Override
	public AttributeSet convertToAttribute(String userId, String heroId) {
		if (extraAttrId <= 0) {
			return null;
		}

		ExtraBuilder builder = new ExtraBuilder();
		builder.setUserId(userId);
		builder.setHeroId(heroId);
		builder.setExtraAttrId(extraAttrId);
		ExtraParam param = builder.build();

		IComponentCalc calc = AttributeBM.getComponentCalc(getComponentTypeEnum());
		if (calc == null) {
			return null;
		}

		return calc.calc(param);
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Extra;
	}
}