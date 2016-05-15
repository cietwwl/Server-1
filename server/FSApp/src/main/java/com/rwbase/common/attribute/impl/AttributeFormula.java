package com.rwbase.common.attribute.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attrdata.AttrData.Builder;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.attribute.AttributeType;
import com.rwbase.common.attribute.IAttributeExtracter;
import com.rwbase.common.attribute.IAttributeFormula;

/*
 * @author HC
 * @date 2016年5月13日 下午6:25:43
 * @Description 
 */
public class AttributeFormula implements IAttributeFormula<AttrData> {

	@Override
	public List<AttributeType> getReCalculateAttributes() {
		// return AttributeType.getReCalcAttributeTypeList();
		return null;
	}

	@Override
	public int recalculate(IAttributeExtracter extracter, AttributeType type) {
		// AttributeType attrType = AttributeType.getReCalcAttributeImpactType(type.getTypeValue());
		// int attributeValue = extracter.getAttributeValue(type);
		// if (attrType == null) {
		// return attributeValue;
		// }
		return 0;
	}

	@Override
	public AttrData convertOne(List<AttributeItem> attributes, boolean isRate) {
		AttrData.Builder builder = new Builder();
		for (int i = attributes.size(); --i >= 0;) {
			AttributeItem item = attributes.get(i);
			int add = item.getIncreaseValue();
			int percent = item.getIncPerTenthousand();

			AttributeType type = item.getType();
			type.setAttributeValue(builder, isRate ? percent : add);
		}

		return builder.build();
	}

	@Override
	public AttrData convert(Map<AttributeType, Integer> result) {
		AttrData.Builder builder = new Builder();
		for (Entry<AttributeType, Integer> e : result.entrySet()) {
			AttributeType key = e.getKey();
			int value = e.getValue();
			key.setAttributeValue(builder, value);
		}

		return builder.build();
	}

	// public AttrData parseAttrData(Map<AttributeType, Integer> result) throws IllegalArgumentException, IllegalAccessException {
	// AttrData attrData = new AttrData();
	// for (Entry<AttributeType, Integer> e : result.entrySet()) {
	// AttributeType key = e.getKey();
	// Field field = fieldMap.get(key.attrFieldName);
	// if (field == null) {
	// continue;
	// }
	//
	// field.setAccessible(true);
	// int value = e.getValue();
	// if (AttributeBM.isInt(key)) {
	// field.setInt(attrData, value);
	// } else {
	// field.setFloat(attrData, value / AttributeConst.BIG_FLOAT);
	// }
	// }
	// return attrData;
	// }
	//
	// static Map<String, Field> fieldMap;
	//
	// public static void main(String[] args) throws SecurityException, IllegalArgumentException, IllegalAccessException {
	// Map<AttributeType, Integer> map = new HashMap<AttributeType, Integer>();
	// AttributeType[] values = AttributeType.values();
	// for (int i = 0, len = values.length; i < len; i++) {
	// map.put(values[i], 10000);
	// }
	//
	// Map<String, Field> fieldMap = new HashMap<String, Field>();
	// Field[] fields = AttrData.class.getDeclaredFields();
	// for (int i = 0, len = fields.length; i < len; i++) {
	// Field field = fields[i];
	// System.err.println("字段名字：" + field.getName());
	// fieldMap.put(field.getName(), field);
	// }
	//
	// int times = 1000000;
	// AttributeFormula.fieldMap = fieldMap;
	//
	// AttributeFormula attributeFormula = new AttributeFormula();
	// long startTime = System.nanoTime();
	// for (int i = 0; i < times; i++) {
	// attributeFormula.convert(map);
	// }
	// long endTime = System.nanoTime();
	// System.err.println("总耗时：" + (endTime - startTime));
	//
	// long startTime1 = System.nanoTime();
	// for (int i = 0; i < times; i++) {
	// attributeFormula.parseAttrData(map);
	// }
	// long endTime1 = System.nanoTime();
	// System.err.println("总耗时：" + (endTime1 - startTime1));
	// }

	// @SynClass
	// static class TestA {
	// private Test t;
	// }
	//
	// @SynClass
	// static class TestB {
	// private Map<String, String> t;
	// }
	//
	// @SynClass
	// static class Test {
	// private int a;
	// private int b;
	// private float c;
	// }
	//
	// public static void main(String[] args) throws Exception {
	// Test t = new Test();
	// t.a = 100;
	// t.b = 99;
	// t.c = 191.12f;
	//
	// TestA tt = new TestA();
	// tt.t = t;
	// ClassInfo4Client c = new ClassInfo4Client(TestA.class);
	// String json = c.toJson(tt);
	// System.err.println(json);
	//
	// Map<String, String> map = new HashMap<String, String>();
	// map.put("a", String.valueOf(t.a));
	// map.put("b", String.valueOf(t.b));
	// map.put("c", String.valueOf(t.c));
	// TestB tb = new TestB();
	// tb.t = map;
	//
	// ClassInfo4Client cc = new ClassInfo4Client(TestB.class);
	// String jsonc = cc.toJson(tb);
	// System.err.println(jsonc);
	// }
}