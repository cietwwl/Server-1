package com.rwbase.dao.openLevelTiggerService.pojo;

import java.util.List;

import javax.persistence.Id;

import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;
import com.rw.fsutil.dao.annotation.CombineSave;

public class OpenLevelTiggerServiceItem implements RoleExtProperty{
	@Id
	private Integer id;//功能type
	
	private String userId;// 对应的角色Id
	
	@CombineSave
	private long creatTime;//该类型对应的开放等级触发时间
	
	@CombineSave
	private List<OpenLevelTiggerServiceSubItem> subItemList;//该类型对应的多次服务
	
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return id;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public void setId(Integer id) {
		this.id = id;
	}

	public long getCreatTime() {
		return creatTime;
	}


	public void setCreatTime(long creatTime) {
		this.creatTime = creatTime;
	}


	public List<OpenLevelTiggerServiceSubItem> getSubItemList() {
		return subItemList;
	}


	public void setSubItemList(List<OpenLevelTiggerServiceSubItem> subItemList) {
		this.subItemList = subItemList;
	}

	
	
}
