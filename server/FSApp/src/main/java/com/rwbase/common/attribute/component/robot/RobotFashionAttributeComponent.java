package com.rwbase.common.attribute.component.robot;

import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.IAttributeComponent;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.param.FashionParam;
import com.rwbase.common.attribute.param.FashionParam.FashionBuilder;

/*
 * @author HC
 * @date 2016年7月14日 下午4:13:05
 * @Description 
 */
public class RobotFashionAttributeComponent implements IAttributeComponent {
	private final int[] fashionId;
	private final int career;
	private final int validCount;

	public RobotFashionAttributeComponent(int[] fashionId, int career, int validCount) {
		this.fashionId = fashionId;
		this.career = career;
		this.validCount = validCount;
	}

	@Override
	public AttributeSet convertToAttribute(String userId, String heroId) {
		FashionBuilder builder = new FashionBuilder();
		builder.setUserId(userId);
		builder.setHeroId(heroId);
		builder.setFashionId(fashionId);
		builder.setCareer(career);
		builder.setVaildCount(validCount);
		FashionParam param = builder.build();

		IComponentCalc calc = AttributeBM.getComponentCalc(getComponentTypeEnum());
		if (calc == null) {
			return null;
		}

		return calc.calc(param);
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Fashion;
	}
}