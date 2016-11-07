package com.bm.saloon.data;

import javax.persistence.Id;

import com.playerdata.dataSyn.annotation.SynClass;


@SynClass
public class SaloonPosition {
	
	@Id
	private String id;
	
	private float px;
	
	private float pz;
	
	public static SaloonPosition newInstance(String idP){
		SaloonPosition position = new SaloonPosition();
		position.id=idP;
		return position;
	}
	public static SaloonPosition newInstance(String idP, float pxP, float pyP){
		SaloonPosition position = new SaloonPosition();
		position.id=idP;
		position.px=pxP;
		position.pz=pyP;
		return position;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public float getPx() {
		return px;
	}

	public void setPx(float px) {
		this.px = px;
	}

	public float getPz() {
		return pz;
	}

	public void setPz(float py) {
		this.pz = py;
	}
	
	
	
}
