package com.playerdata.teambattle.cfg;
import com.common.BaseConfig;

public class MonsterCombinationCfg extends BaseConfig {
	private String id; //id
	private int battleTime; //战斗场次
	private String monsterTeam1; //怪物队伍1
	private String monsterTeam2; //怪物队伍2
	private String monsterTeam3; //怪物队伍3
	private int scoreGain; //获胜可获得积分
	private String mailReward; //邮件奖励
	private int mail; //邮件id

 	public String getId() {
 		return id;
 	}
 	
 	public int getBattleTime() {
 		return battleTime;
 	}
 	
 	public String getMonsterTeam1() {
 		return monsterTeam1;
 	}
 	
 	public String getMonsterTeam2() {
 		return monsterTeam2;
 	}
 	
 	public String getMonsterTeam3() {
 		return monsterTeam3;
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
