package com.rw.handler.sign;

import java.util.List;

public class SignDataHolder {
	private List<String> signDataList;
	private int year;
	private int month;
	private int reSignCount;
	private boolean isSignable;
	
	private List<SignData> signData;
	public List<String> getSignDataList() {
		return signDataList;
	}
	public void setSignDataList(List<String> signDataList) {
		this.signDataList = signDataList;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getReSignCount() {
		return reSignCount;
	}
	public void setReSignCount(int reSignCount) {
		this.reSignCount = reSignCount;
	}
	public List<SignData> getSignData() {
		return signData;
	}
	public void setSignData(List<SignData> signData) {
		this.signData = signData;
	}
	public boolean isSignable() {
		return isSignable;
	}
	public void setSignable(boolean isSignable) {
		this.isSignable = isSignable;
	}
}
