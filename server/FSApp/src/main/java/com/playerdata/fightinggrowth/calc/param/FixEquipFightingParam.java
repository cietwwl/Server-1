package com.playerdata.fightinggrowth.calc.param;

import java.util.List;

import com.playerdata.team.HeroFixEquipInfo;

/**
 * @Author HC
 * @date 2016年10月25日 上午11:17:25
 * @desc 神器战斗力的计算参数
 **/

public class FixEquipFightingParam {
	private final List<HeroFixEquipInfo> fixEquips;// 神器的信息

	private FixEquipFightingParam(List<HeroFixEquipInfo> fixEquips) {
		this.fixEquips = fixEquips;
	}

	public List<HeroFixEquipInfo> getFixEquips() {
		return fixEquips;
	}

	public static class Builder {
		private List<HeroFixEquipInfo> fixEquips;// 神器的信息

		public void setFixEquips(List<HeroFixEquipInfo> fixEquips) {
			this.fixEquips = fixEquips;
		}

		public FixEquipFightingParam build() {
			return new FixEquipFightingParam(fixEquips);
		}
	}
}