package com.rw.service.log.infoPojo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.netty.ServerConfig;
import com.rwbase.dao.zone.TableZoneInfo;


/***
 * 分区注册信息
 * @author Allen
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ZoneRegInfo {

	private String zoneCreatedTime;
	
	private Integer regZoneId;
	
	private Long regZoneTime;
	
	private String regChannelId;
	
	private String regChannelId_uid;
	
	private String regSubChannelId;	
	
	private String regClientPlatForm;
	
	//手机运营商
	private String phoneOp;
	
	final private static Field[] fieldList;
	
	static{
		fieldList = ZoneRegInfo.class.getDeclaredFields();
		for (Field field : fieldList) {
			field.setAccessible(true);
		}
	}
	
	public Map<String,String> achieveInfoMap() throws Exception{
		Map<String, String> infoMap = new HashMap<String, String>();
		for (Field field : fieldList) {
			Object value = field.get(this);
			if(value!=null){
				infoMap.put(field.getName(), value.toString());
			}
		}
		return infoMap;
	}

	public static ZoneRegInfo fromClientInfo(ClientInfo clientInfo, String accountId, String openAccount) {
		ZoneRegInfo zoneRegInfo = new ZoneRegInfo();
		TableZoneInfo serveZoneInfo = ServerConfig.getInstance().getServeZoneInfo();
		zoneRegInfo.setZoneCreatedTime(serveZoneInfo.getOpenTime());
		zoneRegInfo.setRegZoneId(serveZoneInfo.getZoneId());
		zoneRegInfo.setRegZoneTime(System.currentTimeMillis());
		zoneRegInfo.setRegChannelId(clientInfo.getChannelId());
		zoneRegInfo.setRegSubChannelId(clientInfo.getSubChannelId());
		zoneRegInfo.setRegClientPlatForm(clientInfo.getClientPlatForm());
		zoneRegInfo.setPhoneOp(clientInfo.getPhoneOp());
		String uid = "";
		if (!StringUtils.isEmpty(openAccount)) {
			String[] split = openAccount.split("#");
			if (split.length == 2) {
				uid = split[1];
			} else {
				uid = accountId;
			}
		} else {
			uid = accountId;
		}
		zoneRegInfo.setRegChannelId_uid(clientInfo.getChannelId() + "_" + uid);
		return zoneRegInfo;
	}
	
	public Integer getRegZoneId() {
		return regZoneId;
	}

	public void setRegZoneId(Integer regZoneId) {
		this.regZoneId = regZoneId;
	}

	public Long getRegZoneTime() {
		return regZoneTime;
	}

	public void setRegZoneTime(Long regZoneTime) {
		this.regZoneTime = regZoneTime;
	}

	public String getRegChannelId() {
		return regChannelId;
	}

	public void setRegChannelId(String regChannelId) {
		this.regChannelId = regChannelId;
	}

	public String getRegSubChannelId() {
		return regSubChannelId;
	}

	public void setRegSubChannelId(String regSubChannelId) {
		this.regSubChannelId = regSubChannelId;
	}


	public String getZoneCreatedTime() {
		return zoneCreatedTime;
	}

	public void setZoneCreatedTime(String zoneCreatedTime) {
		this.zoneCreatedTime = zoneCreatedTime;
	}

	public String getRegClientPlatForm() {
		return regClientPlatForm;
	}

	public void setRegClientPlatForm(String regClientPlatForm) {
		this.regClientPlatForm = regClientPlatForm;
	}

	public String getPhoneOp() {
		return phoneOp;
	}

	public void setPhoneOp(String phoneOp) {
		this.phoneOp = phoneOp;
	}

	public String getRegChannelId_uid() {
		return regChannelId_uid;
	}

	public void setRegChannelId_uid(String regChannelId_uid) {
		this.regChannelId_uid = regChannelId_uid;
	}


}
