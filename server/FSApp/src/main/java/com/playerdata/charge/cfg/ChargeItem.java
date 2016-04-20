package com.playerdata.charge.cfg;


public class ChargeItem {

	
	private String id;
	private int money;
	private int gold;
	
	
	public ChargeItem(String id, int money,int gold) {		
		this.id = id;
		this.money = money;
		this.gold = gold;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getMoney() {
		return money;
	}
	public void setMoney(int money) {
		this.money = money;
	}
	public int getGold() {
		return gold;
	}
	public void setGold(int gold) {
		this.gold = gold;
	}
	
	
	
	
	
}
