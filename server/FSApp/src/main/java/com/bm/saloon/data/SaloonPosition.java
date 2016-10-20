package com.bm.saloon.data;

import com.playerdata.dataSyn.annotation.SynClass;


@SynClass
public class SaloonPosition {
	
	private String id;
	
	private float px;
	
	private float py;

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

	public float getPy() {
		return py;
	}

	public void setPy(float py) {
		this.py = py;
	}
	
	
	
}
