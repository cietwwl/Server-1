package com.rwbase.dao.magicweapon.pojo;

import java.util.HashMap;

public class MagicExpCfg {

	private int level;
	private String goodsId;// 需要消耗的材料Id
	private int factor;// 继承时把材料转换成801001要的系数
	private int exp;
	private int moneyType;
	private int cost;
	private HashMap<Integer, Integer> consumeMap = new HashMap<Integer, Integer>();

	public int getLevel() {
		return level;
	}

	/**
	 * 强化需要的材料Id
	 * 
	 * @return
	 */
	public String getGoodsId() {
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

	public int getExp() {
		return exp;
	}

	public int getMoneyType() {
		return moneyType;
	}

	public int getCost() {
		return cost;
	}

	public HashMap<Integer, Integer> getConsumeMap() {
		return consumeMap;
	}

	public void setConsumeMap(HashMap<Integer, Integer> consumeMap) {
		this.consumeMap = consumeMap;
	}
}