package com.playerdata.activity.VitalityType.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;


import com.playerdata.activity.dailyCountType.cfg.ActivityDailyCountTypeCfg;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyCountTypeCfgDAO;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "activity_vitalitytype_item")
public class ActivityVitalityTypeItem implements  IMapItem {

	@Id
	private String id;
	
	private String userId;// 对应的角色Id



	@CombineSave
	private boolean closed = false;

	@CombineSave
	private long lastTime;
	
	@CombineSave
	private int activeCount;
	
	@CombineSave
	private List<ActivityVitalityTypeSubItem> subItemList = new ArrayList<ActivityVitalityTypeSubItem>();
	
	
	@CombineSave
	private String version ;
	
//	public void reset(ActivityDailyCountTypeCfg cfg){
//		closed = false;
//		version = cfg.getVersion();
//		setSubItemList(ActivityDailyCountTypeCfgDAO.getInstance().newItemList(cfg));
//		lastTime = System.currentTimeMillis();
//	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}




	public long getLastTime() {
		return lastTime;
	}

	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public List<ActivityVitalityTypeSubItem> getSubItemList() {
		return subItemList;
	}

	public void setSubItemList(List<ActivityVitalityTypeSubItem> subItemList) {
		this.subItemList = subItemList;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}



	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	

	
	
	
}
