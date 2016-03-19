package com.rwbase.dao.fresherActivity.pojo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rwbase.common.enu.eActivityType;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name="fresheractivity")
public class FresherActivityBigItem implements IMapItem{
	
	@Id
	private String id;
	private String ownerId;
	@CombineSave
	private eActivityType activityType;
	@CombineSave
	private List<FresherActivityItem> itemList = new ArrayList<FresherActivityItem>();
	
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return id;
	}
	public eActivityType getActivityType() {
		return activityType;
	}
	public void setActivityType(eActivityType activityType) {
		this.activityType = activityType;
	}
	public List<FresherActivityItem> getItemList() {
		return itemList;
	}
	public void setItemList(List<FresherActivityItem> itemList) {
		this.itemList = itemList;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
}
