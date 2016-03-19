package com.bm.rank.secret;

public class SecretInfoComp implements Comparable<SecretInfoComp>{
	private String secretId;  //秘籍唯一id
	 private long endTime;		//秘境结束时间 
	 private int battleForce;		//战斗力
	 private int sourceNum;//剩余总资源数量
	 public int compareTo(SecretInfoComp o){
		//战力低到高
		 if(getBattleForce()>o.getBattleForce()){
			 return 1;
		 }
		 if(getBattleForce()<o.getBattleForce()){
			 return -1;
		 }
		 
			//结束早的秘境排前面
		 if(getEndTime()<o.getEndTime()){
			 return -1;
		 }
		 
		 
		//资源高到低
		 if(getSourceNum()>o.getSourceNum()){
			 return -1;
		 }
		 if(getSourceNum()<getSourceNum()){ 
			 return 1;
		 }
		 
		 return 0;
	 }
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public int getBattleForce() {
		return battleForce;
	}
	public void setBattleForce(int battleForce) {
		this.battleForce = battleForce;
	}
	public int getSourceNum() {
		return sourceNum;
	}
	public void setSourceNum(int sourceNum) {
		this.sourceNum = sourceNum;
	}
	public String getSecretId() {
		return secretId;
	}
	public void setSecretId(String secretId) {
		this.secretId = secretId;
	}
}
