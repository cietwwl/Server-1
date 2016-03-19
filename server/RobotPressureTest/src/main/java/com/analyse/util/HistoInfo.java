package com.analyse.util;

public class HistoInfo{
	
	private String name;
	private int b4;
	private int after;
	private int delta;
	
	public HistoInfo(String nameP, int b4, int after){
		this.name = nameP;
		this.b4 = b4;
		this.after = after;
		this.delta = b4-after;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getB4() {
		return b4;
	}

	public void setB4(int b4) {
		this.b4 = b4;
	}

	public int getAfter() {
		return after;
	}

	public void setAfter(int after) {
		this.after = after;
	}

	public int getDelta() {
		return delta;
	}

	public void setDelta(int delta) {
		this.delta = delta;
	}
	
	
}