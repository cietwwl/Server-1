package com.playerdata.team;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/*
 * @author HC
 * @date 2016年7月13日 下午7:02:21
 * @Description 
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class FashionInfo {
	private int wing;
	private int suit;
	private int pet;
	private int count;
	private int wCount;// 翅膀的数量
	private int sCount;// 套装数量
	private int pCount;// 宠物的数量

	public int getWing() {
		return wing;
	}

	public void setWing(int wing) {
		this.wing = wing;
	}

	public int getSuit() {
		return suit;
	}

	public void setSuit(int suit) {
		this.suit = suit;
	}

	public int getPet() {
		return pet;
	}

	public void setPet(int pet) {
		this.pet = pet;
	}

	public int getwCount() {
		return wCount;
	}

	public void setwCount(int wCount) {
		this.wCount = wCount;
	}

	public int getsCount() {
		return sCount;
	}

	public void setsCount(int sCount) {
		this.sCount = sCount;
	}

	public int getpCount() {
		return pCount;
	}

	public void setpCount(int pCount) {
		this.pCount = pCount;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}