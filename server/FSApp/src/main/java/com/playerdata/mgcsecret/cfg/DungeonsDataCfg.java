package com.playerdata.mgcsecret.cfg;
import com.common.BaseConfig;

public class DungeonsDataCfg extends BaseConfig {
	private String key; //关键字段
	private int id; //空间id
	private int levelId; //难度id
	private String fabaoBuff; //怪物buff
	private String buffBonus; //buff
	private String coBox; //普通宝箱
	private String hiBox; //高级宝箱
	private String enimy; //敌人
	private int score; //积分
	private int starReward; //简单难度星星奖励
	private String drop; //掉落方案

	public String getKey() {
		return key;
	}
	
	public int getId() {
		return id;
	}
	
	public int getLevelId() {
		return levelId;
	}
	
	public String getFabaoBuff() {
		return fabaoBuff;
	}
	
	public String getBuffBonus() {
		return buffBonus;
	}
	
	public String getCoBox() {
		return coBox;
	}
	
	public String getHiBox() {
		return hiBox;
	}
	
	public String getEnimy() {
	  return enimy;
	}
	
	public int getScore() {
		return score;
	}
	
	public int getStarReward() {
		return starReward;
	}
	
	public String getDrop() {
		return drop;
	}
  
	public int getChapterID(){
		return id/100;
	}

}
