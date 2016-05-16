package com.rwbase.dao.fashion;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.fsutil.dao.annotation.NonSave;

/**
 * 潜规则：wingId,suitId,petId,specialPlanId用 -1 表示空！
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "fashion_being_using")
public class FashionBeingUsed implements FashionUsedIF {
	@Id
	private String userId;
	private int wingId = -1;
	private int suitId = -1;
	private int petId = -1;
	@NonSave
	private int totalEffectPlanId = -1;

	public int[] getUsingList() {
		int[] result = new int[3];
		result[0] = wingId;
		result[1] = suitId;
		result[2] = petId;
		return result;
	}

	// public IEffectCfg[] getEffectList(int validCount,int career){
	// IEffectCfg[] result = new IEffectCfg[4];
	// result[0]=FashionEffectCfgDao.getInstance().getConfig(wingId,career);
	// result[1]=FashionEffectCfgDao.getInstance().getConfig(suitId,career);
	// result[2]=FashionEffectCfgDao.getInstance().getConfig(petId,career);
	// result[3] = FashionQuantityEffectCfgDao.getInstance().searchOption(validCount);
	// return result;
	// }

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getWingId() {
		return wingId;
	}

	public void setWingId(int wingId) {
		this.wingId = wingId;
	}

	public int getSuitId() {
		return suitId;
	}

	public void setSuitId(int suitId) {
		this.suitId = suitId;
	}

	public int getPetId() {
		return petId;
	}

	public void setPetId(int petId) {
		this.petId = petId;
	}

	public int getTotalEffectPlanId() {
		return totalEffectPlanId;
	}

	public void setTotalEffectPlanId(int totalEffectPlanId) {
		this.totalEffectPlanId = totalEffectPlanId;
	}
}
