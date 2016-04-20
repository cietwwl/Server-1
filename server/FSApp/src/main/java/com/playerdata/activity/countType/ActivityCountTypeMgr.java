package com.playerdata.activity.countType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfg;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfgDAO;
import com.playerdata.activity.countType.data.ActivityCountTypeItemHolder;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeSubItem;
import com.rw.fsutil.util.DateUtils;


public class ActivityCountTypeMgr {
	
	private static ActivityCountTypeMgr instance = new ActivityCountTypeMgr();
	
	public static ActivityCountTypeMgr getInstance(){
		return instance;
	}
	
	public void synCountTypeData(Player player){
		ActivityCountTypeItemHolder.getInstance().synAllData(player);
	}
	/**登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空*/
	public void checkActivityOpen(Player player) {
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder.getInstance();
//		int i = 0;
		List<ActivityCountTypeCfg> allCfgList = ActivityCountTypeCfgDAO.getInstance().getAllCfg();
		for (ActivityCountTypeCfg activityCountTypeCfg : allCfgList) {//遍历种类*各类奖励数次数,生成开启的种类个数空数据
//			i++;
//			System.out.println("activitycount--未判断是否已开启 "+"  i = " + i);
			if(isOpen(activityCountTypeCfg)){
				ActivityCountTypeEnum countTypeEnum = ActivityCountTypeEnum.getById(activityCountTypeCfg.getGoToType());
				
				if(countTypeEnum == null){
					continue;
				}else{
//					System.out.println("activitycount--"+countTypeEnum.getCfgId()+"  i = " + i);
				}
				ActivityCountTypeItem targetItem = dataHolder.getItem(player.getUserId(), countTypeEnum);//已在之前生成数据的活动
				if(targetItem == null){
//					System.out.println("activitycount--生成表格  "+"  i = " + i);
					targetItem = ActivityCountTypeCfgDAO.getInstance().newItem(player, countTypeEnum);//生成新开启活动的数据
					if(targetItem!=null){
						 dataHolder.addItem(player, targetItem);
					}
				}
				
			}
		}
		
		
		
	}
	/**传入活动类型判断此活动是否开放*/
	public boolean checkOneActivityISOpen(ActivityCountTypeEnum countType) {
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder.getInstance();		
		List<ActivityCountTypeCfg> allCfgList = ActivityCountTypeCfgDAO.getInstance().getAllCfg();
		for (ActivityCountTypeCfg activityCountTypeCfg : allCfgList) {				
			if(StringUtils.equals(activityCountTypeCfg.getGoToType(), countType.getCfgId())){
				if(isOpen(activityCountTypeCfg)){
					return true;
				}				
			}			
		}		
	return false;
	}
	
	
	
	private boolean isOpen(ActivityCountTypeCfg activityCountTypeCfg) {
		long startTime = DateUtils.YyyymmddhhmmToMillionseconds(activityCountTypeCfg.getStarTime()+"");
		long endTime = DateUtils.YyyymmddhhmmToMillionseconds(activityCountTypeCfg.getEndTime()+"");		
		long currentTime = System.currentTimeMillis();
		
		
		
//		long startTime = activityCountTypeCfg.getStarTime()*1000;
//		long endTime = activityCountTypeCfg.getEndTime()*1000;
//		long currentTime = System.currentTimeMillis();
//		
		return currentTime < endTime && currentTime > startTime;
	}

	public void addCount(Player player, ActivityCountTypeEnum countType){
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder.getInstance();
		
		ActivityCountTypeItem dataItem = dataHolder.getItem(player.getUserId(), countType);
		
		dataItem.setCount(dataItem.getCount()+1);
		
		dataHolder.updateItem(player, dataItem);
	}
	
	public ActivityComResult takeGift(Player player, ActivityCountTypeEnum countType, String subItemId){
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder.getInstance();
		
		ActivityCountTypeItem dataItem = dataHolder.getItem(player.getUserId(), countType);
		ActivityComResult result = ActivityComResult.newInstance(false);
		
		System.out.println("activity.客户端的一次领奖申请"+ countType +" subid" +subItemId);
		//未激活
		if(dataItem == null){
			result.setReason("活动尚未开启");
			
		}else{
			
			
//			ActivityCountTypeSubItem targetItem = null;
//			List<ActivityCountTypeSubItem> takenGiftList = dataItem.getTakenGiftList();
//			for (ActivityCountTypeSubItem itemTmp : takenGiftList) {
//				if(StringUtils.equals(itemTmp.getId(), subItemId)){
//					targetItem = itemTmp;
//					break;
//				}
//			}
//			if(targetItem == null){
//				targetItem = ActivityCountTypeCfgDAO.getInstance().newSubItem(countType, subItemId);
//				
//				
//				if(targetItem == null){
//					result.setReason("该奖励不存在 id:"+countType.getCfgId()+" subItemId:"+subItemId);
//				}else{
//					takenGiftList.add(targetItem);
//					takeGift(targetItem);
//					result.setSuccess(true);
//					dataHolder.updateItem(player, dataItem);
//				}
//			}else{
//				takeGift(targetItem);			
//				result.setSuccess(true);
//				dataHolder.updateItem(player, dataItem);
//			}
			
			
			List<ActivityCountTypeSubItem> takenGiftList = dataItem.getTakenGiftList();
			List<ActivityCountTypeSubItem> newtakenGiftList = new ArrayList<ActivityCountTypeSubItem>();
			for (ActivityCountTypeSubItem itemTmp : takenGiftList) {
				if(StringUtils.equals(itemTmp.getId(), subItemId)){
					newtakenGiftList.add(itemTmp);					
				}
			}
			if(newtakenGiftList.isEmpty())
				System.out.println("activity.派发奖励数据库为0，giftisempty="+ newtakenGiftList.isEmpty());
			if(newtakenGiftList.isEmpty()){//空数据，写入奖励
				newtakenGiftList = ActivityCountTypeCfgDAO.getInstance().newSubItemlist(subItemId);
				if(newtakenGiftList.isEmpty()){
					result.setReason("该奖励不存在 id:"+countType.getCfgId()+" subItemId:"+subItemId);
			}else{
				for (ActivityCountTypeSubItem itemTmp : newtakenGiftList) {
						takenGiftList.add(itemTmp);
						takeGift(player,itemTmp);								
						dataHolder.updateItem(player, dataItem);
					}
					result.setSuccess(true);
				}							
			}else{//有数据，只队未派出的操作
				for (ActivityCountTypeSubItem itemTmp : newtakenGiftList) {					
					if(!itemTmp.isTaken()){
						takeGift(player,itemTmp);							
						dataHolder.updateItem(player, dataItem);
					}else{
						System.out.println("activity.异常,万家申请已领取过的奖励" );
					}
				}

				result.setSuccess(true);
			}
			
		}
		
		
		return result;
	}

	private void takeGift(Player player,ActivityCountTypeSubItem targetItem) {
		
		targetItem.setTaken(true);
		targetItem.getCount();
		targetItem.getGift();
		player.getItemBagMgr().addItem(Integer.parseInt(targetItem.getGift()),targetItem.getCount());
		
		System.out.println("activitycounttypemgr派出了奖励 ，名字：" + targetItem.getGift());
		//TODO: gift take logic
	}
	
	
}
