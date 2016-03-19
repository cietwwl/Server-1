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
	
	//战力
	private Integer fighting;
	
	private Integer careerType;
	
	private String roleCreatedTime;
	
	private String userCreatedTime;
	
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
			roleGameInfo.setOnlineTime(""+onlineTime);
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

	public String getUserCreatedTime() {
		return userCreatedTime;
	}

	public void setUserCreatedTime(String userCreatedTime) {
		this.userCreatedTime = userCreatedTime;
	}

	

}
