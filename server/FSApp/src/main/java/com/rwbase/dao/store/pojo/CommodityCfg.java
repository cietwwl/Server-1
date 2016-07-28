package com.rwbase.dao.store.pojo;

import java.util.ArrayList;
import java.util.List;

public class CommodityCfg {

	private int id;
	private int goodsId;
	private int type;
	private int storeId;
	private int count;
	private int costType;
	private int cost;
	private int prob;
	private String level;
	private int superGoods;
	private int exchangeTime;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getStoreId() {
		return storeId;
	}
	public void setStoreId(int storeId) {
		this.storeId = storeId;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getCostType() {
		return costType;
	}
	public void setCostType(int costType) {
		this.costType = costType;
	}
	public int getCost() {
		return cost;
	}
	public void setCost(int cost) {
		this.cost = cost;
	}
	public int getProb() {
		return prob;
	}
	public void setProb(int prob) {
		this.prob = prob;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public int getSuperGoods() {
		return superGoods;
	}
	public void setSuperGoods(int superGoods) {
		this.superGoods = superGoods;
	}
	public int getExchangeTime() {
		return exchangeTime;
	}
	public void setExchangeTime(int exchangeTime) {
		this.exchangeTime = exchangeTime;
	}
}
