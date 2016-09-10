package com.playerdata.activity.dailyCharge.cfg;
import com.common.BaseConfig;

public class ActivityDailyChargeSubCfg extends BaseConfig {
  private int id; //活动id
  private int type; //父id
  private String emailTitle; //标题
  private int goToType; //活动跳转类型
  private int count; //领取条件
  private String giftId; //奖励礼包
  private String title; //活动子标题
  private String day; //激活时间

  public int getId() {
    return id;
  }
  public int getType() {
    return type;
  }
  public String getEmailTitle() {
    return emailTitle;
  }
  public int getGoToType() {
    return goToType;
  }
  public int getCount() {
    return count;
  }
  public String getGiftId() {
    return giftId;
  }
  public String getTitle() {
    return title;
  }
  public String getDay() {
    return day;
  }

}
