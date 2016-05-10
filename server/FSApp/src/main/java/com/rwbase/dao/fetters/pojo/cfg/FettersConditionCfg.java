package com.rwbase.dao.fetters.pojo.cfg;

/*
 * @author HC
 * @date 2016年4月27日 上午10:46:40
 * @Description 羁绊达成的条件
 */
public class FettersConditionCfg {
	private int uniqueId;// 唯一Id
	private int conditionId;// 条件Id
	private int conditionLevel;// 条件等级
	private String subConditionId;// 子条件Id
	private String fettersAttrData;// 羁绊增加属性
	private String fettersPrecentAttrData;// 羁绊增加的百分比属性

	/**
	 * 获取唯一Id
	 * 
	 * @return
	 */
	public int getUniqueId() {
		return uniqueId;
	}

	/**
	 * 获取条件Id
	 * 
	 * @return
	 */
	public int getConditionId() {
		return conditionId;
	}

	/**
	 * 获取条件的等级
	 * 
	 * @return
	 */
	public int getConditionLevel() {
		return conditionLevel;
	}

	/**
	 * 获取子条件Id列表
	 * 
	 * @return
	 */
	public String getSubConditionId() {
		return subConditionId;
	}

	/**
	 * 获取羁绊增加的属性
	 * 
	 * @return
	 */
	public String getFettersAttrData() {
		return fettersAttrData;
	}

	/**
	 * 获取羁绊增加的百分比属性
	 * 
	 * @return
	 */
	public String getFettersPrecentAttrData() {
		return fettersPrecentAttrData;
	}
}