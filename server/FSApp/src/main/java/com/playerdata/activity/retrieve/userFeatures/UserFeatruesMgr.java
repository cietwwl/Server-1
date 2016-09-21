package com.playerdata.activity.retrieve.userFeatures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.playerdata.activity.retrieve.cfg.RewardBackCfgDAO;
import com.playerdata.activity.retrieve.data.RewardBackItem;
import com.playerdata.activity.retrieve.data.RewardBackSubItem;
import com.playerdata.activity.retrieve.data.RewardBackTodaySubItem;
import com.playerdata.activity.retrieve.userFeatures.userFeaturesType.UserFeatruesBreakfast;


public class UserFeatruesMgr {

	private static UserFeatruesMgr instance = new UserFeatruesMgr();
	
	public static UserFeatruesMgr getInstance(){
		return instance;
	}
	
	private Map<UserFeaturesEnum, IUserFeatruesHandler> featruesHandlerMap = new HashMap<UserFeaturesEnum, IUserFeatruesHandler>();
	
	private UserFeatruesMgr(){
		featruesHandlerMap.put(UserFeaturesEnum.breakfast, new UserFeatruesBreakfast());
	}
	
	public List<RewardBackTodaySubItem> doCreat(String userId){
		List<RewardBackTodaySubItem> subItemList = new ArrayList<RewardBackTodaySubItem>();		
		for(Map.Entry<UserFeaturesEnum, IUserFeatruesHandler> entry : featruesHandlerMap.entrySet()){
			RewardBackTodaySubItem  subItem = new RewardBackTodaySubItem();
			subItem = creatSubItem(userId,entry.getKey(),entry.getValue());
			subItemList.add(subItem);
		}		
		return subItemList;
	}
	
	private RewardBackTodaySubItem creatSubItem(String userId,UserFeaturesEnum iEnum,IUserFeatruesHandler iUserFeatruesHandler){
		RewardBackTodaySubItem subItem = new RewardBackTodaySubItem();		
		if(iUserFeatruesHandler != null){
			subItem = iUserFeatruesHandler.doEvent(userId);
		}
		return subItem;
	}

	public List<RewardBackSubItem> doFresh(String userId,List<RewardBackTodaySubItem> subTodayItemList) {
		RewardBackCfgDAO rewardBackCfgDAO = RewardBackCfgDAO.getInstance();
		List<RewardBackSubItem> subItemList = new ArrayList<RewardBackSubItem>();
		for(RewardBackTodaySubItem todaySubItem : subTodayItemList){
			UserFeaturesEnum iEnum = UserFeaturesEnum.getById(todaySubItem.getId());
			if(iEnum == null){
				continue;
			}
			RewardBackSubItem subItem = new RewardBackSubItem();
			subItem = featruesHandlerMap.get(iEnum).doFresh(todaySubItem,userId,rewardBackCfgDAO);
			subItemList.add(subItem);
		}		
		return subItemList;
	}
	
}
