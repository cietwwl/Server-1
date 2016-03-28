package com.rwbase.dao.fashion;

import com.rw.fsutil.cacheDao.mapItem.IMapItem;

/**
 * 潜规则：wingId,suitId,petId,specialPlanId用 -1 表示空！
 */
public class FashionBeingUsed implements IMapItem, FashionUsedIF{
	private String userId;
	private int wingId;
	private int suitId;
	private int petId;
	private int specialPlanId;
	
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
	public int getSpecialPlanId() {
		return specialPlanId;
	}
	public void setSpecialPlanId(int specialPlanId) {
		this.specialPlanId = specialPlanId;
	}
	
	@Override
	public String getId() {
		return userId;
	}
}
