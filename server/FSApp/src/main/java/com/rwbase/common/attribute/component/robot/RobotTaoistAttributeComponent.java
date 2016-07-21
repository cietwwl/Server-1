package com.rwbase.common.attribute.component.robot;

import java.util.Map;

import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.IAttributeComponent;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.param.TaoistParam;
import com.rwbase.common.attribute.param.TaoistParam.TaoistBuilder;

/*
 * @author HC
 * @date 2016年7月14日 下午12:07:42
 * @Description 
 */
public class RobotTaoistAttributeComponent implements IAttributeComponent {

	private final Map<Integer, Integer> taoistMap;

	public RobotTaoistAttributeComponent(Map<Integer, Integer> taoistMap) {
		this.taoistMap = taoistMap;
	}

	@Override
	public AttributeSet convertToAttribute(String userId, String heroId) {
		TaoistParam.TaoistBuilder builder = new TaoistBuilder();
		builder.setUserId(userId);
		builder.setTaoistMap(taoistMap);

		IComponentCalc calc = AttributeBM.getComponentCalc(getComponentTypeEnum());
		if (calc == null) {
			return null;
		}
		return calc.calc(builder.build());
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Taoist;
	}
}