package com.playerdata.activity.exChangeType;

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
import com.playerdata.activity.ActivityRedPointEnum;
import com.playerdata.activity.ActivityRedPointUpdate;
import com.playerdata.activity.countType.ActivityCountTypeEnum;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeItemHolder;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfg;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfgDAO;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeDropCfg;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeDropCfgDAO;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeSubCfg;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeSubCfgDAO;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItem;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItemHolder;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeSubItem;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.copy.cfg.CopyCfg;
import com.rwbase.dao.copy.pojo.ItemInfo;


public class ActivityExchangeTypeMgr implements ActivityRedPointUpdate{

	private static ActivityExchangeTypeMgr instance = new ActivityExchangeTypeMgr();
	public static final Random random = new Random();
	private Map<Integer, Integer> idAndNumMap =new HashMap<Integer, Integer>();

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
			if(ActivityTypeHelper.isNewDayHourOfActivity(5,targetItem.getLasttime())){
				targetItem.setLasttime(System.currentTimeMillis());
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
	
	/**
	 * 
	 * @param player
	 * @param targetItem
	 * @param isspend 判断并消耗用true；仅判断是否可以触发红点用false
	 * @return
	 */
	public boolean isCanTaken(Player player,ActivityExchangeTypeSubItem targetItem,boolean isspend) {
		ActivityExchangeTypeSubCfg activityExchangeTypeSubCfg = ActivityExchangeTypeSubCfgDAO.getInstance().getById(targetItem.getCfgId());
		if(activityExchangeTypeSubCfg== null){
			GameLog.error(LogModule.ComActivityExchange, null, "通用活动找不到奖励配置文件", null);
			return false;
		}
		if(targetItem.getTime() >= activityExchangeTypeSubCfg.getTime()){
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
		
		if(isspend){
			spendItem(exchangeNeedslist,player);
		}	
		return true;
	}
	
	private void spendItem(Map<String, String> exchangeNeedslist,Player player){
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
	
	
	/**
	 * 根据传入的玩家和副本，额外获得兑换道具;
	 * @param player  玩家等级是否足够
	 * @param copyCfg  战斗场景是否有掉落
	 */
	public Map<Integer, Integer> AddItemOfExchangeActivity(Player player, CopyCfg copyCfg) {
		idAndNumMap = new HashMap<Integer, Integer>();
		List<ActivityExchangeTypeCfg> allCfgList = ActivityExchangeTypeCfgDAO.getInstance().getAllCfg();
		for (ActivityExchangeTypeCfg activityExchangeTypeCfg : allCfgList) {// 遍历所有的活动
			if (!isDropOpen(activityExchangeTypeCfg)) {
				// 活动未开启
				continue;
			}
			if(player.getLevel() < activityExchangeTypeCfg.getLevelLimit()){
				//等级不足
				continue;
			}
			List<ActivityExchangeTypeDropCfg> dropCfgList = ActivityExchangeTypeDropCfgDAO.getInstance().getByParentId(activityExchangeTypeCfg.getId());
			for(ActivityExchangeTypeDropCfg cfg : dropCfgList){//遍历单个活动可能对应的所有掉落道具类型
				Map<Integer, Integer[]> map = cfg.getDropMap();
				
				if(map.get(copyCfg.getLevelType()) != null){//该掉落配置表的该条记录适合此类战斗场景
					Integer[] numAndProbability =map.get(copyCfg.getLevelType());
					
					if(numAndProbability.length != 2){
						GameLog.error("兑换币配置文件错误", player.getUserId(), "物品掉落没有生成几率和数量,str=" + numAndProbability, null);
						idAndNumMap = null;
						return idAndNumMap;
					}
					if(random.nextInt(10000)<=numAndProbability[1]){
//						player.getItemBagMgr().addItem(Integer.parseInt(cfg.getItemId()), numAndProbability[0]);
						idAndNumMap.put(Integer.parseInt(cfg.getItemId()),numAndProbability[0]);
					}
				}				
			}			
		}
		return idAndNumMap;
	}	
	
	/**
	 * 根据传入的玩家和副本，额外获得兑换道具，当前适用副本预掉落
	 * @param player  玩家等级是否足够
	 * @param copyCfg  战斗场景是否有掉落
	 */
	public void AddItemOfExchangeActivityBefore(Player player, CopyCfg copyCfg,List<ItemInfo> itemInfoList) {
		AddItemOfExchangeActivity(player, copyCfg);
		if(idAndNumMap == null){
			//没声场额外掉落
			return;
		}
		for(Map.Entry<Integer, Integer> entry:idAndNumMap.entrySet()){
			ItemInfo itemInfo = new ItemInfo();
			itemInfo.setItemID(entry.getKey());
			itemInfo.setItemNum(entry.getValue());
			itemInfoList.add(itemInfo);
			
		}	
	}

	public boolean isDropOpen(ActivityExchangeTypeCfg activityExchangeTypeCfg) {
		if (activityExchangeTypeCfg != null) {
//			if(player.getLevel() < activityCountTypeCfg.getLevelLimit()){
//				return false;
//			}
			long startTime = activityExchangeTypeCfg.getDropStartTime();
			long endTime = activityExchangeTypeCfg.getDropEndTime();
			long currentTime = System.currentTimeMillis();
			return currentTime < endTime && currentTime > startTime;
		}
		return false;
	}

	@Override
	public void updateRedPoint(Player player, ActivityRedPointEnum eNum) {
		ActivityExchangeTypeItemHolder activityCountTypeItemHolder = new ActivityExchangeTypeItemHolder();
		ActivityExChangeTypeEnum exchangeEnum = ActivityExChangeTypeEnum.getById(eNum.getCfgId());
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
			if(ActivityExchangeTypeMgr.getInstance().isCanTaken(player, subitem,false)){
				if(dataItem.getHistoryRedPoint().contains(subitem.getCfgId())){
					continue;
				}
				dataItem.getHistoryRedPoint().add(subitem.getCfgId());
			}
		}		
		activityCountTypeItemHolder.updateItem(player, dataItem);
	}

}
