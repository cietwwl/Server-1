package com.playerdata.activity.fortuneCatType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.common.serverdata.ServerCommonData;
import com.common.serverdata.ServerCommonDataHolder;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.fortuneCatType.cfg.ActivityFortuneCatTypeCfg;
import com.playerdata.activity.fortuneCatType.cfg.ActivityFortuneCatTypeCfgDAO;
import com.playerdata.activity.fortuneCatType.cfg.ActivityFortuneCatTypeSubCfg;
import com.playerdata.activity.fortuneCatType.cfg.ActivityFortuneCatTypeSubCfgDAO;
import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatTypeItem;
import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatTypeItemHolder;
import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatTypeSubItem;
import com.playerdata.activityCommon.AbstractActivityMgr;
import com.playerdata.activityCommon.UserActivityChecker;
import com.rw.fsutil.util.RandomUtil;
import com.rwproto.ActivityFortuneCatTypeProto.ActivityCommonRspMsg.Builder;
import com.rwproto.ActivityFortuneCatTypeProto.getRecord;

public class ActivityFortuneCatTypeMgr extends AbstractActivityMgr<ActivityFortuneCatTypeItem>{
	
	private static final int ACTIVITY_INDEX_BEGIN = 110000;
	private static final int ACTIVITY_INDEX_END = 120000;
	
	private final static int recordLength = 3;

	private static ActivityFortuneCatTypeMgr instance = new ActivityFortuneCatTypeMgr();

	public static ActivityFortuneCatTypeMgr getInstance() {
		return instance;
	}

	public ActivityComResult getGold(Player player, Builder rsp) {
		ActivityFortuneCatTypeItemHolder dataHolder = ActivityFortuneCatTypeItemHolder.getInstance();
		ActivityFortuneCatTypeItem item = dataHolder.getItem(player.getUserId(), String.valueOf(ActivityFortuneTypeEnum.FortuneCat.getCfgId()));
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
		int getGold = RandomUtil.getRandom().nextInt(length);
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
	 * 保存三条 {666,uid+gold 667,uid+gold 668,uid+gold}格式的摇奖数据
	 * @param tmp
	 *            
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

		if (map.size() < recordLength) {
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
		record.setId(num + recordLength);
		map.put(num + recordLength, record);
		ServerCommonDataHolder.getInstance().update(scdData);
	}

	private boolean isLevelEnough(Player player, ActivityFortuneCatTypeCfg cfg) {
		boolean iscan = false;
		iscan = player.getLevel() >= cfg.getLevelLimit() ? true : false;
		return iscan;
	}
	
	/**
	 * 此红点不和活动红点统一判断，所以没有实现父类的红点方法
	 * @param player
	 * @return
	 */
	public List<String> getRedPoint(Player player) {
		List<String> redPointList = new ArrayList<String>();
		List<ActivityFortuneCatTypeItem> items = getHolder().getItemList(player.getUserId());
		if (null == items || items.isEmpty()){
			return redPointList;
		}
		for (ActivityFortuneCatTypeItem item : items) {
			if(haveRedPoint(player, item)){
				redPointList.add(item.getCfgId());
			}
		}
		return redPointList;
	}
		
	private boolean haveRedPoint(Player player, ActivityFortuneCatTypeItem item) {
		if (!item.isTouchRedPoint()) {
			return true;
		}
		int rewardTimes = item.getTimes();
		int totalTimes = item.getSubItemList().size();
		if(rewardTimes < totalTimes){
			for(int i = rewardTimes; i < totalTimes; i++){
				ActivityFortuneCatTypeSubItem subItem = item.getSubItemList().get(i);
				if(player.getUserGameDataMgr().getGold() >= Integer.parseInt(subItem.getCost()) && player.getVip() >= subItem.getVip()){
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	protected UserActivityChecker<ActivityFortuneCatTypeItem> getHolder(){
		return ActivityFortuneCatTypeItemHolder.getInstance();
	}
	
	@Override
	public boolean isThisActivityIndex(int index){
		return index < ACTIVITY_INDEX_END && index > ACTIVITY_INDEX_BEGIN;
	}
}
