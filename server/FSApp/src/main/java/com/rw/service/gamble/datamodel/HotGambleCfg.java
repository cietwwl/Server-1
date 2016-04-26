package com.rw.service.gamble.datamodel;

import com.common.BaseConfig;

public class HotGambleCfg extends BaseConfig
{
  private int key; //关键字段
  private int month; //月份
  private int day; //日期
  private int groupId; //热点道具组
  private String heroModelId; //热点英雄

  public int getKey() {
    return key;
  }
  public int getMonth() {
    return month;
  }
  public int getDay() {
    return day;
  }
  public int getGroupId() {
    return groupId;
  }
  public String getHeroModelId() {
    return heroModelId;
  }
}