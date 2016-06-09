package com.playerdata.activity.exChangeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;



import com.playerdata.activity.dailyCountType.ActivityDailyTypeEnum;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeCfg;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeCfgDAO;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItem;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItemHolder;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfg;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfgDAO;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeSubCfg;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeSubCfgDAO;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItem;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItemHolder;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeSubItem;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.common.enu.eSpecialItemId;


public class ActivityExchangeTypeMgr {

	private static ActivityExchangeTypeMgr instance = new ActivityExchangeTypeMgr();

	private final static int MAKEUPEMAIL = 10055;

	public static ActivityExchangeTypeMgr getInstance() {
		return instance;
	}

	public void synCountTypeData(Player player) {
		ActivityExchangeTypeItemHolder.getInstance().synAllData(player);
	}
	
	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空 */
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
		checkCfgVersion(player);
		checkOtherDay(player);
	}

	private void checkNewOpen(Player player) {
		ActivityExchangeTypeItemHolder dataHolder = ActivityExchangeTypeItemHolder.getInstance();
		List<ActivityExchangeTypeCfg> allCfgList = ActivityExchangeTypeCfgDAO.getInstance().getAllCfg();
		ArrayList<ActivityExchangeTypeItem> addItemList = null;
		for (ActivityExchangeTypeCfg activityExchangeTypeCfg : allCfgList) {// 遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if (!isOpen(activityExchangeTypeCfg)) {
				// 活动未开启
				continue;
			}
			ActivityExChangeTypeEnum  activityExChangeTypeEnum = ActivityExChangeTypeEnum.getById(activityExchangeTypeCfg.getId());
			if (activityExChangeTypeEnum == null) {
				GameLog.error(LogModule.ComActivityExchange, player.getUserId(), "配置表开启，但找不到枚举", null);
				continue;
			}
			ActivityExchangeTypeItem targetItem = dataHolder.getItem(player.getUserId(), activityExChangeTypeEnum);// 已在之前生成数据的活动
			if (targetItem == null) {						
				targetItem = ActivityExchangeTypeCfgDAO.getInstance().newItem(player, activityExChangeTypeEnum);// 生成新开启活动的数据
				if (targetItem == null) {					
					continue;
				}
				if (addItemList == null) {
					addItemList = new ArrayList<ActivityExchangeTypeItem>();
				}
				addItemList.add(targetItem);
			}
		}
		if (addItemList != null) {
			dataHolder.addItemList(player, addItemList);
		}
	}
	
	
	
	public boolean isOpen(ActivityExchangeTypeCfg activityExchangeTypeCfg) {
		if (activityExchangeTypeCfg != null) {
//			if(player.getLevel() < activityCountTypeCfg.getLevelLimit()){
//				return false;
//			}
			long startTime = activityExchangeTypeCfg.getChangeStartTime();
			long endTime = activityExchangeTypeCfg.getChangeEndTime();
			long currentTime = System.currentTimeMillis();
			return currentTime < endTime && currentTime > startTime;
		}
		return false;
	}
	
	private void checkCfgVersion(Player player) {
		ActivityExchangeTypeItemHolder dataHolder = ActivityExchangeTypeItemHolder.getInstance();
		List<ActivityExchangeTypeItem> itemList = dataHolder.getItemList(player.getUserId());
		for (ActivityExchangeTypeItem targetItem : itemList) {			
			ActivityExchangeTypeCfg targetCfg = ActivityExchangeTypeCfgDAO.getInstance().getConfig(targetItem.getCfgId());
			if(targetCfg == null){
				GameLog.error(LogModule.ComActivityExchange, null, "通用活动找不到配置文件", null);
				continue;
			}			
			
			if (!StringUtils.equals(targetItem.getVersion(), targetCfg.getVersion())) {
				targetItem.reset(targetCfg,ActivityExchangeTypeCfgDAO.getInstance().newItemList(player, targetCfg));
				dataHolder.updateItem(player, targetItem);
			}
		}
	}
	
	private void checkOtherDay(Player player) {
		ActivityExchangeTypeItemHolder dataHolder = ActivityExchangeTypeItemHolder.getInstance();
		List<ActivityExchangeTypeItem> itemlist = dataHolder.getItemList(player.getUserId());
		for (ActivityExchangeTypeItem targetItem : itemlist) {	
			ActivityExchangeTypeCfg targetCfg = ActivityExchangeTypeCfgDAO.getInstance().getConfig(targetItem.getCfgId());
			if(targetCfg == null){
				GameLog.error(LogModule.ComActivityExchange, null, "通用活动找不到配置文件", null);
				continue;
			}
			if(DateUtils.getDayDistance(targetItem.getLasttime(), System.currentTimeMillis())>0){
				List<ActivityExchangeTypeSubItem> subitemlist = targetItem.getSubItemList();
				for(ActivityExchangeTypeSubItem subitem: subitemlist){
					if(subitem.isIsrefresh()){
						subitem.setTime(0);
					}
				}
			}
			dataHolder.updateItem(player, targetItem);
		}
	}
	
	
	public ActivityComResult takeGift(Player player,
			ActivityExChangeTypeEnum countType, String subItemId) {
		ActivityExchangeTypeItemHolder dataHolder = ActivityExchangeTypeItemHolder.getInstance();

		ActivityExchangeTypeItem dataItem = dataHolder.getItem(player.getUserId(), countType);
		ActivityComResult result = ActivityComResult.newInstance(false);

		// 未激活
		if (dataItem == null) {
			result.setReason("活动尚未开启");

		} else {
			ActivityExchangeTypeSubItem targetItem = null;

			List<ActivityExchangeTypeSubItem> subItemList = dataItem.getSubItemList();
			for (ActivityExchangeTypeSubItem itemTmp : subItemList) {
				if (StringUtils.equals(itemTmp.getCfgId(), subItemId)) {
					targetItem = itemTmp;
					break;
				}
			}
			
			if(targetItem == null){
				result.setReason("找不到子活动类型的数据");
				return result;
			}
			
			if (isCanTaken(player,targetItem)) {
				takeGift(player, targetItem);
				result.setSuccess(true);
				dataHolder.updateItem(player, dataItem);
			}else{
				result.setSuccess(false);
				result.setReason("不满足兑换条件");
			}

		}

		return result;
	}
	
	private boolean isCanTaken(Player player,ActivityExchangeTypeSubItem targetItem) {
		ActivityExchangeTypeSubCfg activityExchangeTypeSubCfg = ActivityExchangeTypeSubCfgDAO.getInstance().getById(targetItem.getCfgId());
		if(activityExchangeTypeSubCfg== null){
			GameLog.error(LogModule.ComActivityExchange, null, "通用活动找不到奖励配置文件", null);
			return false;
		}
		if(targetItem.getTime() > activityExchangeTypeSubCfg.getTime()){
			GameLog.error(LogModule.ComActivityExchange, null, "申请次数超过上限，非法", null);
			return false;
		}	
		
		Map<String, String> exchangeNeedslist = activityExchangeTypeSubCfg.getChangelist();
		for(Map.Entry<String, String> entry:exchangeNeedslist.entrySet()){
			int id = Integer.parseInt(entry.getKey());
			if(id < eSpecialItemId.eSpecial_End.getValue()){
				if(player.getReward(eSpecialItemId.getDef(id))<Integer.parseInt(entry.getValue())){
					return false;
				}
			}else{
				if(player.getItemBagMgr().getItemCountByModelId(id) < Integer.parseInt(entry.getValue())){
					return false;
				}		
			}
		}		
		for(Map.Entry<String, String> entry:exchangeNeedslist.entrySet()){
			int id = Integer.parseInt(entry.getKey());
			if(id < eSpecialItemId.eSpecial_End.getValue()){
				Map<Integer, Integer> map = new HashMap<Integer, Integer>();
				map.put(Integer.parseInt(entry.getKey()), -Integer.parseInt(entry.getValue()));
				player.getItemBagMgr().useLikeBoxItem(null, null, map);
			}else{
				player.getItemBagMgr().useItemByCfgId(id, Integer.parseInt(entry.getValue()));
			}
		}
		
		return true;
	}

	private void takeGift(Player player, ActivityExchangeTypeSubItem targetItem) {
		ActivityExchangeTypeSubCfg subCfg = ActivityExchangeTypeSubCfgDAO.getInstance().getById(targetItem.getCfgId());
		if(subCfg == null){
			GameLog.error(LogModule.ComActivityExchange, null, "通用活动找不到奖励配置文件", null);
			return;
		}
		targetItem.setTime(targetItem.getTime()+1);
		ComGiftMgr.getInstance().addGiftById(player, subCfg.getAwardGift());
	}
	
	
}
