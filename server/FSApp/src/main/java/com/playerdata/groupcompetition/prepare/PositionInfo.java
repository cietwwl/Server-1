package com.playerdata.groupcompetition.prepare;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class PositionInfo {

	private volatile float px;
	
	private volatile float py;

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
