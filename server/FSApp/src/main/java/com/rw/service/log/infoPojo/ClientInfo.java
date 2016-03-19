package com.rw.service.log.infoPojo;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.fsutil.util.jackson.JsonUtil;

@JsonIgnoreProperties(ignoreUnknown = false)
public class ClientInfo {
	
	private String channelId;
	
	private String subChannelId;	
	
	private String clientPlatForm;
	
	private String netType;
	
	private String clientVersion;
	
	private String phoneType;
	
	private String clientIp;
	
	private String imei;
	
	private String imac;
	
	//运营商
	private String phoneOp;
	
	public static ClientInfo fromJson(String json){
		ClientInfo clientInfo = JsonUtil.readValue(json, ClientInfo.class);
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
		if(StringUtils.isNotBlank(clientIp) && clientIp.contains(".")){
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
	
	
	
}
