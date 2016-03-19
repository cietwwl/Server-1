package com.rwbase.dao.item.pojo;

import com.rwbase.common.enu.eConsumeTypeDef;

public class ConsumeCfg extends ItemBaseCfg {
//	private eConsumeTypeDef consumeType;            //道具类别
	private int isUse;               //是否可使用
	private int magicForgeExp;		//法宝锻造提供经验
   
    private int value;               //数值
    private String itemspit;			//物品吐槽
    
    public String getItemspit() {
		return itemspit;
	}
	public void setItemspit(String itemspit) {
		this.itemspit = itemspit;
	}
//	public eConsumeTypeDef getConsumeType() {
//		return consumeType;
//	}
//	public void setConsumeType(eConsumeTypeDef consumeType) {
//		this.consumeType = consumeType;
//	}
	public int getIsUse() {
		return isUse;
	}
	public void setIsUse(int isUse) {
		this.isUse = isUse;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public int getMagicForgeExp() {
		return magicForgeExp;
	}
	public void setMagicForgeExp(int magicForgeExp) {
		this.magicForgeExp = magicForgeExp;
	}
}
