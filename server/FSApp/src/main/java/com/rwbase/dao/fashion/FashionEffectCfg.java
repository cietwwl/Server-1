package com.rwbase.dao.fashion;

import java.util.ArrayList;
import java.util.List;

import com.common.BeanCopyer;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attrdata.AttrDataIF;
import com.rwbase.common.enu.ECareer;

/**
 * addedValues,addedPercentages不能被修改，但是AttrData到处使用，暂时没有发现修改的地方
 * @author franky
 *
 */
public class FashionEffectCfg implements IEffectCfg {
	private String key; // 关键字
	private int id; // 时装id
	private ECareer CareerType = ECareer.None; // 职业
	// 战斗增益效果
	private String attrName1; // 属性名
	private int attrValue1; // 属性值
	private AttrValueType attrValueType1=AttrValueType.Value; // 属性值的类型
	private String attrName2; // 属性名
	private int attrValue2; // 属性值
	private AttrValueType attrValueType2=AttrValueType.Value; // 属性值的类型
	private String attrName3; // 属性名
	private int attrValue3; // 属性值
	private AttrValueType attrValueType3=AttrValueType.Value; // 属性值的类型
	private String attrName4; // 属性名
	private int attrValue4; // 属性值
	private AttrValueType attrValueType4=AttrValueType.Value; // 属性值的类型
	private String attrName5; // 属性名
	private int attrValue5; // 属性值
	private AttrValueType attrValueType5=AttrValueType.Value; // 属性值的类型
	// 这个配置所有值（Value）的增益效果
	private AttrData addedValues;
	// 这个配置所有百分比（Percentage）的增益效果
	private AttrData addedPercentages;
	
	public void ExtraInit() {
		// 求增益值和百分比
		addedValues = new AttrData();
		addedPercentages = new AttrData();
		List<IReadOnlyPair<String, Object>> sourceValues = new ArrayList<IReadOnlyPair<String,Object>>();
		List<IReadOnlyPair<String, Object>> sourcePer = new ArrayList<IReadOnlyPair<String,Object>>();
		AttrValueType.collectValue(sourceValues,sourcePer,attrValueType1,attrName1,attrValue1);
		AttrValueType.collectValue(sourceValues,sourcePer,attrValueType2,attrName2,attrValue2);
		AttrValueType.collectValue(sourceValues,sourcePer,attrValueType3,attrName3,attrValue3);
		AttrValueType.collectValue(sourceValues,sourcePer,attrValueType4,attrName4,attrValue4);
		AttrValueType.collectValue(sourceValues,sourcePer,attrValueType5,attrName5,attrValue5);
		BeanCopyer.SetFields(sourceValues , addedValues, null);
		BeanCopyer.SetFields(sourcePer , addedPercentages, null);
	}

	public String getKey() {
		return key;
	}

	public int getFashionId() {
		return id;
	}

	public ECareer getCareerTypeField() {
		return CareerType;
	}

	@Override
	public AttrDataIF getAddedValues() {
		return addedValues;
	}

	@Override
	public AttrDataIF getAddedPercentages() {
		return addedPercentages;
	}
}