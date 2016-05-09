package com.rwbase.dao.upgrade.pojo;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.fsutil.dao.annotation.CombineSave;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name="upgrade_data")
public class TableUpgradeData{
	@Id
	private String ownerId;
	
	@CombineSave
	private long achieveRewardTime;
	@CombineSave
	private String versionNo;
	
	public String getOwnerId() {
		return ownerId;
	}

	public long getAchieveRewardTime() {
		return achieveRewardTime;
	}

	public String getVersionNo() {
		return versionNo;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public void setAchieveRewardTime(long achieveRewardTime) {
		this.achieveRewardTime = achieveRewardTime;
	}

	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}
	
}
