package com.playerdata.fightinggrowth.calc.param;

import java.util.List;
import java.util.Map;

import com.rwbase.dao.fetters.pojo.SynConditionData;

/**
 * @Author HC
 * @date 2016年10月25日 上午11:01:24
 * @desc
 **/

public class FettersFightingParam {
	private final List<Integer> magicFetters;// 法宝的羁绊
	private final List<Integer> fixEquipFetters;// 神器的羁绊
	private final Map<Integer, SynConditionData> heroFetters;// 英雄的羁绊

	private FettersFightingParam(List<Integer> magicFetters, List<Integer> fixEquipFetters, Map<Integer, SynConditionData> heroFetters) {
		this.magicFetters = magicFetters;
		this.fixEquipFetters = fixEquipFetters;
		this.heroFetters = heroFetters;
	}

	public List<Integer> getMagicFetters() {
		return magicFetters;
	}

	public List<Integer> getFixEquipFetters() {
		return fixEquipFetters;
	}

	public Map<Integer, SynConditionData> getHeroFetters() {
		return heroFetters;
	}

	public static class Builder {
		private List<Integer> magicFetters;// 法宝的羁绊
		private List<Integer> fixEquipFetters;// 神器的羁绊
		private Map<Integer, SynConditionData> heroFetters;// 英雄的羁绊

		public void setMagicFetters(List<Integer> magicFetters) {
			this.magicFetters = magicFetters;
		}

		public void setFixEquipFetters(List<Integer> fixEquipFetters) {
			this.fixEquipFetters = fixEquipFetters;
		}

		public void setHeroFetters(Map<Integer, SynConditionData> heroFetters) {
			this.heroFetters = heroFetters;
		}

		public FettersFightingParam build() {
			return new FettersFightingParam(magicFetters, fixEquipFetters, heroFetters);
		}
	}
}