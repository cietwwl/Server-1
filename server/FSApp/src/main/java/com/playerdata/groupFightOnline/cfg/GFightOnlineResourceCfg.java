package com.playerdata.groupFightOnline.cfg;
import com.common.BaseConfig;

public class GFightOnlineResourceCfg extends BaseConfig {
  private int key; //关键字段
  private int resID; //资源点ID
  private String biddingStartTime; //竞标开始时间
  private String prepareStartTime; //备战开始时间
  private String fightStartTime; //开战时间
  private String fightEndTime; //战斗结算时间
  private String biddingBaseCost; //竞标起始资源
  private String biddingAddCost; //加标最少资源
  private int biddingLevelLimit; //竞标团队最低等级
  private String ownerDailyReward; //资源产出
  private int emailId; //对应邮件ID

  public int getKey() {
    return key;
  }
  public int getResID() {
    return resID;
  }
  public String getBiddingStartTime() {
    return biddingStartTime;
  }
  public String getPrepareStartTime() {
    return prepareStartTime;
  }
  public String getFightStartTime() {
    return fightStartTime;
  }
  public String getFightEndTime() {
    return fightEndTime;
  }
  public String getBiddingBaseCost() {
    return biddingBaseCost;
  }
  public String getBiddingAddCost() {
    return biddingAddCost;
  }
  public int getBiddingLevelLimit() {
    return biddingLevelLimit;
  }
  public String getOwnerDailyReward() {
    return ownerDailyReward;
  }
  public int getEmailId() {
    return emailId;
  }

}
