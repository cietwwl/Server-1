package com.playerdata.mgcsecret.cfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rwbase.dao.copy.pojo.ItemInfo;

public class MagicChapterCfg {
	private int chapterId; //章节id
	private int dungeonCount; //空间层数
	private int levelLimit; //开启等级
	private String data; //层数id
	private String passBonus; //通关奖励
	private List<ItemInfo> list_passBonus;

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
	
	public List<ItemInfo> getPassBonus(){
		return list_passBonus;
	}

	public void ExtraInitAfterLoad() {
		List<ItemInfo> tmpList = new ArrayList<ItemInfo>();
		String[] rewardItemStr = passBonus.split(",");
		for(String rewardItem : rewardItemStr){
			String[] itemStrArr = rewardItem.split("_");
			if(itemStrArr.length == 2){
				ItemInfo item = new ItemInfo();
				item.setItemID(Integer.parseInt(itemStrArr[0]));
				item.setItemNum(Integer.parseInt(itemStrArr[1]));
				tmpList.add(item);
			}
		}
		this.list_passBonus = Collections.unmodifiableList(tmpList);
	}
}
