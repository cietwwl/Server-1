package com.rw.service.log.infoPojo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.fsutil.util.jackson.JsonUtil;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientInfo {
	/**注册登录渠道*/
	private String channelId;
	/**注册登录子渠道*/
	private String subChannelId;	
	
	private String regChannelId_uid;
	
	private String clientPlatForm;
	
	private String netType;
	
	private String clientVersion;
	
	private String phoneType;
	
	private String clientIp;
	
	private String imei;
	
	private String imac;
	
	private String accountId;
	
	
	private String adLinkId;
	private String sdkVersion;
	private String systemVersion;
	private String sdk_id;
	
	//运营商
	private String phoneOp;
	
	final private static Field[] fieldList;
	
	static{
		fieldList = ClientInfo.class.getDeclaredFields();
		for (Field field : fieldList) {
			field.setAccessible(true);
		}
	}
	
	public Map<String,String> getInfoMap() throws Exception{
		Map<String, String> infoMap = new HashMap<String, String>();
		for (Field field : fieldList) {
			Object value = field.get(this);
			if(value!=null){
				infoMap.put(field.getName(), value.toString());
			}
		}
		return infoMap;
	}
	
	public static ClientInfo fromJson(String json, String accountId, String openAccount){
		
		ClientInfo clientInfo = JsonUtil.readValue(json, ClientInfo.class);
		if (clientInfo != null) {
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

			clientInfo.setRegChannelId_uid(clientInfo.getChannelId() + "_" + uid);
			clientInfo.setAccountId(accountId);
		} else {
			clientInfo = new ClientInfo();
		}
		
		return clientInfo;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getSubChannelId() {
		return subChannelId;
	}

	public void setSubChannelId(String subChannelId) {
		this.subChannelId = subChannelId;
	}

	public String getClientPlatForm() {
		return clientPlatForm;
	}

	public void setClientPlatForm(String clientPlatForm) {
		this.clientPlatForm = clientPlatForm;
	}

	public String getNetType() {
		return netType;
	}

	public void setNetType(String netType) {
		this.netType = netType;
	}

	public String getClientVersion() {
		return clientVersion;
	}

	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}

	public String getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		if(StringUtils.isNotBlank(clientIp) && StringUtils.contains(clientIp,".")){
			this.clientIp = clientIp;
		}else{
			this.clientIp = "0.0.0.0";
		}
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getImac() {
		return imac;
	}

	public void setImac(String imac) {
		this.imac = imac;
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

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getSdkVersion() {
		return sdkVersion;
	}

	public void setSdkVersion(String sdkVersion) {
		this.sdkVersion = sdkVersion;
	}

	public String getSdk_id() {
		return sdk_id;
	}

	public void setSdk_id(String sdk_id) {
		this.sdk_id = sdk_id;
	}
	public String getAdLinkId() {
		return adLinkId;
	}

	public void setAdLinkId(String adLinkId) {
		this.adLinkId = adLinkId;
	}
	public String getSystemVersion() {
		return systemVersion;
	}

	public void setSystemVersion(String systemVersion) {
		this.systemVersion = systemVersion;
	}
}
