package com.rwbase.dao.role.pojo;

public class EquipAttachCfg {

	private int id ;//ID 
	private int preId; //前一ID
	private int nextId;//下一ID
	private int quality;//品质
	private int needExp;//强化需要经验
	private int needCoin;//强化经验需要铜钱
	private int attriPercent;//属性增加比例,按百分点
	private int starLevel;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPreId(){
		return preId;
	}
	public void setPreId(int preId){
		this.preId = preId;
	}
	public int getNextId() {
		return nextId;
	}
	public void setNextId(int nextId) {
		this.nextId = nextId;
	}
	public int getQuality() {
		return quality;
	}
	public void setQuality(int quality) {
		this.quality = quality;
	}
	public int getNeedExp() {
		return needExp;
	}
	public void setNeedExp(int needExp) {
		this.needExp = needExp;
	}
	public int getNeedCoin() {
		return needCoin;
	}
	public void setNeedCoin(int needCoin) {
		this.needCoin = needCoin;
	}
	public int getAttriPercent() {
		return attriPercent;
	}
	public void setAttriPercent(int attriPercent) {
		this.attriPercent = attriPercent;
	}
	public int getStarLevel() {
		return starLevel;
	}
	public void setStarLevel(int starLevel) {
		this.starLevel = starLevel;
	}
}
