package com.rw.service.log.infoPojo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.playerdata.Player;
import com.playerdata.group.UserGroupAttributeDataMgr;
import com.rw.fsutil.util.DateUtils;
import com.rw.service.group.helper.GroupCmdHelper;
import com.rwbase.dao.copypve.CopyType;
import com.rwbase.dao.group.pojo.readonly.UserGroupAttributeDataIF;


public class RoleGameInfo {

	private String userId;
	
	private Integer vip;
	
	private Integer level;
	//升级前等级，防止跳级频繁
	private Integer levelBeforeUp;
	
	//银汉需求mapid，其实为levelid
	private Integer mapid;
	
	//局次
	private Integer GamesCode;
	
	//战力
	private Integer fighting;
	
	private Integer careerType;
	
	private String factionId;
	private String roleCreatedTime;
	
	private String userCreatedTime;
	
//	private String onlineTime;
	
	//统计信息
	private String statInfo;
	/**副本统计信息*/
	private String copyInfo;
	/**活动统计信息*/
	private String activityInfo;
	/**任务统计信息*/
	private String taskInfo;

	//在线时长
	private String onlineTime;
	
	
	final private static Field[] fieldList;
	
	static{
		fieldList = RoleGameInfo.class.getDeclaredFields();
		for (Field field : fieldList) {
			field.setAccessible(true);
		}
	}
	
	public Map<String,String> getInfoMap() throws Exception{
		Map<String, String> infoMap = new HashMap<String, String>();
		for (Field field : fieldList) {
			try {
				Object value = field.get(this);
				if(value!=null){
					infoMap.put(field.getName(), value.toString());
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		return infoMap;
	}
	
	public static RoleGameInfo fromPlayer(Player player,Map<String, String> moreinfo){
		RoleGameInfo roleGameInfo = new RoleGameInfo();
		roleGameInfo.setUserId(player.getUserId());
		roleGameInfo.setVip(player.getVip());
		roleGameInfo.setLevel(player.getLevel());
		roleGameInfo.setFighting(player.getHeroMgr().getFightingTeam());
		roleGameInfo.setCareerType(player.getCareer());
		
		UserGroupAttributeDataMgr mgr = player.getUserGroupAttributeDataMgr();
		UserGroupAttributeDataIF baseData = mgr.getUserGroupAttributeData();
		String groupId = baseData.getGroupId();
		if (!StringUtils.isEmpty(groupId)) {
			roleGameInfo.setFactionId(groupId);
		}
		
		

		long roleCreatedTime = player.getUserDataMgr().getCreateTime();
		
		if(roleCreatedTime>0){
			roleGameInfo.setRoleCreatedTime(DateUtils.getDateTimeFormatString(roleCreatedTime, "yyyy-MM-dd HH:mm:ss"));
		}
		
		long userCreateTime = player.getUserDataMgr().getCreateTime();
		if(userCreateTime>0){
			roleGameInfo.setUserCreatedTime(DateUtils.getDateTimeFormatString(userCreateTime, "yyyy-MM-dd HH:mm:ss"));
		}
		if(player.getZoneLoginInfo()!=null){
			long onlineTime = (System.currentTimeMillis() - player.getZoneLoginInfo().getLoginZoneTime())/1000;
			StringBuilder statInfo = new StringBuilder();			
			int giftGold = player.getUserGameDataMgr().getGiftGold();
			int chargeGold = player.getUserGameDataMgr().getChargeGold();
			long coin = player.getUserGameDataMgr().getCoin();						
			statInfo.append("online_time:").append(onlineTime).append("#")
					.append("main_coin:").append(chargeGold).append("#")
					.append("gift_coin:").append(giftGold).append("#")
					.append("sub_coin:").append(coin);
			roleGameInfo.setStatInfo(statInfo.toString());			
			roleGameInfo.setOnlineTime("online_time:" + onlineTime);
			
			
			int spcase = -1;
			long nmcase = -1;
			
			String fighttime="";
			if(moreinfo!= null){
				if(moreinfo.containsKey("fightTime")){
					fighttime = moreinfo.get("fightTime");
				}
				if(moreinfo.containsKey("copyLevel")&&moreinfo.containsKey("enemyTimes")){					
					if(Integer.parseInt(moreinfo.get("copyLevel")) == CopyType.COPY_TYPE_NORMAL){
						spcase = Integer.parseInt(moreinfo.get("enemyTimes"));
					}else{
						nmcase = Integer.parseInt(moreinfo.get("enemyTimes"));
					}
				}
			}
			StringBuilder copyInfo = new StringBuilder();
			copyInfo.append("fight_time:").append(fighttime).append("#")
			.append("sp_case:").append(spcase).append("#")
			.append("nm_case:").append(nmcase);
			roleGameInfo.setCopyInfo(copyInfo.toString());
			
			
			String activityTime="";
			String rewardsinfoactivity = "";
			if(moreinfo!= null){
				if(moreinfo.containsKey("activityTime")){
					activityTime = moreinfo.get("activityTime");
				}
				if(moreinfo.containsKey("rewardsinfoactivity")){
					rewardsinfoactivity = moreinfo.get("rewardsinfoactivity");
				}
			}			
			StringBuilder activityInfo = new StringBuilder();
			activityInfo.append("activity_time:").append(activityTime).append("#")
			.append("activity_reward:").append(rewardsinfoactivity);
			roleGameInfo.setActivityInfo(activityInfo.toString());
			
			
			
			String rewardsinfotask = "";
			if(moreinfo!= null){
				if(moreinfo.containsKey("rewardsinfotask")){
					rewardsinfotask = moreinfo.get("rewardsinfotask");
				}
			}			
			StringBuilder taskInfo = new StringBuilder();
			taskInfo.append("activity_time:").append("").append("#")
			.append("task_reward:").append(rewardsinfotask);
			roleGameInfo.setTaskInfo(taskInfo.toString());
					
			
		}
		
		
		
		
		if(player.getCopyRecordMgr().getCalculateState() != null){
			roleGameInfo.setMapId(player.getCopyRecordMgr().getCalculateState().getLastBattleId());
		}
		
		return roleGameInfo;
	}

	
	
	
	public String getFactionId() {
		return factionId;
	}

	public void setFactionId(String factionId) {
		this.factionId = factionId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getVip() {
		return vip;
	}

	public void setVip(Integer vip) {
		this.vip = vip;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}
	
	public Integer getLevelBeforeUp() {
		return levelBeforeUp;
	}

	public void setLevelBeforeUp(Integer levelBeforeUp) {
		this.levelBeforeUp = levelBeforeUp;
	}
	
	public Integer getMapId() {
		return mapid;
	}

	public void setMapId(Integer mapid) {
		this.mapid = mapid;
	}
	
	public Integer getGamesCode() {
		return GamesCode;
	}

	public void setGamesCode(Integer gamescode) {
		this.GamesCode = gamescode;
	}
	
	
	public Integer getFighting() {
		return fighting;
	}

	public void setFighting(Integer fighting) {
		this.fighting = fighting;
	}

	public Integer getCareerType() {
		return careerType;
	}

	public void setCareerType(Integer careerType) {
		this.careerType = careerType;
	}


	public String getRoleCreatedTime() {
		return roleCreatedTime;
	}

	public void setRoleCreatedTime(String roleCreatedTime) {
		this.roleCreatedTime = roleCreatedTime;
	}
	
	

	public String getOnlineTime() {
		return onlineTime;
	}

	public void setOnlineTime(String onlineTime) {
		this.onlineTime = onlineTime;
	}

	public String getStatInfo() {
		return statInfo;
	}

	public void setStatInfo(String statInfo) {
		this.statInfo = statInfo;
	}

	public String getUserCreatedTime() {
		return userCreatedTime;
	}

	public void setUserCreatedTime(String userCreatedTime) {
		this.userCreatedTime = userCreatedTime;
	}
	public String getCopyInfo() {
		return copyInfo;
	}

	public void setCopyInfo(String copyInfo) {
		this.copyInfo = copyInfo;
	}

	public String getActivityInfo() {
		return activityInfo;
	}

	public void setActivityInfo(String activityInfo) {
		this.activityInfo = activityInfo;
	}

	public String getTaskInfo() {
		return taskInfo;
	}

	public void setTaskInfo(String taskInfo) {
		this.taskInfo = taskInfo;
	}
	
	
}
