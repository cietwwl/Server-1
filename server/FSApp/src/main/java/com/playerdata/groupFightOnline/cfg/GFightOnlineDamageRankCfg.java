package com.playerdata.groupFightOnline.cfg;
import com.common.BaseConfig;

public class GFightOnlineDamageRankCfg extends BaseConfig {
  private int key; //关键字段
  private int stageId; //阶段ID
  private int rankEnd; //排名end
  private String reward; //奖励
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
  public int getEmailId() {
    return emailId;
  }

}