package com.playerdata.fightinggrowth.calc.param;

/**
 * @Author HC
 * @date 2016年10月25日 上午10:49:38
 * @desc 时装计算战力的参数
 **/

public class FashionFightingParam {
	private final int suitCount;// 套装的件数
	private final int wingCount;// 翅膀的件数
	private final int petCount;// 宠物的数量

	private FashionFightingParam(int suitCount, int wingCount, int petCount) {
		this.suitCount = suitCount;
		this.wingCount = wingCount;
		this.petCount = petCount;
	}

	public int getSuitCount() {
		return suitCount;
	}

	public int getWingCount() {
		return wingCount;
	}

	public int getPetCount() {
		return petCount;
	}

	public static class Builder {
		private int suitCount;// 套装的件数
		private int wingCount;// 翅膀的件数
		private int petCount;// 宠物的数量

		public void setSuitCount(int suitCount) {
			this.suitCount = suitCount;
		}

		public void setWingCount(int wingCount) {
			this.wingCount = wingCount;
		}

		public void setPetCount(int petCount) {
			this.petCount = petCount;
		}

		public FashionFightingParam build() {
			return new FashionFightingParam(suitCount, wingCount, petCount);
		}
	}
}