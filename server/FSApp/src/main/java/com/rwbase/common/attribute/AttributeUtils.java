package com.rwbase.common.attribute;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.springframework.util.StringUtils;

import com.log.GameLog;
import com.rwbase.dao.attribute.AttributeLanguageCfgDAO;
import com.rwbase.dao.role.pojo.AttrDataInfo;

/*
 * @author HC
 * @date 2016年5月13日 上午11:54:25
 * @Description 
 */
public class AttributeUtils {
	/**
	 * 转换数据成为一个只读的Map
	 * 
	 * @param levelList
	 * @param attrDataStr
	 * @param precentAttrDataStr
	 * @return
	 */
	public static Map<Integer, AttrDataInfo> parseStr2AttrInfoReadOnlyMap(List<Integer> levelList, String attrDataStr, String precentAttrDataStr) {
		if (StringUtils.isEmpty(attrDataStr)) {
			return Collections.emptyMap();
		}

		String[] attrDataArr = attrDataStr.split(",");
		String[] precentAttrDataArr = precentAttrDataStr.split(",");

		int levelSize = levelList.size();
		if (levelSize != attrDataArr.length || levelSize != precentAttrDataArr.length) {
			return Collections.emptyMap();
		}

		Map<Integer, AttrDataInfo> attrDataInfoMap = new HashMap<Integer, AttrDataInfo>(levelSize);

		for (int i = levelSize - 1; i >= 0; --i) {
			attrDataInfoMap.put(levelList.get(i), new AttrDataInfo(attrDataArr[i], precentAttrDataArr[i]));
		}

		return Collections.unmodifiableMap(attrDataInfoMap);
	}

	/**
	 * 解析属性字符串到Map
	 * 
	 * @param attrData
	 * @return
	 */
	public static Map<Integer, Integer> parseAttrDataStr2Map(String attrData) {
		if (StringUtils.isEmpty(attrData)) {
			return Collections.emptyMap();
		} else {
			Map<Integer, Integer> heroFettersAttrDataMap = new HashMap<Integer, Integer>();

			AttributeLanguageCfgDAO cfgDAO = AttributeLanguageCfgDAO.getCfgDAO();
			StringTokenizer token = new StringTokenizer(attrData, ";");
			while (token.hasMoreTokens()) {
				StringTokenizer token1 = new StringTokenizer(token.nextToken(), "_");
				if (token1.countTokens() != 2) {
					continue;
				}

				String name = token1.nextToken();
				if (name.equals("生命恢复")) {
					System.err.println(name + "------------------------");
				}
				AttributeType attributeType = cfgDAO.getAttributeType(name);
				if (attributeType == null) {
					GameLog.error("解析属性到对应的AttributeType", "系统解析配置", String.format("配置表中的中文名字[%s]，无法找到能够映射的AttributeType", name));
					continue;
				}

				String attrValue = token1.nextToken();
				// 检查属性类型是不是int
				int typeValue = attributeType.getTypeValue();
				if (!AttributeBM.isInt(typeValue)) {
					heroFettersAttrDataMap.put(typeValue, (int) (Float.valueOf(attrValue) * AttributeConst.BIG_FLOAT));
				} else {
					heroFettersAttrDataMap.put(typeValue, Integer.valueOf(attrValue));
				}
			}

			return Collections.unmodifiableMap(heroFettersAttrDataMap);
		}
	}

	/**
	 * 计算每个部分的属性，然后整合到<Integer,AttributeItem>的Map中去
	 * 
	 * @param attrDataMap
	 * @param precentAttrDataMap
	 * @param map
	 * @return
	 */
	public static void calcAttribute(Map<Integer, Integer> attrDataMap, Map<Integer, Integer> precentAttrDataMap, HashMap<Integer, AttributeItem> map) {
		// 计算固定值
		for (Entry<Integer, Integer> entry : attrDataMap.entrySet()) {
			Integer key = entry.getKey();
			AttributeItem attributeItem = map.get(key);
			int value = 0;
			int precentValue = 0;
			if (attributeItem != null) {
				value = attributeItem.getIncreaseValue();
				precentValue = attributeItem.getIncPerTenthousand();
			}

			attributeItem = new AttributeItem(AttributeType.getAttributeType(key), entry.getValue() + value, precentValue);
			map.put(key, attributeItem);
		}

		// 计算百分比值
		for (Entry<Integer, Integer> entry : precentAttrDataMap.entrySet()) {
			Integer key = entry.getKey();
			AttributeItem attributeItem = map.get(key);
			int value = 0;
			int precentValue = 0;
			if (attributeItem != null) {
				value = attributeItem.getIncreaseValue();
				precentValue = attributeItem.getIncPerTenthousand();
			}

			attributeItem = new AttributeItem(AttributeType.getAttributeType(key), value, entry.getValue() + precentValue);
			map.put(key, attributeItem);
		}
	}

	/**
	 * <pre>
	 * 计算每个部分的属性，然后整合到<Integer,AttributeItem>的Map中去
	 * 这个方法只适合于，增加的倍数仅限于传递进来的attrDataMap的属性
	 * E.g 装备附灵
	 * </pre>
	 * 
	 * @param attrDataMap
	 * @param precentAttrDataMap
	 * @param map
	 * @param addPrecent
	 * @return
	 */
	public static void calcAttribute(Map<Integer, Integer> attrDataMap, Map<Integer, Integer> precentAttrDataMap, HashMap<Integer, AttributeItem> map, int addPrecent) {
		HashMap<Integer, AttributeItem> attrMap = new HashMap<Integer, AttributeItem>();
		// 计算固定值
		for (Entry<Integer, Integer> entry : attrDataMap.entrySet()) {
			Integer key = entry.getKey();
			AttributeItem attributeItem = attrMap.get(key);

			// 基础增加属性 * 附灵增加的属性
			int value = entry.getValue();
			value += value * addPrecent / AttributeConst.DIVISION;

			int precentValue = 0;
			if (attributeItem != null) {
				value += attributeItem.getIncreaseValue();
				precentValue = attributeItem.getIncPerTenthousand();
			}

			attributeItem = new AttributeItem(AttributeType.getAttributeType(key), value, precentValue);
			attrMap.put(key, attributeItem);
		}

		// 计算百分比值
		for (Entry<Integer, Integer> entry : precentAttrDataMap.entrySet()) {
			Integer key = entry.getKey();
			AttributeItem attributeItem = attrMap.get(key);
			int value = 0;
			int precentValue = 0;
			if (attributeItem != null) {
				value = attributeItem.getIncreaseValue();
				precentValue = attributeItem.getIncPerTenthousand();
			}

			attributeItem = new AttributeItem(AttributeType.getAttributeType(key), value, entry.getValue() + precentValue);
			attrMap.put(key, attributeItem);
		}

		// 把这一块装备属性加到属性上
		for (Entry<Integer, AttributeItem> e : attrMap.entrySet()) {
			Integer key = e.getKey();
			AttributeItem value = e.getValue();
			if (value == null) {
				continue;
			}

			AttributeItem hasAttrItem = map.get(key);
			if (hasAttrItem == null) {
				map.put(key, value);
			} else {
				map.put(key, new AttributeItem(hasAttrItem.getType(), hasAttrItem.getIncreaseValue() + value.getIncreaseValue(), hasAttrItem.getIncPerTenthousand() + value.getIncPerTenthousand()));
			}
		}
	}

	/**
	 * 打印一下某个属性模块的属性值
	 * 
	 * @param componentName 模块名字
	 * @param map 属性值列表
	 * @return
	 */
	public static String partAttrMap2Str(String componentName, Map<Integer, AttributeItem> map) {
		StringBuilder attrData = new StringBuilder();
		attrData.append(componentName).append("-固定值>>>>>");
		StringBuilder pAttrData = new StringBuilder();
		pAttrData.append(componentName).append("-百分比值>>>>>");
		for (Entry<Integer, AttributeItem> e : map.entrySet()) {
			AttributeItem value = e.getValue();
			if (value == null) {
				continue;
			}

			AttributeType type = value.getType();
			if (type == null) {
				continue;
			}

			int increaseValue = value.getIncreaseValue();
			int precentValue = value.getIncPerTenthousand();

			attrData.append(type.attrFieldName).append(":");
			pAttrData.append(type.attrFieldName).append(":").append(precentValue).append(",");
			if (AttributeBM.isInt(type)) {
				attrData.append(increaseValue).append(",");
			} else {
				attrData.append(increaseValue / AttributeConst.BIG_FLOAT).append(",");
			}
		}

		return attrData.append("\n").append(pAttrData.toString()).toString();
	}
}