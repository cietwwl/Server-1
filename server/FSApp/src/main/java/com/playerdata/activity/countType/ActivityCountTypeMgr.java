package com.playerdata.activity.countType;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfgDAO;
import com.playerdata.activity.countType.data.ActivityCountTypeItemHolder;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeSubItem;


public class ActivityCountTypeMgr {
	
	private static ActivityCountTypeMgr instance = new ActivityCountTypeMgr();
	
	public static ActivityCountTypeMgr getInstance(){
		return instance;
	}
	
	public void addCount(Player player, ActivityCountTypeEnum countType){
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder.getInstance();
		
		ActivityCountTypeItem dataItem = dataHolder.getItem(player.getUserId(), countType.getId());
		dataItem.setCount(dataItem.getCount()+1);
		
		dataHolder.updateItem(player, dataItem);
	}
	
	public ActivityComResult takeGift(Player player, ActivityCountTypeEnum countType, String subItemId){
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder.getInstance();
		
		ActivityCountTypeItem dataItem = dataHolder.getItem(player.getUserId(), countType.getId());
		
		ActivityCountTypeSubItem targetItem = null;
		List<ActivityCountTypeSubItem> takenGiftList = dataItem.getTakenGiftList();
		for (ActivityCountTypeSubItem itemTmp : takenGiftList) {
			if(StringUtils.equals(itemTmp.getId(), subItemId)){
				targetItem = itemTmp;
				break;
			}
		}
		ActivityComResult result = ActivityComResult.newInstance(false);
		if(targetItem == null){
			targetItem = ActivityCountTypeCfgDAO.getInstance().newSubItem(countType.getId(), subItemId);
			if(targetItem == null){
				result.setReason("该奖励不存在 id:"+countType.getId()+" subItemId:"+subItemId);
			}else{
				takenGiftList.add(targetItem);
				takeGift(targetItem);
				result.setSuccess(true);
				dataHolder.updateItem(player, dataItem);
			}
		}else{
			takeGift(targetItem);			
			result.setSuccess(true);
			dataHolder.updateItem(player, dataItem);
		}
		
		return result;
	}

	private void takeGift(ActivityCountTypeSubItem targetItem) {
		targetItem.setTaken(true);
		//TODO: gift take logic
	}
	
	
}
