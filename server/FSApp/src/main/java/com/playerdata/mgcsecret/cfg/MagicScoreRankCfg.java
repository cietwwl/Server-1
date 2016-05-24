package com.playerdata.mgcsecret.cfg;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.common.BaseConfig;
import com.rwbase.dao.copy.pojo.ItemInfo;

public class MagicScoreRankCfg extends BaseConfig {
	private int key; //关键字段
	private int stageId; //阶段ID
	private int rankEnd; //排名start
	private String reward; //奖励
	private List<ItemInfo> list_reward = new ArrayList<ItemInfo>();

	public int getKey() {
		return key;
	}
	
	public int getStageId() {
		return stageId;
	}
	
	public int getRankEnd() {
		return rankEnd;
	}
	
	public String getReward() {
		return reward;
	}
	
	public List<ItemInfo> getRewardList(){
		return Collections.unmodifiableList(list_reward);
	}

	@Override
	public void ExtraInitAfterLoad() {
		String[] rewardItemStr = reward.split(",");
		for(String rewardItem : rewardItemStr){
			String[] itemStrArr = rewardItem.split("_");
			if(itemStrArr.length == 2){
				ItemInfo item = new ItemInfo();
				item.setItemID(Integer.parseInt(itemStrArr[0]));
				item.setItemNum(Integer.parseInt(itemStrArr[1]));
				this.list_reward.add(item);
			}
		}
	}
}
