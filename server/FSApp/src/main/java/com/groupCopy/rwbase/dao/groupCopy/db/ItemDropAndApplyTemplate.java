package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.playerdata.dataSyn.annotation.SynClass;


/**
 * 帮派副本地图掉落单个奖励物品对应申请列表
 * @author Alex
 * 2016年6月12日 下午4:54:00
 */
@SynClass
public class ItemDropAndApplyTemplate {

	int itemID;
	
	private List<DropInfo> dropInfoList = new LinkedList<DropInfo>();
	
	//申请的角色列表<key=roleID,value=applyTime>
	private List<ApplyInfo> applyData = new LinkedList<ApplyInfo>();
	
	
	public ItemDropAndApplyTemplate() {
	}

	public ItemDropAndApplyTemplate(int itemID) {
		this.itemID = itemID;
	}

	public void addDropItem(int count) {
		dropInfoList.add(new DropInfo(count, System.currentTimeMillis()));
	}

	public int getItemID() {
		return itemID;
	}

	public void setItemID(int itemID) {
		this.itemID = itemID;
	}

	public List<DropInfo> getDropInfoList() {
		return Collections.unmodifiableList(dropInfoList);
	}

	public void setDropInfoList(List<DropInfo> dropInfoList) {
		this.dropInfoList = dropInfoList;
	}

	public List<ApplyInfo> getApplyData() {
		return Collections.unmodifiableList(applyData);
	}

	public void setApplyData(List<ApplyInfo> applyData) {
		this.applyData = applyData;
	}

	public boolean deleteApplyData(ApplyInfo data){
		return this.applyData.remove(data);
	}

	public void addApplyRole(ApplyInfo info) {
		applyData.add(info);
	}

	public void deleteApply(DropInfo dropInfo, ApplyInfo applyInfo) {
		if(applyInfo != null){
			applyData.remove(applyInfo);
		}
		int left = dropInfo.getCount() - 1;
		if(left <= 0){
			dropInfoList.remove(dropInfo);
		}else{
			dropInfo.setCount(left);
		}
	}


	
}
