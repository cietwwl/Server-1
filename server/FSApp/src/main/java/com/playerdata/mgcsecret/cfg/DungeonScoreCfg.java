package com.playerdata.mgcsecret.cfg;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rwbase.dao.copy.pojo.ItemInfo;

public class DungeonScoreCfg {
	private int key; //关键字段
	private int score; //积分
	private String reward; //奖励
	private List<ItemInfo> list_reward; //经过解析的奖励字段

	public int getKey() {
		return key;
	}
	
	public int getScore() {
		return score;
	}
	
	public String getReward() {
		return reward;
	}

	public List<ItemInfo> getRewardList(){
		return list_reward;
	}
	
	public void ExtraInitAfterLoad(){
		List<ItemInfo> tmpList = new ArrayList<ItemInfo>();
		String[] rewardStrArr = reward.split(",");
		for(String rewardStr : rewardStrArr){
			ItemInfo item = new ItemInfo();
			String[] itemStr = rewardStr.split("_");
			if(itemStr.length == 2){
				item.setItemID(Integer.parseInt(itemStr[0]));
				item.setItemNum(Integer.parseInt(itemStr[1]));
				tmpList.add(item);
			}
		}
		list_reward = Collections.unmodifiableList(tmpList);
	}
}
