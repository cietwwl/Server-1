package com.rwbase.dao.fetters.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.Table;

import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;

/**
 * 角色法宝神器羁绊数据
 * @author Alex
 *
 * 2016年7月18日 下午12:11:53
 */
@Table(name="magic_equip_fetter_record")
public class MagicEquipFetterRecord implements IMapItem{
	@Id
	private String id;

	@CombineSave
	private String userID;
	
	@CombineSave
	private Map<Integer, SynMagicEquipFetterData> dataMap = new HashMap<Integer, SynMagicEquipFetterData>();
	
	
	
	@Override
	public String getId() {
		return id;
	}



	public String getUserID() {
		return userID;
	}



	public void setUserID(String userID) {
		this.userID = userID;
	}


	public Map<Integer, SynMagicEquipFetterData> getDataMap() {
		return dataMap;
	}



	public void setDataMap(Map<Integer, SynMagicEquipFetterData> dataMap) {
		this.dataMap = dataMap;
	}



	public void setId(String id) {
		this.id = id;
	}
	
	
}
