package com.playerdata.activity.exChangeType.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.activity.countType.cfg.ActivityCountTypeCfg;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfgDAO;
import com.playerdata.activity.rateType.cfg.ActivityRateTypeCfgDAO;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "activity_exchange_item")
public class ActivityExchangeTypeItem implements  IMapItem {

	@Id
	private String id;
	
	private String userId;// 对应的角色Id


	@CombineSave
	private String cfgId;
	
	@CombineSave
	private boolean closed = false;

	
	@CombineSave
	private List<ActivityExchangeTypeSubItem> subItemList = new ArrayList<ActivityExchangeTypeSubItem>();
	
	
	@CombineSave
	private String version ;


	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}


	
	public void reset(){
		subItemList = new ArrayList<ActivityExchangeTypeSubItem>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}



	public List<ActivityExchangeTypeSubItem> getSubItemList() {
		return subItemList;
	}

	public void setSubItemList(List<ActivityExchangeTypeSubItem> subItemList) {
		this.subItemList = subItemList;
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

	

	
	
	
}
