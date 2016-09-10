package com.playerdata.activity.fortuneCatType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import com.common.serverdata.ServerCommonData;
import com.common.serverdata.ServerCommonDataHolder;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.ActivityRedPointUpdate;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItem;
import com.playerdata.activity.fortuneCatType.cfg.ActivityFortuneCatTypeCfg;
import com.playerdata.activity.fortuneCatType.cfg.ActivityFortuneCatTypeCfgDAO;
import com.playerdata.activity.fortuneCatType.cfg.ActivityFortuneCatTypeSubCfg;
import com.playerdata.activity.fortuneCatType.cfg.ActivityFortuneCatTypeSubCfgDAO;
import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatTypeItem;
import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatTypeItemHolder;
import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatTypeSubItem;
import com.rw.dataaccess.mapitem.MapItemValidateParam;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwproto.ActivityFortuneCatTypeProto.getRecord;
import com.rwproto.ActivityFortuneCatTypeProto.ActivityCommonRspMsg.Builder;

public class ActivityFortuneCatTypeMgr implements ActivityRedPointUpdate {

	private static Random r = new Random();

	private static ActivityFortuneCatTypeMgr instance = new ActivityFortuneCatTypeMgr();

	public static ActivityFortuneCatTypeMgr getInstance() {
		return instance;
	}

	public void synFortuneCatTypeData(Player player) {
		ActivityFortuneCatTypeItemHolder.getInstance().synAllData(player);
	}

	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空 */
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
		checkCfgVersion(player);
		checkClose(player);
	}

	private void checkNewOpen(Player player) {
		ActivityFortuneCatTypeItemHolder dataHolder = ActivityFortuneCatTypeItemHolder.getInstance();
		List<ActivityFortuneCatTypeItem> addItemList = null;
		String userId = player.getUserId();
		addItemList = creatItems(userId, dataHolder.getItemStore(userId));
		if (addItemList != null) {
			for(ActivityFortuneCatTypeItem item : addItemList ){
				System.out.println("~~~~~~~~~~~~~~~fortunecat.id = " + item.getId());
			}
			dataHolder.addItemList(player, addItemList);
		}
	}

	public List<ActivityFortuneCatTypeItem> creatItems(String userId, MapItemStore<ActivityFortuneCatTypeItem> itemStore) {
		List<ActivityFortuneCatTypeItem> addItemList = null;
		List<ActivityFortuneCatTypeCfg> allCfgList = ActivityFortuneCatTypeCfgDAO.getInstance().getAllCfg();
		for (ActivityFortuneCatTypeCfg cfg : allCfgList) {// 遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if (itemStore != null) {
				if (itemStore.getItem(userId) != null) {
					return addItemList;
				}
			}
			if (!isOpen(cfg)) {
				// 活动未开启
				continue;
			}
			ActivityFortuneCatTypeItem item = new ActivityFortuneCatTypeItem();
			item.setId(userId);
			item.setUserId(userId);
			item.setCfgId(cfg.getId());
			item.setVersion(cfg.getVersion());			
			item.setTimes(0);
			List<ActivityFortuneCatTypeSubItem> subItemList = new ArrayList<ActivityFortuneCatTypeSubItem>();
			List<ActivityFortuneCatTypeSubCfg> subCfgList = ActivityFortuneCatTypeSubCfgDAO.getInstance().getCfgListByParentId(cfg.getId());
			if(subCfgList == null){
				subCfgList = new ArrayList<ActivityFortuneCatTypeSubCfg>();
			}
			for(ActivityFortuneCatTypeSubCfg subCfg : subCfgList){
				ActivityFortuneCatTypeSubItem subitem = new ActivityFortuneCatTypeSubItem();
				subitem.setCfgId(subCfg.getId()+"");
				subitem.setNum(subCfg.getNum());
				subitem.setCost(subCfg.getCost()+"");
				subitem.setVip(subCfg.getVip());
				subitem.setGetGold(0);
				subItemList.add(subitem);
			}
			item.setSubItemList(subItemList);
			if (addItemList == null) {
				addItemList = new ArrayList<ActivityFortuneCatTypeItem>();
			}
			if (addItemList.size() >= 1) {
				// 同时生成了两条以上数据；
				GameLog.error(LogModule.ComActivityFortuneCat, userId, "同时有多个活动开启", null);
				continue;
			}
			addItemList.add(item);
		}
		return addItemList;
	}

	public boolean isOpen(ActivityFortuneCatTypeCfg cfg) {
		if (cfg != null) {

			long startTime = cfg.getStartTime();
			long endTime = cfg.getEndTime();
			long currentTime = System.currentTimeMillis();
			return currentTime < endTime && currentTime >= startTime;
		}
		return false;
	}

	private void checkCfgVersion(Player player) {
		ActivityFortuneCatTypeItemHolder dataHolder = ActivityFortuneCatTypeItemHolder.getInstance();
		ActivityFortuneCatTypeCfgDAO activityFortuneCatTypeCfgDAO = ActivityFortuneCatTypeCfgDAO.getInstance();
		List<ActivityFortuneCatTypeItem> itemList = dataHolder.getItemList(player.getUserId());
		for (ActivityFortuneCatTypeItem targetItem : itemList) {
			ActivityFortuneCatTypeCfg targetCfg = activityFortuneCatTypeCfgDAO.getCfgListByItem(targetItem);
			if (targetCfg == null) {
				GameLog.error(LogModule.ComActivityFortuneCat, null, "通用活动找不到配置文件", null);
				continue;
			}

			if (!StringUtils.equals(targetItem.getVersion(), targetCfg.getVersion())) {
				targetItem.reset(targetCfg, activityFortuneCatTypeCfgDAO.newSubItemList(targetCfg));
				dataHolder.updateItem(player, targetItem);
			}
		}
	}

	private void checkClose(Player player) {
		ActivityFortuneCatTypeItemHolder dataHolder = ActivityFortuneCatTypeItemHolder.getInstance();
		ActivityFortuneCatTypeCfgDAO activityFortuneCatTypeCfgDAO = ActivityFortuneCatTypeCfgDAO.getInstance();
		List<ActivityFortuneCatTypeItem> itemList = dataHolder.getItemList(player.getUserId());
		for (ActivityFortuneCatTypeItem item : itemList) {
			if (item.isClosed()) {
				continue;
			}
			ActivityFortuneCatTypeCfg cfg = activityFortuneCatTypeCfgDAO.getCfgById(item.getCfgId());
			if (cfg == null) {
				GameLog.error(LogModule.ComActivityFortuneCat, player.getUserId(), "玩家登录时服务器配置表已更新，只能通过版本核实来刷新数据", null);
				continue;
			}
			if (isOpen(cfg)) {
				continue;
			}
			item.setClosed(true);
			item.setTouchRedPoint(true);
			dataHolder.updateItem(player, item);
		}
	}

	public ActivityComResult getGold(Player player, Builder rsp) {
		ActivityFortuneCatTypeItemHolder dataHolder = ActivityFortuneCatTypeItemHolder.getInstance();
		ActivityFortuneCatTypeItem item = dataHolder.getItem(player.getUserId());
		ActivityComResult result = ActivityComResult.newInstance(false);
		if (item == null) {
			result.setReason("活动数据异常，活动未开启");
			return result;
		}
		ActivityFortuneCatTypeCfg cfg = ActivityFortuneCatTypeCfgDAO.getInstance().getCfgById(item.getCfgId());
		if (cfg == null) {
			result.setReason("活动配置异常，");
			return result;
		}
		if (!isLevelEnough(player, cfg)) {
			result.setReason("等级不足，");
			return result;
		}
		List<ActivityFortuneCatTypeSubItem> subItemList = item.getSubItemList();
		int times = item.getTimes() + 1;
		ActivityFortuneCatTypeSubItem sub = null;
		for (ActivityFortuneCatTypeSubItem subItem : subItemList) {
			if (times == subItem.getNum() && subItem.getGetGold() == 0) {
				sub = subItem;
				break;
			}
		}
		if (sub == null) {
			result.setReason("摇奖数据异常，");
			return result;
		}
		ActivityFortuneCatTypeSubCfg subCfg = ActivityFortuneCatTypeSubCfgDAO.getInstance().getCfgById(sub.getCfgId());
		if (subCfg == null) {
			result.setReason("子活动配置异常，");
			return result;
		}
		if (player.getVip() < sub.getVip()) {
			result.setReason("vip不足，");
			return result;
		}
		if (player.getUserGameDataMgr().getGold() < Integer.parseInt(sub.getCost())) {
			result.setReason("钻石不足，");
			return result;
		}
		int length = subCfg.getMax() - subCfg.getCost();// 实际获得区间
		if (length < 1) {
			result.setReason(" 获得钻石数异常，");
			return result;
		}
		int getGold = r.nextInt(length);
		int wavepeak = (subCfg.getMax() + subCfg.getMin()) / 2;// 波峰x值
		int wavewidth = (subCfg.getMax() - subCfg.getMin()) / 6;// 波长/6
		getGold = (int) ActivityFortuneCatHelper.normalDistribution(wavepeak, wavewidth);// 获得彩票中的奖
		getGold = getGold - subCfg.getCost();// 扣去买彩票的钱
		player.getUserGameDataMgr().addGold(getGold);
		int tmpGold = getGold + subCfg.getCost();
		sub.setGetGold(tmpGold);// 写入的为额外获得+摇奖押金
		item.setTimes(times);
		dataHolder.updateItem(player, item);
		result.setSuccess(true);
		result.setReason("");
		rsp.setGetGold(tmpGold);
		reFreshRecord(player, tmpGold);
		return result;
	}

	public ActivityComResult getRecord(Player player, Builder response) {
		ActivityComResult result = ActivityComResult.newInstance(true);
		result.setReason("");
		ServerCommonData scdData = ServerCommonDataHolder.getInstance().get();
		if (scdData == null) {
			result.setSuccess(false);
			return result;
		}
		Map<Integer, ActivityFortuneCatRecord> map = scdData.getActivityFortuneCatRecord();
		for (Map.Entry<Integer, ActivityFortuneCatRecord> Entry : map.entrySet()) {
			ActivityFortuneCatRecord entry = Entry.getValue();
			getRecord.Builder record = getRecord.newBuilder();
			record.setId(entry.getId());
			record.setUid(entry.getUid());
			record.setName(entry.getPlayerName());
			record.setGetGold(entry.getGetGold());
			response.addGetRecord(record);
		}

		return result;
	}

	/**
	 * 
	 * @param tmp
	 *            保存三条 {666,uid+gold 667,uid+gold 668,uid+gold}格式的摇奖数据
	 */
	private void reFreshRecord(Player player, int getGold) {
		ServerCommonData scdData = ServerCommonDataHolder.getInstance().get();
		if (scdData == null) {
			return;
		}
		Map<Integer, ActivityFortuneCatRecord> map = scdData.getActivityFortuneCatRecord();
		ActivityFortuneCatRecord record = new ActivityFortuneCatRecord();

		record.setPlayerName(player.getUserName());
		record.setUid(player.getUserId());
		record.setGetGold(getGold);

		if (map.size() < 3) {
			record.setId(map.size());
			map.put(map.size(), record);
			ServerCommonDataHolder.getInstance().update(scdData);
			return;
		}
		int num = 0;
		int i = 0;
		for (Map.Entry<Integer, ActivityFortuneCatRecord> entry : map.entrySet()) {
			if (i == 0) {
				num = entry.getKey();
				i++;
				continue;
			}
			if (entry.getKey() < num) {
				num = entry.getKey();
			}
			i++;
		}
		map.remove(num);
		record.setId(num + 3);
		map.put(num + 3, record);
		ServerCommonDataHolder.getInstance().update(scdData);
	}

	public boolean isLevelEnough(Player player, ActivityFortuneCatTypeCfg cfg) {
		boolean iscan = false;
		iscan = player.getLevel() >= cfg.getLevelLimit() ? true : false;
		return iscan;
	}

	@Override
	public void updateRedPoint(Player player, String eNum) {
		ActivityFortuneCatTypeItemHolder activityFortuneCatTypeItemHolder = new ActivityFortuneCatTypeItemHolder();
		ActivityFortuneCatTypeCfg cfg = ActivityFortuneCatTypeCfgDAO.getInstance().getCfgById(eNum);
		if (cfg == null) {
			return;
		}

		ActivityFortuneCatTypeItem dataItem = activityFortuneCatTypeItemHolder.getItem(player.getUserId());
		if (dataItem == null) {
			GameLog.error(LogModule.ComActivityFortuneCat, player.getUserId(), "无法找到活动数据", null);
			return;
		}
		if (!dataItem.isTouchRedPoint()) {
			dataItem.setTouchRedPoint(true);
		}
		activityFortuneCatTypeItemHolder.updateItem(player, dataItem);
	}

	public boolean isOpen(MapItemValidateParam param) {
		List<ActivityFortuneCatTypeCfg> allCfgList = ActivityFortuneCatTypeCfgDAO.getInstance().getAllCfg();
		for (ActivityFortuneCatTypeCfg cfg : allCfgList) {
			if (isOpen(cfg, param)) {
				return true;
			}
		}
		return false;
	}

	public boolean isOpen(ActivityFortuneCatTypeCfg cfg, MapItemValidateParam param) {
		if (cfg != null) {

			long startTime = cfg.getStartTime();
			long endTime = cfg.getEndTime();
			long currentTime = param.getCurrentTime();
			return currentTime < endTime && currentTime >= startTime;
		}
		return false;
	}

}
