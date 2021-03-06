package com.rwbase.dao.fashion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.common.BaseConfig;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attrdata.AttrDataIF;
import com.rwbase.common.attribute.AttributeConst;
import com.rwbase.common.attribute.AttributeType;
import com.rwbase.common.attribute.AttributeUtils;

public class FashionQuantityEffectCfg extends BaseConfig {
	public static Comparator<? super FashionQuantityEffectCfg> getComparator() {
		return Comparator;
	}

	private static Comparator<? super FashionQuantityEffectCfg> Comparator = new Comparator<FashionQuantityEffectCfg>() {

		@Override
		public int compare(FashionQuantityEffectCfg o1, FashionQuantityEffectCfg o2) {
			return o1.quantity - o2.quantity;
		}
	};

	private int quantity;// 时装数量
	// 战斗增益效果
	private String attrName1; // 属性名
	private int attrValue1; // 属性值
	private AttrValueType attrValueType1 = AttrValueType.Value; // 属性值的类型
	private String attrName2; // 属性名
	private int attrValue2; // 属性值
	private AttrValueType attrValueType2 = AttrValueType.Value; // 属性值的类型
	private String attrName3; // 属性名
	private int attrValue3; // 属性值
	private AttrValueType attrValueType3 = AttrValueType.Value; // 属性值的类型
	private String attrName4; // 属性名
	private int attrValue4; // 属性值
	private AttrValueType attrValueType4 = AttrValueType.Value; // 属性值的类型
	private String attrName5; // 属性名
	private int attrValue5; // 属性值
	private AttrValueType attrValueType5 = AttrValueType.Value; // 属性值的类型
	// 这个配置所有值（Value）的增益效果
	private AttrData addedValues;
	// 这个配置所有百分比（Percentage）的增益效果
	private AttrData addedPercentages;

	private String attrData;// 增加的固定值属性
	private String precentAttrData;// 增加的百分比属性
	private Map<Integer, Integer> attrDataMap;// 增加固定值属性
	private Map<Integer, Integer> precentAttrDataMap;// 增加的百分比属性

	public void ExtraInitAfterLoad() {
		List<IReadOnlyPair<String, Object>> sourceValues = new ArrayList<IReadOnlyPair<String, Object>>();
		List<IReadOnlyPair<String, Object>> sourcePer = new ArrayList<IReadOnlyPair<String, Object>>();
		AttrValueType.collectValue(sourceValues, sourcePer, attrValueType1, attrName1, attrValue1);
		AttrValueType.collectValue(sourceValues, sourcePer, attrValueType2, attrName2, attrValue2);
		AttrValueType.collectValue(sourceValues, sourcePer, attrValueType3, attrName3, attrValue3);
		AttrValueType.collectValue(sourceValues, sourcePer, attrValueType4, attrName4, attrValue4);
		AttrValueType.collectValue(sourceValues, sourcePer, attrValueType5, attrName5, attrValue5);
	}

	public int getQuantity() {
		return quantity;
	}

	public AttrDataIF getAddedValues() {
		return addedValues;
	}

	public AttrDataIF getAddedPercentages() {
		return addedPercentages;
	}

	/**
	 * <pre>
	 * 获取增加的固定值属性
	 * 返回的这个Map的key是{@link AttributeType}的属性类型
	 * 返回的value（都是放大到了{@link AttributeConst#DIVISION}的倍数）有特殊处理，计算属性全部是用的int类型，然而为了防止
	 * 配置中会出现float类型的数据，所有这里凡是遇到在{@link AttrData}
	 * 中字段是float类型的的属性，都会把配置中的值扩大{@link AttributeConst#BIG_FLOAT}
	 * 的倍数
	 * </pre>
	 * 
	 * @return
	 */
	public Map<Integer, Integer> getAttrDataMap() {
		return attrDataMap;
	}

	/**
	 * <pre>
	 * 获取增加的百分比属性
	 * 返回的这个Map的key是{@link AttributeType}的属性类型
	 * 返回的value（都是放大到了{@link AttributeConst#DIVISION}的倍数）有特殊处理，计算属性全部是用的int类型，然而为了防止
	 * 配置中会出现float类型的数据，所有这里凡是遇到在{@link AttrData}
	 * 中字段是float类型的的属性，都会把配置中的值扩大{@link AttributeConst#BIG_FLOAT}
	 * 的倍数
	 * </pre>
	 * 
	 * @return
	 */
	public Map<Integer, Integer> getPrecentAttrDataMap() {
		return precentAttrDataMap;
	}

	/**
	 * 初始化解析属性
	 */
	public void initData() {
		// ===============================增加的固定属性
		this.attrDataMap = AttributeUtils.parseAttrDataStr2Map("FashionQuantityEffectCfg", attrData);
		// ===============================增加的百分比属性
		this.precentAttrDataMap = AttributeUtils.parseAttrDataStr2Map("FashionQuantityEffectCfg", precentAttrData);
	}
}