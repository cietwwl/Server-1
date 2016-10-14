package com.rw.service.guide.datamodel;

import javax.persistence.Id;
import javax.persistence.Table;

import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.OwnerId;
import com.rwbase.common.INotifyChange;

@Table(name = "newguide_give_item_history")

public class GiveItemHistory implements RoleExtProperty {
	@Id
	private Integer id;
	@OwnerId
	private String userId;
	private int giveActionId;
	private boolean given = false;

	/**
	 * 仅仅用于json库的序列化/反序列化，其他人不要调用
	 */
	public GiveItemHistory(){}
	
	public static GiveItemHistory Add(INotifyChange notifyProxy,String userId,int giveActionId){
		GiveItemHistory result = new GiveItemHistory();
		result.userId = userId;
		result.giveActionId=giveActionId;
		result.id = giveActionId;
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
	public Integer getId() {
		return id;
	}


}
