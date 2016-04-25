package com.rwbase.dao.gift;

import java.util.HashMap;
import java.util.Map;



public class ComGiftCfg {

	private String id;
	
	private String gift;
	
	private Map<String, Integer> giftMap = new HashMap<String, Integer>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGift() {
		return gift;
	}

	public void setGift(String gift) {
		this.gift = gift;
	}

	public Map<String, Integer> getGiftMap() {
		return giftMap;
	}

	public void setGiftMap(Map<String, Integer> giftMap) {
		this.giftMap = giftMap;
	}

	
	
	
}
