package com.playerdata.activity.countType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.ActivityRedPointUpdate;
import com.playerdata.activity.VitalityType.ActivityVitalityTypeMgr;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfg;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfgDAO;
import com.playerdata.activity.countType.cfg.ActivityCountTypeSubCfg;
import com.playerdata.activity.countType.cfg.ActivityCountTypeSubCfgDAO;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeItemHolder;
import com.playerdata.activity.countType.data.ActivityCountTypeSubItem;
import com.playerdata.activity.dailyCharge.ActivityDailyRechargeTypeMgr;
import com.playerdata.activity.dailyCountType.ActivityDailyTypeMgr;
import com.playerdata.activity.dailyDiscountType.ActivityDailyDiscountTypeMgr;
import com.playerdata.activity.exChangeType.ActivityExchangeTypeMgr;
import com.playerdata.activity.fortuneCatType.ActivityFortuneCatTypeMgr;
import com.playerdata.activity.limitHeroType.ActivityLimitHeroTypeMgr;
import com.playerdata.activity.rankType.ActivityRankTypeMgr;
import com.playerdata.activity.rateType.ActivityRateTypeMgr;
import com.playerdata.activity.redEnvelopeType.ActivityRedEnvelopeTypeMgr;
import com.playerdata.activity.retrieve.ActivityRetrieveTypeMgr;
import com.playerdata.activity.timeCardType.ActivityTimeCardTypeMgr;
import com.playerdata.activity.timeCountType.ActivityTimeCountTypeMgr;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.dataaccess.mapitem.MapItemValidateParam;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.util.DateUtils;

public class ActivityCountTypeMgr implements ActivityRedPointUpdate {

	private static ActivityCountTypeMgr instance = new ActivityCountTypeMgr();

	private final static int MAKEUPEMAIL = 10055;

	public static ActivityCountTypeMgr getInstance() {
		return instance;
	}

	public void synCountTypeData(Player player) {
		if(isOpen(System.currentTimeMillis())){
			ActivityCountTypeItemHolder.getInstance().synAllData(player);
		}		
	}

	/**
	 * 
	 * @param player
	 *            通用活动数据同步,生成活动奖励空数据；应置于所有通用活动的统计之前；可后期放入初始化模块
	 */
	public void checkActivity(Player player) {
		ActivityCountTypeMgr.getInstance().checkActivityOpen(player);
		ActivityTimeCardTypeMgr.getInstance().checkActivityOpen(player);
		ActivityTimeCountTypeMgr.getInstance().checkActivityOpen(player);
		ActivityRateTypeMgr.getInstance().checkActivityOpen(player);
		ActivityDailyTypeMgr.getInstance().checkActivityOpen(player);
		ActivityExchangeTypeMgr.getInstance().checkActivityOpen(player);
		ActivityVitalityTypeMgr.getInstance().checkActivityOpen(player);
		ActivityRankTypeMgr.getInstance().checkActivityOpen(player);
		ActivityDailyDiscountTypeMgr.getInstance().checkActivityOpen(player);
		ActivityRedEnvelopeTypeMgr.getInstance().checkActivityOpen(player);
		ActivityFortuneCatTypeMgr.getInstance().checkActivityOpen(player);
		ActivityDailyRechargeTypeMgr.getInstance().checkActivityOpen(player);
		ActivityLimitHeroTypeMgr.getInstance().checkActivityOpen(player);
		ActivityRetrieveTypeMgr.getInstance().checkActivityOpen(player);
	}

	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空 */
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
		checkCfgVersion(player);
		checkClose(player);

	}

	/**
	 * 
	 * @param player
	 * 同类型活动同时激活两个以上，会add同样主键到数据报错；风险较高，需增加检查配置的方法；
	 * 也可以将方法里的addlist改为add
	 */
	private void checkNewOpen(Player player) {
//		RoleExtPropertyStoreCache<ActivityCountTypeItem> storeCache = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.ACTIVITY_COUNTTYPE, ActivityCountTypeItem.class);
//		PlayerExtPropertyStore<ActivityCountTypeItem> store = null;
		String userId= player.getUserId();
//		List<ActivityCountTypeItem> addList = null;
//			store = storeCache.getStore(userId);
		creatItems(userId,true);	
//			if(store != null&&addList != null){
//				store.addItem(addList);
//			}
	}

	public List<ActivityCountTypeItem> creatItems(String userId,boolean isHasPlayer) {
		RoleExtPropertyStoreCache<ActivityCountTypeItem> storeCache = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.ACTIVITY_COUNTTYPE, ActivityCountTypeItem.class);
		RoleExtPropertyStore<ActivityCountTypeItem> store = null;
		
		ActivityCountTypeCfgDAO activityCountTypeCfgDAO = ActivityCountTypeCfgDAO.getInstance();
		List<ActivityCountTypeCfg> allCfgList = ActivityCountTypeCfgDAO.getInstance().getAllCfg();
		ArrayList<ActivityCountTypeItem> addItemList = null;
		for (ActivityCountTypeCfg cfg : allCfgList) {// 遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if (!activityCountTypeCfgDAO.isOpen(cfg)) {
				// 活动未开启
				continue;
			}
			ActivityCountTypeEnum countTypeEnum = ActivityCountTypeEnum.getById(cfg.getEnumId());
			if (countTypeEnum == null) {
				continue;
			}
			int id = Integer.parseInt(countTypeEnum.getCfgId());
			if(isHasPlayer){
				try {
					store = storeCache.getStore(userId);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (store != null) {
					if (store.get(id) != null) {
						continue;
					}
				}				
			}
						
			ActivityCountTypeItem item = new ActivityCountTypeItem();
			item.setId(id);
			item.setCfgId(cfg.getId());
			item.setEnumId(cfg.getEnumId());
			item.setUserId(userId);
			item.setVersion(cfg.getVersion());
			List<ActivityCountTypeSubItem> subItemList = new ArrayList<ActivityCountTypeSubItem>();
			List<ActivityCountTypeSubCfg> subItemCfgList = ActivityCountTypeSubCfgDAO.getInstance().getByParentCfgId(cfg.getId());
			if (subItemCfgList == null) {
				subItemCfgList = new ArrayList<ActivityCountTypeSubCfg>();
			}
			for (ActivityCountTypeSubCfg subCfg : subItemCfgList) {
				ActivityCountTypeSubItem subItem = new ActivityCountTypeSubItem();
				subItem.setCfgId(subCfg.getId());
				subItem.setCount(subCfg.getAwardCount());
				subItemList.add(subItem);
			}
			item.setSubItemList(subItemList);
			if (addItemList == null) {
				addItemList = new ArrayList<ActivityCountTypeItem>();
			}
			addItemList.add(item);
		}
		if(isHasPlayer&&addItemList != null){
			try {
				store.addItem(addItemList);
			} catch (DuplicatedKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return addItemList;
	}

	private void checkCfgVersion(Player player) {
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder.getInstance();
		ActivityCountTypeCfgDAO dao = ActivityCountTypeCfgDAO.getInstance();		
		List<ActivityCountTypeItem> itemList = null;//dataHolder.getItemList(player.getUserId());
		List<ActivityCountTypeCfg> cfgList = dao.getAllCfg();
		for(ActivityCountTypeCfg cfg : cfgList){
			if(!dao.isOpen(cfg)){
				continue;
			}
			if(itemList == null){
				itemList = dataHolder.getItemList(player.getUserId());
			}
			ActivityCountTypeItem freshItem = null;
			for(ActivityCountTypeItem item : itemList){
				if(StringUtils.equals(item.getEnumId(), cfg.getEnumId())&&!StringUtils.equals(item.getVersion(), cfg.getVersion())){
					freshItem = item;
				}
			}
			if(freshItem == null){
				continue;
			}
			freshItem.reset(cfg, dao.newItemList(cfg));
			dataHolder.updateItem(player, freshItem);
		}
	}

	private void checkClose(Player player) {
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder.getInstance();
		List<ActivityCountTypeItem> itemList = null;//dataHolder.getItemList(player.getUserId());
		ActivityCountTypeCfgDAO dao = ActivityCountTypeCfgDAO.getInstance();
		List<ActivityCountTypeCfg> cfgList = dao.getAllCfg();
		long createTime = player.getUserDataMgr().getCreateTime();
		long currentTime = DateUtils.getSecondLevelMillis();
		for(ActivityCountTypeCfg cfg : cfgList){
			if(dao.isOpen(cfg)){//配置开启
				continue;
			}
			if(createTime>cfg.getEndTime()){//配置过旧
				continue;
			}
			if(currentTime < cfg.getStartTime()){//配置过新
				continue;
			}
			if(itemList == null){
				itemList = dataHolder.getItemList(player.getUserId());
			}
			ActivityCountTypeItem closeItem = null;
			for(ActivityCountTypeItem item : itemList){
				if(StringUtils.equals(item.getVersion(), cfg.getVersion())&&StringUtils.equals(item.getEnumId(), cfg.getEnumId())){
					closeItem = item;
					break;
				}			
			}
			if(closeItem == null){
				continue;
			}			
			if (!closeItem.isClosed()) {
				List<ActivityCountTypeSubItem> list = closeItem.getSubItemList();
				sendEmailIfGiftNotTaken(player, closeItem, list);
				closeItem.setClosed(true);
				closeItem.setTouchRedPoint(true);
				dataHolder.updateItem(player, closeItem);
			}			
		}
	}

	private void sendEmailIfGiftNotTaken(Player player, ActivityCountTypeItem activityCountTypeItem, List<ActivityCountTypeSubItem> list) {
		ActivityCountTypeSubCfgDAO activityCountTypeSubCfgDAO = ActivityCountTypeSubCfgDAO.getInstance();
		ComGiftMgr comGiftMgr = ComGiftMgr.getInstance();
		for (ActivityCountTypeSubItem subItem : list) {// 配置表里的每种奖励
			ActivityCountTypeSubCfg subItemCfg = activityCountTypeSubCfgDAO.getById(subItem.getCfgId());
			if (subItemCfg == null) {
				continue;
			}
			if (!subItem.isTaken() && activityCountTypeItem.getCount() >= subItemCfg.getAwardCount()) {

				boolean isAdd = comGiftMgr.addGiftTOEmailById(player, subItemCfg.getAwardGift(), MAKEUPEMAIL + "", subItemCfg.getEmailTitle());
				if (isAdd) {
					subItem.setTaken(true);
				}
			}
		}
	}

	public void addCount(Player player, ActivityCountTypeEnum countType, int countadd) {
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder.getInstance();

		ActivityCountTypeItem dataItem = dataHolder.getItem(player.getUserId(), countType);
		if (dataItem == null) {
			return;
		}
		dataItem.setCount(dataItem.getCount() + countadd);
		dataHolder.updateItem(player, dataItem);
	}

	public ActivityComResult takeGift(Player player, ActivityCountTypeEnum countType, String subItemId) {
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder.getInstance();

		ActivityCountTypeItem dataItem = dataHolder.getItem(player.getUserId(), countType);
		ActivityComResult result = ActivityComResult.newInstance(false);

		// 未激活
		if (dataItem == null) {
			result.setReason("活动尚未开启");

		} else {
			ActivityCountTypeSubItem targetItem = null;

			List<ActivityCountTypeSubItem> subItemList = dataItem.getSubItemList();
			for (ActivityCountTypeSubItem itemTmp : subItemList) {
				if (StringUtils.equals(itemTmp.getCfgId(), subItemId)) {
					targetItem = itemTmp;
					break;
				}
			}
			if (targetItem != null && !targetItem.isTaken()) {
				takeGift(player, targetItem);
				result.setSuccess(true);
				dataHolder.updateItem(player, dataItem);
			}

		}

		return result;
	}

	private void takeGift(Player player, ActivityCountTypeSubItem targetItem) {
		ActivityCountTypeSubCfg subCfg = ActivityCountTypeSubCfgDAO.getInstance().getById(targetItem.getCfgId());
		targetItem.setTaken(true);
		if (subCfg == null) {
			// logger
			return;
		}
		ComGiftMgr.getInstance().addGiftById(player, subCfg.getAwardGift());

	}

	public void updateRedPoint(Player player, String target) {
		ActivityCountTypeItemHolder activityCountTypeItemHolder = new ActivityCountTypeItemHolder();
		ActivityCountTypeCfgDAO activityCountTypeCfgDAO = ActivityCountTypeCfgDAO.getInstance();
		ActivityCountTypeCfg cfg = activityCountTypeCfgDAO.getCfgById(target);
		if (cfg == null) {
			return;
		}
		update(player, cfg, activityCountTypeItemHolder);
		if (StringUtils.isEmpty(cfg.getGroup())) {
			// 获取同属一组group的cfg
			return;
		}
		ActivityCountTypeCfg cfgByGroup = null;
		for (ActivityCountTypeCfg cfgTmp : activityCountTypeCfgDAO.getAllCfg()) {
			if (StringUtils.equals(cfg.getGroup(), cfgTmp.getGroup()) && !StringUtils.equals(cfg.getEnumId(), cfgTmp.getEnumId())) {
				cfgByGroup = cfgTmp;
				break;
			}
		}
		if (cfgByGroup == null) {
			return;
		}
		update(player, cfgByGroup, activityCountTypeItemHolder);

	}

	private void update(Player player, ActivityCountTypeCfg cfg, ActivityCountTypeItemHolder activityCountTypeItemHolder) {
		ActivityCountTypeEnum eNum = ActivityCountTypeEnum.getById(cfg.getEnumId());
		if (eNum == null) {
			return;
		}
		ActivityCountTypeItem dataItem = activityCountTypeItemHolder.getItem(player.getUserId(), eNum);
		if (dataItem == null) {
			return;
		}
		if (!dataItem.isTouchRedPoint()) {
			dataItem.setTouchRedPoint(true);
			activityCountTypeItemHolder.updateItem(player, dataItem);
		}

	}

	public boolean isOpen(long param) {
		List<ActivityCountTypeCfg> allCfgList = ActivityCountTypeCfgDAO.getInstance().getAllCfg();
		for (ActivityCountTypeCfg cfg : allCfgList) {// 遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if (isOpen(cfg, param)) {
				// 活动未开启
				return true;
			}
		}
		return false;
	}

	private boolean isOpen(ActivityCountTypeCfg cfg, long currentTime) {
		long startTime = cfg.getStartTime();
		long endTime = cfg.getEndTime();
		return currentTime < endTime && currentTime >= startTime;
	}

}
