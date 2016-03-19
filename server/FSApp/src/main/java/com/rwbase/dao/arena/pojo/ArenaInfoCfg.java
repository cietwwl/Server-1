package com.rwbase.dao.arena.pojo;

public class ArenaInfoCfg {

	private int id;
	private String copyId;
    private int copyType;
    private String name;
    private int count;
    private int openLv;
    private int cost;
    private int cdTime;
    private int maxScore;
    private String describe;
    
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCopyId() {
		return copyId;
	}
	public void setCopyId(String copyId) {
		this.copyId = copyId;
	}
	public int getCopyType() {
		return copyType;
	}
	public void setCopyType(int copyType) {
		this.copyType = copyType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getOpenLv() {
		return openLv;
	}
	public void setOpenLv(int openLv) {
		this.openLv = openLv;
	}
	public int getCost() {
		return cost;
	}
	public void setCost(int cost) {
		this.cost = cost;
	}
	public int getCdTime() {
		return cdTime;
	}
	public void setCdTime(int cdTime) {
		this.cdTime = cdTime;
	}
	public int getMaxScore() {
		return maxScore;
	}
	public void setMaxScore(int maxScore) {
		this.maxScore = maxScore;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	
}
