package com.rw.service.TaoistMagic.datamodel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.common.BaseConfig;
import com.common.ListParser;
import com.rwbase.common.attribute.AttributeType;
import com.rwbase.dao.attribute.AttributeLanguageCfgDAO;

public class TaoistMagicCfg extends BaseConfig {
	private int key; // key
	private int tagNum;// 分页
	private int openLevel; // 分页开放等级
	private int order;// 序号
	private String attribute; // 属性类型
	private String formulaParam; // 属性计算公式参数
	// private com.rwbase.dao.fashion.AttrValueType attrValueType; // 属性值的类型
	private int consumeId; // 技能消耗ID
	private com.rwbase.common.enu.eSpecialItemId coinType; // 货币类型

	private Map<String, TaoistMagicFormula> attrDataMap;// 属性
	private Map<String, TaoistMagicFormula> precentAttrDataMap;// 百分比属性

	// private TaoistMagicFormula formula;

	public int getKey() {
		return key;
	}

	public int getTagNum() {
		return tagNum;
	}

	public int getOpenLevel() {
		return openLevel;
	}

	public int getOrder() {
		return order;
	}

	public String getAttribute() {
		return attribute;
	}

	public String getFormulaParam() {
		return formulaParam;
	}

	// public com.rwbase.dao.fashion.AttrValueType getAttrValueType() {
	// return attrValueType;
	// }

	public int getConsumeId() {
		return consumeId;
	}

	public com.rwbase.common.enu.eSpecialItemId getCoinType() {
		return coinType;
	}

	@Override
	public void ExtraInitAfterLoad() {
		// 检查属性类型是否正确，检查公式参数是否有效，货币类型是否正确
		if (coinType == null) {
			throw new RuntimeException("无效货币类型,key=" + key);
		}

		// if (attrValueType == null) {
		// throw new RuntimeException("无效属性类型,key=" + key);
		// }

		Map<String, TaoistMagicFormula> attrDataMap = new HashMap<String, TaoistMagicFormula>();

		if (StringUtils.isEmpty(formulaParam)) {
			this.attrDataMap = Collections.emptyMap();
		} else {
			String[] attrArr = formulaParam.split(";");
			for (int i = 0, len = attrArr.length; i < len; i++) {
				String[] attr = attrArr[i].split("_");
				if (attr.length != 2) {
					throw new ExceptionInInitializerError(String.format("道术配置表中[%s]的每个属性配置不满足长度为2", key));
				}

				String attrName = attr[0];
				AttributeType attributeType = AttributeLanguageCfgDAO.getCfgDAO().getAttributeType(attrName);
				if (attributeType == null) {
					throw new ExceptionInInitializerError(String.format("道术配置表中[%s]的属性为[%s]没有对应的AttributeType的枚举", key, attrName));
				}

				int[] params = ListParser.ParseIntList(attr[1], "\\|", "道术", "属性计算参数无效", "key=" + key + ",");
				if (params.length < 4) {
					throw new RuntimeException("属性计算参数无效,key=" + key + ",参数:" + formulaParam);
				}

				attrDataMap.put(attrName, TaoistMagicFormula.Create(params[0], params[1], params[2], params[3]));
			}

			this.attrDataMap = Collections.unmodifiableMap(attrDataMap);
		}
	}

	public Map<String, TaoistMagicFormula> getAttrDataMap() {
		return attrDataMap;
	}

	public Map<String, TaoistMagicFormula> getPrecentAttrDataMap() {
		return precentAttrDataMap;
	}

	// public int getMagicValue(int level) {
	// return formula.getValue(level);
	// }
	//
	// public void cacheToLevel(int maxLvl) {
	// formula.cacheToLevel(maxLvl);
	// }
}