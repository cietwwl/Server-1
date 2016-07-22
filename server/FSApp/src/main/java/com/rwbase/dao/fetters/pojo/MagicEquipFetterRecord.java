package com.rwbase.dao.fetters.pojo;

import java.util.ArrayList;
import java.util.Collections;
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
	private List<Integer> fixEquipFetters = new ArrayList<Integer>();
	
	@CombineSave
	private List<Integer> magicFetters = new ArrayList<Integer>();
	
	
	
	public MagicEquipFetterRecord() {
	}



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



	public List<Integer> getFixEquipFetters() {
		return fixEquipFetters;
	}



	public void setFixEquipFetters(List<Integer> fixEquipFetters) {
		this.fixEquipFetters = fixEquipFetters;
	}



	public List<Integer> getMagicFetters() {
		return magicFetters;
	}



	public void setMagicFetters(List<Integer> magicFetters) {
		this.magicFetters = magicFetters;
	}



	public boolean isEmpty() {
		return magicFetters.isEmpty() && fixEquipFetters.isEmpty();
	}
	

	public List<Integer> getAllFetters(){
		List<Integer> tempList = new ArrayList<Integer>();
		tempList.addAll(fixEquipFetters);
		tempList.addAll(magicFetters);
		return tempList;
	}
	
}
