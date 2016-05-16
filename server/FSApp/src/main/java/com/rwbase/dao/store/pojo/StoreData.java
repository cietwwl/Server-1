package com.rwbase.dao.store.pojo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rwbase.common.enu.eStoreExistType;
import com.rwbase.common.enu.eStoreType;

@JsonIgnoreProperties(ignoreUnknown = true)
@SynClass
public class StoreData implements StoreDataIF{

	@Id
	private String id; // ownerId + type 装备唯一id
	private List<CommodityData> commodity = new ArrayList<CommodityData>();
	private int refreshNum;
	private int freeRefreshNum;
	private long lastRefreshTime;
	private eStoreExistType existType;
	private eStoreType type;
	private int version;
	
	public List<CommodityData> getCommodity() {
		return commodity;
	}
	public void setCommodity(List<CommodityData> commodity) {
		this.commodity = commodity;
	}
	public int getRefreshNum() {
		return refreshNum;
	}
	public void setRefreshNum(int refreshNum) {
		this.refreshNum = refreshNum;
	}
	public long getLastRefreshTime() {
		return lastRefreshTime;
	}
	public void setLastRefreshTime(long lastRefreshTime) {
		this.lastRefreshTime = lastRefreshTime;
	}
	public eStoreExistType getExistType() {
		return existType;
	}
	public void setExistType(eStoreExistType existType) {
		this.existType = existType;
	}
	public eStoreType getType() {
		return type;
	}
	public void setType(eStoreType type) {
		this.type = type;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getFreeRefreshNum() {
		return freeRefreshNum;
	}
	public void setFreeRefreshNum(int fressRefreshNum) {
		this.freeRefreshNum = fressRefreshNum;
	}
}
