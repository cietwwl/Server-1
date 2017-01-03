package com.playerdata.activity.redEnvelopeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.common.HPCUtil;
import com.playerdata.ComGiftMgr;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.ActivityRedPointUpdate;
import com.playerdata.activity.ActivityTypeHelper;
import com.playerdata.activity.redEnvelopeType.cfg.ActivityRedEnvelopeTypeCfg;
import com.playerdata.activity.redEnvelopeType.cfg.ActivityRedEnvelopeTypeCfgDAO;
import com.playerdata.activity.redEnvelopeType.cfg.ActivityRedEnvelopeTypeSubCfg;
import com.playerdata.activity.redEnvelopeType.cfg.ActivityRedEnvelopeTypeSubCfgDAO;
import com.playerdata.activity.redEnvelopeType.data.ActivityRedEnvelopeItemHolder;
import com.playerdata.activity.redEnvelopeType.data.ActivityRedEnvelopeTypeItem;
import com.playerdata.activity.redEnvelopeType.data.ActivityRedEnvelopeTypeSubItem;
import com.playerdata.activityCommon.activityType.IndexRankJudgeIF;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.common.enu.eSpecialItemId;

public class ActivityRedEnvelopeTypeMgr implements ActivityRedPointUpdate, IndexRankJudgeIF {
	
	private static final int ACTIVITY_INDEX_BEGIN = 40000;
	private static final int ACTIVITY_INDEX_END = 50000;

	private static ActivityRedEnvelopeTypeMgr instance = new ActivityRedEnvelopeTypeMgr();

	private final static int MAKEUPEMAIL = 10055;

	public static ActivityRedEnvelopeTypeMgr getInstance() {
		return instance;
	}

	public void synRedEnvelopeTypeData(Player player) {
		if (isOpen(System.currentTimeMillis())) {
			ActivityRedEnvelopeItemHolder.getInstance().synAllData(player);
		}
	}

	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空 */
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
		checkCfgVersion(player);
		checkOtherDay(player);
		checkClose(player);
	}

	private void checkNewOpen(Player player) {
		String userId = player.getUserId();
		creatItems(userId, true);

	}

	public List<ActivityRedEnvelopeTypeItem> creatItems(String userId, boolean isHasPlayer) {
		RoleExtPropertyStoreCache<ActivityRedEnvelopeTypeItem> cach = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.ACTIVITY_REDENVELOPE, ActivityRedEnvelopeTypeItem.class);
		RoleExtPropertyStore<ActivityRedEnvelopeTypeItem> store = null;

		List<ActivityRedEnvelopeTypeItem> addItemList = null;
		ActivityRedEnvelopeTypeSubCfgDAO subDao = ActivityRedEnvelopeTypeSubCfgDAO.getInstance();
		List<ActivityRedEnvelopeTypeCfg> cfgList = ActivityRedEnvelopeTypeCfgDAO.getInstance().getAllCfg();
		// String itemId = ActivityRedEnvelopeHelper.getItemId(userId, ActivityRedEnvelopeTypeEnum.redEnvelope);
		int id = Integer.parseInt(ActivityRedEnvelopeTypeEnum.redEnvelope.getCfgId());
		for (ActivityRedEnvelopeTypeCfg cfg : cfgList) {

			if (!isOpen(cfg)) {
				continue;
			}
			if (isHasPlayer) {
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
			ActivityRedEnvelopeTypeItem item = new ActivityRedEnvelopeTypeItem();
			item.setId(id);
			item.setUserId(userId);
			item.setCfgId(String.valueOf(cfg.getId()));
			item.setVersion(String.valueOf(cfg.getVersion()));
			item.setLastTime(System.currentTimeMillis());
			int day = ActivityTypeHelper.getDayBy5Am(cfg.getStartTime());
			item.setDay(day);
			List<ActivityRedEnvelopeTypeSubItem> subItemList = new ArrayList<ActivityRedEnvelopeTypeSubItem>();
			List<ActivityRedEnvelopeTypeSubCfg> subList = subDao.getSubCfgListByParentID(String.valueOf(cfg.getId()));
			if (subList == null) {
				subList = new ArrayList<ActivityRedEnvelopeTypeSubCfg>();
			}
			for (ActivityRedEnvelopeTypeSubCfg subCfg : subList) {
				if (!StringUtils.equals(String.valueOf(cfg.getId()), subCfg.getParantid())) {
					continue;
				}
				ActivityRedEnvelopeTypeSubItem subItem = new ActivityRedEnvelopeTypeSubItem();
				subItem.setCfgId(subCfg.getId());
				subItem.setDay(subCfg.getDay());
				subItem.setDiscount(subCfg.getDiscount());
				subItemList.add(subItem);
			}
			item.setSubItemList(subItemList);
			if (addItemList == null) {
				addItemList = new ArrayList<ActivityRedEnvelopeTypeItem>();
			}
			addItemList.add(item);
		}
		if (isHasPlayer && addItemList != null) {
			try {
				store.addItem(addItemList);
			} catch (DuplicatedKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return addItemList;
	}

	public boolean isOpen(ActivityRedEnvelopeTypeCfg cfg) {
		long startTime = cfg.getStartTime();
		long endTime = cfg.getGetRewardsTime();
		long currentTime = System.currentTimeMillis();
		return currentTime < endTime && currentTime >= startTime;
	}

	private void checkCfgVersion(Player player) {
		ActivityRedEnvelopeItemHolder dataHolder = ActivityRedEnvelopeItemHolder.getInstance();
		List<ActivityRedEnvelopeTypeItem> itemList = null;// dataHolder.getItem(player.getUserId());
		ActivityRedEnvelopeTypeCfgDAO dao = ActivityRedEnvelopeTypeCfgDAO.getInstance();
		List<ActivityRedEnvelopeTypeCfg> cfgList = dao.getAllCfg();
		for (ActivityRedEnvelopeTypeCfg cfg : cfgList) {
			if (!isOpen(cfg)) {
				continue;
			}
			if (itemList == null) {
				itemList = dataHolder.getItemList(player.getUserId());
			}
			ActivityRedEnvelopeTypeItem freshItem = null;
			for (ActivityRedEnvelopeTypeItem item : itemList) {
				if (!StringUtils.equals(item.getVersion(), String.valueOf(cfg.getVersion()))) {
					freshItem = item;
				}
			}
			if (freshItem == null) {
				continue;
			}
			int day = ActivityTypeHelper.getDayBy5Am(cfg.getStartTime());
			List<ActivityRedEnvelopeTypeSubItem> subItemList = ActivityRedEnvelopeTypeCfgDAO.getInstance().getSubList(cfg);
			freshItem.resetByVersion(cfg, subItemList, day);
			dataHolder.updateItem(player, freshItem);
		}
	}

	//
	private void checkOtherDay(Player player) {
		ActivityRedEnvelopeItemHolder dataHolder = ActivityRedEnvelopeItemHolder.getInstance();
		List<ActivityRedEnvelopeTypeItem> itemList = null;// dataHolder.getItem(player.getUserId());
		ActivityRedEnvelopeTypeCfgDAO dao = ActivityRedEnvelopeTypeCfgDAO.getInstance();
		List<ActivityRedEnvelopeTypeCfg> cfgList = dao.getAllCfg();
		for (ActivityRedEnvelopeTypeCfg cfg : cfgList) {
			if (!isOpen(cfg)) {
				continue;
			}
			if (itemList == null) {
				itemList = dataHolder.getItemList(player.getUserId());
			}
			ActivityRedEnvelopeTypeItem freshItem = null;
			for (ActivityRedEnvelopeTypeItem item : itemList) {
				if (StringUtils.equals(item.getVersion(), String.valueOf(cfg.getVersion()))) {
					freshItem = item;
				}
			}
			if (freshItem == null) {
				continue;
			}
			if (HPCUtil.isResetTime(freshItem.getLastTime())) {
				int day = ActivityTypeHelper.getDayBy5Am(cfg.getStartTime());
				freshItem.resetByOtherday(cfg, day);
				dataHolder.updateItem(player, freshItem);
			}
		}

		// if(item == null){
		// return;
		// }
		// ActivityRedEnvelopeTypeCfg cfg = ActivityRedEnvelopeTypeCfgDAO.getInstance().getCfgById(item.getCfgId());
		// if(cfg == null ){
		// return;
		// }
		//
		// if (ActivityTypeHelper.isNewDayHourOfActivity(5,item.getLastTime())) {
		// int day = ActivityTypeHelper.getDayBy5Am(cfg.getStartTime());
		// item.resetByOtherday(cfg, day);
		// dataHolder.updateItem(player, item);
		// }
	}

	private void checkClose(Player player) {
		ActivityRedEnvelopeItemHolder dataHolder = ActivityRedEnvelopeItemHolder.getInstance();
		List<ActivityRedEnvelopeTypeItem> itemList = null;// dataHolder.getItem(player.getUserId());
		ActivityRedEnvelopeTypeCfgDAO dao = ActivityRedEnvelopeTypeCfgDAO.getInstance();
		List<ActivityRedEnvelopeTypeCfg> cfgList = dao.getAllCfg();
		long createTime = player.getUserDataMgr().getCreateTime();
		long currentTime = DateUtils.getSecondLevelMillis();
		for (ActivityRedEnvelopeTypeCfg cfg : cfgList) {
			if (isOpen(cfg)) {// 配置开启
				continue;
			}
			if (createTime > cfg.getEndTime()) {// 配置过旧
				continue;
			}
			if (currentTime < cfg.getStartTime()) {// 配置过新
				continue;
			}
			if (itemList == null) {
				itemList = dataHolder.getItemList(player.getUserId());
			}
			ActivityRedEnvelopeTypeItem closeItem = null;
			for (ActivityRedEnvelopeTypeItem item : itemList) {
				if (StringUtils.equals(item.getVersion(), String.valueOf(cfg.getVersion()))) {
					closeItem = item;
					break;
				}
			}
			if (closeItem == null) {
				continue;
			}
			if (isCanTakeGift(closeItem)) {
				if (!closeItem.isClosed()) {
					closeItem.setClosed(true);
					closeItem.setTouchRedPoint(false);
					dataHolder.updateItem(player, closeItem);
				}
				return;
			}
			if (closeItem.isIstaken()) {
				return;
			}
			String reward = eSpecialItemId.Gold.getValue() + "_" + closeItem.getGoldCount();
			if (closeItem.getGoldCount() > 0) {
				ComGiftMgr.getInstance().addtagInfoTOEmail(player, reward, MAKEUPEMAIL + "", cfg.getEmailTitle());
			}
			closeItem.setIstaken(true);
			closeItem.setClosed(true);
			dataHolder.updateItem(player, closeItem);
		}

		// if(isCanTakeGift(item)){
		// if(!item.isClosed()){
		// item.setClosed(true);
		// item.setTouchRedPoint(false);
		// dataHolder.updateItem(player, item);
		// }
		// return;
		// }
		// if(item.isIstaken()){
		// return;
		// }
		//
		// ActivityRedEnvelopeTypeCfg cfg = ActivityRedEnvelopeTypeCfgDAO.getInstance().getCfgById(item.getCfgId());
		// if(cfg == null){
		// return;
		// }
		// String reward = eSpecialItemId.Gold.getValue() +"_"+item.getGoldCount();
		// if(item.getGoldCount() > 0){
		// ComGiftMgr.getInstance().addtagInfoTOEmail(player, reward, MAKEUPEMAIL+"", cfg.getEmailTitle());
		// }
		// item.setIstaken(true);
		// item.setClosed(true);
		// dataHolder.updateItem(player, item);
	}

	public boolean isClose(ActivityRedEnvelopeTypeItem activityVitalityTypeItem) {
		ActivityRedEnvelopeTypeCfg cfg = ActivityRedEnvelopeTypeCfgDAO.getInstance().getCfgById(activityVitalityTypeItem.getCfgId());
		if (cfg == null) {
			return false;
		}
		long endTime = cfg.getEndTime();
		long currentTime = System.currentTimeMillis();
		return currentTime >= endTime;
	}

	public boolean isCanTakeGift(ActivityRedEnvelopeTypeItem item) {
		ActivityRedEnvelopeTypeCfg cfg = ActivityRedEnvelopeTypeCfgDAO.getInstance().getCfgById(item.getCfgId());

		return isCanTakeGift(cfg);
	}

	public boolean isCanTakeGift(ActivityRedEnvelopeTypeCfg cfg) {
		long takenTime = cfg.getGetRewardsTime();
		long currentTime = System.currentTimeMillis();
		long endTime = cfg.getEndTime();
		return currentTime > endTime && currentTime < takenTime;
	}

	public void addCount(Player player, int countadd) {
		ActivityRedEnvelopeItemHolder dataHolder = ActivityRedEnvelopeItemHolder.getInstance();
		ActivityRedEnvelopeTypeItem dataItem = dataHolder.getItem(player.getUserId());
		ActivityRedEnvelopeTypeCfg Cfg = ActivityRedEnvelopeTypeCfgDAO.getInstance().getCfgById(dataItem.getCfgId());
		List<ActivityRedEnvelopeTypeSubItem> subItemList = dataItem.getSubItemList();
		if (ActivityTypeHelper.getDayBy5Am(Cfg.getStartTime()) > subItemList.size()) {
			// 活动开了n天，但子项只有m<n个；在m天之后n天之前的消费会到这里
			return;
		}
		for (ActivityRedEnvelopeTypeSubItem subItem : subItemList) {
			if (subItem.getDay() == ActivityTypeHelper.getDayBy5Am(Cfg.getStartTime())) {
				subItem.setCount(subItem.getCount() + (countadd * subItem.getDiscount()) / 100);
				break;
			}
		}
		// target.setCount(target.getCount() + countadd);
		dataItem.setGoldCount(0);
		for (ActivityRedEnvelopeTypeSubItem subItem : subItemList) {
			dataItem.setGoldCount(dataItem.getGoldCount() + subItem.getCount());
		}
		dataHolder.updateItem(player, dataItem);
	}

	public boolean isOpen() {
		for (ActivityRedEnvelopeTypeCfg cfg : ActivityRedEnvelopeTypeCfgDAO.getInstance().getAllCfg()) {
			if (isOpen(cfg)) {
				return true;
			}
		}
		return false;
	}

	public ActivityComResult takeGift(Player player) {
		ActivityComResult result = ActivityComResult.newInstance(false);
		ActivityRedEnvelopeItemHolder dataHolder = ActivityRedEnvelopeItemHolder.getInstance();
		ActivityRedEnvelopeTypeItem dataItem = dataHolder.getItem(player.getUserId());
		if (!isCanTakeGift(dataItem)) {
			result.setReason("不在领奖时间");
			return result;
		}
		if (dataItem.isIstaken()) {
			result.setReason("已经领取");
			return result;
		}
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		map.put(eSpecialItemId.Gold.getValue(), dataItem.getGoldCount());
		ItemBagMgr.getInstance().useLikeBoxItem(player, null, null, map);
		dataItem.setIstaken(true);
		result.setSuccess(true);
		result.setReason("领取成功");
		dataHolder.updateItem(player, dataItem);
		return result;
	}

	@Override
	public void updateRedPoint(Player player, String target) {
		ActivityRedEnvelopeItemHolder activityRedEnvelopeTypeItemHolder = new ActivityRedEnvelopeItemHolder();
		ActivityRedEnvelopeTypeCfg cfg = ActivityRedEnvelopeTypeCfgDAO.getInstance().getCfgById(target);
		if (cfg == null) {
			return;
		}
		ActivityRedEnvelopeTypeItem dataItem = activityRedEnvelopeTypeItemHolder.getItem(player.getUserId());
		if (dataItem == null) {
			return;
		}
		if (!dataItem.isTouchRedPoint()) {
			dataItem.setTouchRedPoint(true);
			activityRedEnvelopeTypeItemHolder.updateItem(player, dataItem);
		}

	}

	public boolean isOpen(long param) {
		List<ActivityRedEnvelopeTypeCfg> list = ActivityRedEnvelopeTypeCfgDAO.getInstance().getAllCfg();
		for (ActivityRedEnvelopeTypeCfg cfg : list) {
			if (isOpen(cfg, param)) {
				return true;
			}
		}
		return false;
	}

	private boolean isOpen(ActivityRedEnvelopeTypeCfg cfg, long param) {
		if (cfg != null) {
			long startTime = cfg.getStartTime();
			long endTime = cfg.getGetRewardsTime();
			long currentTime = param;
			return currentTime < endTime && currentTime >= startTime;
		}
		return false;
	}

	public List<String> haveRedPoint(Player player) {
		List<String> redPointList = new ArrayList<String>();
		ActivityRedEnvelopeItemHolder redEnvelopeHolder = ActivityRedEnvelopeItemHolder.getInstance();
		List<ActivityRedEnvelopeTypeItem> itemList = null;
		List<ActivityRedEnvelopeTypeCfg> cfgList = ActivityRedEnvelopeTypeCfgDAO.getInstance().getAllCfg();
		for (ActivityRedEnvelopeTypeCfg cfg : cfgList) {
			if (!isOpen(cfg) && !isCanTakeGift(cfg)) {
				continue;
			}
			if (cfg.getLevelLimit() > player.getLevel()) {
				continue;
			}

			if (itemList == null) {
				itemList = redEnvelopeHolder.getItemList(player.getUserId());
			}
			ActivityRedEnvelopeTypeItem item = null;// itemList.get(Integer.parseInt(ActivityRedEnvelopeTypeEnum.redEnvelope.getCfgId()));
			for (ActivityRedEnvelopeTypeItem temp : itemList) {
				if (StringUtils.equals(temp.getId() + "", ActivityRedEnvelopeTypeEnum.redEnvelope.getCfgId())) {
					item = temp;
					break;
				}
			}
			if (item == null) {
				continue;
			}
			if (!item.isTouchRedPoint()) {
				redPointList.add(item.getCfgId());
				continue;
			}
		}
		return redPointList;
	}

	@Override
	public boolean isThisActivityIndex(int index) {
		return index < ACTIVITY_INDEX_END && index > ACTIVITY_INDEX_BEGIN;
	}
}
