package com.rw.service.guide.datamodel;

import javax.persistence.Id;
import javax.persistence.Table;

import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rwbase.common.INotifyChange;

@Table(name = "newguide_give_item_history")

public class GiveItemHistory implements IMapItem {
	@Id
	private String storeId;
	private String userId;
	private int giveActionId;
	private boolean given = false;

	public static String Convert(String userId,int actId){
		return userId+"_"+actId;
	}
	
	public GiveItemHistory(){}
	
	public static GiveItemHistory Add(INotifyChange notifyProxy,String userId,int giveActionId){
		GiveItemHistory result = new GiveItemHistory();
		result.userId = userId;
		result.giveActionId=giveActionId;
		result.storeId = userId+"_"+giveActionId;
		if (!GiveItemHistoryHolder.getInstance().add(result,notifyProxy)){
			return null;
		}
		return result;
	}
	
	public boolean setGiven(INotifyChange notifyProxy,boolean given) {
		this.given = given;
		if (!GiveItemHistoryHolder.getInstance().update(this, notifyProxy)){
			return false;
		}
		return true;
	}

	public String getStoreId() {
		return storeId;
	}

	public String getUserId() {
		return userId;
	}

	public int getGiveActionId() {
		return giveActionId;
	}

	public boolean isGiven() {
		return given;
	}

	@Override
	public String getId() {
		return storeId;
	}

}
