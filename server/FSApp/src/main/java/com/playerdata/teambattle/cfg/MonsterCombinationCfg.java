package com.playerdata.teambattle.cfg;
import java.util.List;

import com.common.BaseConfig;
import com.playerdata.groupFightOnline.bm.GFightHelper;
import com.rwbase.dao.copy.pojo.ItemInfo;

public class MonsterCombinationCfg extends BaseConfig {
	private String id; //id
	private String name;	//怪物名字
	private int battleTime; //战斗场次
	private int copyId; //copyId
	private int scoreGain; //获胜可获得积分
	private String reward;	//完成的奖励
	private List<ItemInfo> list_reward;
	private String mailReward; //邮件奖励
	private int mail; //邮件id

 	public String getId() {
 		return id;
 	}

 	public String getName() {
		return name;
	}

	public int getBattleTime() {
 		return battleTime;
 	}

 	public int getCopyId() {
		return copyId;
	}

	public int getScoreGain() {
 		return scoreGain;
 	}
 	
	public List<ItemInfo> getReward(){
		return list_reward;
	}
	
 	public String getMailReward() {
 		return mailReward;
 	}
 	
 	public int getMail() {
 		return mail;
 	}
 	
 	@Override
 	public void ExtraInitAfterLoad() {
 		this.list_reward = GFightHelper.stringToItemList(reward, "_");
 	}
}
