package com.playerdata.activity.growthFund.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.activityCommon.activityType.ActivityTypeItemIF;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.dao.annotation.CombineSave;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityGrowthFundItem implements ActivityTypeItemIF<ActivityGrowthFundSubItem> {

	@Id
	private Integer id;		//cfgId_userId
	
	private String userId;	//对应的角色Id
	
	@CombineSave
	private String cfgId;	//活动的id
	
	@CombineSave
	private boolean closed = false;
	
	@CombineSave
	private List<ActivityGrowthFundSubItem> subItemList = new ArrayList<ActivityGrowthFundSubItem>();
	
	@CombineSave
	private boolean hasViewed;	//是否已经查看过该活动
	
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
		this.subItemList = (List<ActivityGrowthFundSubItem>) subItemList;
	}

	public boolean isHasViewed() {
		return hasViewed;
	}

	public void setHasViewed(boolean hasViewed) {
		this.hasViewed = hasViewed;
	}
	
	@Override
	public synchronized void reset(){
		// 重置需要做的事情
	}

	@Override
	public void setVersion(int version) {
		// Do Nothing
	}
}
