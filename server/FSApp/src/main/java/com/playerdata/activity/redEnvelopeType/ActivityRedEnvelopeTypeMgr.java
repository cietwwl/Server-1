package com.playerdata.activity.redEnvelopeType;




import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.ActivityTypeHelper;
import com.playerdata.activity.redEnvelopeType.cfg.ActivityRedEnvelopeTypeCfg;
import com.playerdata.activity.redEnvelopeType.cfg.ActivityRedEnvelopeTypeCfgDAO;
import com.playerdata.activity.redEnvelopeType.data.ActivityRedEnvelopeItemHolder;
import com.playerdata.activity.redEnvelopeType.data.ActivityRedEnvelopeTypeItem;
import com.playerdata.activity.redEnvelopeType.data.ActivityRedEnvelopeTypeSubItem;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.common.enu.eSpecialItemId;


public class ActivityRedEnvelopeTypeMgr {

	private static ActivityRedEnvelopeTypeMgr instance = new ActivityRedEnvelopeTypeMgr();

	private final static int MAKEUPEMAIL = 10055;

	public static ActivityRedEnvelopeTypeMgr getInstance() {
		return instance;
	}

	public void synRedEnvelopeTypeData(Player player) {
		ActivityRedEnvelopeItemHolder.getInstance().synAllData(player);
	}
	


	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空 */
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
		checkCfgVersion(player);
		checkOtherDay(player);
		checkClose(player);
	}
	
	private void checkNewOpen(Player player) {
		ActivityRedEnvelopeItemHolder dataHolder = ActivityRedEnvelopeItemHolder.getInstance();
		ActivityRedEnvelopeTypeCfg cfg = ActivityRedEnvelopeTypeCfgDAO.getInstance().getCfgById(ActivityRedEnvelopeTypeEnum.redEnvelope.getCfgId());
		if (!isOpen(cfg)) {
			// 活动未开启
			return;
		}
		ActivityRedEnvelopeTypeItem targetItem = dataHolder.getItem(player.getUserId());// 已在之前生成数据的活动
		if(targetItem != null){
			return;
		}
		targetItem = ActivityRedEnvelopeTypeCfgDAO.getInstance().newItem(player,
				ActivityRedEnvelopeTypeEnum.redEnvelope);// 生成新开启活动的数据
		if (targetItem == null) {
			GameLog.error("ActivityRedEnvelopeTypeMgr", "#checkNewOpen()",
					"根据活动类型枚举找不到对应的cfg：" + cfg.getId());
			return;
		}
		dataHolder.addItem(player, targetItem);
	}
	
	private void checkCfgVersion(Player player) {
		ActivityRedEnvelopeItemHolder dataHolder = ActivityRedEnvelopeItemHolder
				.getInstance();
		ActivityRedEnvelopeTypeItem activityVitalityTypeItem = dataHolder
				.getItem(player.getUserId());
		if (activityVitalityTypeItem == null) {
			return;
		}
		ActivityRedEnvelopeTypeCfg cfg = ActivityRedEnvelopeTypeCfgDAO
				.getInstance().getCfgById(activityVitalityTypeItem.getCfgId());
		if (cfg == null) {
			dataHolder.removeItem(player, activityVitalityTypeItem);
			return;
		}
		ActivityRedEnvelopeTypeEnum cfgenum = ActivityRedEnvelopeTypeEnum
				.getById(cfg.getId());
		if (cfgenum == null) {
			dataHolder.removeItem(player, activityVitalityTypeItem);
			return;
		}
		if (!StringUtils.equals(activityVitalityTypeItem.getVersion(),
				cfg.getVersion())) {
			int day = ActivityTypeHelper.getDayBy5Am(cfg.getStartTime());
			List<ActivityRedEnvelopeTypeSubItem> subItemList = ActivityRedEnvelopeTypeCfgDAO
					.getInstance().getSubList();
			activityVitalityTypeItem.resetByVersion(cfg, subItemList, day);
			dataHolder.updateItem(player, activityVitalityTypeItem);
		}
}
//
	private void checkOtherDay(Player player) {
		ActivityRedEnvelopeItemHolder dataHolder = ActivityRedEnvelopeItemHolder.getInstance();		
		ActivityRedEnvelopeTypeItem item = dataHolder.getItem(player.getUserId());
		
		if(item == null){
			return;
		}
		if(!StringUtils.equals(ActivityRedEnvelopeTypeEnum.redEnvelope.getCfgId(), item.getCfgId() )){
			return;
		}
		ActivityRedEnvelopeTypeCfg cfg = ActivityRedEnvelopeTypeCfgDAO.getInstance().getCfgById(item.getCfgId());
		if(cfg == null ){
			return;
		}
		
		if (DateUtils.isNewDayHour(5,item.getLastTime())) {					
			int day = ActivityTypeHelper.getDayBy5Am(cfg.getStartTime());					
			item.resetByOtherday(cfg, day);
			dataHolder.updateItem(player, item);
		}			
	}
	
	
	private void checkClose(Player player) {
		ActivityRedEnvelopeItemHolder dataHolder = ActivityRedEnvelopeItemHolder.getInstance();
		ActivityRedEnvelopeTypeItem item = dataHolder.getItem(player.getUserId());
		if (!isClose(item)) {			
			return;	
		}
		item.setClosed(true);
		if(isCanTakeGift(item)){
			dataHolder.updateItem(player, item);
			return;
		}
		if(item.isIstaken()){
			dataHolder.updateItem(player, item);
			return;
		}
		List<ActivityRedEnvelopeTypeSubItem> subItemList = item.getSubItemList();
		for(ActivityRedEnvelopeTypeSubItem subItem : subItemList){
			item.setGoldCount(item.getGoldCount() + subItem.getCount());
		}
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		map.put(eSpecialItemId.Gold.getValue(), item.getGoldCount());
		player.getItemBagMgr().useLikeBoxItem(null, null, map);
		item.setIstaken(true);
		dataHolder.updateItem(player, item);
	}

	public boolean isOpen(ActivityRedEnvelopeTypeCfg vitalityCfg) {
		long startTime = vitalityCfg.getStartTime();
		long endTime = vitalityCfg.getEndTime();
		long currentTime = System.currentTimeMillis();
		return currentTime < endTime && currentTime > startTime;
	}

	public boolean isClose(ActivityRedEnvelopeTypeItem activityVitalityTypeItem) {
		if (activityVitalityTypeItem != null) {
			ActivityRedEnvelopeTypeCfg cfg = ActivityRedEnvelopeTypeCfgDAO.getInstance().getCfgById(activityVitalityTypeItem.getCfgId());			
			if(cfg == null){
				GameLog.error("activityRedEnvelopetypemgr","" , "配置文件找不到数据奎对应的活动");
				return false;
			}						
			long endTime = cfg.getEndTime();
			long currentTime = System.currentTimeMillis();
			return currentTime > endTime;			
		}
		return false;
	}
	
	private boolean isCanTakeGift(ActivityRedEnvelopeTypeItem item) {
		ActivityRedEnvelopeTypeCfg cfg = ActivityRedEnvelopeTypeCfgDAO.getInstance().getCfgById(item.getCfgId());	
		long takenTime = cfg.getGetRewardsTime();
		long currentTime = System.currentTimeMillis();
		long endTime = cfg.getEndTime();
		return currentTime > endTime&& currentTime< takenTime;
	}


	public void addCount(Player player,  int countadd) {
		ActivityRedEnvelopeItemHolder dataHolder = ActivityRedEnvelopeItemHolder.getInstance();
		ActivityRedEnvelopeTypeItem dataItem = dataHolder.getItem(player.getUserId());		
		List<ActivityRedEnvelopeTypeSubItem> subItemList = dataItem.getSubItemList();	
		if(ActivityTypeHelper.getDayBy5Am(System.currentTimeMillis())>subItemList.size()){
			//活动开了n天，但子项只有m<n个；在m天之后n天之前的消费会到这里
			GameLog.error(LogModule.ComActivityRedEnvelope, player.getUserId(), "活动开了n天，但子项只有m<n个；在m天之后n天之前的消费会到这里", null);
			return;
		}
		ActivityRedEnvelopeTypeSubItem target = null;
		for(ActivityRedEnvelopeTypeSubItem subItem: subItemList){
			if(subItem.getDay() == ActivityTypeHelper.getDayBy5Am(System.currentTimeMillis())){
				target = subItem;
				break;
			}			
		}
		target.setCount(target.getCount() + countadd);		
		dataHolder.updateItem(player, dataItem);
	}
	
	
	
	public ActivityComResult takeGift(Player player) {
		ActivityComResult result = ActivityComResult.newInstance(false);
		ActivityRedEnvelopeItemHolder dataHolder = ActivityRedEnvelopeItemHolder.getInstance();
		ActivityRedEnvelopeTypeItem dataItem = dataHolder.getItem(player.getUserId());
		
		
		if(!isCanTakeGift(dataItem)){
			result.setReason("不在领奖时间");
			return result;
		}
		if(dataItem.isIstaken()){
			result.setReason("已经领取");	
			return result;
		}


		List<ActivityRedEnvelopeTypeSubItem> subItemList = dataItem.getSubItemList();
		for(ActivityRedEnvelopeTypeSubItem subItem : subItemList){
			dataItem.setGoldCount(dataItem.getGoldCount() + subItem.getCount());
		}
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		map.put(eSpecialItemId.Gold.getValue(), dataItem.getGoldCount());
		player.getItemBagMgr().useLikeBoxItem(null, null, map);
				
		dataItem.setIstaken(true);
		result.setSuccess(true);
		result.setReason("领取成功");
		dataHolder.updateItem(player, dataItem);


		return result;
	}	

}
