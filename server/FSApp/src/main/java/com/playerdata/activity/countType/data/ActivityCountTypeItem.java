package com.playerdata.activity.countType.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.activity.countType.cfg.ActivityCountTypeSubItem;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "activity_counttype_item")
public class ActivityCountTypeItem implements  IMapItem {

	private String id;
	private int count;
	
	//已领取的奖励condition id
	private List<ActivityCountTypeSubItem> takenGiftList = new ArrayList<ActivityCountTypeSubItem>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<ActivityCountTypeSubItem> getTakenGiftList() {
		return takenGiftList;
	}

	public void setTakenGiftList(List<ActivityCountTypeSubItem> takenGiftList) {
		this.takenGiftList = takenGiftList;
	}


	
	
	
}
