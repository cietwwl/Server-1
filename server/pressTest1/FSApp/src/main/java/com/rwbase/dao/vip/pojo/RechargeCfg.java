package com.rwbase.dao.vip.pojo;

public class RechargeCfg {

	private int slot;//位置
	private String icon;//图标	
	private String title;//标题	
	private String desc;//描述	
	private int recommend;//是否推荐	
	private int goldCount;//充值钻石数量	
	private int moneyCount;//充值金额	
	private int extraGive;//额外赠送钻石	
	private int giveCount;//赠送次数	
	private int daysDraw;//每天领取
	public int getSlot() {
		return slot;
	}
	public void setSlot(int slot) {
		this.slot = slot;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public int getRecommend() {
		return recommend;
	}
	public void setRecommend(int recommend) {
		this.recommend = recommend;
	}
	public int getGoldCount() {
		return goldCount;
	}
	public void setGoldCount(int goldCount) {
		this.goldCount = goldCount;
	}
	public int getMoneyCount() {
		return moneyCount;
	}
	public void setMoneyCount(int moneyCount) {
		this.moneyCount = moneyCount;
	}
	public int getExtraGive() {
		return extraGive;
	}
	public void setExtraGive(int extraGive) {
		this.extraGive = extraGive;
	}
	public int getGiveCount() {
		return giveCount;
	}
	public void setGiveCount(int giveCount) {
		this.giveCount = giveCount;
	}
	public int getDaysDraw() {
		return daysDraw;
	}
	public void setDaysDraw(int daysDraw) {
		this.daysDraw = daysDraw;
	}


}
