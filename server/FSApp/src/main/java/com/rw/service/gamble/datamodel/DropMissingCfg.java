package com.rw.service.gamble.datamodel;

import org.apache.commons.lang3.StringUtils;

import com.common.BaseConfig;
import com.common.ListParser;

public class DropMissingCfg extends BaseConfig {
	private String key; // 关键字段
	private int minQualityLevel; // 最小品阶
	private int maxQualityLevel; // 最大品阶
	private String excludeList; // 排除位置
	private int[] excludeEquipPosition;

	@Override
	public void ExtraInitAfterLoad() {
		if (StringUtils.isNotBlank(excludeList)){
			excludeEquipPosition = ListParser.ParseIntList(excludeList, ",", "钓鱼台", "", "排除位置");
		}else{
			excludeEquipPosition = new int[0];
		}
	}

	public int[] getExcludeEquipPosition() {
		return excludeEquipPosition;
	}

	public String getKey() {
		return key;
	}

	public boolean isQualityInRange(int quality) {
		return minQualityLevel <= quality && quality <= maxQualityLevel;
	}

}
