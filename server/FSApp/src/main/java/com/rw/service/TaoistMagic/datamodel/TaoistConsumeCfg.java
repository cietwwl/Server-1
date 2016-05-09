package com.rw.service.TaoistMagic.datamodel;
import com.common.BaseConfig;

public class TaoistConsumeCfg extends BaseConfig {
  private int key; //key
  private int consumeId; //技能消耗ID
  private int skillLevel; //技能等级
  private com.rwbase.common.enu.eSpecialItemId coinType; //货币类型
  private int coinCount; //消耗
  private String criticalPlans; //暴击组合序列

  public int getKey() {
    return key;
  }
  public int getConsumeId() {
    return consumeId;
  }
  public int getSkillLevel() {
    return skillLevel;
  }
  public com.rwbase.common.enu.eSpecialItemId getCoinType() {
    return coinType;
  }
  public int getCoinCount() {
    return coinCount;
  }
  public String getCriticalPlans() {
    return criticalPlans;
  }

}
