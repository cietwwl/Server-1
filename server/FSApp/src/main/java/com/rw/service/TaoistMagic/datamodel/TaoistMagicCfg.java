package com.rw.service.TaoistMagic.datamodel;

import com.common.BaseConfig;
import com.common.ListParser;

public class TaoistMagicCfg extends BaseConfig {
	private int key; // key
	private int tagNum;// 分页
	private int openLevel; // 分页开放等级
	private String attribute; // 属性类型
	private String formulaParam; // 属性计算公式参数
	private com.rwbase.dao.fashion.AttrValueType attrValueType; // 属性值的类型
	private int consumeId; // 技能消耗ID
	private TaoistMagicFormula formula;

	public int getKey() {
		return key;
	}

	public int getTagNum() {
		return tagNum;
	}

	public int getOpenLevel() {
		return openLevel;
	}

	public String getAttribute() {
		return attribute;
	}

	public String getFormulaParam() {
		return formulaParam;
	}

	public com.rwbase.dao.fashion.AttrValueType getAttrValueType() {
		return attrValueType;
	}

	public int getConsumeId() {
		return consumeId;
	}

	@Override
	public void ExtraInitAfterLoad() {
		// 检查属性类型是否正确，检查公式参数是否有效，
		if (attrValueType == null){
			throw new RuntimeException("无效属性类型,key="+key);
		}
		int[] params = ListParser.ParseIntList(formulaParam, "|", "道术", "属性计算参数无效", "key="+key+",");
		if (params.length <4){
			throw new RuntimeException("属性计算参数无效,key="+key+",参数:"+formulaParam);
		}
		formula = new TaoistMagicFormula(params[0],params[1],params[2],params[3]);
	}

	public int getMagicValue(int level){
		return formula.getValue(level);
	}
}
