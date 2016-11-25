package com.bm.worldBoss.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;

@JsonIgnoreProperties(ignoreUnknown = true)
@SynClass
public class WBBroatCastData {

	
	private String killBossRole;
	
	private boolean bossLeave;



	public boolean isBossLeave() {
		return bossLeave;
	}

	public void setBossLeave(boolean bossLeave) {
		this.bossLeave = bossLeave;
	}

	public String getKillBossRole() {
		return killBossRole;
	}

	public void setKillBossRole(String killBossRole) {
		this.killBossRole = killBossRole;
	}
	
	
	
	
	
}
