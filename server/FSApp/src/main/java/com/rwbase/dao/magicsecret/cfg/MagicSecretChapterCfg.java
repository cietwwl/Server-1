package com.rwbase.dao.magicsecret.cfg;

import com.common.ListParser;

public class MagicSecretChapterCfg {
	String key;
	String remarks;
	int chapterId;
	int dungeonCount;
	int levelLimit;
	String data;
	
	int[] dungeonIDArr;
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getRemarks() {
		return remarks;
	}
	
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	public int getChapterId() {
		return chapterId;
	}
	
	public void setChapterId(int chapterId) {
		this.chapterId = chapterId;
	}
	
	public int getDungeonCount() {
		return dungeonCount;
	}
	
	public void setDungeonCount(int dungeonCount) {
		this.dungeonCount = dungeonCount;
	}
	
	public int getLevelLimit() {
		return levelLimit;
	}
	
	public void setLevelLimit(int levelLimit) {
		this.levelLimit = levelLimit;
	}
	
	public String getData() {
		return data;
	}
	
	public void setData(String data) {
		this.data = data;
		setDungeonIDArr();
	}
	
	private void setDungeonIDArr(){
		this.dungeonIDArr = ListParser.ParseIntList(data, ";", "Magic Secret Chapter", key, "不能转化成整数数组");
	}
}
