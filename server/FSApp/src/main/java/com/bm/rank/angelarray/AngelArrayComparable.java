package com.bm.rank.angelarray;

import com.rwbase.dao.angelarray.pojo.cfg.dao.AngelArrayMatchCfgCsvDao;

/*
 * @author HC
 * @date 2016年3月18日 下午4:05:01
 * @Description 万仙阵比较器
 */
public class AngelArrayComparable implements Comparable<AngelArrayComparable> {
	private int level;// 等级
	private int fighting;// 战斗力

	@Override
	public int compareTo(AngelArrayComparable o) {
		int result = compareLevel(level, o.level);
		if (result != 0) {
			return result;
		}

		// 比较战力
		if (fighting > o.fighting) {
			return 1;
		} else if (fighting < o.fighting) {
			return -1;

		}
		return 0;
	}

	/**
	 * 比较分段数据,同一个分段无所谓谁先后
	 * 
	 * @param firstLevel
	 * @param secondLevel
	 * @return
	 */
	private int compareLevel(int firstLevel, int secondLevel) {
		AngelArrayMatchCfgCsvDao cfgDAO = AngelArrayMatchCfgCsvDao.getCfgDAO();
		int firstLevelLimit = cfgDAO.getLevelLimit(firstLevel);
		int secondLevelLimit = cfgDAO.getLevelLimit(secondLevel);

		if (firstLevelLimit == secondLevelLimit) {// 同一个分段，谁先谁后都可以
			return 0;
		}

		if (firstLevelLimit > secondLevelLimit) {// 第一个分段大，
			return 1;
		}

		return -1;
	}

	public int getLevel() {
		return level;
	}

	public int getFighting() {
		return fighting;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setFighting(int fighting) {
		this.fighting = fighting;
	}
}