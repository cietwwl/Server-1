package com.rw.service.PeakArena.datamodel;
import com.common.BaseConfig;

public class peakArenaCost extends BaseConfig {
  private String key; //关键字段
  private String time; //次数
  private int resetCost; //重置花费
  private int buyTimeCost; //购买花费
  private com.rwbase.common.enu.eSpecialItemId coinType; //货币类型

  public String getKey() {
    return key;
  }
  public String getTime() {
    return time;
  }
  public int getResetCost() {
    return resetCost;
  }
  public int getBuyTimeCost() {
    return buyTimeCost;
  }
  public com.rwbase.common.enu.eSpecialItemId getCoinType() {
    return coinType;
  }

}
