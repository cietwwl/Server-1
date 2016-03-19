package com.rwbase.dao.battletower.pojo.cfg;

/*
 * @author HC
 * @date 2015年9月7日 下午2:16:48
 * @Description 试练塔Boss模版
 */
public class BattleTowerBossCfg {
	private int bossId;// Boss配置Id
	private int pro;// 出现的权重
	private int levelLimit;// 当前分段属于那个等级段
	private String rewardInfo;// 奖励的物品数据
	private String dropIds;// 掉落的配置Id

	public int getBossId() {
		return bossId;
	}

	public int getPro() {
		return pro;
	}

	public int getLevelLimit() {
		return levelLimit;
	}

	public String getRewardInfo() {
		return rewardInfo;
	}

	public String getDropIds() {
		return dropIds;
	}

	// public static void main(String[] args) {
	// TreeMap<Integer, Integer> tMap = new TreeMap<Integer, Integer>();
	// for (int i = 1; i <= 10; i++) {
	// tMap.put(i * 10, i * 100);
	// }
	//
	// int key = 21;
	// Entry<Integer, Integer> ceilingEntry = tMap.ceilingEntry(key);
	// System.err.println(ceilingEntry.getKey() + "," + ceilingEntry.getValue());
	//
	// Entry<Integer, Integer> floorEntry = tMap.floorEntry(key);
	// System.err.println(floorEntry.getKey() + "," + floorEntry.getValue());
	//
	// Entry<Integer, Integer> higherEntry = tMap.higherEntry(key);
	// System.err.println(higherEntry.getKey() + "," + higherEntry.getValue());
	//
	// Entry<Integer, Integer> lowerEntry = tMap.lowerEntry(key);
	// System.err.println(lowerEntry.getKey() + "," + lowerEntry.getValue());
	// }
}