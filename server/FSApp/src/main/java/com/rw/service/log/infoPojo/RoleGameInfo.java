package com.rw.service.log.infoPojo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.playerdata.Player;
import com.rw.fsutil.util.DateUtils;


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
	
	private String roleCreatedTime;
	
	private String userCreatedTime;
	
//	private String onlineTime;
	
	//统计信息
	private String statInfo;
	
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
	
	public static RoleGameInfo fromPlayer(Player player){
		RoleGameInfo roleGameInfo = new RoleGameInfo();
		roleGameInfo.setUserId(player.getUserId());
		roleGameInfo.setVip(player.getVip());
		roleGameInfo.setLevel(player.getLevel());
		roleGameInfo.setFighting(player.getHeroMgr().getFightingTeam());
		roleGameInfo.setCareerType(player.getCareer());
		
		
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
		}
		if(player.getCopyRecordMgr().getCalculateState() != null){
			roleGameInfo.setMapId(player.getCopyRecordMgr().getCalculateState().getLastBattleId());
		}
		
		return roleGameInfo;
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

	

}
