package com.rw.service.log.infoPojo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.fsutil.util.DateUtils;
import com.rw.manager.GameManager;
import com.rw.netty.ServerConfig;
import com.rwbase.dao.zone.TableZoneInfo;


/**
 * 分区登录信息
 * @author Allen
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ZoneLoginInfo {

	
	private Integer loginZoneId;
	
	private Long loginZoneTime;
	
	private String loginChannelId;
	
	private String loginSubChannelId;	
	
	private String loginClientPlatForm;
	
	private String loginNetType;
	
	private String clientVersion;
	
	private String loginPhoneType;
	
	private String loginClientIp;
	
	private String loginImei;
	
	private String loginImac;
	
	private String loginsdkVersion;
	


	private String loginsystemVersion;
	
	private String loginadLinkId;




	final private static Field[] fieldList;
	
	static{
		fieldList = ZoneLoginInfo.class.getDeclaredFields();
		for (Field field : fieldList) {
			field.setAccessible(true);
		}
	}
	
	@JsonIgnore
	public Map<String,String> getInfoMap() throws Exception{
		Map<String, String> infoMap = new HashMap<String, String>();
		for (Field field : fieldList) {
			Object value = field.get(this);
			if(value!=null){
				if(field.getName().equals("loginZoneTime")){
					value = DateUtils.getDateTimeFormatString((Long)value, "yyyy-MM-dd HH:mm:ss");
				}
				infoMap.put(field.getName(), value.toString());
			}
		}
		return infoMap;
	}
	

	public static ZoneLoginInfo fromClientInfo(ClientInfo clientInfo){
		ZoneLoginInfo zoneLoginInfo = new ZoneLoginInfo();
		TableZoneInfo serveZoneInfo = ServerConfig.getInstance().getServeZoneInfo();
		zoneLoginInfo.setLoginZoneId(GameManager.getZoneId());
		zoneLoginInfo.setLoginZoneTime(System.currentTimeMillis());
		zoneLoginInfo.setLoginChannelId(clientInfo.getChannelId());
		zoneLoginInfo.setLoginSubChannelId(clientInfo.getSubChannelId());
		zoneLoginInfo.setLoginClientPlatForm(clientInfo.getClientPlatForm());
		zoneLoginInfo.setLoginNetType(clientInfo.getNetType());
		zoneLoginInfo.setClientVersion(clientInfo.getClientVersion());
		zoneLoginInfo.setLoginPhoneType(clientInfo.getPhoneType());
		zoneLoginInfo.setLoginClientIp(clientInfo.getClientIp());
		zoneLoginInfo.setLoginImei(clientInfo.getImei());
		zoneLoginInfo.setLoginImac(clientInfo.getImac());
		zoneLoginInfo.setLoginsdkVersion(clientInfo.getSdkVersion());
		zoneLoginInfo.setLoginsystemVersion(clientInfo.getSystemVersion());
		zoneLoginInfo.setLoginadLinkId(clientInfo.getAdLinkId());
		return zoneLoginInfo;
	}
	

	public String getLoginClientPlatForm() {
		return loginClientPlatForm;
	}



	public void setLoginClientPlatForm(String loginClientPlatForm) {
		this.loginClientPlatForm = loginClientPlatForm;
	}



	public String getLoginNetType() {
		return loginNetType;
	}



	public void setLoginNetType(String loginNetType) {
		this.loginNetType = loginNetType;
	}



	public Integer getLoginZoneId() {
		return loginZoneId;
	}

	public void setLoginZoneId(Integer loginZoneId) {
		this.loginZoneId = loginZoneId;
	}

	public Long getLoginZoneTime() {
		return loginZoneTime;
	}

	public void setLoginZoneTime(Long loginZoneTime) {
		this.loginZoneTime = loginZoneTime;
	}

	public String getLoginChannelId() {
		return loginChannelId;
	}

	public void setLoginChannelId(String loginChannelId) {
		this.loginChannelId = loginChannelId;
	}

	public String getLoginSubChannelId() {
		return loginSubChannelId;
	}

	public void setLoginSubChannelId(String loginSubChannelId) {
		this.loginSubChannelId = loginSubChannelId;
	}


	public String getClientVersion() {
		return clientVersion;
	}

	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}



	public String getLoginPhoneType() {
		return loginPhoneType;
	}



	public void setLoginPhoneType(String loginPhoneType) {
		this.loginPhoneType = loginPhoneType;
	}



	public String getLoginClientIp() {
		return loginClientIp;
	}



	public void setLoginClientIp(String loginClientIp) {
		this.loginClientIp = loginClientIp;
	}



	public String getLoginImei() {
		return loginImei;
	}



	public void setLoginImei(String loginImei) {
		this.loginImei = loginImei;
	}



	public String getLoginImac() {
		return loginImac;
	}



	public void setLoginImac(String loginImac) {
		this.loginImac = loginImac;
	}
	
	public String getLoginsdkVersion() {
		return loginsdkVersion;
	}


	public void setLoginsdkVersion(String logsdkVersion) {
		if(logsdkVersion == null){
			logsdkVersion = "";
		}
		this.loginsdkVersion = logsdkVersion;
	}
	
	public String getLoginsystemVersion() {
		return loginsystemVersion;
	}


	public void setLoginsystemVersion(String loginsystemVersion) {
		if(loginsystemVersion == null){
			loginsystemVersion = "";
		}
		this.loginsystemVersion = loginsystemVersion;
	}
	public String getLoginadLinkId() {
		return loginadLinkId;
	}


	public void setLoginadLinkId(String loginadLinkId) {
		if(loginadLinkId == null){
			loginadLinkId = "";
		}		
		this.loginadLinkId = loginadLinkId;
	}

	
}
