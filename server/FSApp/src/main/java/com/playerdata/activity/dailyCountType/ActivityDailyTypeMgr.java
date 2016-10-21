package com.playerdata.activity.dailyCountType;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.ActivityRedPointUpdate;
import com.playerdata.activity.ActivityTypeHelper;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfg;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfgDAO;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeSubItem;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeCfg;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeCfgDAO;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeSubCfg;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeSubCfgDAO;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItem;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItemHolder;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeSubItem;
import com.playerdata.activity.rateType.data.ActivityRateTypeItem;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.dataaccess.mapitem.MapItemValidateParam;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.util.DateUtils;

public class ActivityDailyTypeMgr implements ActivityRedPointUpdate {

	private static ActivityDailyTypeMgr instance = new ActivityDailyTypeMgr();

	private final static int MAKEUPEMAIL = 10055;

	public static ActivityDailyTypeMgr getInstance() {
		return instance;
	}

	public void synCountTypeData(Player player) {
		if(isOpen(System.currentTimeMillis())){
			ActivityDailyTypeItemHolder.getInstance().synAllData(player);
		}
		
	}

	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空 */
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
		checkCfgVersion(player);
		checkOtherDay(player);
		checkClose(player);

	}

	/**
	 * 
	 * @param player
	 *            配表如果同时开启，则会add第一个生效的数据，风险较低，需要一个检查配置的方法
	 */
	private void checkNewOpen(Player player) {

		String userId = player.getUserId();
//		List<ActivityDailyTypeItem> addList =null;
//		RoleExtPropertyStoreCache<ActivityDailyTypeItem> cach = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.ACTIVITY_DAILYTYPE, ActivityDailyTypeItem.class);
//		PlayerExtPropertyStore<ActivityDailyTypeItem> store = null;
//		try {
//			store = cach.getStore(userId);
		creatItems(userId, true);
//			if(addList != null){
//				store.addItem(addList);
//			}
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Throwable e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}		
	}

	public List<ActivityDailyTypeItem> creatItems(String userId, boolean isHasPlayer) {
		RoleExtPropertyStoreCache<ActivityDailyTypeItem> cach = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.ACTIVITY_DAILYTYPE, ActivityDailyTypeItem.class);
		RoleExtPropertyStore<ActivityDailyTypeItem> store = null;
		
		
		List<ActivityDailyTypeCfg> activityDailyTypeCfgList = ActivityDailyTypeCfgDAO.getInstance().getAllCfg();
		ActivityDailyTypeCfgDAO activityDailyTypeCfgDAO = ActivityDailyTypeCfgDAO.getInstance();
//		String itemId = ActivityDailyTypeHelper.getItemId(userId, ActivityDailyTypeEnum.Daily);
		int id = Integer.parseInt(ActivityDailyTypeEnum.Daily.getCfgId());
		List<ActivityDailyTypeItem> addItemList = null;
		for (ActivityDailyTypeCfg cfg : activityDailyTypeCfgList) {
			
			if (!activityDailyTypeCfgDAO.isOpen(cfg)) {
				continue;
			}
			if(isHasPlayer){
				try {
					store = cach.getStore(userId);
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
			
			
			ActivityDailyTypeItem item = new ActivityDailyTypeItem();
			item.setId(id);
			item.setUserId(userId);
			item.setCfgid(cfg.getId());
			item.setVersion(cfg.getVersion());
			item.setLastTime(System.currentTimeMillis());
			ActivityDailyTypeSubCfgDAO activityDailyTypeSubCfgDAO = ActivityDailyTypeSubCfgDAO.getInstance();
			List<ActivityDailyTypeSubItem> subItemList = new ArrayList<ActivityDailyTypeSubItem>();
			List<ActivityDailyTypeSubCfg> subCfgListByParentid = ActivityDailyTypeSubCfgDAO.getInstance().getCfgMapByParentid(cfg.getId());
			for (ActivityDailyTypeSubCfg subCfg : subCfgListByParentid) {
				if (!activityDailyTypeSubCfgDAO.isOpen(subCfg)) {
					// 该子类型活动当天没开启
					continue;
				}
				ActivityDailyTypeSubItem subitem = new ActivityDailyTypeSubItem();
				subitem.setCfgId(subCfg.getId());
				subitem.setCount(0);
				subitem.setTaken(false);
				subitem.setGiftId(subCfg.getGiftId());
				subItemList.add(subitem);
			}
			item.setSubItemList(subItemList);
			if (addItemList == null) {
				addItemList = new ArrayList<ActivityDailyTypeItem>();
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
		ActivityDailyTypeItemHolder dataHolder = ActivityDailyTypeItemHolder.getInstance();
		ActivityDailyTypeCfgDAO dao = ActivityDailyTypeCfgDAO.getInstance();
		List<ActivityDailyTypeItem> itemList = null;//dataHolder.getItemList(player.getUserId());
		List<ActivityDailyTypeCfg> cfgList = dao.getAllCfg();
		for(ActivityDailyTypeCfg cfg : cfgList){
			if(!dao.isOpen(cfg)){
				continue;
			}
			if(itemList == null){
				itemList = dataHolder.getItemList(player.getUserId());
			}
			ActivityDailyTypeItem freshItem = null;
			for(ActivityDailyTypeItem item : itemList){
				if(!StringUtils.equals(item.getVersion(), cfg.getVersion())){
					freshItem = item;
				}
			}
			if(freshItem == null){
				continue;
			}
			freshItem.reset(cfg);
			dataHolder.updateItem(player, freshItem);
		}
	}

	private void checkOtherDay(Player player) {
		ActivityDailyTypeItemHolder dataHolder = ActivityDailyTypeItemHolder.getInstance();
		List<ActivityDailyTypeItem> itemList = null;//dataHolder.getItemList(player.getUserId());
		ActivityDailyTypeCfgDAO dao = ActivityDailyTypeCfgDAO.getInstance();
		List<ActivityDailyTypeCfg> cfgList = dao.getAllCfg();
		for(ActivityDailyTypeCfg cfg : cfgList){
			if(!dao.isOpen(cfg)){
				continue;
			}
			if(itemList == null){
				itemList = dataHolder.getItemList(player.getUserId());
			}
			ActivityDailyTypeItem freshItem = null;
			for(ActivityDailyTypeItem item : itemList){
				if(StringUtils.equals(item.getVersion(), cfg.getVersion())){
					freshItem = item;
				}
			}
			if(freshItem == null){
				continue;
			}
			if (ActivityTypeHelper.isNewDayHourOfActivity(5, freshItem.getLastTime())) {
				sendEmailIfGiftNotTaken(player, freshItem.getSubItemList());
				freshItem.reset(cfg);
//				System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~reset");
				dataHolder.updateItem(player, freshItem);
			}
		}		
//		for (ActivityDailyTypeItem targetItem : itemList) {
//			ActivityDailyTypeCfg targetCfg = activityDailyTypeCfgDAO.getConfig(targetItem.getCfgid());
//			if (targetCfg == null) {
//				continue;
//			}
//			if (ActivityTypeHelper.isNewDayHourOfActivity(5, targetItem.getLastTime())) {
//				sendEmailIfGiftNotTaken(player, targetItem.getSubItemList());
//				targetItem.reset(targetCfg);
//				dataHolder.updateItem(player, targetItem);
//			}
//		}
	}

	private void checkClose(Player player) {
		ActivityDailyTypeItemHolder dataHolder = ActivityDailyTypeItemHolder.getInstance();
		List<ActivityDailyTypeItem> itemList = null;//dataHolder.getItemList(player.getUserId());
		ActivityDailyTypeCfgDAO dao = ActivityDailyTypeCfgDAO.getInstance();
		List<ActivityDailyTypeCfg> cfgList = dao.getAllCfg();
		long createTime = player.getUserDataMgr().getCreateTime();
		long currentTime = DateUtils.getSecondLevelMillis();
		for(ActivityDailyTypeCfg cfg : cfgList){
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
			ActivityDailyTypeItem closeItem = null;
			for(ActivityDailyTypeItem item : itemList){
				if(StringUtils.equals(item.getVersion(), cfg.getVersion())){
					closeItem = item;
					break;
				}			
			}
			if(closeItem == null){
				continue;
			}			
			if (!closeItem.isClosed()) {
				List<ActivityDailyTypeSubItem> list = closeItem.getSubItemList();
				sendEmailIfGiftNotTaken(player, list);
				closeItem.setClosed(true);
				closeItem.setTouchRedPoint(true);
				dataHolder.updateItem(player, closeItem);
			}			
		}
	}

	private boolean isClose(ActivityDailyTypeItem activityDailyCountTypeItem) {
		ActivityDailyTypeCfg cfgById = ActivityDailyTypeCfgDAO.getInstance().getCfgById(activityDailyCountTypeItem.getCfgid());
		if (cfgById == null) {
			return false;
		}
		long endTime = cfgById.getEndTime();
		long currentTime = System.currentTimeMillis();
		return currentTime > endTime;
	}

	private void sendEmailIfGiftNotTaken(Player player, List<ActivityDailyTypeSubItem> subItemList) {
		ActivityDailyTypeSubCfgDAO activityDailyTypeSubCfgDAO = ActivityDailyTypeSubCfgDAO.getInstance();
		ComGiftMgr comGiftMgr = ComGiftMgr.getInstance();
		for (ActivityDailyTypeSubItem subItem : subItemList) {// 配置表里的每种奖励
			ActivityDailyTypeSubCfg subItemCfg = activityDailyTypeSubCfgDAO.getById(subItem.getCfgId());
			if (subItemCfg == null) {
				return;
			}
			if (subItem.getCount() >= subItemCfg.getCount() && !subItem.isTaken()) {
				comGiftMgr.addGiftTOEmailById(player, subItemCfg.getGiftId(), MAKEUPEMAIL + "", subItemCfg.getEmailTitle());
				subItem.setTaken(true);
			}
		}
	}

	public void addCount(Player player, ActivityDailyTypeEnum countType, int countadd) {
		ActivityDailyTypeItemHolder dataHolder = ActivityDailyTypeItemHolder.getInstance();
		ActivityDailyTypeItem dataItem = dataHolder.getItem(player.getUserId());
		if (dataItem == null) {
			return;
		}
		ActivityDailyTypeSubItem subItem = getbyDailyCountTypeEnum(player, countType, dataItem);
		if (subItem == null) {
			return;
		}
		subItem.setCount(subItem.getCount() + countadd);
		dataHolder.updateItem(player, dataItem);
	}

	public ActivityDailyTypeSubItem getbyDailyCountTypeEnum(Player player, ActivityDailyTypeEnum typeEnum, ActivityDailyTypeItem dataItem) {
		ActivityDailyTypeSubCfgDAO activityDailyTypeSubCfgDAO = ActivityDailyTypeSubCfgDAO.getInstance();
		ActivityDailyTypeSubItem subItem = null;
		ActivityDailyTypeSubCfg cfg = null;
		List<ActivityDailyTypeSubCfg> subcfglist = ActivityDailyTypeSubCfgDAO.getInstance().getAllCfg();
		for (ActivityDailyTypeSubCfg subcfg : subcfglist) {
			if (StringUtils.equals(subcfg.getEnumId(), typeEnum.getCfgId()) && activityDailyTypeSubCfgDAO.isOpen(subcfg)) {
				cfg = subcfg;
			}
		}

		if (cfg == null) {
			return subItem;
		}

		if (dataItem != null) {
			List<ActivityDailyTypeSubItem> sublist = dataItem.getSubItemList();
			for (ActivityDailyTypeSubItem subitem : sublist) {
				if (StringUtils.equals(cfg.getId(), subitem.getCfgId())) {
					subItem = subitem;
					break;
				}
			}

		}

		return subItem;
	}

	public ActivityComResult takeGift(Player player, ActivityDailyTypeEnum countType, String subItemId) {
		ActivityDailyTypeItemHolder dataHolder = ActivityDailyTypeItemHolder.getInstance();

		ActivityDailyTypeItem dataItem = dataHolder.getItem(player.getUserId());
		ActivityComResult result = ActivityComResult.newInstance(false);

		// 未激活
		if (dataItem == null) {
			result.setReason("活动尚未开启");

		} else {
			ActivityDailyTypeSubItem targetItem = null;

			List<ActivityDailyTypeSubItem> subItemList = dataItem.getSubItemList();
			for (ActivityDailyTypeSubItem itemTmp : subItemList) {
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

	private void takeGift(Player player, ActivityDailyTypeSubItem targetItem) {
		ActivityDailyTypeSubCfg subCfg = ActivityDailyTypeSubCfgDAO.getInstance().getById(targetItem.getCfgId());
		if (subCfg == null) {
			return;
		}
		targetItem.setTaken(true);
		ComGiftMgr.getInstance().addGiftById(player, subCfg.getGiftId());

	}

	@Override
	public void updateRedPoint(Player player, String eNum) {
		ActivityDailyTypeItemHolder activityCountTypeItemHolder = new ActivityDailyTypeItemHolder();
		ActivityDailyTypeCfg cfg = ActivityDailyTypeCfgDAO.getInstance().getCfgById(eNum);
		if (cfg == null) {
			return;
		}
		ActivityDailyTypeEnum dailyEnum = ActivityDailyTypeEnum.getById(cfg.getEnumId());
		if (dailyEnum == null) {
			return;
		}
		ActivityDailyTypeItem dataItem = activityCountTypeItemHolder.getItem(player.getUserId());
		if (dataItem == null) {
			return;
		}
		if (!dataItem.isTouchRedPoint()) {
			dataItem.setTouchRedPoint(true);
			activityCountTypeItemHolder.updateItem(player, dataItem);
		}

	}

	public boolean isOpen(long param) {
		List<ActivityDailyTypeCfg> allList = ActivityDailyTypeCfgDAO.getInstance().getReadOnlyAllCfg();
		for (ActivityDailyTypeCfg cfg : allList) {
			if (isOpen(cfg, param)) {
				return true;
			}
		}
		return false;
	}

	private boolean isOpen(ActivityDailyTypeCfg cfg, long param) {
		if (cfg != null) {
			long startTime = cfg.getStartTime();
			long endTime = cfg.getEndTime();
			long currentTime = param;
			return currentTime < endTime && currentTime >= startTime;
		}
		return false;
	}

	public List<String> haveRedPoint(Player player) {
		List<String> redPointList = new ArrayList<String>();
		ActivityDailyTypeItemHolder dailyDataHolder = ActivityDailyTypeItemHolder.getInstance();
		ActivityDailyTypeItem dailyTargetItem = null;// dailyDataHolder.getItem(player.getUserId());
		ActivityDailyTypeCfgDAO dao = ActivityDailyTypeCfgDAO.getInstance();
		List<ActivityDailyTypeCfg> cfgList = dao.getAllCfg();
		ActivityDailyTypeSubCfgDAO subDao = ActivityDailyTypeSubCfgDAO.getInstance();
		for(ActivityDailyTypeCfg cfg : cfgList){
			if(!dao.isOpen(cfg)){
				continue;
			}
			if(cfg.getLevelLimit()> player.getLevel()){
				continue;
			}
			
			if(dailyTargetItem == null){
				dailyTargetItem = dailyDataHolder.getItem(player.getUserId());
			}
			if (!dailyTargetItem.isTouchRedPoint()) {
				redPointList.add(cfg.getId());
				continue;
			} 
			
			for (ActivityDailyTypeSubItem subitem : dailyTargetItem.getSubItemList()) {
				ActivityDailyTypeSubCfg subItemCfg = subDao.getById(subitem.getCfgId());
				if (subitem.getCount() >= subItemCfg.getCount() && !subitem.isTaken()) {
					redPointList.add(cfg.getId());
					break;
				}
			}
		}
		return redPointList;
	}
}
