package com.playerdata.charge.cfg;


public class ChargeItem {

	
	private String id;
	private int money;
	
	
	public ChargeItem(String id, int money) {		
		this.id = id;
		this.money = money;
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
	
	
	
	
	
}
