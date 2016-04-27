package com.common;

import java.util.List;
import java.util.Random;

import com.log.GameLog;
import com.rw.fsutil.common.Pair;

public class RandomIntGroups {
	private int[] plans;
	private int[] distributions;
	private int accumulation;

	public static RandomIntGroups Create(String module, String moduleID, String pairSeperator, String keyValueSeperator,
			String pairListStr){
		return new RandomIntGroups(module, moduleID, pairSeperator, keyValueSeperator, pairListStr);
	}

	protected RandomIntGroups(){}
	
	protected RandomIntGroups(String module, String moduleID, String pairSeperator, String keyValueSeperator,
			String pairListStr) {
		this(ListParser.ParsePairList(module, moduleID, pairSeperator,
				keyValueSeperator, pairListStr));
	}
	
	protected RandomIntGroups(List<Pair<Integer, Integer>> pairList){
		int ordinarySize = pairList.size();
		plans = new int[ordinarySize];
		distributions = new int[ordinarySize];
		accumulation = 0;
		for (int i = 0; i < ordinarySize; i++) {
			Pair<Integer, Integer> pair = pairList.get(i);
			plans[i] = pair.getT1().intValue();
			accumulation += pair.getT2().intValue();
			distributions[i] = accumulation;
		}
	}
	
	public int getRandomGroup(Random r){
		int ran = r.nextInt(accumulation);
		for (int i = 0; i < distributions.length; i++) {
			int dis = distributions[i];
			if (ran < dis){
				return plans[i];
			}
		}
		//bug here!
		GameLog.error("RandomGroups", "getRandomGroup", "bug in distribution");
		return plans[0];
	}
}
