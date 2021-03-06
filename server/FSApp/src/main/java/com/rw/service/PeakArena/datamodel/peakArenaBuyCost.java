package com.rw.service.PeakArena.datamodel;

import com.common.PairParser;
import com.log.GameLog;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.fsutil.common.Pair;

public class peakArenaBuyCost extends AbsRangeConfig {
	private int key; // 关键字段
	private String time; // 购买挑战次数
	private int cost; // 购买花费
	private com.rwbase.common.enu.eSpecialItemId coinType; // 货币类型

	public int getKey() {
		return key;
	}

	public int getCost() {
		return cost;
	}

	public com.rwbase.common.enu.eSpecialItemId getCoinType() {
		return coinType;
	}

	private Pair<Integer, Integer> range;

	@Override
	public void ExtraInitAfterLoad() {
		if (coinType == null) {
			GameLog.error("巅峰竞技场", "货币配置错误", "");
			throw new RuntimeException();
		}
		if (cost <= 0) {
			GameLog.error("巅峰竞技场", "购买挑战次数费用配置错误", "" + cost);
			throw new RuntimeException();
		}
		range = PairParser.ParseRange(time, "~", "巅峰竞技场", "配置次数有误:", time, true);
	}

	public int getMinCount() {
		return range.getT1();
	}

	public int getMaxCount() {
		return range.getT2();
	}

	@Override
	public IReadOnlyPair<Integer, Integer> getRange() {
		return range;
	}
}
