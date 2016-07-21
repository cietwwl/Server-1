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

	
	private String userId;
	
	//已经开启羁绊id
	@CombineSave
	private List<Integer> fetterIDs = new ArrayList<Integer>();
	
	
	
	@Override
	public String getId() {
		return id;
	}



	public String getUserId() {
		return userId;
	}



	public void setUserId(String userID) {
		this.userId = userID;
	}



	public void setId(String id) {
		this.id = id;
	}
	
	
	public List<Integer> getFetterIDs() {
		return fetterIDs;
	}



	public void setFetterIDs(List<Integer> fetterIDs) {
		this.fetterIDs = fetterIDs;
	}



	public void AddFetter(int fetterID){
		//已经开启羁绊id
		fetterIDs.add(fetterID);
	}
	
	
	
}
