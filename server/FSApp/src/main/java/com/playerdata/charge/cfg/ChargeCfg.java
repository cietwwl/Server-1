package com.playerdata.charge.cfg;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.fsutil.common.EnumIndex;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChargeCfg {
	
	private String id;
	private int slot;//位置
	private String icon;//图标	
	private String title;//标题	
	private String desc;//描述	
	private int recommend;//是否推荐	
	private int goldCount;//充值钻石数量	
	private int vipExp;//充值增加vip经验数
	private int extraGive;//额外赠送钻石	
	private int moneyCount;//充值金额(单位：分)
	private int moneyYuan; // 充值金额（单位：元）
	private int extraGiftId;//额外赠送礼包
	@EnumIndex
	private ChargeTypeEnum chargeType;//充值类型...
	private int giveCount;//单个商品 限购的次数
	
	public String getId() {
		return id;
	}
	public int getGiveCount() {
		return giveCount;
	}
	public void setGiveCount(int giveCount) {
		this.giveCount = giveCount;
	}
	public void setId(String id) {
		this.id = id;
	}
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
	/**
	 * 
	 * 获取充值的money，单位：分
	 * 
	 * @return
	 */
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
	public int getExtraGiftId() {
		return extraGiftId;
	}
	public void setExtraGiftId(int extraGiftId) {
		this.extraGiftId = extraGiftId;
	}
	public ChargeTypeEnum getChargeType() {
		return chargeType;
	}
	public void setChargeType(ChargeTypeEnum chargeType) {
		this.chargeType = chargeType;
	}
	public int getVipExp() {
		return vipExp;
	}
	public void setVipExp(int vipExp) {
		this.vipExp = vipExp;
	}
	/**
	 * 获取充值金额（单位：元）
	 * @return
	 */
	public int getMoneyYuan() {
		return moneyYuan;
	}
	public void setMoneyYuan(int moneyYuan) {
		this.moneyYuan = moneyYuan;
	}

	
}
