package com.rwbase.dao.magicweapon.pojo;

public class MagicExpCfg {

	private int level;
	private int goodsId;// 需要消耗的材料Id
	private int factor;// 继承时把材料转换成801001要的系数
	private int goods;// 需要的数量
	private int exp;
	private int moneyType;
	private int cost;

	public int getLevel() {
		return level;
	}

	/**
	 * 强化需要的材料Id
	 * 
	 * @return
	 */
	public int getGoodsId() {
		return goodsId;
	}

	/**
	 * 继承时候需要要把材料转换成801001的系数
	 * 
	 * @return
	 */
	public int getFactor() {
		return factor;
	}

	/**
	 * 强化需要的材料数量
	 * 
	 * @return
	 */
	public int getGoods() {
		return goods;
	}

	public int getExp() {
		return exp;
	}

	public int getMoneyType() {
		return moneyType;
	}

	public int getCost() {
		return cost;
	}
}