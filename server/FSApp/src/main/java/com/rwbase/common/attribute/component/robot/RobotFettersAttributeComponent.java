package com.rwbase.common.attribute.component.robot;

import java.util.Map;

import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.IAttributeComponent;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.param.FettersParam;
import com.rwbase.common.attribute.param.FettersParam.FettersBuilder;
import com.rwbase.dao.fetters.pojo.SynConditionData;

/*
 * @author HC
 * @date 2016年7月14日 下午3:48:02
 * @Description 
 */
public class RobotFettersAttributeComponent implements IAttributeComponent {

	private final Map<Integer, SynConditionData> fettersMap;

	public RobotFettersAttributeComponent(Map<Integer, SynConditionData> fettersMap) {
		this.fettersMap = fettersMap;
	}

	@Override
	public AttributeSet convertToAttribute(String userId, String heroId) {
		FettersBuilder fettersBuilder = new FettersBuilder();
		fettersBuilder.setUserId(userId);
		fettersBuilder.setHeroId(heroId);
		fettersBuilder.setOpenMap(fettersMap);
		FettersParam param = fettersBuilder.build();

		IComponentCalc calc = AttributeBM.getComponentCalc(getComponentTypeEnum());
		if (calc == null) {
			return null;
		}

		return calc.calc(param);
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Fetters;
	}
}