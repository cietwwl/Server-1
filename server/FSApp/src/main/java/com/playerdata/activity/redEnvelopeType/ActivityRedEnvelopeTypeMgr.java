package com.playerdata.activity.redEnvelopeType;




import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.ActivityRedPointUpdate;
import com.playerdata.activity.ActivityTypeHelper;
import com.playerdata.activity.rateType.cfg.ActivityRateTypeCfg;
import com.playerdata.activity.rateType.cfg.ActivityRateTypeCfgDAO;
import com.playerdata.activity.redEnvelopeType.cfg.ActivityRedEnvelopeTypeCfg;
import com.playerdata.activity.redEnvelopeType.cfg.ActivityRedEnvelopeTypeCfgDAO;
import com.playerdata.activity.redEnvelopeType.data.ActivityRedEnvelopeItemHolder;
import com.playerdata.activity.redEnvelopeType.data.ActivityRedEnvelopeTypeItem;
import com.playerdata.activity.redEnvelopeType.data.ActivityRedEnvelopeTypeSubItem;
import com.rw.dataaccess.mapitem.MapItemValidateParam;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.common.enu.eSpecialItemId;


public class ActivityRedEnvelopeTypeMgr implements ActivityRedPointUpdate{

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
		ActivityRedEnvelopeTypeCfgDAO activityRedEnvelopeTypeCfgDAO = ActivityRedEnvelopeTypeCfgDAO.getInstance();
		List<ActivityRedEnvelopeTypeCfg> cfgList = ActivityRedEnvelopeTypeCfgDAO.getInstance().getAllCfg();
		for(ActivityRedEnvelopeTypeCfg cfg : cfgList){
			if(!isOpen(cfg)){
				continue;
			}
			ActivityRedEnvelopeTypeItem targetItem = dataHolder.getItem(player.getUserId());
			if(targetItem != null){
				continue;
			}
			targetItem = activityRedEnvelopeTypeCfgDAO.newItem(player, cfg);
			if(targetItem == null){
				GameLog.error(LogModule.ComActivityRedEnvelope,player.getUserId(), "生成数据失败",null);
				continue;
			}
			dataHolder.addItem(player, targetItem);
		}
	}
	
	public boolean isOpen(ActivityRedEnvelopeTypeCfg cfg) {
		long startTime = cfg.getStartTime();
		long endTime = cfg.getEndTime();
		long currentTime = System.currentTimeMillis();
		return currentTime < endTime && currentTime >= startTime;
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
				.getInstance().getCfgByItemOfVersion(activityVitalityTypeItem);
		if (cfg == null) {
			return;
		}
		
		if (!StringUtils.equals(activityVitalityTypeItem.getVersion(),
				cfg.getVersion())) {
			int day = ActivityTypeHelper.getDayBy5Am(cfg.getStartTime());
			List<ActivityRedEnvelopeTypeSubItem> subItemList = ActivityRedEnvelopeTypeCfgDAO
					.getInstance().getSubList(cfg);
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
		ActivityRedEnvelopeTypeCfg cfg = ActivityRedEnvelopeTypeCfgDAO.getInstance().getCfgById(item.getCfgId());
		if(cfg == null ){
			return;
		}
		
		if (ActivityTypeHelper.isNewDayHourOfActivity(5,item.getLastTime())) {					
			int day = ActivityTypeHelper.getDayBy5Am(cfg.getStartTime());					
			item.resetByOtherday(cfg, day);
			dataHolder.updateItem(player, item);
		}			
	}
	
	
	private void checkClose(Player player) {
		ActivityRedEnvelopeItemHolder dataHolder = ActivityRedEnvelopeItemHolder.getInstance();
		ActivityRedEnvelopeTypeItem item = dataHolder.getItem(player.getUserId());
		if(item == null){
			return;
		}
		if (!isClose(item)) {			
			return;	
		}
		
		if(isCanTakeGift(item)){
			if(!item.isClosed()){
				item.setClosed(true);
				item.setTouchRedPoint(false);
				dataHolder.updateItem(player, item);
			}			
			return;
		}		
		if(item.isIstaken()){
			return;
		}

		ActivityRedEnvelopeTypeCfg cfg = ActivityRedEnvelopeTypeCfgDAO.getInstance().getCfgById(item.getCfgId());
		if(cfg == null){
			return;
		}		
		String reward = eSpecialItemId.Gold.getValue() +"_"+item.getGoldCount();
		if(item.getGoldCount() > 0){
			ComGiftMgr.getInstance().addtagInfoTOEmail(player, reward, MAKEUPEMAIL+"", cfg.getEmailTitle());
		}
		item.setIstaken(true);
		item.setClosed(true);
		dataHolder.updateItem(player, item);
	}

	

	public boolean isClose(ActivityRedEnvelopeTypeItem activityVitalityTypeItem) {
		ActivityRedEnvelopeTypeCfg cfg = ActivityRedEnvelopeTypeCfgDAO
				.getInstance().getCfgById(activityVitalityTypeItem.getCfgId());
		if (cfg == null) {
			return false;
		}
		long endTime = cfg.getEndTime();
		long currentTime = System.currentTimeMillis();
		return currentTime >= endTime;
	}
	
	public boolean isCanTakeGift(ActivityRedEnvelopeTypeItem item) {
		ActivityRedEnvelopeTypeCfg cfg = ActivityRedEnvelopeTypeCfgDAO.getInstance().getCfgById(item.getCfgId());	
		long takenTime = cfg.getGetRewardsTime();
		long currentTime = System.currentTimeMillis();
		long endTime = cfg.getEndTime();
		return currentTime > endTime&& currentTime< takenTime;
	}


	public void addCount(Player player,  int countadd) {
		ActivityRedEnvelopeItemHolder dataHolder = ActivityRedEnvelopeItemHolder.getInstance();
		ActivityRedEnvelopeTypeItem dataItem = dataHolder.getItem(player.getUserId());
		ActivityRedEnvelopeTypeCfg Cfg=ActivityRedEnvelopeTypeCfgDAO.getInstance().getCfgById(dataItem.getCfgId());
		List<ActivityRedEnvelopeTypeSubItem> subItemList = dataItem.getSubItemList();
		if(ActivityTypeHelper.getDayBy5Am(Cfg.getStartTime())>subItemList.size()){
			//活动开了n天，但子项只有m<n个；在m天之后n天之前的消费会到这里
			return;
		}
		for(ActivityRedEnvelopeTypeSubItem subItem: subItemList){
			if(subItem.getDay() == ActivityTypeHelper.getDayBy5Am(Cfg.getStartTime())){
				subItem.setCount(subItem.getCount()+(countadd*subItem.getDiscount())/100);
				break;
			}			
		}
//		target.setCount(target.getCount() + countadd);
		dataItem.setGoldCount(0);
		for(ActivityRedEnvelopeTypeSubItem subItem : subItemList){
			dataItem.setGoldCount(dataItem.getGoldCount() + subItem.getCount());
		}		
		dataHolder.updateItem(player, dataItem);
	}
	
	public boolean isOpen(){
		for(ActivityRedEnvelopeTypeCfg cfg : ActivityRedEnvelopeTypeCfgDAO.getInstance().getAllCfg()){
			if(isOpen(cfg)){
				return true;
			}
		}		
		return false;
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
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		map.put(eSpecialItemId.Gold.getValue(), dataItem.getGoldCount());
		player.getItemBagMgr().useLikeBoxItem(null, null, map);				
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
		if(cfg == null ){
			return;
		}		
		ActivityRedEnvelopeTypeItem dataItem = activityRedEnvelopeTypeItemHolder.getItem(player.getUserId());
		if(dataItem == null){
			return;
		}
		if(!dataItem.isTouchRedPoint()){
			dataItem.setTouchRedPoint(true);
			activityRedEnvelopeTypeItemHolder.updateItem(player, dataItem);
		}	
		
	}

	public boolean isOpen(MapItemValidateParam param) {
		List<ActivityRedEnvelopeTypeCfg> list = ActivityRedEnvelopeTypeCfgDAO.getInstance().getAllCfg();
		for(ActivityRedEnvelopeTypeCfg cfg : list){
			if(isOpen(cfg,param)){
				return true;
			}
		}				
		return false;
	}

	private boolean isOpen(ActivityRedEnvelopeTypeCfg cfg,
			MapItemValidateParam param) {
		if (cfg != null) {
			long startTime = cfg.getStartTime();
			long endTime = cfg.getEndTime();
			long currentTime = param.getCurrentTime();
			return currentTime < endTime && currentTime >= startTime;
		}
		return false;
	}	

}
