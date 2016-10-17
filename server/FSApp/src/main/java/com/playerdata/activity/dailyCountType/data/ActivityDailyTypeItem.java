package com.playerdata.activity.dailyCountType.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;




import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeCfg;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeCfgDAO;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rw.fsutil.dao.annotation.OwnerId;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "activity_dailycounttype_item")
public class ActivityDailyTypeItem implements  RoleExtProperty {

	@Id
	private Integer id;
	@OwnerId
	private String userId;// 对应的角色Id
	@CombineSave
    private String cfgid;
	

	
	


	public String getCfgid() {
		return cfgid;
	}

	public void setCfgid(String cfgid) {
		this.cfgid = cfgid;
	}

	@CombineSave
	private boolean closed = false;

	@CombineSave
	private long lastTime;
	
	@CombineSave
	private List<ActivityDailyTypeSubItem> subItemList = new ArrayList<ActivityDailyTypeSubItem>();
	
	
	@CombineSave
	private String version ;
	
	@CombineSave
	private long redPointLastTime;	
	
	public long getRedPointLastTime() {
		return redPointLastTime;
	}

	public void setRedPointLastTime(long redPointLastTime) {
		this.redPointLastTime = redPointLastTime;
	}
	
	@CombineSave
	private boolean isTouchRedPoint;	

	public boolean isTouchRedPoint() {
		return isTouchRedPoint;
	}

	public void setTouchRedPoint(boolean isTouchRedPoint) {
		this.isTouchRedPoint = isTouchRedPoint;
	}
	
	public void reset(ActivityDailyTypeCfg cfg){
		cfgid = cfg.getId();
		closed = false;
		version = cfg.getVersion();
		setSubItemList(ActivityDailyTypeCfgDAO.getInstance().newItemList(cfg.getId()));
		lastTime = System.currentTimeMillis();
		isTouchRedPoint = false;
	}

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




	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<ActivityDailyTypeSubItem> getSubItemList() {
		return subItemList;
	}

	public void setSubItemList(List<ActivityDailyTypeSubItem> subItemList) {
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
