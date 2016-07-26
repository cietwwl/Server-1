package com.rwbase.dao.store.pojo;

/**
 * 觉醒抽奖奖励池
 * @author lida
 *
 */
public class WakenLotteryRewardPoolCfg {
	private int ID;
	private int poolId;
	private int rewardItemId;
	private int count;
	private int weight;
	private int isGuarantee;
	private int isShow;
	
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public int getPoolId() {
		return poolId;
	}
	public void setPoolId(int poolId) {
		this.poolId = poolId;
	}
	public int getRewardItemId() {
		return rewardItemId;
	}
	public void setRewardItemId(int rewardItemId) {
		this.rewardItemId = rewardItemId;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public int getIsGuarantee() {
		return isGuarantee;
	}
	public void setIsGuarantee(int isGuarantee) {
		this.isGuarantee = isGuarantee;
	}
	public int getIsShow() {
		return isShow;
	}
	public void setIsShow(int isShow) {
		this.isShow = isShow;
	}
}
