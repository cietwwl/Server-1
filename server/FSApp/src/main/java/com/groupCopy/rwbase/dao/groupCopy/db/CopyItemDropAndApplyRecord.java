package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.HashMap;

import javax.persistence.Id;
import javax.persistence.Table;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;

@Table(name = "group_copy_item_drop_apply_record")
@SynClass
public class CopyItemDropAndApplyRecord implements IMapItem {

	@Id
	private String id; //groupID_chaterID 
	@IgnoreSynField
	private String groupId; // 帮派ID

	@CombineSave
	private String chaterID;//对应章节id
	/** 当前地图的掉落物品及对应的申请列表<key=itemID,value=掉落记录> */
	@CombineSave
	@IgnoreSynField
	private HashMap<String, ItemDropAndApplyTemplate> daMap = new HashMap<String, ItemDropAndApplyTemplate>();

	public CopyItemDropAndApplyRecord(String id, String groupId) {
		this.id = groupId+"_"+id;
		this.groupId = groupId;
		this.chaterID = id;
	}

	public CopyItemDropAndApplyRecord() {
	}

	/**
	 * 此方法只是提供序列化使用，一般功能禁止调用， 如需迭代，请使用
	 * {@link GroupCopyMapRecord#getDropApplyEnumeration()}
	 * */
	public HashMap<String, ItemDropAndApplyTemplate> getDaMap() {
		return daMap;
	}

	/** 此方法只是提供序列化使用，一般功能禁止调用 */
	public void setDaMap(HashMap<String, ItemDropAndApplyTemplate> daMap) {
		this.daMap = daMap;
	}

	public ItemDropAndApplyTemplate getDropApplyRecord(String key) {
		return daMap.get(key);
	}

	@Override
	public String getId() {
		return id;
	}

	
	public String getChaterID() {
		return chaterID;
	}

	
	public String getGroupId() {
		return groupId;
	}

	
}
