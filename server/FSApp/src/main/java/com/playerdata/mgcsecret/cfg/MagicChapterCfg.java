package com.playerdata.mgcsecret.cfg;
import com.common.BaseConfig;

public class MagicChapterCfg extends BaseConfig {
	private int chapterId; //章节id
	private int dungeonCount; //空间层数
	private int levelLimit; //开启等级
	private String data; //层数id

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
  
}
