package com.rwbase.common.enu;

public enum EColor {
	Gray(1),
	Red(2),
	Oringe(3),
	Green(4),
	Gold(5),
	Oringe_Red(6)
	;
	private int value;
	private EColor(int value){
		this.value = value;
	}
	public int getValue() {
		return value;
	}
}
