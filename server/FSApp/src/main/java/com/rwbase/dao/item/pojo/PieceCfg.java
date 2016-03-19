package com.rwbase.dao.item.pojo;

public class PieceCfg extends ItemBaseCfg
{
	private int enchantExp;
	private int composeItemID;       //合成装备ID...
	private int composeNeedNum;      //合成需要碎片数量...
    private int composeCostCoin;     //合成花费...
    public int getComposeItemID() {
		return composeItemID;
	}
	public void setComposeItemID(int composeItemID) {
		this.composeItemID = composeItemID;
	}
	public int getComposeNeedNum() {
		return composeNeedNum;
	}
	public void setComposeNeedNum(int composeNeedNum) {
		this.composeNeedNum = composeNeedNum;
	}
	public int getComposeCostCoin() {
		return composeCostCoin;
	}
	public void setComposeCostCoin(int composeCostCoin) {
		this.composeCostCoin = composeCostCoin;
	}

	public int getEnchantExp() {
		return enchantExp;
	}
	public void setEnchantExp(int enchantExp) {
		this.enchantExp = enchantExp;
	}
}
