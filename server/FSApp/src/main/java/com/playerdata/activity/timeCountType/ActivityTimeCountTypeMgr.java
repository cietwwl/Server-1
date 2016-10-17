package com.playerdata.activity.timeCountType;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItem;
import com.playerdata.activity.timeCardType.cfg.ActivityTimeCardTypeCfg;
import com.playerdata.activity.timeCardType.cfg.ActivityTimeCardTypeCfgDAO;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeItem;
import com.playerdata.activity.timeCountType.cfg.ActivityTimeCountTypeCfg;
import com.playerdata.activity.timeCountType.cfg.ActivityTimeCountTypeCfgDAO;
import com.playerdata.activity.timeCountType.cfg.ActivityTimeCountTypeSubCfg;
import com.playerdata.activity.timeCountType.cfg.ActivityTimeCountTypeSubCfgDAO;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeItem;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeItemHolder;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeSubItem;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.util.DateUtils;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.template.BIActivityCode;
import com.rw.service.log.template.BILogTemplateHelper;
import com.rw.service.log.template.BilogItemInfo;

public class ActivityTimeCountTypeMgr {

	private static ActivityTimeCountTypeMgr instance = new ActivityTimeCountTypeMgr();

	private final static int MAKEUPEMAIL = 10055;

	public static ActivityTimeCountTypeMgr getInstance() {
		return instance;
	}

	public void synTimeCountTypeData(Player player) {
		doTimeCount(player, ActivityTimeCountTypeEnum.role_online);
		ActivityTimeCountTypeItemHolder.getInstance().synAllData(player);
	}

	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空 */
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
//		checkVersion(player);
		checkClose(player);

	}



	private void checkNewOpen(Player player) {
//		String userId= player.getUserId();
//		creatItems(userId, true);		
	}
	
	public List<ActivityTimeCountTypeItem> creatItems(String userId,boolean isHasPlayer){		

		
		List<ActivityTimeCountTypeCfg> allCfgList = ActivityTimeCountTypeCfgDAO.getInstance().getAllCfg();
		List<ActivityTimeCountTypeItem> addItemList = null;		
		
		for (ActivityTimeCountTypeCfg cfg : allCfgList) {// 遍历种类*各类奖励数次数,生成开启的种类个数空数据			
			if (!isOpen(cfg)) {
				// 活动未开启
				continue;
			}
			ActivityTimeCountTypeEnum TimeCountTypeEnum = ActivityTimeCountTypeEnum.getById(cfg.getId());
			
			if (TimeCountTypeEnum == null) {
				continue;
			}
			int id = Integer.parseInt(TimeCountTypeEnum.getCfgId());
//			String itemId = ActivityTimeCountTypeHelper.getItemId(userId,TimeCountTypeEnum);
				
			ActivityTimeCountTypeItem item = new ActivityTimeCountTypeItem();
			item.setId(id);
			item.setCfgId(TimeCountTypeEnum.getCfgId());
			item.setUserId(userId);
			item.setVersion(cfg.getVersion());
			item.setCount(1);
			List<ActivityTimeCountTypeSubItem> newItemList = new ArrayList<ActivityTimeCountTypeSubItem>();
			List<ActivityTimeCountTypeSubCfg> subItemCfgList = ActivityTimeCountTypeSubCfgDAO.getInstance().getByParentCfgId(cfg.getId());
			if(subItemCfgList == null){
				subItemCfgList = new ArrayList<ActivityTimeCountTypeSubCfg>();
			}
			for (ActivityTimeCountTypeSubCfg subCfg : subItemCfgList) {
				ActivityTimeCountTypeSubItem subItem = new ActivityTimeCountTypeSubItem();
				subItem.setCfgId(subCfg.getId());	
				subItem.setTaken(false);
				newItemList.add(subItem);
			}	
			item.setSubItemList(newItemList);
			
			if (addItemList == null) {
				addItemList = new ArrayList<ActivityTimeCountTypeItem>();
			}
			if (addItemList.size() >= 1) {
				// 同时生成了两条以上数据；
				GameLog.error(LogModule.ComActivityTimeCount, userId, "同时有多个活动开启", null);
				continue;
			}
//			Player player = PlayerMgr.getInstance().find(userId);//蛋疼
//			if(player != null){
//				biLogMgr.logActivityBegin(player, null, BIActivityCode.ACTIVITY_TIME_COUNT_PACKAGE,0,0);	
//			}			
			
			addItemList.add(item);					
		}		
		
		
		return addItemList;
	}
	
	private void checkVersion(Player player) {
//		if (!StringUtils.equals(targetItem.getVersion(), cfg.getVersion())) {//需求是一次性永久判断，一般不会更改版本号。
//			targetItem.reset(cfg, cfgDao.newItemList(player, cfg));
//			dataHolder.updateItem(player, targetItem);
//		}
		
	}
	
	private void checkClose(Player player) {
		ActivityTimeCountTypeItemHolder dataHolder = ActivityTimeCountTypeItemHolder.getInstance();
		ActivityTimeCountTypeSubCfgDAO activityTimeCountTypeSubCfgDAO = ActivityTimeCountTypeSubCfgDAO.getInstance();
		ComGiftMgr comGiftMgr = ComGiftMgr.getInstance();
		List<ActivityTimeCountTypeItem> itemList = dataHolder.getItemList(player.getUserId());

		for (ActivityTimeCountTypeItem activityTimeCountTypeItem : itemList) {// 每种活动
			if (isClose(activityTimeCountTypeItem)) {

				List<ActivityTimeCountTypeSubItem> list = activityTimeCountTypeItem.getSubItemList();
				for (ActivityTimeCountTypeSubItem subItem : list) {// 配置表里的每种奖励
					ActivityTimeCountTypeSubCfg subItemCfg = activityTimeCountTypeSubCfgDAO.getById(subItem.getCfgId());
					if(subItemCfg == null){
						continue;
					}					
					if (!subItem.isTaken() && activityTimeCountTypeItem.getCount() >= subItemCfg.getCount()) {

						boolean isAdd = comGiftMgr.addGiftTOEmailById(player, subItemCfg.getGiftId(), MAKEUPEMAIL + "","");
						if (isAdd) {
							subItem.setTaken(true);
						}
					}
				}
				activityTimeCountTypeItem.setClosed(true);
				dataHolder.updateItem(player, activityTimeCountTypeItem);
			}
		}

	}

	private boolean isClose(ActivityTimeCountTypeItem activityTimeCountTypeItem) {

		ActivityTimeCountTypeCfg cfgById = ActivityTimeCountTypeCfgDAO.getInstance().getCfgById(activityTimeCountTypeItem.getCfgId());
		if(cfgById == null){
			return false;
		}		
		long endTime = cfgById.getEndTime();
		long currentTime = System.currentTimeMillis();

		return currentTime > endTime;
	}

	private boolean isOpen(ActivityTimeCountTypeCfg activityTimeCountTypeCfg) {

		if (activityTimeCountTypeCfg != null) {
			long startTime = activityTimeCountTypeCfg.getStartTime();
			long endTime = activityTimeCountTypeCfg.getEndTime();
			long currentTime = System.currentTimeMillis();
			return currentTime < endTime && currentTime >= startTime;
		}
		return false;
	}

	public void doTimeCount(Player player, ActivityTimeCountTypeEnum TimeCountType) {
		ActivityTimeCountTypeItemHolder dataHolder = ActivityTimeCountTypeItemHolder.getInstance();

		ActivityTimeCountTypeItem dataItem = dataHolder.getItem(player.getUserId(), TimeCountType);

		if (dataItem == null) {

			return;
		}
		if (dataItem.isClosed()) {
			// 玩家已领取完奖励
			return;
		}
		long currentTimeMillis = System.currentTimeMillis();
		long lastCountTime = dataItem.getLastCountTime();
		long timeSpan = currentTimeMillis - lastCountTime;
		boolean istimeout = false;
		boolean isAllGet = true;
		ActivityTimeCountTypeSubCfgDAO subDao = ActivityTimeCountTypeSubCfgDAO.getInstance();
		List<ActivityTimeCountTypeSubCfg> subCfgList = subDao.getAllCfg();
		for (ActivityTimeCountTypeSubCfg cfg : subCfgList) {
			if (cfg.getCount() >= dataItem.getCount()) {
				isAllGet = false;
				break;
			}
		}
		
		List<ActivityTimeCountTypeSubItem> subList = dataItem.getSubItemList();
		for(ActivityTimeCountTypeSubItem subItem : subList){
			ActivityTimeCountTypeSubCfg subCfg = subDao.getById(subItem.getCfgId());
			if(subCfg.getCount() <= dataItem.getCount()&&(!subItem.isTaken())){
				istimeout = true;
			}
		}
		
		
		
		if (istimeout) {
			// 某个礼包处于可领取状态，不加时间
			dataItem.setLastCountTime(currentTimeMillis);
			dataHolder.lazyUpdateItem(player, dataItem);
			return;
		}
		if(isAllGet){
			//所有礼包全部领完
			dataHolder.lazyUpdateItem(player, dataItem);
			return;
		}
		if (timeSpan < ActivityTimeCountTypeHelper.FailCountTimeSpanInSecond * 1000) {
			// 过长时间没有响应，比如离线
			dataItem.setCount(dataItem.getCount() + (int) (timeSpan / 1000));

		}
		// 礼包处于有效计时状态
		dataItem.setLastCountTime(currentTimeMillis);
		dataHolder.lazyUpdateItem(player, dataItem);
	}

	
	

	public ActivityComResult takeGift(Player player, ActivityTimeCountTypeEnum TimeCountType, String subItemId) {		
		ActivityTimeCountTypeItemHolder dataHolder = ActivityTimeCountTypeItemHolder.getInstance();
		doTimeCount(player, TimeCountType);
		ActivityTimeCountTypeItem dataItem = dataHolder.getItem(player.getUserId(), TimeCountType);
		ActivityComResult result = ActivityComResult.newInstance(false);

		// 未激活
		if (dataItem == null) {
			result.setReason("活动尚未开启");

		} else {
			ActivityTimeCountTypeSubItem targetItem = null;
			
			ActivityTimeCountTypeSubCfg subcfg = null;
			List<ActivityTimeCountTypeSubCfg> subCfgList = ActivityTimeCountTypeSubCfgDAO.getInstance().getAllCfg();
			for(ActivityTimeCountTypeSubCfg cfg : subCfgList){
				if(StringUtils.equals(cfg.getId(), subItemId)){
					subcfg = cfg;
					break;
				}				
			}		
			if(dataItem.getCount() < subcfg.getCount()){
				result.setReason("时间未到");
				return result;
			}
			

			List<ActivityTimeCountTypeSubItem> subItemList = dataItem.getSubItemList();
			for (ActivityTimeCountTypeSubItem itemTmp : subItemList) {
				if (StringUtils.equals(itemTmp.getCfgId(), subItemId)) {
					targetItem = itemTmp;
					break;
				}
			}
			
			if (targetItem != null && !targetItem.isTaken()) {			
				dataItem.setCount(1);
				dataItem.setLastCountTime(DateUtils.getSecondLevelMillis());
				takeGift(player, targetItem);
				result.setSuccess(true);
				result.setReason("领取成功");
				checkGiftIsAllTake(player,dataItem);
				dataHolder.updateItem(player, dataItem);
			}
		}
		return result;
	}
	/**此类活动室无限开启，直到奖励全部领取完毕为止*/
	private void checkGiftIsAllTake(Player player,ActivityTimeCountTypeItem dataItem) {
		List<ActivityTimeCountTypeSubItem> subItemList = dataItem.getSubItemList();
		boolean isTakeAll = true;
		for (ActivityTimeCountTypeSubItem itemTmp : subItemList) {
			if(!itemTmp.isTaken()){
				isTakeAll = false;
			}
		}
		if(!isTakeAll){
			BILogMgr.getInstance().logActivityBegin(player, null, BIActivityCode.ACTIVITY_TIME_COUNT_PACKAGE,0,0);
		}
		dataItem.setClosed(isTakeAll);
		
	}

	private void takeGift(Player player, ActivityTimeCountTypeSubItem targetItem) {	
		
		ActivityTimeCountTypeSubCfg subCfg = ActivityTimeCountTypeSubCfgDAO.getInstance().getById(targetItem.getCfgId());
		if(subCfg == null){
			return;
		}	
		targetItem.setTaken(true);
		ComGiftMgr.getInstance().addGiftById(player, subCfg.getGiftId());
		List<BilogItemInfo> rewardslist = BilogItemInfo.fromComGiftID(subCfg.getGiftId());
		String rewardInfoActivity = BILogTemplateHelper.getString(rewardslist);		
		BILogMgr.getInstance().logActivityEnd(player, null, BIActivityCode.ACTIVITY_TIME_COUNT_PACKAGE, 0, true, 0,rewardInfoActivity,0);

		
		
	}

	public boolean isOpen() {
			List<ActivityTimeCountTypeCfg> list = ActivityTimeCountTypeCfgDAO.getInstance().getAllCfg();
			return !(list == null||list.isEmpty());
	}
}
