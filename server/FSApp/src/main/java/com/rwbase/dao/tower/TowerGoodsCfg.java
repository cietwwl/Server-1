package com.rwbase.dao.tower;

/**
 * 
 * @author HC
 * @date 2015年11月14日 下午4:53:01
 * @Description 万仙阵随机掉落奖励
 */
public class TowerGoodsCfg {
	private int itemId;// 物品id
	private int weight;// 权重
	private int leastNum;// 最少数量
	private int maxNum;// 最大数量
	private int formatId;// 掉落方案

	// ////////////////////////////////////////////////////GET区域
	public int getItemId() {
		return itemId;
	}

	public int getWeight() {
		return weight;
	}

	public int getLeastNum() {
		return leastNum;
	}

	public int getMaxNum() {
		return maxNum;
	}

	public int getFormatId() {
		return formatId;
	}

	// ////////////////////////////////////////////////////SET区域
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public void setLeastNum(int leastNum) {
		this.leastNum = leastNum;
	}

	public void setMaxNum(int maxNum) {
		this.maxNum = maxNum;
	}

	public void setFormatId(int formatId) {
		this.formatId = formatId;
	}
}