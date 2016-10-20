package com.bm.saloon.data;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class SaloonPlayerFashion {

	private int wingId = -1;
	private int suitId = -1;
	private int petId = -1;
	
	public int getWingId() {
		return wingId;
	}
	public void setWingId(int wingId) {
		this.wingId = wingId;
	}
	public int getSuitId() {
		return suitId;
	}
	public void setSuitId(int suitId) {
		this.suitId = suitId;
	}
	public int getPetId() {
		return petId;
	}
	public void setPetId(int petId) {
		this.petId = petId;
	}
	
	
}
