package com.rwbase.dao.fashion;

public class FashionCommonCfg
{
  private  int id; //时装id
  private  String attrName1; //属性名
  private  int attrValue1; //属性值
  private String attrValueType1;
  private  AttrValueType attrValueType1Field; //属性值的类型
  private  String attrName2; //属性名
  private  int attrValue2; //属性值
  private String attrValueType2;
  private  AttrValueType attrValueType2Field; //属性值的类型
  private  String attrName3; //属性名
  private  int attrValue3; //属性值
  private String attrValueType3;
  private  AttrValueType attrValueType3Field; //属性值的类型
  private  String attrName4; //属性名
  private  int attrValue4; //属性值
  private String attrValueType4;
  private  AttrValueType attrValueType4Field; //属性值的类型
  private  String attrName5; //属性名
  private  int attrValue5; //属性值
  private String attrValueType5;
  private  AttrValueType attrValueType5Field; //属性值的类型

  public void ExtraInit(){
	  attrValueType1Field = AttrValueType.valueOf(attrValueType1);
	  attrValueType2Field = AttrValueType.valueOf(attrValueType2);
	  attrValueType3Field = AttrValueType.valueOf(attrValueType3);
	  attrValueType4Field = AttrValueType.valueOf(attrValueType4);
	  attrValueType5Field = AttrValueType.valueOf(attrValueType5);
  }
  
  public int getId() {
    return id;
  }
  public String getAttrName1() {
    return attrName1;
  }
  public int getAttrValue1() {
    return attrValue1;
  }
  public AttrValueType getAttrValueType1() {
    return attrValueType1Field;
  }
  public String getAttrName2() {
    return attrName2;
  }
  public int getAttrValue2() {
    return attrValue2;
  }
  public AttrValueType getAttrValueType2() {
    return attrValueType2Field;
  }
  public String getAttrName3() {
    return attrName3;
  }
  public int getAttrValue3() {
    return attrValue3;
  }
  public AttrValueType getAttrValueType3() {
    return attrValueType3Field;
  }
  public String getAttrName4() {
    return attrName4;
  }
  public int getAttrValue4() {
    return attrValue4;
  }
  public AttrValueType getAttrValueType4() {
    return attrValueType4Field;
  }
  public String getAttrName5() {
    return attrName5;
  }
  public int getAttrValue5() {
    return attrValue5;
  }
  public AttrValueType getAttrValueType5() {
    return attrValueType5Field;
  }
}