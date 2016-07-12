package com.playerdata.activity.VitalityType;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.ActivityRedPointEnum;
import com.playerdata.activity.ActivityRedPointUpdate;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityCfg;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityCfgDAO;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityRewardCfg;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityRewardCfgDAO;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalitySubCfg;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalitySubCfgDAO;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeItem;
import com.playerdata.activity.VitalityType.data.ActivityVitalityItemHolder;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeSubBoxItem;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeSubItem;
import com.playerdata.activity.countType.ActivityCountTypeEnum;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfg;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfgDAO;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeItemHolder;
import com.rw.fsutil.util.DateUtils;


public class ActivityVitalityTypeMgr implements ActivityRedPointUpdate{

	private static ActivityVitalityTypeMgr instance = new ActivityVitalityTypeMgr();

	private final static int MAKEUPEMAIL = 10055;

	public static ActivityVitalityTypeMgr getInstance() {
		return instance;
	}

	public void synVitalityTypeData(Player player) {
		checkCfgVersion(player);
		ActivityVitalityItemHolder.getInstance().synAllData(player);
	}
	


	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空 */
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
		checkCfgVersion(player);
		checkOtherDay(player);
		checkClose(player);
	}
	
	private void checkNewOpen(Player player) {
		ActivityVitalityItemHolder dataHolder = ActivityVitalityItemHolder.getInstance();
		List<ActivityVitalityCfg> allCfgList = ActivityVitalityCfgDAO.getInstance().getAllCfg();
		ArrayList<ActivityVitalityTypeItem> addItemList = null;
		for (ActivityVitalityCfg activityVitalityCfg : allCfgList) {// 遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if (!isOpen(activityVitalityCfg)) {
				// 活动未开启
				continue;
			}
			ActivityVitalityTypeEnum acVitalityTypeEnum = ActivityVitalityTypeEnum.getById(activityVitalityCfg.getId());
			if (acVitalityTypeEnum == null) {
				GameLog.error("ActivityCountTypeMgr", "#checkNewOpen()", "找不到活动类型枚举：" + activityVitalityCfg.getId());
				continue;
			}
			ActivityVitalityTypeItem targetItem = dataHolder.getItem(player.getUserId(), acVitalityTypeEnum);// 已在之前生成数据的活动
			if (targetItem == null) {
				targetItem = ActivityVitalityCfgDAO.getInstance().newItem(player, acVitalityTypeEnum);// 生成新开启活动的数据
				if (targetItem == null) {
					GameLog.error("ActivityCountTypeMgr", "#checkNewOpen()", "根据活动类型枚举找不到对应的cfg：" + activityVitalityCfg.getId());
					continue;
				}
				if (addItemList == null) {
					addItemList = new ArrayList<ActivityVitalityTypeItem>();
				}
				addItemList.add(targetItem);
			}
			
		}	
		if (addItemList != null) {
			dataHolder.addItemList(player, addItemList);
		}
	}
	
	private void checkCfgVersion(Player player) {
	ActivityVitalityItemHolder dataHolder = ActivityVitalityItemHolder.getInstance();
	List<ActivityVitalityTypeItem> itemList = dataHolder.getItemList(player.getUserId());
	for(ActivityVitalityTypeItem activityVitalityTypeItem: itemList){
		ActivityVitalityCfg cfg = ActivityVitalityCfgDAO.getInstance().getCfgByItem(activityVitalityTypeItem);		
		if(cfg == null ){
			dataHolder.removeItem(player, activityVitalityTypeItem);
			continue;
		}
		ActivityVitalityTypeEnum cfgenum = ActivityVitalityTypeEnum.getById(cfg.getId());
		if(cfgenum == null){
			dataHolder.removeItem(player, activityVitalityTypeItem);
			continue;
		}
		if (!StringUtils.equals(activityVitalityTypeItem.getVersion(), cfg.getVersion())) {
			activityVitalityTypeItem.reset(cfg,cfgenum);
			dataHolder.updateItem(player, activityVitalityTypeItem);
		}		
	}	
}

	private void checkOtherDay(Player player) {
		ActivityVitalityItemHolder dataHolder = ActivityVitalityItemHolder.getInstance();		
		List<ActivityVitalityTypeItem> item = dataHolder.getItemList(player.getUserId());
		
		for(ActivityVitalityTypeItem activityVitalityTypeItem: item){
			if(!StringUtils.equals(ActivityVitalityTypeEnum.Vitality.getCfgId(), activityVitalityTypeItem.getCfgId() )){
				continue;
			}
			ActivityVitalityCfg cfg = ActivityVitalityCfgDAO.getInstance().getCfgByItem(activityVitalityTypeItem);
			if(cfg == null ){
				continue;
			}
			ActivityVitalityTypeEnum cfgenum = ActivityVitalityTypeEnum.getById(cfg.getId());
			if(cfgenum == null){				
				continue;
			}
			if (DateUtils.isNewDayHour(5,activityVitalityTypeItem.getLastTime())) {
				sendEmailIfGiftNotTaken(player,  activityVitalityTypeItem.getSubItemList());
				sendEmailIfBoxGiftNotTaken(player, activityVitalityTypeItem);
				activityVitalityTypeItem.reset(cfg,cfgenum);
				dataHolder.updateItem(player, activityVitalityTypeItem);
			}		
		}			
	}
	

	
	
	private void checkClose(Player player) {
		ActivityVitalityItemHolder dataHolder = ActivityVitalityItemHolder.getInstance();
		List<ActivityVitalityTypeItem> itemList = dataHolder.getItemList(player.getUserId());

		for (ActivityVitalityTypeItem activityVitalityTypeItem : itemList) {// 每种活动
			if (isClose(activityVitalityTypeItem)) {
				sendEmailIfGiftNotTaken(player,  activityVitalityTypeItem.getSubItemList());
				sendEmailIfBoxGiftNotTaken(player, activityVitalityTypeItem);
				activityVitalityTypeItem.setClosed(true);
				dataHolder.updateItem(player, activityVitalityTypeItem);
			}
		}
	}
	
	
	

	

	public boolean isOpen(ActivityVitalityCfg vitalityCfg) {
		long startTime = vitalityCfg.getStartTime();
		long endTime = vitalityCfg.getEndTime();
		long currentTime = System.currentTimeMillis();
		return currentTime < endTime && currentTime > startTime;
	}
//	
	public boolean isClose(ActivityVitalityTypeItem activityVitalityTypeItem) {
		if (activityVitalityTypeItem != null) {
			ActivityVitalityCfg cfg = ActivityVitalityCfgDAO.getInstance().getCfgByItem(activityVitalityTypeItem);			
			if(cfg == null){
				GameLog.error("activitydailycounttypemgr","" , "配置文件找不到数据奎对应的活动");
				return false;
			}						
			long endTime = cfg.getEndTime();
			long currentTime = System.currentTimeMillis();
			return currentTime > endTime;			
		}
		return false;
	}
	
	private void sendEmailIfGiftNotTaken(Player player,List<ActivityVitalityTypeSubItem> subItemList) {
		for (ActivityVitalityTypeSubItem subItem : subItemList) {// 配置表里的每种奖励
			ActivityVitalitySubCfg subItemCfg = ActivityVitalitySubCfgDAO.getInstance().getById(subItem.getCfgId());
			if (subItemCfg == null) {
				GameLog.error(LogModule.ComActivityVitality, null,
						"通用活动找不到配置文件", null);
				return;
			}
			if (subItem.getCount() >= subItemCfg.getCount()
					&& !subItem.isTaken()) {
				boolean isAdd = ComGiftMgr.getInstance().addGiftTOEmailById(
						player, subItemCfg.getGiftId(), MAKEUPEMAIL + "",
						subItemCfg.getEmailTitle());
				subItem.setTaken(true);
				if (!isAdd)
					GameLog.error(LogModule.ComActivityVitality,
							player.getUserId(), "通用活动关闭后未领取奖励获取邮件内容失败", null);
			}
		}
	}
	
	private void sendEmailIfBoxGiftNotTaken(Player player,ActivityVitalityTypeItem Item) {		
		ActivityVitalityCfg cfg = ActivityVitalityCfgDAO.getInstance().getCfgByItem(Item);
		if(cfg == null){
			GameLog.error(LogModule.ComActivityVitality, null,"通用活动找不到配置文件", null);
			return;
		}
		if(!cfg.isCanGetReward()){
			//不派发宝箱
			return;
		}
		
		List<ActivityVitalityTypeSubBoxItem> subBoxItemList = Item.getSubBoxItemList();		
		for (ActivityVitalityTypeSubBoxItem subItem : subBoxItemList) {// 配置表里的每种奖励
			ActivityVitalityRewardCfg subItemCfg = ActivityVitalityRewardCfgDAO.getInstance().getById(subItem.getCfgId());
			if (subItemCfg == null) {
				GameLog.error(LogModule.ComActivityVitality, null,
						"通用活动找不到配置文件", null);
				return;
			}
			if (Item.getActiveCount() >= subItemCfg.getActivecount()
					&& !subItem.isTaken()) {
				boolean isAdd = ComGiftMgr.getInstance().addGiftTOEmailById(
						player, subItemCfg.getGiftId(), MAKEUPEMAIL + "",
						subItemCfg.getEmailTitle());
				subItem.setTaken(true);
				if (!isAdd)
					GameLog.error(LogModule.ComActivityVitality,
							player.getUserId(), "通用活动关闭后未领取奖励获取邮件内容失败", null);
			}
		}
	}
	
	
	
	
	
//	
	
	public boolean isLevelEnough(ActivityVitalityTypeEnum eNum ,Player player) {
		ActivityVitalityCfg vitalityCfg = null;
		List<ActivityVitalityCfg> cfgList = ActivityVitalityCfgDAO.getInstance().getAllCfg();
		for(ActivityVitalityCfg cfg: cfgList){
			if(StringUtils.equals(eNum.getCfgId(), cfg.getId())){
				vitalityCfg = cfg;
				break;
			}			
		}		
		if(vitalityCfg == null){
			GameLog.error("activityDailyCountTypeMgr", "list", "配置文件总表错误" );
			return false;
		}
		if(player.getLevel() < vitalityCfg.getLevelLimit()){
			return false;
		}		
		return true;
	}
	
	public void addCount(Player player, ActivityVitalityTypeEnum countType,ActivityVitalitySubCfg subCfg, int countadd) {
		ActivityVitalityItemHolder dataHolder = ActivityVitalityItemHolder.getInstance();
		ActivityVitalityTypeItem dataItem = dataHolder.getItem(player.getUserId(),ActivityVitalityTypeEnum.Vitality);		
		ActivityVitalityTypeSubItem subItem = getbyVitalityTypeEnum(player, countType, dataItem);	
		
		addVitalitycount(dataItem,subItem,subCfg,countadd);
		subItem.setCount(subItem.getCount() + countadd);
		dataHolder.updateItem(player, dataItem);
	}
	
	public void addCountTwo(Player player, ActivityVitalityTypeEnum countType,ActivityVitalitySubCfg subCfg, int countadd) {
		ActivityVitalityItemHolder dataHolder = ActivityVitalityItemHolder.getInstance();
		
		ActivityVitalityTypeItem dataItem = dataHolder.getItem(player.getUserId(),ActivityVitalityTypeEnum.VitalityTwo);		
		ActivityVitalityTypeSubItem subItem = getbyVitalityTypeEnumTwo(player, countType, dataItem);	
		
		addVitalitycount(dataItem,subItem,subCfg,countadd);
		subItem.setCount(subItem.getCount() + countadd);
		dataHolder.updateItem(player, dataItem);
	}
	/**增加活跃度*/
    private void addVitalitycount(ActivityVitalityTypeItem dataItem, ActivityVitalityTypeSubItem subItem,
    		ActivityVitalitySubCfg subCfg,int countadd) {
		if(subItem.getCount() < subCfg.getCount() && (subItem.getCount() + countadd >= subCfg.getCount())){
			dataItem.setActiveCount(dataItem.getActiveCount() + subCfg.getActiveCount());
		}   	
	}

	
	private ActivityVitalityTypeSubItem getbyVitalityTypeEnumTwo(Player player,
			ActivityVitalityTypeEnum countType,
			ActivityVitalityTypeItem dataItem) {
		ActivityVitalityTypeSubItem subItem = null;
		ActivityVitalitySubCfg cfg = null;
		List<ActivityVitalitySubCfg> subcfglist = ActivityVitalitySubCfgDAO.getInstance().getAllCfg();
		for(ActivityVitalitySubCfg subcfg :subcfglist){						
			if(StringUtils.equals(subcfg.getType(), countType.getCfgId())){
			cfg = subcfg;
			break;
			}
		}
		if(cfg == null){
			GameLog.error("Activitydailycounttypemgr", "uid=" + player.getUserId(), "事件判断活动开启中,但活动配置生成的cfg没有对应的事件枚举");
			return subItem;
		}
		if(dataItem != null){
			List<ActivityVitalityTypeSubItem> sublist = dataItem.getSubItemList();
			for(ActivityVitalityTypeSubItem subitem : sublist){
				if(StringUtils.equals(cfg.getId(), subitem.getCfgId())){				
					subItem = subitem;
					break;
				}
			}			
		}		
		return subItem;
	}

	
	//	
	public ActivityVitalityTypeSubItem getbyVitalityTypeEnum (Player player,ActivityVitalityTypeEnum typeEnum,ActivityVitalityTypeItem dataItem){		
		ActivityVitalityTypeSubItem subItem = null;
		ActivityVitalitySubCfg cfg = null;
		List<ActivityVitalitySubCfg> subcfglist = ActivityVitalitySubCfgDAO.getInstance().getAllCfg();
		for(ActivityVitalitySubCfg subcfg :subcfglist){
			if(ActivityVitalityCfgDAO.getInstance().getday() != subcfg.getDay()){
				continue;
			}			
			if(StringUtils.equals(subcfg.getType(), typeEnum.getCfgId())){
			cfg = subcfg;
			break;
			}
		}
		if(cfg == null){
			GameLog.error("Activitydailycounttypemgr", "uid=" + player.getUserId(), "事件判断活动开启中,但活动配置生成的cfg没有对应的事件枚举");
			return subItem;
		}
		
		if(dataItem != null){
			List<ActivityVitalityTypeSubItem> sublist = dataItem.getSubItemList();
			for(ActivityVitalityTypeSubItem subitem : sublist){
				if(StringUtils.equals(cfg.getId(), subitem.getCfgId())){				
					subItem = subitem;
					break;
				}
			}			
		}
		
		if(subItem == null){
			GameLog.error("Activitydailycounttypemgr", "uid=" + player.getUserId(), "事件判断活动开启,找到了cfg,玩家数据每找到item或subitem");
		}		
		return   subItem;
	}
	
	
	public ActivityComResult takeGift(Player player, String subItemId) {
		ActivityVitalityItemHolder dataHolder = ActivityVitalityItemHolder.getInstance();
		ActivityVitalityTypeItem dataItem = null;
		ActivityVitalityTypeSubItem item = null;
		List<ActivityVitalityTypeItem> dataitemList = dataHolder.getItemList(player.getUserId());
		for(ActivityVitalityTypeItem dataitemtmp : dataitemList){
			List<ActivityVitalityTypeSubItem> subitemlist = dataitemtmp.getSubItemList();
			for(ActivityVitalityTypeSubItem subitem: subitemlist){
				if(StringUtils.equals(subItemId, subitem.getCfgId())){
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
			if(item.isTaken()){
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
		for(ActivityVitalityTypeItem dataitemtmp : dataitemList){
			List<ActivityVitalityTypeSubBoxItem> subitemlist = dataitemtmp.getSubBoxItemList();
			for(ActivityVitalityTypeSubBoxItem subitem: subitemlist){
				if(StringUtils.equals(rewardItemId, subitem.getCfgId())){
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
			if(item.getCount() > dataItem.getActiveCount()){
				result.setReason("积分不足");					
				return result;
			}
			if(item.isTaken()){
				result.setReason("已经领取");	
				return result;
			}
			
			
			takeBoxGift(player, item);
			result.setSuccess(true);
			dataHolder.updateItem(player, dataItem);
		}
		return result;
	}

	private void takeBoxGift(Player player,	ActivityVitalityTypeSubBoxItem targetItem) {
		
		targetItem.setTaken(true);
		ComGiftMgr.getInstance().addGiftById(player, targetItem.getGiftId());
		
	}

	@Override
	public void updateRedPoint(Player player, ActivityRedPointEnum eNum) {
		ActivityVitalityItemHolder activityCountTypeItemHolder = new ActivityVitalityItemHolder();
		ActivityVitalityTypeEnum vitalityEnum = ActivityVitalityTypeEnum.getById(eNum.getCfgId());
		if(vitalityEnum == null){
			GameLog.error(LogModule.ComActivityVitality, player.getUserId(), "心跳传入id获得的页签枚举无法找到活动枚举", null);
			return;
		}
		ActivityVitalityTypeItem dataItem = activityCountTypeItemHolder.getItem(player.getUserId(),vitalityEnum);
		if(dataItem == null){
			GameLog.error(LogModule.ComActivityVitality, player.getUserId(), "心跳传入id获得的页签枚举无法找到活动数据", null);
			return;
		}
		if(!dataItem.isTouchRedPoint()){
			dataItem.setTouchRedPoint(true);
			activityCountTypeItemHolder.updateItem(player, dataItem);
		}
		
	}




	
	

}
