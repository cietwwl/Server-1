package com.rw.service.PeakArena.datamodel;

import com.common.PairParser;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.fsutil.common.Pair;

public class peakArenaPrize extends AbsRangeConfig{
	private int key; // 关键字段
	private String range; // 排名分段
	private int prizeCountPerHour; // 每小时可以领取的奖励

	public int getKey() {
		return key;
	}

	public int getPrizeCountPerHour() {
		return prizeCountPerHour;
	}

	private Pair<Integer, Integer> rankRange;

	@Override
	public void ExtraInitAfterLoad() {
		if (prizeCountPerHour < 0) {
			throw new RuntimeException(
					"peakArenaPrize.csv:无效的奖励,key=" + key + ",prizeCountPerHour=" + prizeCountPerHour);
		}
		rankRange = PairParser.ParseRange(range, "~", "巅峰竞技场,peakArenaPrize.csv", "key=" + key, "无效排名分段" + range, true);
		if (rankRange.getT1()<=0){
			throw new RuntimeException("无效排名分段:"+range);
		}
	}

	@Override
	public IReadOnlyPair<Integer, Integer> getRange() {
		return rankRange;
	}

}
