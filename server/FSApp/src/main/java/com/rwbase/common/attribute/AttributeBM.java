package com.rwbase.common.attribute;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.playerdata.team.HeroInfo;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attribute.calc.HeroBaseAttrCalc;
import com.rwbase.common.attribute.calc.HeroEquipAttrCalc;
import com.rwbase.common.attribute.calc.HeroGemAttrCalc;
import com.rwbase.common.attribute.calc.HeroMagicAttrCalc;
import com.rwbase.common.attribute.calc.HeroSkillAttrCalc;
import com.rwbase.common.attribute.component.HeroBaseAttributeComponent;
import com.rwbase.common.attribute.component.HeroEquipAttributeComponent;
import com.rwbase.common.attribute.component.HeroFashionAttributeComponent;
import com.rwbase.common.attribute.component.HeroFettersAttributeComponent;
import com.rwbase.common.attribute.component.HeroGemAttributeComponent;
import com.rwbase.common.attribute.component.HeroGroupSkillAttributeComponent;
import com.rwbase.common.attribute.component.HeroMagicAttributeComponent;
import com.rwbase.common.attribute.component.HeroSkillAttributeComponent;
import com.rwbase.common.attribute.component.HeroTaoistAttributeComponent;
import com.rwbase.common.attribute.component.robot.RobotBaseAttributeComponent;
import com.rwbase.common.attribute.component.robot.RobotEquipAttributeComponent;
import com.rwbase.common.attribute.component.robot.RobotGemAttributeComponent;
import com.rwbase.common.attribute.component.robot.RobotMagicAttributeComponent;
import com.rwbase.common.attribute.component.robot.RobotSkillAttributeComponent;
import com.rwbase.common.attribute.impl.AttributeFormula;
import com.rwbase.common.attribute.param.MagicParam;

/*
 * @author HC
 * @date 2016年5月12日 下午3:42:32
 * @Description 属性管理
 */
public class AttributeBM {
	/** <类型,是否是int> */
	private static Map<Integer, Boolean> attributeIntOrBoolMap = new HashMap<Integer, Boolean>();
	/** 属性计算公式类 */
	private static IAttributeFormula<AttrData> attributeFormula = new AttributeFormula();
	/** 需要计算属性的模块 */
	private static List<IAttributeComponent> componentList = new ArrayList<IAttributeComponent>();
	/** 枚举对应的属性计算类 */
	private static EnumMap<AttributeComponentEnum, IComponentCalc> calcMap = new EnumMap<AttributeComponentEnum, IComponentCalc>(AttributeComponentEnum.class);

	static {
		// 角色属性计算需要的组件
		componentList.add(new HeroBaseAttributeComponent());
		componentList.add(new HeroGemAttributeComponent());
		componentList.add(new HeroMagicAttributeComponent());
		componentList.add(new HeroEquipAttributeComponent());
		componentList.add(new HeroSkillAttributeComponent());
		componentList.add(new HeroFettersAttributeComponent());
		componentList.add(new HeroFashionAttributeComponent());
		componentList.add(new HeroGroupSkillAttributeComponent());
		componentList.add(new HeroTaoistAttributeComponent());

		// 属性计算类初始化
		IComponentCalc heroBaseAttrCalc = new HeroBaseAttrCalc();
		IComponentCalc heroEquipAttrCalc = new HeroEquipAttrCalc();
		IComponentCalc heroGemAttrCalc = new HeroGemAttrCalc();
		IComponentCalc heroSkillAttrCalc = new HeroSkillAttrCalc();
		IComponentCalc heroMagicAttrCalc = new HeroMagicAttrCalc();
		calcMap.put(heroBaseAttrCalc.getComponentTypeEnum(), heroBaseAttrCalc);
		calcMap.put(heroEquipAttrCalc.getComponentTypeEnum(), heroEquipAttrCalc);
		calcMap.put(heroGemAttrCalc.getComponentTypeEnum(), heroGemAttrCalc);
		calcMap.put(heroSkillAttrCalc.getComponentTypeEnum(), heroSkillAttrCalc);
		calcMap.put(heroMagicAttrCalc.getComponentTypeEnum(), heroMagicAttrCalc);
	}

	/**
	 * 初始化{@link AttributeType}和{@link AttrData}中字段的映射关系，已经每个字段是否是整数的映射
	 */
	public static void initAttributeMap() {
		// 对类AttrData进行一个反射
		Map<String, Boolean> fieldTypeMap = new HashMap<String, Boolean>();
		Field[] fields = AttrData.class.getDeclaredFields();
		for (int i = 0, len = fields.length; i < len; i++) {
			Field f = fields[i];
			f.setAccessible(true);
			String name = f.getName();
			fieldTypeMap.put(name, f.getType() == int.class);
			// System.err.println(name + "," + f.getType() + "," + fieldTypeMap.get(name) + "," + fieldTypeMap.size());
		}

		Map<Integer, Boolean> attributeIntOrBoolMap = new HashMap<Integer, Boolean>();
		AttributeType[] values = AttributeType.values();
		for (int i = 0, len = values.length; i < len; i++) {
			AttributeType attributeType = values[i];
			if (attributeType == null) {
				continue;
			}

			String name = attributeType.attrFieldName;
			if (fieldTypeMap.containsKey(name)) {
				attributeIntOrBoolMap.put(attributeType.getTypeValue(), fieldTypeMap.get(name));
				// } else {
				// System.err.println(attributeType.toString() + ",没有指定的FieldName");
			}
		}

		AttributeBM.attributeIntOrBoolMap = attributeIntOrBoolMap;

		System.err.println(JsonUtil.writeValue(AttributeBM.attributeIntOrBoolMap));
	}

	/**
	 * 判断传递进来的属性类型是不是int类型的值
	 * 
	 * @param attributeType
	 * @return
	 */
	public static boolean isInt(AttributeType attributeType) {
		if (attributeType == null) {
			return true;
		}

		return isInt(attributeType.getTypeValue());
	}

	/**
	 * 判断传递来的属性类型是不是int类型的值
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isInt(int type) {
		if (attributeIntOrBoolMap.containsKey(type)) {
			return attributeIntOrBoolMap.get(type);
		}

		return true;
	}

	/**
	 * 创建一个跟角色相关的属性计算类
	 * 
	 * @param playerId
	 * @param heroId
	 * @return
	 */
	public static AttributeCalculator<AttrData> getAttributeCalculator(String playerId, String heroId) {
		return new AttributeCalculator<AttrData>(playerId, heroId, componentList, attributeFormula);
	}

	/**
	 * 获取对应的计算类
	 * 
	 * @param component
	 * @return
	 */
	public static IComponentCalc getComponentCalc(AttributeComponentEnum component) {
		return calcMap.get(component);
	}

	public static AttrData getRobotAttrData(String userId, HeroInfo heroInfo, MagicParam magicInfo) {
		List<IAttributeComponent> componentList = new ArrayList<IAttributeComponent>();
		componentList.add(new RobotBaseAttributeComponent(heroInfo));
		componentList.add(new RobotEquipAttributeComponent(heroInfo));
		componentList.add(new RobotGemAttributeComponent(heroInfo));
		componentList.add(new RobotMagicAttributeComponent(magicInfo));
		componentList.add(new RobotSkillAttributeComponent(heroInfo));

		AttributeCalculator<AttrData> attributeCalculator = new AttributeCalculator<AttrData>(userId, heroInfo.getBaseInfo().getTmpId(), componentList, attributeFormula);
		attributeCalculator.updateAttribute();
		return attributeCalculator.getResult();
	}
}