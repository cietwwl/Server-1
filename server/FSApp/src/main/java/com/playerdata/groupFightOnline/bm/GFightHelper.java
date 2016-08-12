package com.playerdata.groupFightOnline.bm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.rwbase.dao.copy.pojo.ItemInfo;

public class GFightHelper {
	public static String itemListToString(List<ItemInfo> items){
		if(items == null) return null;
		if(items.isEmpty()) return "";
		StringBuffer sbuff = new StringBuffer();
		for(ItemInfo item : items){
			sbuff.append(item.getItemID());
			sbuff.append("~");
			sbuff.append(item.getItemNum());
			sbuff.append(",");
		}
		try{
			sbuff.deleteCharAt(sbuff.lastIndexOf(","));
		}catch(Exception ex){
			
		}
		return sbuff.toString();
	}
	
	public static List<ItemInfo> stringToItemList(String rewardStr, String splitStr){
		if(StringUtils.isBlank(rewardStr)) return null;
		List<ItemInfo> tmpList = new ArrayList<ItemInfo>();
		String[] rewardItemStr = rewardStr.split(",");
		for(String rewardItem : rewardItemStr){
			String[] itemStrArr = rewardItem.split(splitStr);
			if(itemStrArr.length == 2){
				ItemInfo item = new ItemInfo();
				item.setItemID(Integer.parseInt(itemStrArr[0]));
				item.setItemNum(Integer.parseInt(itemStrArr[1]));
				tmpList.add(item);
			}
		}
		return Collections.unmodifiableList(tmpList);
	}
}
