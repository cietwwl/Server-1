package com.playerdata.activity.VitalityType.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.activity.VitalityType.cfg.ActivityVitalityCfg;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityCfgDAO;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rw.fsutil.dao.annotation.OwnerId;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "activity_vitalitytype_item")
public class ActivityVitalityTypeItem implements  RoleExtProperty {

	@Id
	private int id;
	@OwnerId
	private String userId;// 对应的角色Id

	@CombineSave
	private String cfgId;

	@CombineSave
	private boolean closed = false;

	@CombineSave
	private long lastTime;
	
	@CombineSave
	private int activeCount;
	
	@CombineSave
	private boolean isCanGetReward;
	
	@CombineSave
	private List<ActivityVitalityTypeSubItem> subItemList = new ArrayList<ActivityVitalityTypeSubItem>();
	
	@CombineSave
	private List<ActivityVitalityTypeSubBoxItem> subBoxItemList = new ArrayList<ActivityVitalityTypeSubBoxItem>();
	
	@CombineSave
	private String version ;
	
	@CombineSave
	private long redPointLastTime;	
	
	@CombineSave
	private String enumId;	
	
	public String getEnumId() {
		return enumId;
	}

	public void setEnumId(String enumId) {
		this.enumId = enumId;
	}

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
	
	public void reset(ActivityVitalityCfg cfg){
		this.cfgId = String.valueOf(cfg.getId());
		closed = false;
		version = String.valueOf(cfg.getVersion());
		setSubItemList(ActivityVitalityCfgDAO.getInstance().newItemList(ActivityVitalityCfgDAO.getInstance().getday(cfg) ,cfg));
		List<ActivityVitalityTypeSubBoxItem> boxlist = ActivityVitalityCfgDAO.getInstance().newBoxItemList(ActivityVitalityCfgDAO.getInstance().getday(cfg) ,cfg);
		if(boxlist != null&&!boxlist.isEmpty()){
			setSubBoxItemList(boxlist);
		}
		lastTime = System.currentTimeMillis();
		activeCount = 0;
		isTouchRedPoint = false;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public boolean isCanGetReward() {
		return isCanGetReward;
	}

	public void setCanGetReward(boolean isCanGetReward) {
		this.isCanGetReward = isCanGetReward;
	}

	public String getCfgId() {
		return cfgId;
	}

	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
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

	public void setId(int id) {
		this.id = id;
	}

	public List<ActivityVitalityTypeSubItem> getSubItemList() {
		return subItemList;
	}

	public void setSubItemList(List<ActivityVitalityTypeSubItem> subItemList) {
		this.subItemList = subItemList;
	}

	public List<ActivityVitalityTypeSubBoxItem> getSubBoxItemList() {
		return subBoxItemList;
	}

	public void setSubBoxItemList(
			List<ActivityVitalityTypeSubBoxItem> subBoxItemList) {
		this.subBoxItemList = subBoxItemList;
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

	public int getActiveCount() {
		return activeCount;
	}

	public void setActiveCount(int activeCount) {
		this.activeCount = activeCount;
	}

	public ActivityVitalityTypeSubItem getByType(String type){
		ActivityVitalityTypeSubItem subitem = null;
		for(ActivityVitalityTypeSubItem subitemtmp : subItemList){
			if(StringUtils.equals(subitemtmp.getType(), type)){
				subitem = subitemtmp;
				break;
			}
		}
		
		return subitem;
	}
}
