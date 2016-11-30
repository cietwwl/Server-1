package com.playerdata.activity.growthFund.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.activity.growthFund.GrowthFundType;
import com.playerdata.activityCommon.activityType.ActivityTypeItemIF;
import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.dao.annotation.CombineSave;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityGrowthFundItem implements ActivityTypeItemIF<ActivityGrowthFundSubItem> {

	@Id
	private Integer id; // cfgId_userId

	private String userId; // 对应的角色Id

	@CombineSave
	private String cfgId; // 活动的id

	@CombineSave
	private boolean closed = false;

	@CombineSave
	private List<ActivityGrowthFundSubItem> subItemList = new ArrayList<ActivityGrowthFundSubItem>();

	@CombineSave
	private boolean hasViewed; // 是否已经查看过该活动

	@CombineSave
	private boolean bought; // 是否已经购买了成长基金的礼包
	
	private int boughtCount; // 已经购买的人数
	
	@JsonIgnore
	private boolean sorted; // 是否已经排过序
	
	@IgnoreSynField
	private GrowthFundType _growthFundType;
	
	@IgnoreSynField
	private int version;

	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCfgId() {
		return cfgId;
	}

	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public List<ActivityGrowthFundSubItem> getSubItemList() {
		return subItemList;
	}

	public void setSubItemList(List<ActivityGrowthFundSubItem> subItemList) {
		this.subItemList = subItemList;
	}

	public boolean isHasViewed() {
		return hasViewed;
	}

	public void setHasViewed(boolean hasViewed) {
		this.hasViewed = hasViewed;
	}

	@Override
	public synchronized void reset() {
		// 重置需要做的事情
	}

	@Override
	public void setVersion(int version) {
		this.version = version;
	}
	
	@Override
	public int getVersion() {
		return version;
	}

	public boolean isBought() {
		return bought;
	}

	public void setBought(boolean pBoughtValue) {
		this.bought = pBoughtValue;
	}

	@JsonIgnore
	public GrowthFundType getGrowthFundType() {
		return _growthFundType;
	}

	@JsonIgnore
	public void setGrowthFundType(GrowthFundType pGrowthFundType) {
		this._growthFundType = pGrowthFundType;
	}

	@JsonIgnore
	public int getBoughtCount() {
		return boughtCount;
	}

	@JsonIgnore
	public void setBoughtCount(int boughtCount) {
		this.boughtCount = boughtCount;
	}

	@JsonIgnore
	public boolean isSorted() {
		return sorted;
	}

	@JsonIgnore
	public void setSorted(boolean sorted) {
		this.sorted = sorted;
	}
}
