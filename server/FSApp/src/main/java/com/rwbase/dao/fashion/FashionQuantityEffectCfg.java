package com.rwbase.dao.fashion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.common.BeanCopyer;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attrdata.AttrDataIF;

public class FashionQuantityEffectCfg implements IEffectCfg{
	public static Comparator<? super FashionQuantityEffectCfg> getComparator(){
		return Comparator;
	}
	private static Comparator<? super FashionQuantityEffectCfg> Comparator = new Comparator<FashionQuantityEffectCfg>() {

		@Override
		public int compare(FashionQuantityEffectCfg o1, FashionQuantityEffectCfg o2) {
			return o1.quantity - o2.quantity;
		}
	};
	
	public static FashionQuantityEffectCfg ZeroEffect(){
		if (zeroEff == null){
			zeroEff = new FashionQuantityEffectCfg();
			zeroEff.addedPercentages = new AttrData();
			zeroEff.addedValues = new AttrData();
		}
		return zeroEff;
	}
	private static FashionQuantityEffectCfg zeroEff;
	
	private int quantity;//时装数量
	// 战斗增益效果
	private String attrName1; // 属性名
	private int attrValue1; // 属性值
	private String attrValueType1;
	private AttrValueType attrValueType1Field; // 属性值的类型
	private String attrName2; // 属性名
	private int attrValue2; // 属性值
	private String attrValueType2;
	private AttrValueType attrValueType2Field; // 属性值的类型
	private String attrName3; // 属性名
	private int attrValue3; // 属性值
	private String attrValueType3;
	private AttrValueType attrValueType3Field; // 属性值的类型
	private String attrName4; // 属性名
	private int attrValue4; // 属性值
	private String attrValueType4;
	private AttrValueType attrValueType4Field; // 属性值的类型
	private String attrName5; // 属性名
	private int attrValue5; // 属性值
	private String attrValueType5;
	private AttrValueType attrValueType5Field; // 属性值的类型
	// 这个配置所有值（Value）的增益效果
	private AttrData addedValues;
	// 这个配置所有百分比（Percentage）的增益效果
	private AttrData addedPercentages;
	
	public void ExtraInit() {
		// 枚举转换异常处理
		attrValueType1Field = AttrValueType.valueOf(attrValueType1,AttrValueType.Value);
		attrValueType2Field = AttrValueType.valueOf(attrValueType2,AttrValueType.Value);
		attrValueType3Field = AttrValueType.valueOf(attrValueType3,AttrValueType.Value);
		attrValueType4Field = AttrValueType.valueOf(attrValueType4,AttrValueType.Value);
		attrValueType5Field = AttrValueType.valueOf(attrValueType5,AttrValueType.Value);
		// 求增益值和百分比
		addedValues = new AttrData();
		addedPercentages = new AttrData();
		List<IReadOnlyPair<String, Object>> sourceValues = new ArrayList<IReadOnlyPair<String,Object>>();
		List<IReadOnlyPair<String, Object>> sourcePer = new ArrayList<IReadOnlyPair<String,Object>>();
		AttrValueType.collectValue(sourceValues,sourcePer,attrValueType1Field,attrName1,attrValue1);
		AttrValueType.collectValue(sourceValues,sourcePer,attrValueType2Field,attrName2,attrValue2);
		AttrValueType.collectValue(sourceValues,sourcePer,attrValueType3Field,attrName3,attrValue3);
		AttrValueType.collectValue(sourceValues,sourcePer,attrValueType4Field,attrName4,attrValue4);
		AttrValueType.collectValue(sourceValues,sourcePer,attrValueType5Field,attrName5,attrValue5);
		BeanCopyer.SetFields(sourceValues , addedValues, null);
		BeanCopyer.SetFields(sourcePer , addedPercentages, null);
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
}
