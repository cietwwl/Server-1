package com.rwbase.dao.role.pojo;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.common.HPCUtil;
import com.rwbase.common.attribute.AttributeUtils;

public class InlayCfg {

	private String roleId;// 佣兵ID
	private String openLv;// 宝石槽解锁等级
	private String prior;// 宝石选择优先级
	private int extraNum1;// 额外奖励条件1需求宝石数
	private String extraLv1;// 额外奖励条件1需求宝石等级
	private String extraValue1;// 奖励数值
	private String attrData1;// 增加的固定值属性
	private String precentAttrData1;// 增加的百分比属性
	private List<Integer> extraLvList1;// 额外宝石的等级需求
	/** <等级，属性> */
	private Map<Integer, AttrDataInfo> attrDataInfoMap1;// 额外宝石增加的属性Map

	private int extraNum2;// 额外奖励条件1需求宝石数
	private String extraLv2;// 额外奖励条件1需求宝石等级
	private String extraValue2;// 奖励数值
	private String attrData2;// 增加的固定值属性
	private String precentAttrData2;// 增加的百分比属性
	private List<Integer> extraLvList2;// 额外宝石的等级需求
	/** <等级，属性> */
	private Map<Integer, AttrDataInfo> attrDataInfoMap2;// 额外宝石增加的属性Map

	private int extraNum3;// 额外奖励条件1需求宝石数
	private String extraLv3;// 额外奖励条件1需求宝石等级
	private String extraValue3;// 奖励数值
	private String attrData3;// 增加的固定值属性
	private String precentAttrData3;// 增加的百分比属性
	private List<Integer> extraLvList3;// 额外宝石的等级需求
	/** <等级，属性> */
	private Map<Integer, AttrDataInfo> attrDataInfoMap3;// 额外宝石增加的属性Map

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getOpenLv() {
		return openLv;
	}

	public void setOpenLv(String openLv) {
		this.openLv = openLv;
	}

	public String getPrior() {
		return prior;
	}

	public void setPrior(String prior) {
		this.prior = prior;
	}

	public int getExtraNum1() {
		return extraNum1;
	}

	public void setExtraNum1(int extraNum1) {
		this.extraNum1 = extraNum1;
	}

	public String getExtraLv1() {
		return extraLv1;
	}

	public void setExtraLv1(String extraLv1) {
		this.extraLv1 = extraLv1;
	}

	public String getExtraValue1() {
		return extraValue1;
	}

	public void setExtraValue1(String extraValue1) {
		this.extraValue1 = extraValue1;
	}

	public int getExtraNum2() {
		return extraNum2;
	}

	public void setExtraNum2(int extraNum2) {
		this.extraNum2 = extraNum2;
	}

	public String getExtraLv2() {
		return extraLv2;
	}

	public void setExtraLv2(String extraLv2) {
		this.extraLv2 = extraLv2;
	}

	public String getExtraValue2() {
		return extraValue2;
	}

	public void setExtraValue2(String extraValue2) {
		this.extraValue2 = extraValue2;
	}

	public int getExtraNum3() {
		return extraNum3;
	}

	public void setExtraNum3(int extraNum3) {
		this.extraNum3 = extraNum3;
	}

	public String getExtraLv3() {
		return extraLv3;
	}

	public void setExtraLv3(String extraLv3) {
		this.extraLv3 = extraLv3;
	}

	public String getExtraValue3() {
		return extraValue3;
	}

	public void setExtraValue3(String extraValue3) {
		this.extraValue3 = extraValue3;
	}

	/**
	 * 获取额外的等级列表
	 * 
	 * @return
	 */
	public List<Integer> getExtraLvList1() {
		return extraLvList1;
	}

	/**
	 * 获取额外的等级列表
	 * 
	 * @return
	 */
	public List<Integer> getExtraLvList2() {
		return extraLvList2;
	}

	/**
	 * 获取额外的等级列表
	 * 
	 * @return
	 */
	public List<Integer> getExtraLvList3() {
		return extraLvList3;
	}

	/**
	 * 获取第一个附加的增加属性
	 * 
	 * @param level
	 * @return
	 */
	public AttrDataInfo getExtraAttrDataInfo1(int level) {
		return attrDataInfoMap1.get(level);
	}

	/**
	 * 获取第二个附加的增加属性
	 * 
	 * @param level
	 * @return
	 */
	public AttrDataInfo getExtraAttrDataInfo2(int level) {
		return attrDataInfoMap2.get(level);
	}

	/**
	 * 获取第三个附加的增加属性
	 * 
	 * @param level
	 * @return
	 */
	public AttrDataInfo getExtraAttrDataInfo3(int level) {
		return attrDataInfoMap3.get(level);
	}

	/**
	 * 加载的时候解析下数据
	 */
	public void initData() {
		// 数据解析1
		if (StringUtils.isEmpty(extraLv1)) {
			extraLvList1 = Collections.emptyList();
		} else {
			extraLvList1 = Collections.unmodifiableList(HPCUtil.parseIntegerList(extraLv1, ","));
		}

		attrDataInfoMap1 = AttributeUtils.parseStr2AttrInfoReadOnlyMap(extraLvList1, attrData1, precentAttrData1);
		// 数据解析2
		if (StringUtils.isEmpty(extraLv2)) {
			extraLvList2 = Collections.emptyList();
		} else {
			extraLvList2 = Collections.unmodifiableList(HPCUtil.parseIntegerList(extraLv2, ","));
		}

		attrDataInfoMap2 = AttributeUtils.parseStr2AttrInfoReadOnlyMap(extraLvList2, attrData2, precentAttrData2);
		// 数据解析3
		if (StringUtils.isEmpty(extraLv3)) {
			extraLvList3 = Collections.emptyList();
		} else {
			extraLvList3 = Collections.unmodifiableList(HPCUtil.parseIntegerList(extraLv3, ","));
		}
		attrDataInfoMap3 = AttributeUtils.parseStr2AttrInfoReadOnlyMap(extraLvList3, attrData3, precentAttrData3);
	}
}