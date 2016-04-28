package com.playerdata.activity.countType;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfg;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfgDAO;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeItemHolder;
import com.playerdata.activity.countType.data.ActivityCountTypeSubItem;


public class ActivityCountTypeMgr {
	
	private static ActivityCountTypeMgr instance = new ActivityCountTypeMgr();
	
	private final static int MAKEUPEMAIL = 10055;
	
	public static ActivityCountTypeMgr getInstance(){
		return instance;
	}
	
	public void synCountTypeData(Player player){
		ActivityCountTypeItemHolder.getInstance().synAllData(player);
	}
	
	public void refreshDateFreshActivity(Player player){
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder.getInstance();
		List<ActivityCountTypeCfg> allCfgList = ActivityCountTypeCfgDAO.getInstance().getAllCfg();
		for (ActivityCountTypeCfg activityCountTypeCfg : allCfgList) {//遍历种类*各类奖励数次数,生成开启的种类个数空数据
			ActivityCountTypeEnum countTypeEnum = ActivityCountTypeEnum.getById(activityCountTypeCfg.getId());
			if(countTypeEnum != null && activityCountTypeCfg.isDateFresh()){
				ActivityCountTypeItem targetItem = dataHolder.getItem(player.getUserId(), countTypeEnum);//已在之前生成数据的活动
				if(targetItem != null){
					targetItem.reset();
				}				
			}
			
		}
	}
	
	/**登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空*/
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);		
		checkClose(player);
		
	}

	private void checkNewOpen(Player player) {
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder.getInstance();
		List<ActivityCountTypeCfg> allCfgList = ActivityCountTypeCfgDAO.getInstance().getAllCfg();
		for (ActivityCountTypeCfg activityCountTypeCfg : allCfgList) {//遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if(isOpen(activityCountTypeCfg)){
				ActivityCountTypeEnum countTypeEnum = ActivityCountTypeEnum.getById(activityCountTypeCfg.getId());
				if(countTypeEnum != null){
					ActivityCountTypeItem targetItem = dataHolder.getItem(player.getUserId(), countTypeEnum);//已在之前生成数据的活动
					if(targetItem != null){
						if(targetItem.isClosed()){
							dataHolder.removeitem(player, countTypeEnum);
							
						}
					}
					
					if(targetItem == null){
						targetItem = ActivityCountTypeCfgDAO.getInstance().newItem(player, countTypeEnum);//生成新开启活动的数据
						if(targetItem!=null){
							dataHolder.addItem(player, targetItem);
						}
					}
				}
				
				
			}
		}
	}
	private void checkClose(Player player) {
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder.getInstance();
		List<ActivityCountTypeItem> itemList = dataHolder.getItemList(player.getUserId());
		
		for (ActivityCountTypeItem activityCountTypeItem : itemList) {//每种活动
			if(isClose(activityCountTypeItem)){
				List<ActivityCountTypeSubItem>  list = ActivityCountTypeCfgDAO.getInstance().getCfgById(activityCountTypeItem.getCfgId()).getSubItemList();
				for(ActivityCountTypeSubItem subitem : list){//配置表里的每种奖励
					if(subitem.getCount() > activityCountTypeItem.getCount()){
						continue;
					}else{
						Boolean ismakeup = true;
						for(ActivityCountTypeSubItem sub :activityCountTypeItem.getTakenGiftList()){
							if(sub.getCount() == subitem.getCount()){
								ismakeup = false;
								break;
							}
						}
						if(ismakeup){
							ActivityCountTypeSubItem targetItem = ActivityCountTypeCfgDAO.getInstance().newSubItem(ActivityCountTypeEnum.getById(activityCountTypeItem.getCfgId()), subitem.getId());				
							activityCountTypeItem.getTakenGiftList().add(targetItem);
//							takeGift(player,targetItem);
												
							
							boolean isAdd = ComGiftMgr.getInstance().addGiftTOEmailById(player, subitem.getGift(), MAKEUPEMAIL+"");	
							if (isAdd) {
								targetItem.setTaken(true);
							} else {
								GameLog.error("通用活动关闭后未领取奖励获取邮件内容失败：" + player.getUserId() + "," );
							}		
							dataHolder.updateItem(player, activityCountTypeItem);//写入数据库,领取为false=邮件派发.	
							
						}						
					}					
				}						
				activityCountTypeItem.setClosed(true);
				dataHolder.updateItem(player, activityCountTypeItem);
			}
		}

		
	}



	/**传入活动类型判断此活动是否开放*/
	public boolean checkOneActivityISOpen(Player player,ActivityCountTypeEnum countType) {
		
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder.getInstance();		
		
		return dataHolder.getItem(player.getUserId(), countType)!=null;		
	}
	
	
	public boolean isClose(ActivityCountTypeItem activityCountTypeItem) {
		
		ActivityCountTypeCfg cfgById = ActivityCountTypeCfgDAO.getInstance().getCfgById(activityCountTypeItem.getCfgId());
		
		long endTime = cfgById.getEndTime();		
		long currentTime = System.currentTimeMillis();

		return currentTime > endTime;
	}
	
	
	public boolean isOpen(ActivityCountTypeCfg activityCountTypeCfg) {
		
		long startTime = activityCountTypeCfg.getStartTime();
		long endTime = activityCountTypeCfg.getEndTime();		
		long currentTime = System.currentTimeMillis();
		return currentTime < endTime && currentTime > startTime;
	}

	public void addCount(Player player, ActivityCountTypeEnum countType,int countadd){
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder.getInstance();
		
		ActivityCountTypeItem dataItem = dataHolder.getItem(player.getUserId(), countType);
		dataItem.setCount(dataItem.getCount()+countadd);
		
		
		
		dataHolder.updateItem(player, dataItem);
	}
	




	public ActivityComResult takeGift(Player player, ActivityCountTypeEnum countType, String subItemId){
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder.getInstance();
		
		ActivityCountTypeItem dataItem = dataHolder.getItem(player.getUserId(), countType);
		ActivityComResult result = ActivityComResult.newInstance(false);
		
		//未激活
		if(dataItem == null){
			result.setReason("活动尚未开启");
			
		}else{			
			ActivityCountTypeSubItem targetItem = null;
			List<ActivityCountTypeSubItem> takenGiftList = dataItem.getTakenGiftList();
			for (ActivityCountTypeSubItem itemTmp : takenGiftList) {
				if(StringUtils.equals(itemTmp.getId(), subItemId)){
					targetItem = itemTmp;
					break;
				}
			}
			if(targetItem == null){
				targetItem = ActivityCountTypeCfgDAO.getInstance().newSubItem(countType, subItemId);				
				if(targetItem == null){
					result.setReason("该奖励不存在 id:"+countType.getCfgId()+" subItemId:"+subItemId);
				}else{
					takenGiftList.add(targetItem);
					takeGift(player,targetItem);
					result.setSuccess(true);
					dataHolder.updateItem(player, dataItem);
				}
			}else{
				if(!targetItem.isTaken()){
					takeGift(player,targetItem);			
					result.setSuccess(true);
					dataHolder.updateItem(player, dataItem);
				}else{//申请已领取过的奖励
				}
			}
			
			
//			List<ActivityCountTypeSubItem> takenGiftList = dataItem.getTakenGiftList();
//			List<ActivityCountTypeSubItem> newtakenGiftList = new ArrayList<ActivityCountTypeSubItem>();
//			for (ActivityCountTypeSubItem itemTmp : takenGiftList) {
//				if(StringUtils.equals(itemTmp.getId(), subItemId)){
//					newtakenGiftList.add(itemTmp);					
//				}
//			}
//			if(newtakenGiftList.isEmpty())
//				System.out.println("activity.派发奖励数据库为0，giftisempty="+ newtakenGiftList.isEmpty());
//			if(newtakenGiftList.isEmpty()){//空数据，写入奖励
//				newtakenGiftList = ActivityCountTypeCfgDAO.getInstance().newSubItemlist(subItemId);
//				if(newtakenGiftList.isEmpty()){
//					result.setReason("该奖励不存在 id:"+countType.getCfgId()+" subItemId:"+subItemId);
//			}else{
//				for (ActivityCountTypeSubItem itemTmp : newtakenGiftList) {
//						takenGiftList.add(itemTmp);
//						takeGift(player,itemTmp);								
//						dataHolder.updateItem(player, dataItem);
//					}
//					result.setSuccess(true);
//				}							
//			}else{//有数据，只队未派出的操作
//				for (ActivityCountTypeSubItem itemTmp : newtakenGiftList) {					
//					if(!itemTmp.isTaken()){
//						takeGift(player,itemTmp);							
//						dataHolder.updateItem(player, dataItem);
//					}else{
//						System.out.println("activity.异常,万家申请已领取过的奖励" );
//					}
//				}
//
//				result.setSuccess(true);
//			}
			
		}
		
		
		return result;
	}

	private  void takeGift(Player player,ActivityCountTypeSubItem targetItem) {
		targetItem.setTaken(true);
		targetItem.getCount();
		targetItem.getGift();	
		ComGiftMgr.getInstance().addGiftById(player,targetItem.getGift());	
//		player.getItemBagMgr().addItem(Integer.parseInt(targetItem.getGift()),targetItem.getCount());		
//		System.out.println("activitycounttypemgr派出了奖励 ，名字：" + targetItem.getGift());
		//TODO: gift take logic
	}
	
	

}
