package com.rwbase.dao.copy.itemPrivilege;

import java.util.List;

import com.playerdata.readonly.ItemInfoIF;
import com.rwbase.dao.copy.pojo.ItemInfo;

public class ItemPrivilegeFactory {
	
	public static ItemInfoIF createPrivilegeItem(ItemInfoIF itemInfo, PrivilegeDescItem priDesc){
		if(!priDesc.isAllIDHave() && itemInfo.getItemID() != priDesc.getItemID()) return itemInfo;
		ItemAppendPrivilege result = new ItemAppendPrivilege();
		result.setItemInfoIF(itemInfo);
		result.setValue(priDesc.getValue());
		result.setPersent(priDesc.isPersent());
		return result;
	}
	
	public static ItemInfoIF createPrivilegeItem(ItemInfoIF itemInfo, List<PrivilegeDescItem> priDescList){
		for(PrivilegeDescItem priDesc : priDescList){
			itemInfo = createPrivilegeItem(itemInfo, priDesc);
		}
		return itemInfo;
	}
	
	/**
	 * 获取添加了增益效果的item
	 * @param itemInfo 添加了增益效果的接口
	 * @return
	 */
	public static ItemInfo getItemInfo(ItemInfoIF itemInfo){
		ItemInfo item = new ItemInfo();
		item.setItemID(itemInfo.getItemID());
		item.setItemNum(itemInfo.getItemNum());
		return item;
	}
	
}
