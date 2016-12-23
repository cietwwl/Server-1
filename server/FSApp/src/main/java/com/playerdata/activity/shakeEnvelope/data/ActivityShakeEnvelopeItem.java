package com.playerdata.activity.shakeEnvelope.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.activityCommon.activityType.ActivityTypeItemIF;
import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.dao.annotation.CombineSave;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityShakeEnvelopeItem implements ActivityTypeItemIF<ActivityShakeEnvelopeSubItem> {

	@Id
	private Integer id;
	
	private String userId;	//对应的角色Id
	
	@CombineSave
	private String cfgId;	//活动的id
	
	@CombineSave
	private int version;	//cfg的版本号
	
	@CombineSave
	private boolean hasViewed;	//是否已经查看过该活动
	
	@CombineSave
	private boolean hasReward;	//当前是否有可以领取的红包
	
	@IgnoreSynField
	private List<ActivityShakeEnvelopeSubItem> subItems = new ArrayList<ActivityShakeEnvelopeSubItem>();
	
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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public boolean isHasViewed() {
		return hasViewed;
	}

	public void setHasViewed(boolean hasViewed) {
		this.hasViewed = hasViewed;
	}

	public boolean isHasReward() {
		return hasReward;
	}

	public void setHasReward(boolean hasReward) {
		this.hasReward = hasReward;
	}

	@Override
	public void setSubItemList(List<ActivityShakeEnvelopeSubItem> subItemList) {
		subItems = subItemList;
	}

	@Override
	public List<ActivityShakeEnvelopeSubItem> getSubItemList() {
		return subItems;
	}

	@Override
	public void reset() {
		subItems = new ArrayList<ActivityShakeEnvelopeSubItem>();
	}
	
	public ActivityShakeEnvelopeSubItem getSubItemByStartTime(long startTime){
		for(ActivityShakeEnvelopeSubItem subItem : subItems){
			if(startTime == subItem.getStartTime()){
				return subItem;
			}
		}
		return null;
	}
}
