package com.rwbase.dao.fresherActivity.pojo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;
import com.rwbase.common.enu.eActivityType;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "fresheractivity")
public class FresherActivityBigItem implements RoleExtProperty {
	@Id
	private int id;
	private eActivityType activityType;
	private List<FresherActivityItem> itemList = new ArrayList<FresherActivityItem>();

	@Override
	public Integer getId() {
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

	public void setId(int id) {
		this.id = id;
	}
}
