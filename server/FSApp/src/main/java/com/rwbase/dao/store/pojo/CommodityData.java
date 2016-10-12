package com.rwbase.dao.store.pojo;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class CommodityData {
	private int id;
	private int count;
	private int solt;
	private int exchangeCount;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getSolt() {
		return solt;
	}
	public void setSolt(int solt) {
		this.solt = solt;
	}
	public int getExchangeCount() {
		return exchangeCount;
	}
	public void setExchangeCount(int exchangeCount) {
		this.exchangeCount = exchangeCount;
	}
}
