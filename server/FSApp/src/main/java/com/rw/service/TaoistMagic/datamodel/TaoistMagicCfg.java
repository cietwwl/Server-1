package com.rw.service.TaoistMagic.datamodel;
import com.common.BaseConfig;

public class TaoistMagicCfg extends BaseConfig {
  private int key; //key
  private int tagNum;//分页
  private int openLevel; //分页开放等级
  private String attribute; //属性类型
  private String formulaParam; //属性计算公式参数
  private com.rwbase.dao.fashion.AttrValueType attrValueType; //属性值的类型
  private int consumeId; //技能消耗ID

  public int getKey() {
    return key;
  }
  public int getTagNum() {
    return tagNum;
  }
  public int getOpenLevel() {
    return openLevel;
  }
  public String getAttribute() {
    return attribute;
  }
  public String getFormulaParam() {
    return formulaParam;
  }
  public com.rwbase.dao.fashion.AttrValueType getAttrValueType() {
    return attrValueType;
  }
  public int getConsumeId() {
    return consumeId;
  }

}
