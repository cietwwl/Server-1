package com.rwbase.dao.battle.pojo.cfg;

import java.util.Map;

import com.alibaba.druid.util.StringUtils;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attribute.AttributeConst;
import com.rwbase.common.attribute.AttributeType;
import com.rwbase.common.attribute.AttributeUtils;

/*
 * @author HC
 * @date 2016年5月14日 下午4:12:52
 * @Description 
 */
public class BufferCfg {
	private String Id;
	private int Type;// 法宝类型
	private String BuffValue;// 解析法宝buff
	// private String attrData;// 增加的固定值属性
	// private String precentAttrData;// 增加的百分比属性

	private Map<Integer, Integer> attrDataMap;// 增加固定值属性
	private Map<Integer, Integer> precentAttrDataMap;// 增加的百分比属性

	public String getId() {
		return Id;
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
		// luther说这里填属性的只有类型18跟7，服务器不需要太详细的解析方式
		if (Type != 18 && Type != 7) {
			return;
		}

		if (StringUtils.isEmpty(BuffValue)) {
			return;
		}

		String[] sArr = BuffValue.split("\\|");
		String attrDataStr = null;
		String precentAttrDataStr = null;
		if (sArr.length < 2) {
			attrDataStr = sArr[0];
		} else {
			attrDataStr = sArr[0];
			precentAttrDataStr = sArr[1];
		}

		// ===============================增加的固定属性
		this.attrDataMap = AttributeUtils.parseAttrDataStr2Map("BufferCfg", attrDataStr);
		// ===============================增加的百分比属性
		this.precentAttrDataMap = AttributeUtils.parseAttrDataStr2Map("BufferCfg", precentAttrDataStr);
	}
}