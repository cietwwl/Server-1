package com.playerdata.groupFightOnline.cfg;
import java.util.List;

import com.common.BaseConfig;
import com.playerdata.groupFightOnline.bm.GFightHelper;
import com.rwbase.dao.copy.pojo.ItemInfo;

public class GFightOnlineDefeatRankCfg extends BaseConfig {
	private int key; //关键字段
	private int stageId; //阶段ID
	private int rankEnd; //排名end
	private String reward; //奖励
	private List<ItemInfo> list_reward;
	private int emailId; //对应邮件ID

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
		return list_reward;
	}
	
	public int getEmailId() {
		return emailId;
	}
  
  	@Override
	public void ExtraInitAfterLoad() {
		this.list_reward = GFightHelper.stringToItemList(reward);
	}
}
