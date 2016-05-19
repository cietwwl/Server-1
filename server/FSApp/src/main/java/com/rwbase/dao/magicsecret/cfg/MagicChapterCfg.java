package com.rwbase.dao.magicsecret.cfg;


public class MagicChapterCfg
{
	private int chapterId; 
	private int dungeonCount; 
	private int levelLimit; 
	private String data; 

	public int getChapterId() {
		return chapterId;
	}
	
	public int getDungeonCount() {
		return dungeonCount;
	}
	
	public int getLevelLimit() {
		return levelLimit;
	}
	
	public String getData() {
		return data;
	}

	public void setChapterId(int chapterId) {
		this.chapterId = chapterId;
	}

	public void setDungeonCount(int dungeonCount) {
		this.dungeonCount = dungeonCount;
	}

	public void setLevelLimit(int levelLimit) {
		this.levelLimit = levelLimit;
	}

	public void setData(String data) {
		this.data = data;
	}

}