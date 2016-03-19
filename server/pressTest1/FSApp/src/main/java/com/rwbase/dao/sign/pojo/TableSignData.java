package com.rwbase.dao.sign.pojo;
import java.util.Calendar;
import java.util.TreeMap;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "mt_sign_data")
public class TableSignData 
{
	@Id
	private String userId; 						   // 用户ID
	private int currentResignCount;
	private Calendar lastUpate;					   //获取上一次刷新数据的具体时间...
	private TreeMap<String, SignData> signDataMap; //<2015_2_14, SignData>数据结构
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getCurrentResignCount() {
		return currentResignCount;
	}
	public void setCurrentResignCount(int currentResignCount) {
		this.currentResignCount = currentResignCount;
	}
	public Calendar getLastUpate() {
		return lastUpate;
	}
	public void setLastUpate(Calendar lastUpate) {
		this.lastUpate = lastUpate;
	}
	public TreeMap<String, SignData> getSignDataMap() {
		return signDataMap;
	}
	public void setSignDataMap(TreeMap<String, SignData> signList) {
		this.signDataMap = signList;
	}
	
}
