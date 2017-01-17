package com.playerdata.activity.VitalityType;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.StringUtils;

import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.ActivityRedPointUpdate;
import com.playerdata.activity.ActivityTypeHelper;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityCfg;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityCfgDAO;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityRewardCfg;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityRewardCfgDAO;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalitySubCfg;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalitySubCfgDAO;
import com.playerdata.activity.VitalityType.data.ActivityVitalityItemHolder;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeItem;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeSubBoxItem;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeSubItem;
import com.playerdata.activityCommon.activityType.IndexRankJudgeIF;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;

public class ActivityVitalityTypeMgr implements ActivityRedPointUpdate, IndexRankJudgeIF{
	
	private static final int ACTIVITY_INDEX_BEGIN = 50000;
	private static final int ACTIVITY_INDEX_END = 60000;

	private static ActivityVitalityTypeMgr instance = new ActivityVitalityTypeMgr();

	private final static int MAKEUPEMAIL = 10055;

	public static ActivityVitalityTypeMgr getInstance() {
		return instance;
	}

	private void synVitalityTypeData(Player player) {
		if(isOpen(System.currentTimeMillis())){
			checkCfgVersion(player);
			ActivityVitalityItemHolder.getInstance().synAllData(player);
		}
	}

	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空 */
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
		checkCfgVersion(player);
		checkOtherDay(player);
		checkClose(player);
		synVitalityTypeData(player);
	}

	private void checkNewOpen(Player player) {
		String userId = player.getUserId();
		creatItems(userId, true);
	}

	public List<ActivityVitalityTypeItem> creatItems(String userId, boolean isHasPlayer) {
		RoleExtPropertyStoreCache<ActivityVitalityTypeItem> storeCach = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.ACTIVITY_VITALITY, ActivityVitalityTypeItem.class);
		RoleExtPropertyStore<ActivityVitalityTypeItem> store = null;
		List<ActivityVitalityTypeItem> addItemList = null;
		ActivityVitalityCfgDAO dao = ActivityVitalityCfgDAO.getInstance();
		List<ActivityVitalityCfg> allCfgList = ActivityVitalityCfgDAO.getInstance().getAllCfg();

		for (ActivityVitalityCfg cfg : allCfgList) {// 遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if (!isOpen(cfg)) {
				// 活动未开启
				continue;
			}
			ActivityVitalityTypeEnum acVitalityTypeEnum = ActivityVitalityTypeEnum.getById(String.valueOf(cfg.getEnumID()));
			if (acVitalityTypeEnum == null) {
				continue;
			}
			int id = Integer.parseInt(acVitalityTypeEnum.getCfgId());
			if(isHasPlayer){
				try {
					store = storeCach.getStore(userId);
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
			ActivityVitalityTypeItem item = new ActivityVitalityTypeItem();
			item = dao.newItem(userId, cfg);
			if (addItemList == null) {
				addItemList = new ArrayList<ActivityVitalityTypeItem>();
			}
			addItemList.add(item);
		}
		if(isHasPlayer&&addItemList != null){
			try {
				store.addItem(addItemList);
			} catch (DuplicatedKeyException e) {
				e.printStackTrace();
			}
		}
		return addItemList;
	}

	public boolean isOpen(ActivityVitalityCfg vitalityCfg) {
		long startTime = vitalityCfg.getStartTime();
		long endTime = vitalityCfg.getEndTime();
		long currentTime = System.currentTimeMillis();
		return currentTime < endTime && currentTime >= startTime;
	}

	private void checkCfgVersion(Player player) {
		ActivityVitalityItemHolder dataHolder = ActivityVitalityItemHolder.getInstance();
		ActivityVitalityCfgDAO dao = ActivityVitalityCfgDAO.getInstance();
		List<ActivityVitalityTypeItem> itemList = null;//dataHolder.getItemList(player.getUserId());
		List<ActivityVitalityCfg> cfgList = dao.getAllCfg();
		for(ActivityVitalityCfg cfg : cfgList){
			if(!isOpen(cfg)){
				continue;
			}
			if(itemList == null){
				itemList = dataHolder.getItemList(player.getUserId());
			}
			ActivityVitalityTypeItem freshItem = null;
			for(ActivityVitalityTypeItem item : itemList){
				if(StringUtils.equals(item.getEnumId(), String.valueOf(cfg.getEnumID()))&&!StringUtils.equals(item.getVersion(), String.valueOf(cfg.getVersion()))){
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
		ActivityVitalityItemHolder dataHolder = ActivityVitalityItemHolder.getInstance();
		ActivityVitalityCfgDAO dao = ActivityVitalityCfgDAO.getInstance();
		List<ActivityVitalityTypeItem> itemList = null;//dataHolder.getItemList(player.getUserId());
		List<ActivityVitalityCfg> cfgList = dao.getAllCfg();
		for(ActivityVitalityCfg cfg : cfgList){
			if(!isOpen(cfg)){
				continue;
			}
			if(!StringUtils.equals(ActivityVitalityTypeEnum.Vitality.getCfgId(), String.valueOf(cfg.getEnumID()))){
				continue;
			}
			if(itemList == null){
				itemList = dataHolder.getItemList(player.getUserId());
			}
			ActivityVitalityTypeItem freshItem = null;
			for(ActivityVitalityTypeItem item : itemList){
				if(StringUtils.equals(item.getEnumId(), String.valueOf(cfg.getEnumID()))&&StringUtils.equals(item.getVersion(), String.valueOf(cfg.getVersion()))){
					freshItem = item;
				}
			}
			if(freshItem == null){
				continue;
			}
			if (ActivityTypeHelper.isNewDayHourOfActivity(5, freshItem.getLastTime())) {
				sendEmailIfGiftNotTaken(player, freshItem.getSubItemList());
				sendEmailIfBoxGiftNotTaken(player, freshItem);
				freshItem.reset(cfg);
				dataHolder.updateItem(player, freshItem);
			}
		}	
	}

	private void checkClose(Player player) {
		ActivityVitalityItemHolder dataHolder = ActivityVitalityItemHolder.getInstance();
		List<ActivityVitalityTypeItem> itemList = null;
		ActivityVitalityCfgDAO dao = ActivityVitalityCfgDAO.getInstance();
		List<ActivityVitalityCfg> cfgList = dao.getAllCfg();
		for(ActivityVitalityCfg cfg : cfgList){
			if(isOpen(cfg)){//配置开启
				continue;
			}
			if(itemList == null){
				itemList = dataHolder.getItemList(player.getUserId());
			}
			ActivityVitalityTypeItem closeItem = null;
			for(ActivityVitalityTypeItem item : itemList){
				if(StringUtils.equals(item.getEnumId(), String.valueOf(cfg.getEnumID()))&&StringUtils.equals(item.getVersion(), String.valueOf(cfg.getVersion()))){
					closeItem = item;
					break;
				}			
			}
			if(closeItem == null){
				continue;
			}			
			if (!closeItem.isClosed()) {
				sendEmailIfGiftNotTaken(player, closeItem.getSubItemList());
				sendEmailIfBoxGiftNotTaken(player, closeItem);
				closeItem.setClosed(true);
				dataHolder.updateItem(player, closeItem);
			}else{
				dataHolder.removeItem(player, closeItem);
			}
		}
	}

	public boolean isHasCfg(ActivityVitalityTypeItem activityVitalityTypeItem) {
		ActivityVitalityCfg cfg = ActivityVitalityCfgDAO.getInstance().getCfgById(activityVitalityTypeItem.getCfgId());
		if (cfg == null) {
			return false;
		}
		return true;
	}

	//
	public boolean isClose(ActivityVitalityTypeItem activityVitalityTypeItem) {
		ActivityVitalityCfg cfg = ActivityVitalityCfgDAO.getInstance().getCfgById(activityVitalityTypeItem.getCfgId());
		if (cfg == null) {
			return false;
		}
		long endTime = cfg.getEndTime();
		long currentTime = System.currentTimeMillis();
		return currentTime > endTime;
	}

	private void sendEmailIfGiftNotTaken(Player player, List<ActivityVitalityTypeSubItem> subItemList) {
		ActivityVitalitySubCfgDAO activityVitalitySubCfgDAO = ActivityVitalitySubCfgDAO.getInstance();
		ComGiftMgr comGiftMgr = ComGiftMgr.getInstance();
		for (ActivityVitalityTypeSubItem subItem : subItemList) {// 配置表里的每种奖励
			ActivityVitalitySubCfg subItemCfg = activityVitalitySubCfgDAO.getCfgById(subItem.getCfgId());
			if (subItemCfg == null) {
				return;
			}
			if (subItem.getCount() >= subItemCfg.getCount() && !subItem.isTaken()) {
				comGiftMgr.addGiftTOEmailById(player, subItemCfg.getGiftId(), MAKEUPEMAIL + "", subItemCfg.getEmailTitle());
				subItem.setTaken(true);

			}
		}
	}

	private void sendEmailIfBoxGiftNotTaken(Player player, ActivityVitalityTypeItem Item) {
		ActivityVitalityCfg cfg = ActivityVitalityCfgDAO.getInstance().getCfgById(Item.getCfgId());
		if (cfg == null) {
			return;
		}
		if (!cfg.isCanGetReward()) {
			// 不派发宝箱
			return;
		}
		ActivityVitalityRewardCfgDAO activityVitalityRewardCfgDAO = ActivityVitalityRewardCfgDAO.getInstance();
		ComGiftMgr comGiftMgr = ComGiftMgr.getInstance();
		List<ActivityVitalityTypeSubBoxItem> subBoxItemList = Item.getSubBoxItemList();
		for (ActivityVitalityTypeSubBoxItem subItem : subBoxItemList) {// 配置表里的每种奖励
			ActivityVitalityRewardCfg subItemCfg = activityVitalityRewardCfgDAO.getCfgById(subItem.getCfgId());
			if (subItemCfg == null) {
				return;
			}
			if (Item.getActiveCount() >= subItemCfg.getActivecount() && !subItem.isTaken()) {
				comGiftMgr.addGiftTOEmailById(player, subItemCfg.getGiftId(), MAKEUPEMAIL + "", subItemCfg.getEmailTitle());
				subItem.setTaken(true);
			}
		}
	}

	public boolean isLevelEnough(ActivityVitalityTypeEnum eNum, Player player) {
		ActivityVitalityCfg vitalityCfg = null;
		List<ActivityVitalityCfg> cfgList = ActivityVitalityCfgDAO.getInstance().getAllCfg();
		for (ActivityVitalityCfg cfg : cfgList) {
			if (!StringUtils.equals(eNum.getCfgId(), String.valueOf(cfg.getEnumID()))) {
				continue;
			}
			if (!isOpen(cfg)) {
				continue;
			}
			vitalityCfg = cfg;
			break;
		}
		if (vitalityCfg == null) {
			// GameLog.error("activityDailyCountTypeMgr", "list", "配置文件总表错误" );
			return false;
		}
		if (player.getLevel() < vitalityCfg.getLevelLimit()) {
			return false;
		}
		return true;
	}

	public void addCount(Player player, ActivityVitalityTypeEnum countType, ActivityVitalitySubCfg subCfg, int countadd) {
		ActivityVitalityItemHolder dataHolder = ActivityVitalityItemHolder.getInstance();
		ActivityVitalityTypeItem dataItem = dataHolder.getItem(player.getUserId(), countType);
		ActivityVitalityTypeSubItem subItem = getbyVitalityTypeEnum(player, subCfg, dataItem);
		addVitalitycount(dataItem, subItem, subCfg, countadd);
		subItem.setCount(subItem.getCount() + countadd);
		dataHolder.updateItem(player, dataItem);
	}

	public ActivityVitalityTypeSubItem getbyVitalityTypeEnum(Player player, ActivityVitalitySubCfg subCfg, ActivityVitalityTypeItem dataItem) {
		ActivityVitalityTypeSubItem subItem = null;
		if (subCfg == null) {
			return subItem;
		}
		if (dataItem != null) {
			List<ActivityVitalityTypeSubItem> sublist = dataItem.getSubItemList();
			for (ActivityVitalityTypeSubItem subitem : sublist) {
				if (StringUtils.equals(subCfg.getId(), subitem.getCfgId())) {
					subItem = subitem;
					break;
				}
			}
		}
		return subItem;
	}

	/** 增加活跃度 */
	private void addVitalitycount(ActivityVitalityTypeItem dataItem, ActivityVitalityTypeSubItem subItem, ActivityVitalitySubCfg subCfg, int countadd) {
		if (subItem.getCount() < subCfg.getCount() && (subItem.getCount() + countadd >= subCfg.getCount())) {
			dataItem.setActiveCount(dataItem.getActiveCount() + subCfg.getActiveCount());
		}
	}

	public ActivityComResult takeGift(Player player, String subItemId) {
		ActivityVitalityItemHolder dataHolder = ActivityVitalityItemHolder.getInstance();
		ActivityVitalityTypeItem dataItem = null;
		ActivityVitalityTypeSubItem item = null;
		List<ActivityVitalityTypeItem> dataitemList = dataHolder.getItemList(player.getUserId());
		for (ActivityVitalityTypeItem dataitemtmp : dataitemList) {
			List<ActivityVitalityTypeSubItem> subitemlist = dataitemtmp.getSubItemList();
			for (ActivityVitalityTypeSubItem subitem : subitemlist) {
				if (StringUtils.equals(subItemId, subitem.getCfgId())) {
					item = subitem;
					dataItem = dataitemtmp;
					break;
				}
			}
		}
		ActivityComResult result = ActivityComResult.newInstance(false);

		if (dataItem == null) {
			result.setReason("活动尚未开启");
		} else {
			if (item.isTaken()) {
				result.setReason("已经领取");
				return result;
			}
			takeGift(player, item);
			result.setSuccess(true);
			dataHolder.updateItem(player, dataItem);

		}

		return result;
	}

	private void takeGift(Player player, ActivityVitalityTypeSubItem targetItem) {
		targetItem.setTaken(true);
		ComGiftMgr.getInstance().addGiftById(player, targetItem.getGiftId());
	}

	public ActivityComResult openBox(Player player, String rewardItemId) {
		ActivityVitalityItemHolder dataHolder = ActivityVitalityItemHolder.getInstance();
		ActivityVitalityTypeItem dataItem = null;
		ActivityVitalityTypeSubBoxItem item = null;
		List<ActivityVitalityTypeItem> dataitemList = dataHolder.getItemList(player.getUserId());
		for (ActivityVitalityTypeItem dataitemtmp : dataitemList) {
			List<ActivityVitalityTypeSubBoxItem> subitemlist = dataitemtmp.getSubBoxItemList();
			for (ActivityVitalityTypeSubBoxItem subitem : subitemlist) {
				if (StringUtils.equals(rewardItemId, subitem.getCfgId())) {
					item = subitem;
					dataItem = dataitemtmp;
					break;
				}
			}
		}

		ActivityComResult result = ActivityComResult.newInstance(false);

		// 未激活
		if (dataItem == null) {
			result.setReason("活动未开启");
			return result;
		} else {
			if (item.getCount() > dataItem.getActiveCount()) {
				result.setReason("积分不足");
				return result;
			}
			if (item.isTaken()) {
				result.setReason("已经领取");
				return result;
			}

			takeBoxGift(player, item);
			result.setSuccess(true);
			dataHolder.updateItem(player, dataItem);
		}
		return result;
	}

	private void takeBoxGift(Player player, ActivityVitalityTypeSubBoxItem targetItem) {

		targetItem.setTaken(true);
		ComGiftMgr.getInstance().addGiftById(player, targetItem.getGiftId());

	}

	@Override
	public void updateRedPoint(Player player, String eNum) {
		ActivityVitalityItemHolder activityCountTypeItemHolder = new ActivityVitalityItemHolder();
		ActivityVitalityCfg cfg = ActivityVitalityCfgDAO.getInstance().getCfgById(eNum);
		if (cfg == null) {
			return;
		}
		ActivityVitalityTypeEnum vitalityEnum = ActivityVitalityTypeEnum.getById(String.valueOf(cfg.getEnumID()));// cfg
		if (vitalityEnum == null) {
			return;
		}
		ActivityVitalityTypeItem dataItem = activityCountTypeItemHolder.getItem(player.getUserId(), vitalityEnum);
		if (dataItem == null) {
			return;
		}
		if (!dataItem.isTouchRedPoint()) {
			dataItem.setTouchRedPoint(true);
			activityCountTypeItemHolder.updateItem(player, dataItem);
		}
	}

	public boolean isOpen(long param) {
		List<ActivityVitalityCfg> list = ActivityVitalityCfgDAO.getInstance().getAllCfg();
		for (ActivityVitalityCfg cfg : list) {
			if (isOpen(cfg, param)) {
				return true;
			}
		}
		return false;
	}

	private boolean isOpen(ActivityVitalityCfg cfg, long param) {
		if (cfg != null) {
			long startTime = cfg.getStartTime();
			long endTime = cfg.getEndTime();
			long currentTime = param;
			return currentTime < endTime && currentTime >= startTime;
		}
		return false;
	}

	/**
	 * 有红点的配置id
	 * @param player
	 * @return
	 */
	public List<String> haveRedPoint(Player player) {
		List<String> redPointList = new ArrayList<String>();
		ActivityVitalityItemHolder vitalityDataHolder = ActivityVitalityItemHolder.getInstance();
		List<ActivityVitalityTypeItem> vitalityItemList = null;
		ActivityVitalitySubCfgDAO subCfgDAO = ActivityVitalitySubCfgDAO.getInstance();
		ActivityVitalityCfgDAO vitalityCfgDAO = ActivityVitalityCfgDAO.getInstance();
		ActivityVitalityRewardCfgDAO rewardCfgDAO = ActivityVitalityRewardCfgDAO.getInstance();
		List<ActivityVitalityCfg> cfgList = vitalityCfgDAO.getAllCfg();
		for(ActivityVitalityCfg cfg : cfgList){
			if(!isOpen(cfg)){
				continue;
			}
			if(vitalityItemList == null){
				vitalityItemList = vitalityDataHolder.getItemList(player.getUserId());
			}
			ActivityVitalityTypeItem item = null;
			for(ActivityVitalityTypeItem temp : vitalityItemList){
				if(temp.getId() == cfg.getEnumID()){
					item = temp ;
					break;
				}
			}
			if(item == null){
				continue;
			}
			if (!item.isTouchRedPoint() || isHaveSubItemReward(item, subCfgDAO) || isHaveBoxReward(item, rewardCfgDAO)) {
				redPointList.add(item.getCfgId());
			}
		}
		return redPointList;
	}
	
	/**
	 * 是否有活跃项
	 * @param item
	 * @param subCfgDAO
	 * @return
	 */
	private boolean isHaveSubItemReward(ActivityVitalityTypeItem item, ActivityVitalitySubCfgDAO subCfgDAO){
		List<ActivityVitalityTypeSubItem> vitalitySubItemList = item.getSubItemList();
		for (ActivityVitalityTypeSubItem subItem : vitalitySubItemList) {// 配置表里的每种奖励
			ActivityVitalitySubCfg subItemCfg = subCfgDAO.getCfgById(subItem.getCfgId());
			if (subItemCfg == null) {
				continue;
			}
			if (subItem.getCount() >= subItemCfg.getCount() && !subItem.isTaken()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 是否有活跃宝箱
	 * @param item
	 * @param rewardCfgDAO
	 * @return
	 */
	private boolean isHaveBoxReward(ActivityVitalityTypeItem item, ActivityVitalityRewardCfgDAO rewardCfgDAO){
		List<ActivityVitalityTypeSubBoxItem> vitalitySubBoxItemList = item.getSubBoxItemList();
		for (ActivityVitalityTypeSubBoxItem subItem : vitalitySubBoxItemList) {// 配置表里的每种奖励
			ActivityVitalityRewardCfg subItemCfg = rewardCfgDAO.getCfgById(subItem.getCfgId());
			if (subItemCfg == null) {
				continue;
			}
			if (item.getActiveCount() >= subItemCfg.getActivecount() && !subItem.isTaken()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isThisActivityIndex(int index) {
		return index < ACTIVITY_INDEX_END && index > ACTIVITY_INDEX_BEGIN;
	}
}
