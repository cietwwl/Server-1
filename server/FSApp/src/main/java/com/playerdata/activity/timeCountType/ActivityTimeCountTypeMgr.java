package com.playerdata.activity.timeCountType;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.timeCountType.cfg.ActivityTimeCountTypeCfg;
import com.playerdata.activity.timeCountType.cfg.ActivityTimeCountTypeCfgDAO;
import com.playerdata.activity.timeCountType.cfg.ActivityTimeCountTypeSubCfg;
import com.playerdata.activity.timeCountType.cfg.ActivityTimeCountTypeSubCfgDAO;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeItem;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeItemHolder;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeSubItem;
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
		checkClose(player);

	}

	private void checkNewOpen(Player player) {
		ActivityTimeCountTypeItemHolder dataHolder = ActivityTimeCountTypeItemHolder.getInstance();
		List<ActivityTimeCountTypeCfg> allCfgList = ActivityTimeCountTypeCfgDAO.getInstance().getAllCfg();
		ArrayList<ActivityTimeCountTypeItem> addItemList = null;
		for (ActivityTimeCountTypeCfg activityTimeCountTypeCfg : allCfgList) {// 遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if (!isOpen(activityTimeCountTypeCfg)) {
				// 活动未开启
				continue;
			}
			ActivityTimeCountTypeEnum TimeCountTypeEnum = ActivityTimeCountTypeEnum.getById(activityTimeCountTypeCfg.getId());
			if (TimeCountTypeEnum == null) {
				GameLog.error("ActivityTimeCountTypeMgr", "#checkNewOpen()", "找不到活动类型：" + activityTimeCountTypeCfg.getId());
				continue;
			}
			ActivityTimeCountTypeItem targetItem = dataHolder.getItem(player.getUserId(), TimeCountTypeEnum);// 已在之前生成数据的活动
			if (targetItem == null) {
				targetItem = ActivityTimeCountTypeCfgDAO.getInstance().newItem(player, TimeCountTypeEnum);// 生成新开启活动的数据
				if (targetItem == null) {
					// logger
					continue;
				}
				if (addItemList == null) {
					addItemList = new ArrayList<ActivityTimeCountTypeItem>();
				}
				addItemList.add(targetItem);
				BILogMgr.getInstance().logActivityBegin(player, null, BIActivityCode.ACTIVITY_TIME_COUNT_PACKAGE,0,0);
			} else {
				if (!StringUtils.equals(targetItem.getVersion(), activityTimeCountTypeCfg.getVersion())) {//需求是一次性永久判断，一般不会更改版本号。
					targetItem.reset(activityTimeCountTypeCfg, ActivityTimeCountTypeCfgDAO.getInstance().newItemList(player, activityTimeCountTypeCfg));
					dataHolder.updateItem(player, targetItem);
				}
			}
		}
		if (addItemList != null) {
			dataHolder.addItemList(player, addItemList);
		}
	}

	private void checkClose(Player player) {
		ActivityTimeCountTypeItemHolder dataHolder = ActivityTimeCountTypeItemHolder.getInstance();
		List<ActivityTimeCountTypeItem> itemList = dataHolder.getItemList(player.getUserId());

		for (ActivityTimeCountTypeItem activityTimeCountTypeItem : itemList) {// 每种活动
			if (isClose(activityTimeCountTypeItem)) {

				List<ActivityTimeCountTypeSubItem> list = activityTimeCountTypeItem.getSubItemList();
				for (ActivityTimeCountTypeSubItem subItem : list) {// 配置表里的每种奖励
					ActivityTimeCountTypeSubCfg subItemCfg = ActivityTimeCountTypeSubCfgDAO.getInstance().getById(subItem.getCfgId());
					if(subItemCfg == null){
						GameLog.error(LogModule.ComActivityTimeCount, player.getUserId(), "通用活动找不到配置文件", null);
						continue;
					}					
					if (!subItem.isTaken() && activityTimeCountTypeItem.getCount() >= subItemCfg.getCount()) {

						boolean isAdd = ComGiftMgr.getInstance().addGiftTOEmailById(player, subItemCfg.getGiftId(), MAKEUPEMAIL + "","");
						if (isAdd) {
							subItem.setTaken(true);
						} else {
							GameLog.error(LogModule.ComActivityTimeCount, player.getUserId(), "通用活动关闭后未领取奖励获取邮件内容失败", null);
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
			GameLog.error(LogModule.ComActivityTimeCount, null, "通用活动找不到配置文件", null);
			return true;
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
		boolean istimeout = true;
		List<ActivityTimeCountTypeSubCfg> subCfgList = ActivityTimeCountTypeSubCfgDAO
				.getInstance().getAllCfg();
		for (ActivityTimeCountTypeSubCfg cfg : subCfgList) {
			if (cfg.getCount() >= dataItem.getCount()) {
				istimeout = false;
				break;
			}
		}
		if (istimeout) {
			// 礼包处于可领取状态
			return;
		}
		if (timeSpan < ActivityTimeCountTypeHelper.FailCountTimeSpanInSecond * 1000) {
			// 过长时间没有响应，比如离线
			dataItem.setCount(dataItem.getCount() + (int) (timeSpan / 1000));

		}
		// 礼包处于有效计时状态
		dataItem.setLastCountTime(currentTimeMillis);
		dataHolder.updateItem(player, dataItem);
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
				GameLog.error("activitytimecounttypemgr", "uid=" + player.getUserId(), "时间未到但申请领取");
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
			GameLog.error(LogModule.ComActivityTimeCount, player.getUserId(), "通用活动找不到配置文件", null);
			return;
		}	
		targetItem.setTaken(true);
		ComGiftMgr.getInstance().addGiftById(player, subCfg.getGiftId());
		List<BilogItemInfo> rewardslist = BilogItemInfo.fromComGiftID(subCfg.getGiftId());
		String rewardInfoActivity = BILogTemplateHelper.getString(rewardslist);		
		BILogMgr.getInstance().logActivityEnd(player, null, BIActivityCode.ACTIVITY_TIME_COUNT_PACKAGE, 0, true, 0,rewardInfoActivity,0);

		
		
	}
}
