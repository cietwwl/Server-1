package com.playerdata;

import java.util.ArrayList;

import com.rwbase.common.MD5;
import com.rwbase.dao.user.UserUniqueNameDAO;
import com.rwbase.dao.user.pojo.TableUniqueName;

public class UniqueNameMgr {

	private UserUniqueNameDAO userUniqueNameDAO = UserUniqueNameDAO.getInstance();
	private TableUniqueName userUniqueName;
	
	
	
	/**检查该名字是否可以注册
	 * true可以注册
	 * false不可以
	 * */
	public boolean validateName(String name,int zone){
		String md5ofStr = MD5.getMD5ofStr(name);
		TableUniqueName userUniqueName = this.get(md5ofStr);
		if(userUniqueName.getZoneList().indexOf(zone) >= 0){
			return false;
		}
		return true;
	}
	/**将该名字注册到该区
	 * */
	public boolean registeNameToThisZone(String name,int zone){
		String md5ofStr = MD5.getMD5ofStr(name);
		userUniqueName = this.get(md5ofStr);
		userUniqueName.setNick(name);
		userUniqueName.getZoneList().add(zone);
		return this.save();
	}
	
	public void deleteNameFromThisZone(String name,int zone){
		String md5ofStr = MD5.getMD5ofStr(name);
		TableUniqueName userUniqueName = this.get(md5ofStr);
		int zoneIndex = userUniqueName.getZoneList().indexOf(zone);
		if(zoneIndex >= 0) {
			userUniqueName.getZoneList().remove(zoneIndex);
			this.update(userUniqueName);
		}
	}
	
	public TableUniqueName get(String id){
		TableUniqueName userUniqueName = userUniqueNameDAO.get(id);
		if(userUniqueName == null){
			userUniqueName = new TableUniqueName();
			userUniqueName.setMd5(id);
			userUniqueName.setZoneList(new ArrayList<Integer>());
			this.update(userUniqueName);
		}
		return userUniqueName;
	}
	public boolean save(){
		if(userUniqueName == null){
			return false;
		}
		return userUniqueNameDAO.update(userUniqueName);
	}
	public boolean update(TableUniqueName userUniqueName){
		return userUniqueNameDAO.update(userUniqueName);
	}


}
