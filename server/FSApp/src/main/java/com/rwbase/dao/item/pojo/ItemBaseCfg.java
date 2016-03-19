package com.rwbase.dao.item.pojo;

public class ItemBaseCfg {
	private int id;        //物品ID
	private String name;   //名字
	private int quality;   //品质
	private int stackNum;  //最大堆叠数量
    private String icon;   //图标名
    private String atlas;  //图集名
    private String description; //描述
    private int sellPrice;		//出售价格
    private int enchantExp ;//附灵经验
    private int consumeType;//道具类型
    private int cost;
    
    public int getQuality() {
		return quality;
	}
	public void setQuality(int quality) {
		this.quality = quality;
	}
    public int getSellPrice() {
		return sellPrice;
	}
	public void setSellPrice(int sellPrice) {
		this.sellPrice = sellPrice;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getStackNum() {
		return stackNum;
	}
	public void setStackNum(int stackNum) {
		this.stackNum = stackNum;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getAtlas() {
		return atlas;
	}
	public void setAtlas(String atlas) {
		this.atlas = atlas;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getEnchantExp() {
		return enchantExp;
	}
	public void setEnchantExp(int enchantExp) {
		this.enchantExp = enchantExp;
	}
	public int getConsumeType() {
		return consumeType;
	}
	public void setConsumeType(int consumeType) {
		this.consumeType = consumeType;
	}
	public int getCost() {
		return cost;
	}
	public void setCost(int cost) {
		this.cost = cost;
	}
	
}
