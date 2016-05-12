package com.rwbase.dao.fetters.pojo.cfg;

/*
 * @author HC
 * @date 2016年4月27日 上午10:46:58
 * @Description 羁绊判定达成的子条件
 */
public class FettersSubConditionCfg {
	private int subConditionId;// 子条件Id
	private int subConditionRestrictType;// 子条件限定
	private int subConditionRestrictValue;// 子条件限定值
	private String subConditionValue;// 条件值

	/**
	 * 子条件Id
	 * 
	 * @return
	 */
	public int getSubConditionId() {
		return subConditionId;
	}

	/**
	 * 获取子条件强制限定的类型
	 * 
	 * @return
	 */
	public int getSubConditionRestrictType() {
		return subConditionRestrictType;
	}

	/**
	 * 获取子条件强制限定的值
	 * 
	 * @return
	 */
	public int getSubConditionRestrictValue() {
		return subConditionRestrictValue;
	}

	/**
	 * 条件值
	 * 
	 * @return
	 */
	public String getSubConditionValue() {
		return subConditionValue;
	}
}