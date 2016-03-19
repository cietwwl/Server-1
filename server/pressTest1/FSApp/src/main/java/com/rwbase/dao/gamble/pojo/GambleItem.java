package com.rwbase.dao.gamble.pojo;

import java.util.HashMap;
import java.util.Map;

import com.rwproto.GambleServiceProtos.EGambleType;
import com.rwproto.GambleServiceProtos.ELotteryType;

public class GambleItem{
	private Map<EGambleType, Map<ELotteryType, Integer>> goldCount = new HashMap<EGambleType, Map<ELotteryType,Integer>>();//收费抽奖记录
	private Map<EGambleType, Map<ELotteryType, Integer>> freeCount = new HashMap<EGambleType, Map<ELotteryType,Integer>>();//免费抽奖记录
	private long lastPrayTime;//上一次高级祈祷的时间戳
	private long lastOrdinaryTime;//上一次普通祈祷时间戳
	private int surplusOrdinaryCount;//当天已用普通免费次数

	/**上一次高级祈祷的时间点*/
	public long getLastPrayTime() {
		return lastPrayTime;
	}

	public void setLastPrayTime(long lastPrayTime) {
		this.lastPrayTime = lastPrayTime;
	}

	public Map<EGambleType, Map<ELotteryType, Integer>> getGoldCount() {
		return goldCount;
	}

	public void setGoldCount(Map<EGambleType, Map<ELotteryType, Integer>> goldCount) {
		this.goldCount = goldCount;
	}

	public long getLastOrdinaryTime() {
		return lastOrdinaryTime;
	}

	public void setLastOrdinaryTime(long lastOrdinaryTime) {
		this.lastOrdinaryTime = lastOrdinaryTime;
	}

	public int getSurplusOrdinaryCount() {
		return surplusOrdinaryCount;
	}

	public void setSurplusOrdinaryCount(int surplusOrdinaryCount) {
		this.surplusOrdinaryCount = surplusOrdinaryCount;
	}

	public Map<EGambleType, Map<ELotteryType, Integer>> getFreeCount() {
		return freeCount;
	}

	public void setFreeCount(Map<EGambleType, Map<ELotteryType, Integer>> freeCount) {
		this.freeCount = freeCount;
	}
}
