package com.rwbase.dao.spriteattach;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rw.fsutil.dao.annotation.NonSave;
import com.rw.fsutil.dao.annotation.OwnerId;

@SynClass
public class SpriteAttachSyn implements RoleExtProperty {

	@Id
	private Integer id;
	@OwnerId
	private String ownerId; // 英雄id

	@CombineSave
	private List<SpriteAttachItem> items = new ArrayList<SpriteAttachItem>();

	@IgnoreSynField
	@NonSave
	private Map<Integer, SpriteAttachItem> itemMap = new HashMap<Integer, SpriteAttachItem>();

	public List<SpriteAttachItem> getItems() {
		return items;
	}

	@JsonIgnore
	public Map<Integer, SpriteAttachItem> getItemMap() {
		if (itemMap.size() != items.size()) {
			itemMap.clear();
			for (SpriteAttachItem spriteAttachItem : items) {
				itemMap.put(spriteAttachItem.getIndex(), spriteAttachItem);
			}
		}
		return itemMap;
	}

	public void addItem(SpriteAttachItem item) {
		int index = item.getIndex();
		this.items.add(item);
		this.itemMap.put(index, item);
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

	public void setItems(List<SpriteAttachItem> items) {
		this.items = items;
	}

}
