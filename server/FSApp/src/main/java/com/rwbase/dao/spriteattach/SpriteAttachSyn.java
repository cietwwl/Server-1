package com.rwbase.dao.spriteattach;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;
import com.rw.fsutil.dao.annotation.OwnerId;

@SynClass
public class SpriteAttachSyn implements RoleExtProperty{
	
	@Id
	private Integer id;
	@OwnerId
	private String ownerId; //英雄id
	
	private List<SpriteAttachItem> items = new ArrayList<SpriteAttachItem>();

	public List<SpriteAttachItem> getItems() {
		return items;
	}
	public void setItems(List<SpriteAttachItem> items) {
		this.items = items;
	}
	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
}
