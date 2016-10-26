package com.playerdata.fightinggrowth.calc.param;

import com.rwbase.common.attrdata.AttrData;

/**
 * @Author HC
 * @date 2016年10月25日 上午10:44:43
 * @desc 计算英雄基础战斗里的参数
 **/

public class HeroBaseFightingParam {
	private final String heroTmpId;// 英雄的模版Id
	private final AttrData baseData;// 英雄的基础属性

	private HeroBaseFightingParam(String heroTmpId, AttrData baseData) {
		this.heroTmpId = heroTmpId;
		this.baseData = baseData;
	}

	public String getHeroTmpId() {
		return heroTmpId;
	}

	public AttrData getBaseData() {
		return baseData;
	}

	public static class Builder {
		private String heroTmpId;// 英雄的模版Id
		private AttrData baseData;// 英雄的基础属性

		public void setHeroTmpId(String heroTmpId) {
			this.heroTmpId = heroTmpId;
		}

		public void setBaseData(AttrData baseData) {
			this.baseData = baseData;
		}

		public HeroBaseFightingParam build() {
			return new HeroBaseFightingParam(heroTmpId, baseData);
		}
	}
}