package com.playerdata.groupFightOnline.cfg;
import com.common.BaseConfig;

public class GFightBiddingCfg extends BaseConfig {
  private int key; //关键字段
  private int typeId; //竞标种类ID
  private int rate; //奖励倍率
  private int vip; //VIP限制
  private String cost; //花费
  private String biddingReward; //压标奖励
  private String victoryReward; //被压标奖励
  private int victoryMaxRate; //被压上奖限倍率
  private int emailId; //压标成功邮件ID
  private int failEmailID; //压标失败对应邮件ID

  public int getKey() {
    return key;
  }
  public int getTypeId() {
    return typeId;
  }
  public int getRate() {
    return rate;
  }
  public int getVip() {
    return vip;
  }
  public String getCost() {
    return cost;
  }
  public String getBiddingReward() {
    return biddingReward;
  }
  public String getVictoryReward() {
    return victoryReward;
  }
  public int getVictoryMaxRate() {
    return victoryMaxRate;
  }
  public int getEmailId() {
    return emailId;
  }
  public int getFailEmailID() {
    return failEmailID;
  }

}
