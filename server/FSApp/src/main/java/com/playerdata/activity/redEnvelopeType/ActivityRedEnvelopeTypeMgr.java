package com.playerdata.activity.redEnvelopeType;




import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.playerdata.Player;
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
	
//	private void sendEmailIfGiftNotTaken(Player player,List<ActivityVitalityTypeSubItem> subItemList) {
//		for (ActivityVitalityTypeSubItem subItem : subItemList) {// 配置表里的每种奖励
//			ActivityVitalitySubCfg subItemCfg = ActivityVitalitySubCfgDAO.getInstance().getById(subItem.getCfgId());
//			if (subItemCfg == null) {
//				GameLog.error(LogModule.ComActivityVitality, null,
//						"通用活动找不到配置文件", null);
//				return;
//			}
//			if (subItem.getCount() >= subItemCfg.getCount()
//					&& !subItem.isTaken()) {
//				boolean isAdd = ComGiftMgr.getInstance().addGiftTOEmailById(
//						player, subItemCfg.getGiftId(), MAKEUPEMAIL + "",
//						subItemCfg.getEmailTitle());
//				subItem.setTaken(true);
//				if (!isAdd)
//					GameLog.error(LogModule.ComActivityVitality,
//							player.getUserId(), "通用活动关闭后未领取奖励获取邮件内容失败", null);
//			}
//		}
//	}
//	
//	private void sendEmailIfBoxGiftNotTaken(Player player,ActivityVitalityTypeItem Item) {		
//		ActivityVitalityCfg cfg = ActivityVitalityCfgDAO.getInstance().getCfgByItem(Item);
//		if(cfg == null){
//			GameLog.error(LogModule.ComActivityVitality, null,"通用活动找不到配置文件", null);
//			return;
//		}
//		if(!cfg.isCanGetReward()){
//			//不派发宝箱
//			return;
//		}
//		
//		List<ActivityVitalityTypeSubBoxItem> subBoxItemList = Item.getSubBoxItemList();		
//		for (ActivityVitalityTypeSubBoxItem subItem : subBoxItemList) {// 配置表里的每种奖励
//			ActivityVitalityRewardCfg subItemCfg = ActivityVitalityRewardCfgDAO.getInstance().getById(subItem.getCfgId());
//			if (subItemCfg == null) {
//				GameLog.error(LogModule.ComActivityVitality, null,
//						"通用活动找不到配置文件", null);
//				return;
//			}
//			if (Item.getActiveCount() >= subItemCfg.getActivecount()
//					&& !subItem.isTaken()) {
//				boolean isAdd = ComGiftMgr.getInstance().addGiftTOEmailById(
//						player, subItemCfg.getGiftId(), MAKEUPEMAIL + "",
//						subItemCfg.getEmailTitle());
//				subItem.setTaken(true);
//				if (!isAdd)
//					GameLog.error(LogModule.ComActivityVitality,
//							player.getUserId(), "通用活动关闭后未领取奖励获取邮件内容失败", null);
//			}
//		}
//	}
//	
//	
//	
//	
//	
////	
//	
//	public boolean isLevelEnough(ActivityVitalityTypeEnum eNum ,Player player) {
//		ActivityVitalityCfg vitalityCfg = null;
//		List<ActivityVitalityCfg> cfgList = ActivityVitalityCfgDAO.getInstance().getAllCfg();
//		for(ActivityVitalityCfg cfg: cfgList){
//			if(StringUtils.equals(eNum.getCfgId(), cfg.getId())){
//				vitalityCfg = cfg;
//				break;
//			}			
//		}		
//		if(vitalityCfg == null){
//			GameLog.error("activityDailyCountTypeMgr", "list", "配置文件总表错误" );
//			return false;
//		}
//		if(player.getLevel() < vitalityCfg.getLevelLimit()){
//			return false;
//		}		
//		return true;
//	}
	
//	public void addCount(Player player, ActivityVitalityTypeEnum countType,ActivityVitalitySubCfg subCfg, int countadd) {
//		ActivityVitalityItemHolder dataHolder = ActivityVitalityItemHolder.getInstance();
//		ActivityVitalityTypeItem dataItem = dataHolder.getItem(player.getUserId(),ActivityVitalityTypeEnum.Vitality);		
//		ActivityVitalityTypeSubItem subItem = getbyVitalityTypeEnum(player, countType, dataItem);	
//		
//		addVitalitycount(dataItem,subItem,subCfg,countadd);
//		subItem.setCount(subItem.getCount() + countadd);
//		dataHolder.updateItem(player, dataItem);
//	}
	
	
	
//	public ActivityComResult takeGift(Player player, String subItemId) {
//		ActivityVitalityItemHolder dataHolder = ActivityVitalityItemHolder.getInstance();
//		ActivityVitalityTypeItem dataItem = null;
//		ActivityVitalityTypeSubItem item = null;
//		List<ActivityVitalityTypeItem> dataitemList = dataHolder.getItemList(player.getUserId());
//		for(ActivityVitalityTypeItem dataitemtmp : dataitemList){
//			List<ActivityVitalityTypeSubItem> subitemlist = dataitemtmp.getSubItemList();
//			for(ActivityVitalityTypeSubItem subitem: subitemlist){
//				if(StringUtils.equals(subItemId, subitem.getCfgId())){
//					item = subitem;
//					dataItem = dataitemtmp;
//					break;
//				}
//			}			
//		}		
//		ActivityComResult result = ActivityComResult.newInstance(false);
//
//		if (dataItem == null) {
//			result.setReason("活动尚未开启");
//
//		} else {			
//			if(item.isTaken()){
//				result.setReason("已经领取");	
//				return result;
//			}		
//			takeGift(player, item);
//			result.setSuccess(true);
//			dataHolder.updateItem(player, dataItem);
//
//		}
//
//		return result;
//	}	

}
