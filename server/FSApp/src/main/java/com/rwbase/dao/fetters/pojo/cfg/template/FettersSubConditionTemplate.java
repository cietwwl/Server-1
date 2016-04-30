package com.rwbase.dao.fetters.pojo.cfg.template;

import java.util.Collections;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.common.HPCUtil;
import com.rwbase.dao.fetters.pojo.cfg.FettersSubConditionCfg;

/*
 * @author HC
 * @date 2016年4月27日 下午5:03:47
 * @Description 
 */
public class FettersSubConditionTemplate {
	private final int subConditionId;// 子条件Id
	private final int subConditionRestrictType;// 子条件限定
	private final int subConditionRestrictValue;// 子条件限定值
	private final Map<Integer, Integer> subConditionValueMap;

	public FettersSubConditionTemplate(FettersSubConditionCfg cfg) {
		this.subConditionId = cfg.getSubConditionId();
		this.subConditionRestrictType = cfg.getSubConditionRestrictType();
		this.subConditionRestrictValue = cfg.getSubConditionRestrictValue();

		String subConditionValue = cfg.getSubConditionValue();
		if (StringUtils.isEmpty(subConditionValue)) {
			this.subConditionValueMap = Collections.emptyMap();
		} else {
			this.subConditionValueMap = Collections.unmodifiableMap(HPCUtil.parseIntegerMap(subConditionValue, ";", "_"));
		}
	}

	/**
	 * 获取限定的子条件Id
	 * 
	 * @return
	 */
	public int getSubConditionId() {
		return subConditionId;
	}

	/**
	 * 获取强制限定子条件的类型
	 * 
	 * @return
	 */
	public int getSubConditionRestrictType() {
		return subConditionRestrictType;
	}

	/**
	 * 获取强制限定子条件的值
	 * 
	 * @return
	 */
	public int getSubConditionRestrictValue() {
		return subConditionRestrictValue;
	}

	/**
	 * 获取子条件的值
	 * 
	 * @return
	 */
	public Map<Integer, Integer> getSubConditionValueMap() {
		return subConditionValueMap;
	}
}