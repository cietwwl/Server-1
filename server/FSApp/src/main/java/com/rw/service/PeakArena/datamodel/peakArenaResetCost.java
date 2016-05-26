package com.rw.service.PeakArena.datamodel;

import com.common.BaseConfig;
import com.common.ListParser;
import com.log.GameLog;

public class peakArenaResetCost extends BaseConfig {
	private String key; // 关键字段
	private String time; // 重置次数
	private int cost; // 重置花费
	private com.rwbase.common.enu.eSpecialItemId coinType; // 货币类型

	public String getKey() {
		return key;
	}

	public int getCost() {
		return cost;
	}

	public com.rwbase.common.enu.eSpecialItemId getCoinType() {
		return coinType;
	}

	private int minCount;
	private int maxCount;

	@Override
	public void ExtraInitAfterLoad() {
		if (coinType == null) {
			GameLog.error("巅峰竞技场", "货币配置错误", "");
			throw new RuntimeException();
		}
		if (cost <= 0) {
			GameLog.error("巅峰竞技场", "重置费用配置错误", ""+cost);
			throw new RuntimeException();
		}
		int[] result = ListParser.ParseIntList(time, "~", "巅峰竞技场", "配置次数有误:", time);
		minCount = result[0];
		if (result.length > 1) {
			maxCount = result[1];
		} else {
			maxCount = minCount;
		}
		if (minCount <= 0 || maxCount <= 0 || minCount > maxCount) {
			GameLog.error("巅峰竞技场", "配置次数错误:", time);
			throw new RuntimeException();
		}
	}

	public int getMinCount() {
		return minCount;
	}

	public int getMaxCount() {
		return maxCount;
	}

}
