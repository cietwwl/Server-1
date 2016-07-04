package com.playerdata.groupFightOnline.cfg;
import java.util.Calendar;
import java.util.List;

import com.common.BaseConfig;
import com.playerdata.groupFightOnline.bm.GFightHelper;
import com.playerdata.groupFightOnline.enums.GFResourceState;
import com.rwbase.dao.copy.pojo.ItemInfo;

public class GFightOnlineResourceCfg extends BaseConfig {
	private int key; //关键字段
	private String resName;
	private int resID; //资源点ID
	private String biddingStartTime; //竞标开始时间
	private GFTimeStruct biddingStartObj;
	private String prepareStartTime; //备战开始时间
	private GFTimeStruct prepareStartObj;
	private String fightStartTime; //开战时间
	private GFTimeStruct fightStartObj;
	private String fightEndTime; //战斗结算时间
	private GFTimeStruct fightEndObj;
	private int biddingBaseCost; //竞标起始资源
	private int biddingAddCost; //加标最少资源
	private int biddingLevelLimit; //竞标团队最低等级
	private String ownerDailyReward; //资源产出
	private int emailId; //对应邮件ID
	private String victoryMemReward;	//胜利帮派的成员奖励
	private List<ItemInfo> list_victoryMemReward;
	private String victoryLeaderReward;	//胜利帮派的帮主奖励
	private List<ItemInfo> list_victoryLeaderReward;
	private int victoryEmailID;		//胜利奖励的邮件ID
	private String failMemReward;	//失败帮派的成员奖励
	private List<ItemInfo> list_failMemReward;
	private String failLeaderReward;	//失败帮派的帮主奖励
	private List<ItemInfo> list_failLeaderReward;
	private int failEmailID;		//失败奖励的邮件ID
	
	public int getKey() {
		return key;
	}
	
	public String getResName(){
		return resName;
	}
	
	public int getResID() {
	    return resID;
	}
	
	public String getBiddingStartTime() {
		return biddingStartTime;
	}
	
	public String getPrepareStartTime() {
	    return prepareStartTime;
	}
	
	public String getFightStartTime() {
		return fightStartTime;
	}
	
	public String getFightEndTime() {
		return fightEndTime;
	}
	
	public int getBiddingBaseCost() {
		return biddingBaseCost;
	}
	
	public int getBiddingAddCost() {
		return biddingAddCost;
	}
	
	public int getBiddingLevelLimit() {
		return biddingLevelLimit;
	}
	
	public String getOwnerDailyReward() {
		return ownerDailyReward;
	}
	
	public int getEmailId() {
		return emailId;
	}

	public String getVictoryMemReward() {
		return victoryMemReward;
	}

	public String getVictoryLeaderReward() {
		return victoryLeaderReward;
	}

	public int getVictoryEmailID() {
		return victoryEmailID;
	}

	public String getFailMemReward() {
		return failMemReward;
	}

	public String getFailLeaderReward() {
		return failLeaderReward;
	}

	public int getFailEmailID() {
		return failEmailID;
	}
	
	public List<ItemInfo> getVictoryMemRewardItems() {
		return list_victoryMemReward;
	}

	public List<ItemInfo> getVictoryLeaderRewardItems() {
		return list_victoryLeaderReward;
	}

	public List<ItemInfo> getFailMemRewardItems() {
		return list_failMemReward;
	}

	public List<ItemInfo> getFailLeaderRewardItems() {
		return list_failLeaderReward;
	}

	public void ExtraInitAfterLoad() {
		this.biddingStartObj = fromStringToTimeStruct(biddingStartTime);
		this.prepareStartObj = fromStringToTimeStruct(prepareStartTime);
		this.fightStartObj = fromStringToTimeStruct(fightStartTime);
		this.fightEndObj = fromStringToTimeStruct(fightEndTime);
		if(biddingStartObj.dayOfWeek > prepareStartObj.dayOfWeek){
			prepareStartObj.isNextWeek = true;
			fightStartObj.isNextWeek = true;
			fightEndObj.isNextWeek = true;
		}else if(prepareStartObj.dayOfWeek > fightStartObj.dayOfWeek){
			fightStartObj.isNextWeek = true;
			fightEndObj.isNextWeek = true;
		}else if(fightStartObj.dayOfWeek > fightEndObj.dayOfWeek){
			fightEndObj.isNextWeek = true;
		}

		this.list_victoryMemReward = GFightHelper.stringToItemList(victoryMemReward);
		this.list_victoryLeaderReward = GFightHelper.stringToItemList(victoryLeaderReward);
		this.list_failMemReward = GFightHelper.stringToItemList(failMemReward);
		this.list_failLeaderReward = GFightHelper.stringToItemList(failLeaderReward);
	}
	
	public GFResourceState checkResourceState(){
		Calendar cal = Calendar.getInstance();
		long current = cal.getTimeInMillis();
		boolean isLastWeekLoop = false; //是否还在上一周的循环
		long bidStart = getStageTime(cal, biddingStartObj, isLastWeekLoop);
		if(current < bidStart) isLastWeekLoop = true;
		long prepareStart = getStageTime(cal, prepareStartObj, isLastWeekLoop);
		if(current < prepareStart) return GFResourceState.BIDDING;
		long fightStart = getStageTime(cal, fightStartObj, isLastWeekLoop);
		if(current < fightStart) return GFResourceState.PREPARE;
		long fightEnd = getStageTime(cal, fightEndObj, isLastWeekLoop);
		if(current < fightEnd) return GFResourceState.FIGHT;
		return GFResourceState.REST;
	}
	
	private long getStageTime(Calendar cal, GFTimeStruct time, boolean isLastWeekLoop){
		cal.set(Calendar.DAY_OF_WEEK, time.dayOfWeek);
		cal.set(Calendar.HOUR_OF_DAY, time.hour);
		cal.set(Calendar.MINUTE, time.minute);
		cal.set(Calendar.SECOND, time.second);
		//处理跨周的问题
		long offset = 0;
		if(isLastWeekLoop) offset -= 7 * 24 * 60 * 60 *1000;
		if(time.isNextWeek) offset += 7 * 24 * 60 * 60 *1000;
		return cal.getTimeInMillis() + offset;
	}
	
	private GFTimeStruct fromStringToTimeStruct(String time){
		String[] strArr = time.split("_");
		int dayOfWeek = (Integer.valueOf(strArr[0]) + 1)%7;
		if(dayOfWeek == 0) dayOfWeek = 7;
		String[] timeArr = strArr[1].split(":");
		int hour = Integer.valueOf(timeArr[0]);
		int minute = Integer.valueOf(timeArr[1]);
		int second = Integer.valueOf(timeArr[2]);
		return new GFTimeStruct(dayOfWeek, hour, minute, second);
	}
}
