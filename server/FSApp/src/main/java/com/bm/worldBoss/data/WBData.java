package com.bm.worldBoss.data;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.army.CurAttrData;
import com.playerdata.dataSyn.annotation.SynClass;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "wbdata")
@SynClass
public class WBData {

	@Id
	private String id;
	private String wbcfgId;	
	
	private long startTime;
	private long endTime;
	
	private CurAttrData curAttrData;
	//版本号，通过这个版本来判断是不是同一个boss，新boss会加1
	private int version = 0;
	//最后一击信息
	private LastFightInfo lastFightInfo;
	
	public static WBData newInstance(String idP){
		WBData data = new WBData();
		data.id = idP;
		return data;
	}
	
	public WBData newInstance(){
		WBData data = new WBData();
		data.id = this.id;
		data.version = this.version+1;
		return data;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getWbcfgId() {
		return wbcfgId;
	}

	public void setWbcfgId(String wbcfgId) {
		this.wbcfgId = wbcfgId;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public CurAttrData getCurAttrData() {
		return curAttrData;
	}

	public void setCurAttrData(CurAttrData curAttrData) {
		this.curAttrData = curAttrData;
	}

	public LastFightInfo getLastFightInfo() {
		return lastFightInfo;
	}

	public void setLastFightInfo(LastFightInfo lastFightInfo) {
		this.lastFightInfo = lastFightInfo;
	}
	
	

	
	
}
