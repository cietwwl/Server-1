package com.playerdata.activity.redEnvelopeType.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;











import com.playerdata.activity.VitalityType.ActivityVitalityTypeEnum;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityCfg;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityCfgDAO;
import com.playerdata.activity.redEnvelopeType.cfg.ActivityRedEnvelopeTypeCfg;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rw.fsutil.util.DateUtils;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "activity_redenvelope_item")
public class ActivityRedEnvelopeTypeItem implements  IMapItem {
	@Id
	private String id;
	
	private String userId;// 对应的角色Id

	@CombineSave
	private String cfgId;

	@CombineSave
	private boolean closed = false;
	
	@CombineSave
	private boolean istaken = false;

	@CombineSave
	private long lastTime;
	
	@CombineSave
	private int day;
	
	@CombineSave
	private List<ActivityRedEnvelopeTypeSubItem> subItemList = new ArrayList<ActivityRedEnvelopeTypeSubItem>();

	@CombineSave
	private String version;
	
	@CombineSave
	private int goldCount;
	
	@CombineSave
	private boolean isTouchRedPoint;	

	public boolean isTouchRedPoint() {
		return isTouchRedPoint;
	}

	public void setTouchRedPoint(boolean isTouchRedPoint) {
		this.isTouchRedPoint = isTouchRedPoint;
	}
	
	public void resetByVersion(ActivityRedEnvelopeTypeCfg cfg,List<ActivityRedEnvelopeTypeSubItem> subItemList,int day){
		this.cfgId = cfg.getId();
		closed = false;
		lastTime = System.currentTimeMillis();
		version = cfg.getVersion();
		this.subItemList = subItemList;
		this.day = day;
		istaken = false;
		goldCount = 0;
		isTouchRedPoint = false;
	}
	
	public void resetByOtherday(ActivityRedEnvelopeTypeCfg cfg,int day){
		lastTime = System.currentTimeMillis();
		this.day = day;
	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}	
	
	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<ActivityRedEnvelopeTypeSubItem> getSubItemList() {
		return subItemList;
	}

	public void setSubItemList(List<ActivityRedEnvelopeTypeSubItem> subItemList) {
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

	public boolean isIstaken() {
		return istaken;
	}

	public void setIstaken(boolean istaken) {
		this.istaken = istaken;
	}

	public int getGoldCount() {
		return goldCount;
	}

	public void setGoldCount(int goldCount) {
		this.goldCount = goldCount;
	}	
	
	
}
