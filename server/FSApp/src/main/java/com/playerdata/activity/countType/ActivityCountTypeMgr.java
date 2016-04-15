package com.playerdata.activity.countType;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfg;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfgDAO;
import com.playerdata.activity.countType.data.ActivityCountTypeItemHolder;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeSubItem;


public class ActivityCountTypeMgr {
	
	private static ActivityCountTypeMgr instance = new ActivityCountTypeMgr();
	
	public static ActivityCountTypeMgr getInstance(){
		return instance;
	}
	
	public void synCountTypeData(Player player){
		ActivityCountTypeItemHolder.getInstance().synAllData(player);
	}
	
	public void checkActivityOpen(Player player){
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder.getInstance();
		
		List<ActivityCountTypeCfg> allCfgList = ActivityCountTypeCfgDAO.getInstance().getAllCfg();
		for (ActivityCountTypeCfg activityCountTypeCfg : allCfgList) {
			if(isOpen(activityCountTypeCfg)){
				ActivityCountTypeEnum countTypeEnum = ActivityCountTypeEnum.getById(activityCountTypeCfg.getId());
				if(countTypeEnum == null)
					continue;
				ActivityCountTypeItem targetItem = dataHolder.getItem(player.getUserId(), countTypeEnum);
				if(targetItem == null){
					
					targetItem = ActivityCountTypeCfgDAO.getInstance().newItem(player, countTypeEnum);
					if(targetItem!=null){
						 dataHolder.addItem(player, targetItem);
					}
				}
				
			}
		}
		
	}
	
	private boolean isOpen(ActivityCountTypeCfg activityCountTypeCfg) {
		long startTime = activityCountTypeCfg.getStarTime()*1000;
		long endTime = activityCountTypeCfg.getEndTime()*1000;
		long currentTime = System.currentTimeMillis();
		
		return currentTime < endTime && currentTime > startTime;
	}

	public void addCount(Player player, ActivityCountTypeEnum countType){
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder.getInstance();
		
		ActivityCountTypeItem dataItem = dataHolder.getItem(player.getUserId(), countType);
		
		dataItem.setCount(dataItem.getCount()+1);
		
		dataHolder.updateItem(player, dataItem);
	}
	
	public ActivityComResult takeGift(Player player, ActivityCountTypeEnum countType, String subItemId){
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder.getInstance();
		
		ActivityCountTypeItem dataItem = dataHolder.getItem(player.getUserId(), countType);
		ActivityComResult result = ActivityComResult.newInstance(false);
		//未激活
		if(dataItem == null){
			result.setReason("活动尚未开启");
		}else{
			ActivityCountTypeSubItem targetItem = null;
			List<ActivityCountTypeSubItem> takenGiftList = dataItem.getTakenGiftList();
			for (ActivityCountTypeSubItem itemTmp : takenGiftList) {
				if(StringUtils.equals(itemTmp.getId(), subItemId)){
					targetItem = itemTmp;
					break;
				}
			}
			if(targetItem == null){
				targetItem = ActivityCountTypeCfgDAO.getInstance().newSubItem(countType, subItemId);
				if(targetItem == null){
					result.setReason("该奖励不存在 id:"+countType.getCfgId()+" subItemId:"+subItemId);
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
			
		}
		
		
		return result;
	}

	private void takeGift(ActivityCountTypeSubItem targetItem) {
		targetItem.setTaken(true);
		//TODO: gift take logic
	}
	
	
}
