package com.playerdata;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class MapAnimationState {
	private int normalMapId;

    private int normalAnimState;

    private int eliteMapId;

    private int eliteAnimState;

	public int getNormalMapId() {
		return normalMapId;
	}

	public void setNormalMapId(int normalMapId) {
		this.normalMapId = normalMapId;
	}

	public int getNormalAnimState() {
		return normalAnimState;
	}

	public void setNormalAnimState(int normalAnimState) {
		this.normalAnimState = normalAnimState;
	}

	public int getEliteMapId() {
		return eliteMapId;
	}

	public void setEliteMapId(int eliteMapId) {
		this.eliteMapId = eliteMapId;
	}

	public int getEliteAnimState() {
		return eliteAnimState;
	}

	public void setEliteAnimState(int eliteAnimState) {
		this.eliteAnimState = eliteAnimState;
	}
}
