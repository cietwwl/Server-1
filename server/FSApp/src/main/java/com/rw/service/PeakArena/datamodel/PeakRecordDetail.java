package com.rw.service.PeakArena.datamodel;

import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 
 * 巅峰竞技场战报的详细信息，记录两方的出阵阵容
 * 
 * @author CHEN.P
 *
 */
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PeakRecordDetail {

	@JsonProperty("1")
	private int teamId; // 队伍id
	@JsonProperty("2")
	private List<PeakRecordHeroInfo> myCamp; // 我方英雄的记录
	@JsonProperty("3")
	private PeakRecordMagicInfo myMagic; // 我方法宝的记录
	@JsonProperty("4")
	private List<PeakRecordHeroInfo> enemyCamp; // 敌方英雄的记录
	@JsonProperty("5")
	private PeakRecordMagicInfo enemyMagic; // 敌方法宝的记录
	
	public int getTeamId() {
		return teamId;
	}

	public void setTeamId(int pTeamId) {
		this.teamId = pTeamId;
	}
	
	public List<PeakRecordHeroInfo> getMyCamp() {
		return myCamp;
	}
	
	public void setMyCamp(List<PeakRecordHeroInfo> myCamp) {
		this.myCamp = myCamp;
	}
	
	public PeakRecordMagicInfo getMyMagic() {
		return myMagic;
	}
	
	public void setMyMagic(PeakRecordMagicInfo myMagic) {
		this.myMagic = myMagic;
	}
	
	public List<PeakRecordHeroInfo> getEnemyCamp() {
		return enemyCamp;
	}
	
	public void setEnemyCamp(List<PeakRecordHeroInfo> enemyCamp) {
		this.enemyCamp = enemyCamp;
	}
	
	public PeakRecordMagicInfo getEnemyMagic() {
		return enemyMagic;
	}
	
	public void setEnemyMagic(PeakRecordMagicInfo enemyMagic) {
		this.enemyMagic = enemyMagic;
	}
}
