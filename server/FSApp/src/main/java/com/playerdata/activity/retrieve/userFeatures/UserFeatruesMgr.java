package com.playerdata.activity.retrieve.userFeatures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.retrieve.cfg.RewardBackCfgDAO;
import com.playerdata.activity.retrieve.data.ActivityRetrieveTypeHolder;
import com.playerdata.activity.retrieve.data.RewardBackItem;
import com.playerdata.activity.retrieve.data.RewardBackSubItem;
import com.playerdata.activity.retrieve.data.RewardBackTodaySubItem;
import com.playerdata.activity.retrieve.userFeatures.userFeaturesType.UserFeatruesBreakfast;
import com.rwbase.dao.copy.cfg.CopyCfg;
import com.rwbase.dao.copypve.pojo.CopyInfoCfg;


public class UserFeatruesMgr {
	private static final int dailyTaskBreakfast = 10002;
	private static final int dailyTasklunch = 10001;
	private static final int dailyTaskdinner = 10003;
	private static final int dailyTasksupper = 10004;
	
	public static final int jbzd = 1;
	public static final int lxsg = 2;
	public static final int celestial_kunlun = 3;//逻辑得到copy,copylevel，最后得到copyinfo才能判断时哪种幻境
	public static final int celestial_penglai = 4;
	
	private static UserFeatruesMgr instance = new UserFeatruesMgr();
	
	public static UserFeatruesMgr getInstance(){
		return instance;
	}
	
	private Map<UserFeaturesEnum, IUserFeatruesHandler> featruesHandlerMap = new HashMap<UserFeaturesEnum, IUserFeatruesHandler>();
	
	private UserFeatruesMgr(){
		featruesHandlerMap.put(UserFeaturesEnum.breakfast, new UserFeatruesBreakfast());
	}
	
	public List<RewardBackTodaySubItem> doCreat(Player player){
		List<RewardBackTodaySubItem> subItemList = new ArrayList<RewardBackTodaySubItem>();		
		for(Map.Entry<UserFeaturesEnum, IUserFeatruesHandler> entry : featruesHandlerMap.entrySet()){
			RewardBackTodaySubItem  subItem = new RewardBackTodaySubItem();
			subItem = creatSubItem(player,entry.getKey(),entry.getValue());
			subItemList.add(subItem);
		}		
		return subItemList;
	}
	
	private RewardBackTodaySubItem creatSubItem(Player player,UserFeaturesEnum iEnum,IUserFeatruesHandler iUserFeatruesHandler){
		RewardBackTodaySubItem subItem = new RewardBackTodaySubItem();		
		if(iUserFeatruesHandler != null){
			subItem = iUserFeatruesHandler.doEvent(player);
		}
		return subItem;
	}

	public List<RewardBackSubItem> doFresh(String userId,List<RewardBackTodaySubItem> subTodayItemList) {
		RewardBackCfgDAO rewardBackCfgDAO = RewardBackCfgDAO.getInstance();
		List<RewardBackSubItem> subItemList = new ArrayList<RewardBackSubItem>();
		for(RewardBackTodaySubItem todaySubItem : subTodayItemList){
			UserFeaturesEnum iEnum = UserFeaturesEnum.getById(todaySubItem.getId());
			if(iEnum == null){
				continue;
			}
			RewardBackSubItem subItem = new RewardBackSubItem();
			subItem = featruesHandlerMap.get(iEnum).doFresh(todaySubItem,userId,rewardBackCfgDAO);
			subItemList.add(subItem);
		}		
		return subItemList;
	}

	public void doFinish(Player player,UserFeaturesEnum iEnum){		
		doFinishFinally(player,iEnum,1);		
	}	

	//带参数的计数
	public void doFinishOfCount(Player player,UserFeaturesEnum iEnum,int count){		
		doFinishFinally(player,iEnum,count);
	}	
	
	private void doFinishFinally(Player player, UserFeaturesEnum iEnum, int count) {
		ActivityRetrieveTypeHolder dataholder = ActivityRetrieveTypeHolder.getInstance();
		String userId = player.getUserId();
		RewardBackTodaySubItem subItem = null;
		RewardBackItem item = dataholder.getItem(userId);
		List<RewardBackTodaySubItem> todaySubitemList = item.getTodaySubitemList();
		
		for(RewardBackTodaySubItem temp : todaySubitemList){
			if(StringUtils.equals(temp.getId(), iEnum.getId())){
				subItem = temp;
				break;
			}
		}
		if(subItem == null){
			//当天没生成活动数据，但功能又跑进来了
			GameLog.error(LogModule.ComActivityRetrieve, userId, "当天没生成活动数据，但功能又跑进来了", null);
			return;
		}
		
		if(subItem.getMaxCount() > subItem.getCount()){
			int tmp = subItem.getCount() ;
			tmp = tmp + count > subItem.getMaxCount() ? subItem.getMaxCount() : tmp + count;
			subItem.setCount(tmp);
			dataholder.updateItem(player, item);
		}else{
			//可能策划配错表；可能此功能可打10次，但只对前五进行找回
		}		
	}

	/**完成日常任务时判断下是否为早午晚餐等*/
	public void checkDailyTask(Player player, int id) {
		if(id == dailyTaskBreakfast){
			doFinish(player, UserFeaturesEnum.breakfast);
		}else if(id == dailyTasklunch){
			doFinish(player, UserFeaturesEnum.lunch);
		}else if(id == dailyTaskdinner){
			doFinish(player, UserFeaturesEnum.dinner);
		}else if(id == dailyTasksupper){
			doFinish(player, UserFeaturesEnum.supper);
		}
	}

	/*完成生存幻境是判断是哪一种，蛋疼的一笔*/
	public void checkCelestial(Player player, CopyCfg copyCfg) {
		CopyInfoCfg infoCfg = player.getCopyDataMgr().getCopyInfoCfgByLevelID(String.valueOf(copyCfg.getLevelID()));
		if(infoCfg == null){
			return;
		}
		if(infoCfg.getId() == celestial_kunlun){
			doFinish(player, UserFeaturesEnum.celestial_KunLunWonderLand);
		}else if(infoCfg.getId() == celestial_penglai){
			doFinish(player, UserFeaturesEnum.celestial_PengLaiIsland);
		}			
	}
	
	/**五档买体,传入买体成功后的当日已买次数;策划要改就加字段*/
	public void buyPower(Player player, int buyPowerCount) {
		if(buyPowerCount >= 1&& buyPowerCount <=3){
			doFinish(player, UserFeaturesEnum.buyPowerOne);
		}else if(buyPowerCount >= 4&& buyPowerCount <=6){
			doFinish(player, UserFeaturesEnum.buyPowerTwo);
		}else if(buyPowerCount >= 7&& buyPowerCount <=9){
			doFinish(player, UserFeaturesEnum.buyPowerThree);
		}else if(buyPowerCount >= 10&& buyPowerCount <=12){
			doFinish(player, UserFeaturesEnum.buyPowerFour);
		}else if(buyPowerCount >= 13&& buyPowerCount <=15){
			doFinish(player, UserFeaturesEnum.buyPowerFive);
		}		
	}
	
	
}
