package com.bm.saloon.data;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rwbase.dao.fashion.FashionBeingUsed;

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
	public static SaloonPlayerFashion from(FashionBeingUsed fashionBeingUsed) {
		
		SaloonPlayerFashion fashion = new SaloonPlayerFashion();
		fashion.wingId = fashionBeingUsed.getWingId();
		fashion.suitId = fashionBeingUsed.getSuitId();
		fashion.petId = fashionBeingUsed.getPetId();
		
		return fashion;
	}
	
	
}
