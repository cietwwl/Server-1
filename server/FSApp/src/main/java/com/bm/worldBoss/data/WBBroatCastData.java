package com.bm.worldBoss.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;

@JsonIgnoreProperties(ignoreUnknown = true)
@SynClass
public class WBBroatCastData {

	
	private boolean bossDie;
	
	private boolean bossLeave;

	public boolean isBossDie() {
		return bossDie;
	}

	public void setBossDie(boolean bossDie) {
		this.bossDie = bossDie;
	}

	public boolean isBossLeave() {
		return bossLeave;
	}

	public void setBossLeave(boolean bossLeave) {
		this.bossLeave = bossLeave;
	}
	
	
	
	
	
}
