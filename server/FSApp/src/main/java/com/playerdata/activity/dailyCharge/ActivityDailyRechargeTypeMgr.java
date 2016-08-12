package com.playerdata.activity.dailyCharge;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.ActivityRedPointUpdate;
import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeCfg;
import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeCfgDAO;
import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeSubCfg;
import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeSubCfgDAO;
import com.playerdata.activity.dailyCharge.data.ActivityDailyRechargeTypeItem;
import com.playerdata.activity.dailyCharge.data.ActivityDailyRechargeTypeItemHolder;
import com.playerdata.activity.dailyCharge.data.ActivityDailyRechargeTypeSubItem;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfg;


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
		
	}

	public boolean isLevelEnough(Player player,ActivityExchangeTypeCfg cfg){
		boolean iscan = false;
		iscan = player.getLevel() >= cfg.getLevelLimit() ? true : false;	
		return iscan;
		
	}
	
	public ActivityComResult takeGift(Player player, String activityID, String subItemId) {
		ActivityDailyRechargeTypeItemHolder dataHolder = ActivityDailyRechargeTypeItemHolder.getInstance();

		ActivityDailyRechargeTypeItem dataItem = dataHolder.getItem(player.getUserId(), activityID);
		ActivityComResult result = ActivityComResult.newInstance(false);
		// 未激活
		if (dataItem == null) {
			result.setReason("活动尚未开启");
		} else {
			ActivityDailyRechargeTypeSubItem targetItem = null;
			List<ActivityDailyRechargeTypeSubItem> subItemList = dataItem.getSubItemList();
			for (ActivityDailyRechargeTypeSubItem itemTmp : subItemList) {
				if (StringUtils.equals(itemTmp.getCfgId(), subItemId)) {
					targetItem = itemTmp;
					break;
				}
			}
			if(targetItem == null){
				result.setReason("找不到子活动类型的数据");
				return result;
			}
			
			// 判断领取条件
			if (true) {
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
	
	private void takeGift(Player player, ActivityDailyRechargeTypeSubItem targetItem){
		ActivityDailyChargeSubCfg subCfg = ActivityDailyChargeSubCfgDAO.getInstance().getCfgById(targetItem.getCfgId());
		if(subCfg == null){
			GameLog.error(LogModule.ComActivityDailyRecharge, null, "通用活动找不到奖励配置文件", null);
			return;
		}
		targetItem.setGet(true);
		player.getItemBagMgr().addItem(Integer.parseInt(subCfg.getGiftId()), 1);
	}

	@Override
	public void updateRedPoint(Player player, String eNum) {
		ActivityDailyRechargeTypeItemHolder activityDailyRechargeItemHolder = ActivityDailyRechargeTypeItemHolder.getInstance();
		ActivityDailyChargeCfg cfg = ActivityDailyChargeCfgDAO.getInstance().getCfgById(eNum);
		if(cfg == null ){
			return;
		}
		ActivityDailyRechargeTypeItem dataItem = activityDailyRechargeItemHolder.getItem(player.getUserId(), eNum);
		if(dataItem == null){
			GameLog.error(LogModule.ComActivityDailyRecharge, player.getUserId(), "心跳传入id获得的页签枚举无法找到活动数据", null);
			return;
		}
		
		if(!dataItem.isHasViewed()){
			dataItem.setHasViewed(true);
		}
		activityDailyRechargeItemHolder.updateItem(player, dataItem);
	}
}
