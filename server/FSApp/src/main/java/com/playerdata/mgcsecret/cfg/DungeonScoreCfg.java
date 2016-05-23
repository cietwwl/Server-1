package com.playerdata.mgcsecret.cfg;
import java.util.ArrayList;

import com.common.BaseConfig;
import com.rwbase.dao.copy.pojo.ItemInfo;

public class DungeonScoreCfg extends BaseConfig {
	private int key; //关键字段
	private int score; //积分
	private String reward; //奖励
	private ArrayList<ItemInfo> list_reward = new ArrayList<ItemInfo>();

	public int getKey() {
		return key;
	}
	
	public int getScore() {
		return score;
	}
	
	public String getReward() {
		return reward;
	}

	public ArrayList<ItemInfo> getRewardList(){
		return list_reward;
	}
	
	@Override
	public void ExtraInitAfterLoad(){
		list_reward.clear();
		String[] rewardStrArr = reward.split(",");
		for(String rewardStr : rewardStrArr){
			ItemInfo item = new ItemInfo();
			String[] itemStr = rewardStr.split("_");
			if(itemStr.length == 2){
				item.setItemID(Integer.parseInt(itemStr[0]));
				item.setItemNum(Integer.parseInt(itemStr[1]));
				list_reward.add(item);
			}
		}
	}
}
