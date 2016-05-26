package com.rw.service.PeakArena.datamodel;
import com.common.BaseConfig;

public class peakArenaPrize extends BaseConfig {
  private String key; //关键字段
  private String range; //排名分段
  private int prizeCountPerHour; //每小时可以领取的奖励

  public String getKey() {
    return key;
  }
  public String getRange() {
    return range;
  }
  public int getPrizeCountPerHour() {
    return prizeCountPerHour;
  }

}
