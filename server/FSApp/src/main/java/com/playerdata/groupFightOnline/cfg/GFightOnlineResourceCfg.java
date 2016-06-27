package com.playerdata.groupFightOnline.cfg;
import java.util.Calendar;

import com.common.BaseConfig;
import com.playerdata.groupFightOnline.dataForClient.GFResourceState;

public class GFightOnlineResourceCfg extends BaseConfig {
	private int key; //关键字段
	private int resID; //资源点ID
	private String biddingStartTime; //竞标开始时间
	private GFTimeStruct biddingStartObj;
	private String prepareStartTime; //备战开始时间
	private GFTimeStruct prepareStartObj;
	private String fightStartTime; //开战时间
	private GFTimeStruct fightStartObj;
	private String fightEndTime; //战斗结算时间
	private GFTimeStruct fightEndObj;
	private String biddingBaseCost; //竞标起始资源
	private String biddingAddCost; //加标最少资源
	private int biddingLevelLimit; //竞标团队最低等级
	private String ownerDailyReward; //资源产出
	private int emailId; //对应邮件ID

	public int getKey() {
		return key;
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
	public String getBiddingBaseCost() {
		return biddingBaseCost;
	}
	public String getBiddingAddCost() {
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
