package com.playerdata.groupcompetition.prepare;

import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.dataSyn.sameSceneSyn.SameSceneDataBaseIF;

@SynClass
public class PositionInfo extends SameSceneDataBaseIF{

	private float px;
	
	private float py;

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
