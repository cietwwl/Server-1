package com.playerdata.activity.dailyCharge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.ActivityRedPointUpdate;
import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeCfg;
import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeCfgDAO;
import com.playerdata.activity.dailyCharge.data.ActivityDailyRechargeTypeItemHolder;
import com.playerdata.activity.exChangeType.ActivityExChangeTypeEnum;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfg;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfgDAO;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeSubCfg;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeSubCfgDAO;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItem;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItemHolder;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeSubItem;


public class ActivityDailyRechargeTypeMgr implements ActivityRedPointUpdate{

	private static ActivityDailyRechargeTypeMgr instance = new ActivityDailyRechargeTypeMgr();

	public static ActivityDailyRechargeTypeMgr getInstance() {
		return instance;
	}

	public void synData(Player player) {
		ActivityDailyRechargeTypeItemHolder.getInstance().synAllData(player);
	}
	
	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空 */
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
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
			ActivityExChangeTypeEnum  activityExChangeTypeEnum = ActivityExChangeTypeEnum.getById(activityExchangeTypeCfg.getEnumId());
			if (activityExChangeTypeEnum == null) {
				GameLog.error(LogModule.ComActivityExchange, player.getUserId(), "配置表开启，但找不到枚举", null);
				continue;
			}
			ActivityExchangeTypeItem targetItem = dataHolder.getItem(player.getUserId(), activityExChangeTypeEnum);// 已在之前生成数据的活动
			if (targetItem == null) {						
				targetItem = ActivityExchangeTypeCfgDAO.getInstance().newItem(player, activityExchangeTypeCfg);// 生成新开启活动的数据
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
	
	public boolean isLevelEnough(Player player,ActivityExchangeTypeCfg cfg){
		boolean iscan = false;
		iscan = player.getLevel() >= cfg.getLevelLimit() ? true : false;	
		return iscan;
		
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
			
			if (isCanTaken(player,targetItem,true)) {
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
	
	private void takeGift(Player player, ActivityExchangeTypeSubItem targetItem) {
		ActivityExchangeTypeSubCfg subCfg = ActivityExchangeTypeSubCfgDAO.getInstance().getById(targetItem.getCfgId());
		if(subCfg == null){
			GameLog.error(LogModule.ComActivityExchange, null, "通用活动找不到奖励配置文件", null);
			return;
		}
		targetItem.setTime(targetItem.getTime()+1);
//		ComGiftMgr.getInstance().addGiftById(player, subCfg.getAwardGift());
		
		String[] str = subCfg.getAwardGift().split("_");
		player.getItemBagMgr().addItem(Integer.parseInt(str[0]),Integer.parseInt(str[1]));
	}

	@Override
	public void updateRedPoint(Player player, String eNum) {
		ActivityDailyRechargeTypeItemHolder activityCountTypeItemHolder = new ActivityDailyRechargeTypeItemHolder();
		ActivityDailyChargeCfg cfg = ActivityDailyChargeCfgDAO.getInstance().getCfgById(eNum);
		if(cfg == null ){
			return;
		}
		ActivityExChangeTypeEnum exchangeEnum = ActivityExChangeTypeEnum.getById(cfg.getEnumId());
		if(exchangeEnum == null){
			GameLog.error(LogModule.ComActivityExchange, player.getUserId(), "心跳传入id获得的页签枚举无法找到活动枚举", null);
			return;
		}
		ActivityExchangeTypeItem dataItem = activityCountTypeItemHolder.getItem(player.getUserId(),exchangeEnum);
		if(dataItem == null){
			GameLog.error(LogModule.ComActivityExchange, player.getUserId(), "心跳传入id获得的页签枚举无法找到活动数据", null);
			return;
		}
		if(!dataItem.isTouchRedPoint()){
			dataItem.setTouchRedPoint(true);			
		}
		
		List<ActivityExchangeTypeSubItem> exchangeSubitemlist= dataItem.getSubItemList();
		for(ActivityExchangeTypeSubItem subitem:exchangeSubitemlist){
			if(ActivityDailyRechargeTypeMgr.getInstance().isCanTaken(player, subitem,false)){
				if(dataItem.getHistoryRedPoint().contains(subitem.getCfgId())){
					continue;
				}
				dataItem.getHistoryRedPoint().add(subitem.getCfgId());
			}
		}		
		activityCountTypeItemHolder.updateItem(player, dataItem);
	}
}
