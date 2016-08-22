package com.playerdata.activity.fortuneCatType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;



import com.playerdata.activity.ActivityTypeHelper;
import com.playerdata.activity.ActivityRedPointUpdate;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityCfg;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityCfgDAO;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfg;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfgDAO;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeDropCfg;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeDropCfgDAO;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeSubCfg;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeSubCfgDAO;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItem;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItemHolder;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeSubItem;
import com.playerdata.activity.fortuneCatType.cfg.ActivityFortuneCatTypeCfg;
import com.playerdata.activity.fortuneCatType.cfg.ActivityFortuneCatTypeCfgDAO;
import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatTypeItem;
import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatTypeItemHolder;
import com.playerdata.activity.rateType.cfg.ActivityRateTypeCfg;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.copy.cfg.CopyCfg;
import com.rwbase.dao.copy.pojo.ItemInfo;


public class ActivityFortuneCatTypeMgr implements ActivityRedPointUpdate{

	private static ActivityFortuneCatTypeMgr instance = new ActivityFortuneCatTypeMgr();

	public static ActivityFortuneCatTypeMgr getInstance() {
		return instance;
	}

//	public void synCountTypeData(Player player) {
//		ActivityExchangeTypeItemHolder.getInstance().synAllData(player);
//	}
	
	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空 */
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
		checkCfgVersion(player);
		checkClose(player);
	}

	private void checkNewOpen(Player player) {
		ActivityFortuneCatTypeItemHolder dataHolder = ActivityFortuneCatTypeItemHolder.getInstance();
		List<ActivityFortuneCatTypeCfg> allCfgList = ActivityFortuneCatTypeCfgDAO.getInstance().getAllCfg();
		ArrayList<ActivityFortuneCatTypeItem> addItemList = null;
		for (ActivityFortuneCatTypeCfg cfg : allCfgList) {// 遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if (!isOpen(cfg)) {
				// 活动未开启
				continue;
			}

			ActivityFortuneCatTypeItem targetItem = dataHolder.getItem(player.getUserId());// 已在之前生成数据的活动
			if (targetItem == null) {						
				targetItem = ActivityFortuneCatTypeCfgDAO.getInstance().newItem(player, cfg);// 生成新开启活动的数据
				if (targetItem == null) {					
					continue;
				}
				if (addItemList == null) {
					addItemList = new ArrayList<ActivityFortuneCatTypeItem>();
				}
				addItemList.add(targetItem);
			}
		}
		if (addItemList != null) {
			dataHolder.addItemList(player, addItemList);
		}
	}
	
	
	
	public boolean isOpen(ActivityFortuneCatTypeCfg cfg) {
		if (cfg != null) {

			long startTime = cfg.getStartTime();
			long endTime = cfg.getEndTime();
			long currentTime = System.currentTimeMillis();
			return currentTime < endTime && currentTime >= startTime;
		}
		return false;
	}
	
	private void checkCfgVersion(Player player) {
		ActivityFortuneCatTypeItemHolder dataHolder = ActivityFortuneCatTypeItemHolder.getInstance();
		List<ActivityFortuneCatTypeItem> itemList = dataHolder.getItemList(player.getUserId());
		for (ActivityFortuneCatTypeItem targetItem : itemList) {			
			ActivityFortuneCatTypeCfg targetCfg = ActivityFortuneCatTypeCfgDAO.getInstance().getCfgListByItem(targetItem);
			if(targetCfg == null){
				GameLog.error(LogModule.ComActivityFortuneCat, null, "通用活动找不到配置文件", null);
				continue;
			}			
			
			if (!StringUtils.equals(targetItem.getVersion(), targetCfg.getVersion())) {
				targetItem.reset(targetCfg,ActivityFortuneCatTypeCfgDAO.getInstance().newSubItemList( targetCfg));
				dataHolder.updateItem(player, targetItem);
			}
		}
	}
	

	
	private void checkClose(Player player){
		ActivityFortuneCatTypeItemHolder dataHolder = ActivityFortuneCatTypeItemHolder.getInstance();
		List<ActivityFortuneCatTypeItem> itemList = dataHolder.getItemList(player.getUserId());
		for(ActivityFortuneCatTypeItem item : itemList){
			if(item.isClosed()){
				continue;			
			}
			ActivityFortuneCatTypeCfg cfg = ActivityFortuneCatTypeCfgDAO.getInstance().getCfgById(item.getCfgId());
			if(cfg == null){
				GameLog.error(LogModule.ComActivityFortuneCat, player.getUserId(), "玩家登录时服务器配置表已更新，只能通过版本核实来刷新数据", null);
				continue;
			}
			if(isOpen(cfg)){
				continue;
			}
			item.setClosed(true);
			item.setTouchRedPoint(true);
			dataHolder.updateItem(player, item);			
		}		
	}
	
//	
//	public boolean isLevelEnough(Player player,ActivityExchangeTypeCfg cfg){
//		boolean iscan = false;
//		iscan = player.getLevel() >= cfg.getLevelLimit() ? true : false;	
//		return iscan;
//		
//	}
	
	
	
	
//	public ActivityComResult takeGift(Player player,
//			ActivityExChangeTypeEnum countType, String subItemId) {
//		ActivityExchangeTypeItemHolder dataHolder = ActivityExchangeTypeItemHolder.getInstance();
//
//		ActivityExchangeTypeItem dataItem = dataHolder.getItem(player.getUserId(), countType);
//		ActivityComResult result = ActivityComResult.newInstance(false);
//
//		// 未激活
//		if (dataItem == null) {
//			result.setReason("活动尚未开启");
//
//		} else {
//			ActivityExchangeTypeSubItem targetItem = null;
//
//			List<ActivityExchangeTypeSubItem> subItemList = dataItem.getSubItemList();
//			for (ActivityExchangeTypeSubItem itemTmp : subItemList) {
//				if (StringUtils.equals(itemTmp.getCfgId(), subItemId)) {
//					targetItem = itemTmp;
//					break;
//				}
//			}
//			
//			if(targetItem == null){
//				result.setReason("找不到子活动类型的数据");
//				return result;
//			}
//			
//			if (isCanTaken(player,targetItem,true)) {
//				takeGift(player, targetItem);
//				result.setSuccess(true);
//				dataHolder.updateItem(player, dataItem);
//			}else{
//				result.setSuccess(false);
//				result.setReason("不满足兑换条件");
//			}
//
//		}
//
//		return result;
//	}
	
	
	
	

	@Override
	public void updateRedPoint(Player player, String eNum) {
//		ActivityExchangeTypeItemHolder activityCountTypeItemHolder = new ActivityExchangeTypeItemHolder();
//		ActivityExchangeTypeCfg cfg = ActivityExchangeTypeCfgDAO.getInstance().getCfgById(eNum);
//		if(cfg == null ){
//			return;
//		}
//		ActivityExChangeTypeEnum exchangeEnum = ActivityExChangeTypeEnum.getById(cfg.getEnumId());
//		if(exchangeEnum == null){
//			GameLog.error(LogModule.ComActivityExchange, player.getUserId(), "心跳传入id获得的页签枚举无法找到活动枚举", null);
//			return;
//		}
//		ActivityExchangeTypeItem dataItem = activityCountTypeItemHolder.getItem(player.getUserId(),exchangeEnum);
//		if(dataItem == null){
//			GameLog.error(LogModule.ComActivityExchange, player.getUserId(), "心跳传入id获得的页签枚举无法找到活动数据", null);
//			return;
//		}
//		if(!dataItem.isTouchRedPoint()){
//			dataItem.setTouchRedPoint(true);			
//		}
//		
//		List<ActivityExchangeTypeSubItem> exchangeSubitemlist= dataItem.getSubItemList();
//		for(ActivityExchangeTypeSubItem subitem:exchangeSubitemlist){
//			if(ActivityFortuneCatTypeMgr.getInstance().isCanTaken(player, subitem,false)){
//				if(dataItem.getHistoryRedPoint().contains(subitem.getCfgId())){
//					continue;
//				}
//				dataItem.getHistoryRedPoint().add(subitem.getCfgId());
//			}
//		}		
//		activityCountTypeItemHolder.updateItem(player, dataItem);
	}

}
