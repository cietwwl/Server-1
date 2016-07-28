package com.playerdata.teambattle.cfg;
import com.common.BaseConfig;

public class MonsterCombinationCfg extends BaseConfig {
	private String id; //id
	private int battleTime; //战斗场次
	private int copyId; //copyId
	private int scoreGain; //获胜可获得积分
	private String mailReward; //邮件奖励
	private int mail; //邮件id

 	public String getId() {
 		return id;
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
 	
 	public String getMailReward() {
 		return mailReward;
 	}
 	
 	public int getMail() {
 		return mail;
 	}
}
