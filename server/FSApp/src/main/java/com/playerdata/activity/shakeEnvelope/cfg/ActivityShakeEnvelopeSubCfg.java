package com.playerdata.activity.shakeEnvelope.cfg;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.common.BaseConfig;
import com.rwbase.dao.copy.pojo.ItemInfo;

public class ActivityShakeEnvelopeSubCfg extends BaseConfig{
	
	private int id; //阶段ID
	private int rankEnd; //排名start
	private String reward; //奖励
	private List<ItemInfo> list_reward;
	private int emailId; //对应邮件ID
	
	public int getId() {
		return id;
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

	public void ExtraInitAfterLoad() {
		List<ItemInfo> tmpList = new ArrayList<ItemInfo>();
		String[] rewardItemStr = reward.split(",");
		for(String rewardItem : rewardItemStr){
			String[] itemStrArr = rewardItem.split("~");
			if(itemStrArr.length == 2){
				ItemInfo item = new ItemInfo();
				item.setItemID(Integer.parseInt(itemStrArr[0]));
				item.setItemNum(Integer.parseInt(itemStrArr[1]));
				tmpList.add(item);
			}
		}
		this.list_reward = Collections.unmodifiableList(tmpList);
	}
}
