package com.rwbase.dao.fashion;

/**
 * 潜规则：wingId,suitId,petId,specialPlanId用 -1 表示空！
 */
public interface FashionUsedIF {
	public String getUserId();
	public int getWingId();
	public int getSuitId();
	public int getPetId();
	public int getTotalEffectPlanId();
}
