package com.rwbase.dao.worship.pojo;


public class CfgWorshipReward {
	private String key;
	private int rewardType;
	private String rewardStr;

	private String round;
	
	private int upper;//上限
	
	private int lowwer;//下限
	
	public void format() throws RuntimeException{
		String[] str = round.split("~");
		if(str.length < 2){
			throw new RuntimeException("格式化膜拜配置表时发现配置["+key+"]的人数格式不正确:"+ round);
		}
		lowwer = Integer.parseInt(str[0].toString().trim());
		upper = Integer.parseInt(str[1].toString().trim());
	}
	
	
	public int getRewardType() {
		return rewardType;
	}
	public void setRewardType(int rewardType) {
		this.rewardType = rewardType;
	}
	public String getRewardStr() {
		return rewardStr;
	}
	public void setRewardStr(String rewardStr) {
		this.rewardStr = rewardStr;
	}
	public String getRound() {
		return round;
	}
	public int getUpper() {
		return upper;
	}
	public int getLowwer() {
		return lowwer;
	}

	
	
}
