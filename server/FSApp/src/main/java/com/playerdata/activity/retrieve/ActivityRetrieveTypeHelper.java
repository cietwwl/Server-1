package com.playerdata.activity.retrieve;

import java.util.Calendar;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.retrieve.cfg.CostOrder;
import com.playerdata.activity.retrieve.cfg.NormalRewardsCfg;
import com.playerdata.activity.retrieve.cfg.PerfectRewardsCfg;
import com.playerdata.activity.retrieve.data.RewardBackSubItem;
import com.playerdata.activity.retrieve.data.RewardBackTodaySubItem;
import com.playerdata.activity.retrieve.userFeatures.UserFeatruesMgr;
import com.playerdata.activity.retrieve.userFeatures.UserFeaturesEnum;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwproto.PrivilegeProtos.LoginPrivilegeNames;

public class ActivityRetrieveTypeHelper {

	public static String getItemId(String userId, ActivityRetrieveTypeEnum typeEnum) {
		return userId + "_" + typeEnum.getId();
	}
	
	private static ActivityRetrieveTypeHelper instance = new ActivityRetrieveTypeHelper();
	
	public static ActivityRetrieveTypeHelper getInstance(){
		return instance;
	}
	
	/**分段函数,用于对体力溢出以及购买体力的不同次数匹配不同的消耗；本应该策划填表直接读取*/
	public  int getCostByCountWithCostOrderList(List<CostOrder> list,int count){
		float cost = 0;
		int addWidth = 0;
		int widthTmp = 0;
		for(CostOrder costOrder : list){
			if(count <= widthTmp){
				break;
			}
			int tmp = count - costOrder.getWidth();
			tmp = tmp >= 0?costOrder.getWidth()- widthTmp:count-addWidth;
			cost += tmp*costOrder.getCost();
			addWidth +=tmp;			
			widthTmp = costOrder.getWidth();
		}		
		return (int)cost;
	}
	
	/**用于统一处理早午晚类生成*/
	public  RewardBackTodaySubItem doEatEvent(UserFeaturesEnum iEnum){
		RewardBackTodaySubItem subItem = new RewardBackTodaySubItem();
		subItem.setId(iEnum.getId());		
		subItem.setCount(0);
		subItem.setMaxCount(1);
		return subItem;	
	}
	
	/**用于统一处理早午晚类刷新*/
	public  void doEatFresh(RewardBackTodaySubItem todaySubItem, Player player, CfgOpenLevelLimitDAO dao){
		int level = player.getLevel();
		if(level >= dao.checkIsOpen(eOpenLevelType.DAILY, player)){
			todaySubItem.setMaxCount(1);			
		}else{
			todaySubItem.setMaxCount(0);
		}	
	}
	
	/**用于统一处理购买体力类生成*/
	public  RewardBackTodaySubItem doBuyPowerEvent(UserFeaturesEnum iEnum){
		RewardBackTodaySubItem subItem = new RewardBackTodaySubItem();
		subItem.setId(iEnum.getId());		
		subItem.setCount(0);
		subItem.setMaxCount(3);
		return subItem;	
	}
	
	/**用于统一处理购买体力类刷新*/
	public  void doBuyPowerFresh(RewardBackTodaySubItem todaySubItem, Player player, CfgOpenLevelLimitDAO dao,int buyType){
		int level = player.getLevel();		
		if(level >= dao.checkIsOpen(eOpenLevelType.MAIN_CITY, player)){
			int time = player.getPrivilegeMgr().getIntPrivilege(LoginPrivilegeNames.buyPowerCount);
			int cutTime = time >= buyType?time-UserFeatruesMgr.buyPowerLength*(buyType/UserFeatruesMgr.buyPowerLength):0;
			cutTime = cutTime > UserFeatruesMgr.buyPowerLength?UserFeatruesMgr.buyPowerLength : cutTime;
			todaySubItem.setMaxCount(cutTime);			
		}else{
			todaySubItem.setMaxCount(0);
		}
	}
	
	/**用于统一处理获得体力类的普通奖励，此方法本可不必，策划给表就好*/
	public  String doBuyPowerGetNormalReward(NormalRewardsCfg cfg,RewardBackSubItem subItem){
		int times = subItem.getMaxCount() - subItem.getCount();
		if(times <= 0||times >UserFeatruesMgr.buyPowerLength){
			return null;
		}
		String tmp = "3:" + 60*times;
		return tmp;
	}
	
	/**用于统一处理获得体力类的普通奖励，此方法本可不必，策划给表就好*/
	public  String doBuyPowerGetPerfectReward(PerfectRewardsCfg cfg,RewardBackSubItem subItem){
		int times = subItem.getMaxCount() - subItem.getCount();
		if(times <= 0||times >UserFeatruesMgr.buyPowerLength){
			return null;
		}
		String tmp = "3:" + 120*times;
		return tmp;
	}
	
	/**
	 * 
	 * @param copyInfoId copyinfo表的id
	 * @return 目前服务器是没有判断生存幻境某个类型是否开启的，丢一个在这里,方便  《每日找回》 功能判断；等策划要放到服务器功能时在挪走
	 */
	public static boolean isOpenOfCelestial(int copyInfoId){
		Calendar currentDay = DateUtils.getCurrent();
		int day = currentDay.get(Calendar.DAY_OF_WEEK);// 当前天数
		if(copyInfoId == 3){//昆仑虚
			if(day == Calendar.MONDAY || day == Calendar.WEDNESDAY || day == Calendar.FRIDAY || day == Calendar.SUNDAY){
				return true;
			}
		}else if(copyInfoId == 4){//蓬莱岛
			if(day == Calendar.TUESDAY || day == Calendar.THURSDAY || day == Calendar.SATURDAY || day == Calendar.SUNDAY){
				return true;
			}
		}
		return false;
	}
	
}
