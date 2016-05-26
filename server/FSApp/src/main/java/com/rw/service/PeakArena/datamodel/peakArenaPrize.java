package com.rw.service.PeakArena.datamodel;

import com.common.BaseConfig;
import com.common.ListParser;

public class peakArenaPrize extends BaseConfig {
	private String key; // 关键字段
	private String range; // 排名分段
	private int prizeCountPerHour; // 每小时可以领取的奖励

	public String getKey() {
		return key;
	}

	public int getPrizeCountPerHour() {
		return prizeCountPerHour;
	}

	private int min;
	private int max;

	@Override
	public void ExtraInitAfterLoad() {
		if (prizeCountPerHour < 0) {
			throw new RuntimeException(
					"peakArenaPrize.csv:无效的奖励,key=" + key + ",prizeCountPerHour=" + prizeCountPerHour);
		}
		int[] result = ListParser.ParseIntList(range, "~", "巅峰竞技场,peakArenaPrize.csv", "key=" + key, "无效分段" + range);
		min = result[0];
		if (result.length > 1) {
			max = result[1];
		} else {
			max = min;
		}
		if (min <= 0 || max <= 0 || min > max) {
			throw new RuntimeException("peakArenaPrize.csv:无效的排名分段,key=" + key + ",range=" + range);
		}
	}

}
