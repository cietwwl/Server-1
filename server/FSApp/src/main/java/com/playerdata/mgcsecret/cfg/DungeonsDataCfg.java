package com.playerdata.mgcsecret.cfg;


public class DungeonsDataCfg
{
	private String key;
	private int id;
	private int levelId; 
	private int fabaoBuff; 
	private String buffBonus; 
	private String coBox; 
	private String hiBox; 
	private String enimy; 
	private int score; 
	private int starReward; 
	private String drop; 

	public String getKey() {
		return key;
	}
	  
	public int getId() {
		return id;
	}
		  
	public int getLevelId() {
		return levelId;
	}
	
	public int getFabaoBuff() {
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

	public void setKey(String key) {
		this.key = key;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLevelId(int levelId) {
		this.levelId = levelId;
	}

	public void setFabaoBuff(int fabaoBuff) {
		this.fabaoBuff = fabaoBuff;
	}

	public void setBuffBonus(String buffBonus) {
		this.buffBonus = buffBonus;
	}

	public void setCoBox(String coBox) {
		this.coBox = coBox;
	}

	public void setHiBox(String hiBox) {
		this.hiBox = hiBox;
	}

	public void setEnimy(String enimy) {
		this.enimy = enimy;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public void setStarReward(int starReward) {
		this.starReward = starReward;
	}

	public void setDrop(String drop) {
		this.drop = drop;
	}
}