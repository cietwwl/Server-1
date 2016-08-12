package com.rwbase.dao.store.pojo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.dao.annotation.NonSave;
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
	private boolean isFirstFreeLottery = true; // 是否第一次免费抽箱
	private boolean isFirstPayLottery = true;	//是否第一次付费抽箱
	private long lastDrawTime;   		//上一次抽箱时间    	
	private int drawTime;				//免费抽箱的次数
	private int recordGuaranteeTime;    //记录保底次数
	@NonSave
	private boolean refresh;            //是否刷新状态
	
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
	public boolean isFirstFreeLottery() {
		return isFirstFreeLottery;
	}
	public void setFirstFreeLottery(boolean isFirstFreeLottery) {
		this.isFirstFreeLottery = isFirstFreeLottery;
	}
	public boolean isFirstPayLottery() {
		return isFirstPayLottery;
	}
	public void setFirstPayLottery(boolean isFirstPayLottery) {
		this.isFirstPayLottery = isFirstPayLottery;
	}
	public long getLastDrawTime() {
		return lastDrawTime;
	}
	public void setLastDrawTime(long lastDrawTime) {
		this.lastDrawTime = lastDrawTime;
	}
	public int getDrawTime() {
		return drawTime;
	}
	public void setDrawTime(int drawTime) {
		this.drawTime = drawTime;
	}
	public int getRecordGuaranteeTime() {
		return recordGuaranteeTime;
	}
	public void setRecordGuaranteeTime(int recordGuaranteeTime) {
		this.recordGuaranteeTime = recordGuaranteeTime;
	}
	public boolean isRefresh() {
		return refresh;
	}
	public void setRefresh(boolean refresh) {
		this.refresh = refresh;
	}
}
