package com.common;

import java.util.List;
import java.util.Random;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.log.GameLog;
import com.rw.fsutil.common.Pair;

public class RandomStringGroups {
	protected String[] plans;// 物品的Id
	protected int[] distributions;// 物品累计的概率
	protected int accumulation;// 总概率

	@JsonIgnore
	public static RandomStringGroups Create(String module, String moduleID, String pairSeperator, String keyValueSeperator, String pairListStr) {
		return new RandomStringGroups(module, moduleID, pairSeperator, keyValueSeperator, pairListStr);
	}

	@JsonIgnore
	public static RandomStringGroups Create(List<Pair<String, Integer>> pairList) {
		return new RandomStringGroups(pairList);
	}

	protected RandomStringGroups() {
	}

	@JsonIgnore
	protected RandomStringGroups(String module, String moduleID, String pairSeperator, String keyValueSeperator, String pairListStr) {
		this(ListParser.ParseStrIntPairList(module, moduleID, pairSeperator, keyValueSeperator, pairListStr));
	}

	@JsonIgnore
	protected RandomStringGroups(List<Pair<String, Integer>> pairList) {
		int ordinarySize = pairList.size();
		plans = new String[ordinarySize];
		distributions = new int[ordinarySize];
		accumulation = 0;
		for (int i = 0; i < ordinarySize; i++) {
			Pair<String, Integer> pair = pairList.get(i);
			plans[i] = pair.getT1();// 物品Id
			accumulation += pair.getT2().intValue();
			distributions[i] = accumulation;
		}
	}

	/**
	 * 获取随机到的道具所属的索引，以及权重
	 *
	 * @param r
	 * @param planIndex
	 * @param weight
	 * @return
	 */
	@JsonIgnore
	public String getRandomGroup(Random r, RefInt planIndex, RefInt weight) {
		int ran = r.nextInt(accumulation);
		for (int i = 0; i < distributions.length; i++) {
			int dis = distributions[i];
			if (ran < dis) {
				if (planIndex != null) {
					planIndex.value = i;
				}
				if (weight != null) {
					weight.value = i > 0 ? dis - distributions[i - 1] : dis;
				}
				return plans[i];
			}
		}
		// bug here!
		GameLog.error("RandomGroups", "getRandomGroup", "bug in distribution");
		return null;
	}

	@JsonIgnore
	public int size() {
		return distributions.length;
	}
}