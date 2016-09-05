package com.playerdata.activity.dailyDiscountType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.ActivityTypeHelper;
import com.playerdata.activity.ActivityRedPointUpdate;
import com.playerdata.activity.countType.ActivityCountTypeEnum;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeItemHolder;
import com.playerdata.activity.dailyDiscountType.cfg.ActivityDailyDiscountItemCfg;
import com.playerdata.activity.dailyDiscountType.cfg.ActivityDailyDiscountItemCfgDao;
import com.playerdata.activity.dailyDiscountType.cfg.ActivityDailyDiscountTypeCfg;
import com.playerdata.activity.dailyDiscountType.cfg.ActivityDailyDiscountTypeCfgDAO;
import com.playerdata.activity.dailyDiscountType.cfg.ActivityDailyDiscountTypeSubCfgDAO;
import com.playerdata.activity.dailyDiscountType.data.ActivityDailyDiscountTypeItem;
import com.playerdata.activity.dailyDiscountType.data.ActivityDailyDiscountTypeItemHolder;
import com.playerdata.activity.dailyDiscountType.data.ActivityDailyDiscountTypeSubItem;
import com.playerdata.activity.rankType.cfg.ActivityRankTypeCfg;
import com.playerdata.activity.rankType.cfg.ActivityRankTypeCfgDAO;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.common.enu.eSpecialItemId;


public class ActivityDailyDiscountTypeMgr implements ActivityRedPointUpdate{

	private static ActivityDailyDiscountTypeMgr instance = new ActivityDailyDiscountTypeMgr();


	public static ActivityDailyDiscountTypeMgr getInstance() {
		return instance;
	}
	
	public void synCountTypeData(Player player) {
		ActivityDailyDiscountTypeItemHolder.getInstance().synAllData(player);
	}



	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空 */
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
		checkCfgVersion(player);	
		checkOtherDay(player);
		checkClose(player);

	}

	
	
	
	private void checkNewOpen(Player player) {
		ActivityDailyDiscountTypeItemHolder dataHolder = ActivityDailyDiscountTypeItemHolder.getInstance();
		ActivityDailyDiscountTypeCfgDAO activityDailyDiscountTypeCfgDAO = ActivityDailyDiscountTypeCfgDAO.getInstance();
		List<ActivityDailyDiscountTypeCfg> activitydailydiscountcfglist = ActivityDailyDiscountTypeCfgDAO.getInstance().getAllCfg();
		ArrayList<ActivityDailyDiscountTypeItem> addItemList = null;
		for (ActivityDailyDiscountTypeCfg activityCountTypeCfg : activitydailydiscountcfglist) {// 遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if (!isOpen(activityCountTypeCfg)) {
				// 活动未开启
				continue;
			}
			ActivityDailyDiscountTypeEnum countTypeEnum = ActivityDailyDiscountTypeEnum.getById(activityCountTypeCfg.getEnumId());
			if (countTypeEnum == null) {
				GameLog.error("ActivityDailyDisCountTypeMgr", "#checkNewOpen()", "找不到活动类型枚举：" + activityCountTypeCfg.getId());
				continue;
			}
			ActivityDailyDiscountTypeItem targetItem = dataHolder.getItem(player.getUserId(), countTypeEnum);// 已在之前生成数据的活动
			if (targetItem == null) {
						
				targetItem = activityDailyDiscountTypeCfgDAO.newItem(player, activityCountTypeCfg);// 生成新开启活动的数据
				if (targetItem == null) {
					GameLog.error("ActivityDailyDisCountTypeMgr", "#checkNewOpen()", "根据活动类型枚举找不到对应的cfg：" + activityCountTypeCfg.getId());
					continue;
				}
				if (addItemList == null) {
					addItemList = new ArrayList<ActivityDailyDiscountTypeItem>();
				}
				addItemList.add(targetItem);
			}
		}
		if (addItemList != null) {
			dataHolder.addItemList(player, addItemList);
		}
	}
	
	public boolean isOpen(ActivityDailyDiscountTypeCfg activityCountTypeCfg) {

		if (activityCountTypeCfg != null) {
			long startTime = activityCountTypeCfg.getStartTime();
			long endTime = activityCountTypeCfg.getEndTime();
			long currentTime = System.currentTimeMillis();
			return currentTime < endTime && currentTime >= startTime;
		}
		return false;
	}
	
	
	
	private void checkCfgVersion(Player player) {
		ActivityDailyDiscountTypeItemHolder dataHolder = ActivityDailyDiscountTypeItemHolder.getInstance();
		ActivityDailyDiscountTypeCfgDAO activityDailyDiscountTypeCfgDAO = ActivityDailyDiscountTypeCfgDAO.getInstance();
		List<ActivityDailyDiscountTypeItem> itemList = dataHolder.getItemList(player.getUserId());
		for (ActivityDailyDiscountTypeItem targetItem : itemList) {			
			ActivityDailyDiscountTypeCfg targetCfg = activityDailyDiscountTypeCfgDAO.getCfgByItem(targetItem);
			if(targetCfg == null){
				return;
			}			
			
			if (!StringUtils.equals(targetItem.getVersion(), targetCfg.getVersion())) {
				targetItem.reset(targetCfg);
				dataHolder.updateItem(player, targetItem);
			}
		}		
	}
	
	private void checkOtherDay(Player player) {
		ActivityDailyDiscountTypeItemHolder dataHolder = ActivityDailyDiscountTypeItemHolder.getInstance();
		List<ActivityDailyDiscountTypeItem> itemList = dataHolder.getItemList(player.getUserId());
		List<ActivityDailyDiscountTypeCfg> cfglist = ActivityDailyDiscountTypeCfgDAO.getInstance().getAllCfg();
		for (ActivityDailyDiscountTypeItem targetItem : itemList) {
			ActivityDailyDiscountTypeCfg cfgtmp = null;
			for(ActivityDailyDiscountTypeCfg cfg:cfglist){
				if(StringUtils.equals(cfg.getId(), targetItem.getCfgId())){
					cfgtmp = cfg;
					break;
				}
			}
			if(cfgtmp == null){
				//以前开过的活动现在没找到配置文件
				continue;
			}			
			if(ActivityTypeHelper.isNewDayHourOfActivity(5,targetItem.getLastTime())){
				targetItem.reset(cfgtmp);
				dataHolder.updateItem(player, targetItem);
			}
		}		
	}
	
	private void checkClose(Player player) {
		ActivityDailyDiscountTypeItemHolder dataHolder = ActivityDailyDiscountTypeItemHolder.getInstance();
		List<ActivityDailyDiscountTypeItem> itemList = dataHolder.getItemList(player.getUserId());

		for (ActivityDailyDiscountTypeItem activityDailyCountTypeItem : itemList) {// 每种活动
			if (isClose(activityDailyCountTypeItem)) {
				activityDailyCountTypeItem.setClosed(true);
				activityDailyCountTypeItem.setTouchRedPoint(true);
				dataHolder.updateItem(player, activityDailyCountTypeItem);
			}
		}
	}
	
	private boolean isClose(
			ActivityDailyDiscountTypeItem activityDailyCountTypeItem) {
		ActivityDailyDiscountTypeCfg cfgById = ActivityDailyDiscountTypeCfgDAO
				.getInstance().getCfgById(activityDailyCountTypeItem.getCfgId());
		if (cfgById == null) {
			return false;
		}
		long endTime = cfgById.getEndTime();
		long currentTime = System.currentTimeMillis();
		return currentTime > endTime;
	}
	

		
	public ActivityComResult buyItem(Player player, ActivityDailyDiscountTypeCfg cfg, String subItemId) {
		ActivityDailyDiscountTypeItemHolder dataHolder = ActivityDailyDiscountTypeItemHolder.getInstance();

		ActivityDailyDiscountTypeItem dataItem = dataHolder.getItem(player.getUserId(),ActivityDailyDiscountTypeEnum.getById(cfg.getEnumId()));
		ActivityComResult result = ActivityComResult.newInstance(false);

		// 未激活
		if (dataItem == null) {
			result.setReason("活动尚未开启");
			result.setSuccess(false);
		} else {
			ActivityDailyDiscountTypeSubItem targetItem = null;
			List<ActivityDailyDiscountTypeSubItem> subItemList = dataItem.getSubItemList();
			ActivityDailyDiscountItemCfg itemCfg = null;
			List<ActivityDailyDiscountItemCfg> itemCfgList = ActivityDailyDiscountItemCfgDao.getInstance().getAllCfg();
			
			
			for (ActivityDailyDiscountTypeSubItem itemTmp : subItemList) {
				if (StringUtils.equals(itemTmp.getCfgId(), subItemId)) {
					targetItem = itemTmp;
					break;
				}
			}
			if(targetItem == null){
				result.setReason("数据异常，请重登陆");
				result.setSuccess(false);
				return result;
			}
			
			for(ActivityDailyDiscountItemCfg itemCfgTmp : itemCfgList){
				if(StringUtils.equals(itemCfgTmp.getId(), subItemId)){
					itemCfg = itemCfgTmp;
					break;
				}
			}
			if(itemCfg == null){
				result.setReason("异常，请联系游戏官方");
				result.setSuccess(false);
				return result;		
			}			
			
			if(!isLevelEnough(player, cfg)){
				result.setReason("等级不足");
				result.setSuccess(false);
				return result;
			}
			if(!isCountEnough(targetItem.getCount(),itemCfg)){
				result.setReason("次数不足，请隔天刷新");
				result.setSuccess(false);
				return result;				
			}
			if(!isGoldEnough(player,itemCfg)){
				result.setReason("钻石不足");
				result.setSuccess(false);
				return result;
			}else{
				Map<Integer, Integer> map = new HashMap<Integer, Integer>();
				map.put(eSpecialItemId.Gold.getValue(), -itemCfg.getPriceAfterDiscount());
				player.getItemBagMgr().useLikeBoxItem(null, null, map);
			}
			getItem(player, targetItem);
			dataHolder.updateItem(player, dataItem);
			result.setReason("购买成功");
			result.setSuccess(true);
			
		}
		return result;
	}
	


	public boolean isCountEnough(int count, ActivityDailyDiscountItemCfg cfg) {
		if(count < cfg.getCountLimit()){
			return true;
		}
		return false;
	}

	public boolean isLevelEnough(Player player,ActivityDailyDiscountTypeCfg cfg) {
		if(cfg == null){
//			GameLog.error("activityDailyDisCountTypeMgr", "list", "配置文件总表错误" );
			return false;
		}
		if(player.getLevel() < cfg.getLevelLimit()){
			return false;
		}		
		return true;
	}
	
	public boolean isGoldEnough(Player player,ActivityDailyDiscountItemCfg itemCfg){
		if(player.getUserGameDataMgr().isEnoughCurrency(eSpecialItemId.Gold, itemCfg.getPriceAfterDiscount())){
			return true;
		}
		return false;
	}
	
	
	private void getItem(Player player, ActivityDailyDiscountTypeSubItem targetItem) {
		ActivityDailyDiscountItemCfg subCfg = ActivityDailyDiscountItemCfgDao.getInstance().getCfgById(targetItem.getCfgId());
		if(subCfg == null){
			return;
		}
		targetItem.setCount(targetItem.getCount()+1);
		player.getItemBagMgr().addItem(targetItem.getItemId(),targetItem.getItemNum());
	}

	@Override
	public void updateRedPoint(Player player, String eNum) {
		ActivityDailyDiscountTypeItemHolder activityCountTypeItemHolder = new ActivityDailyDiscountTypeItemHolder();
		ActivityDailyDiscountTypeCfg cfg = ActivityDailyDiscountTypeCfgDAO.getInstance().getCfgById(eNum);
		if(cfg == null ){
			return;
		}
		ActivityDailyDiscountTypeEnum dailyDiscountEnum = ActivityDailyDiscountTypeEnum.getById(cfg.getEnumId());//cfg
		if(dailyDiscountEnum == null){
			return;
		}
		ActivityDailyDiscountTypeItem dataItem = activityCountTypeItemHolder.getItem(player.getUserId(),dailyDiscountEnum);
		if(dataItem == null){
			return;
		}
		if(!dataItem.isTouchRedPoint()){
			dataItem.setTouchRedPoint(true);
			activityCountTypeItemHolder.updateItem(player, dataItem);
		}	
		
	}	
	
}
