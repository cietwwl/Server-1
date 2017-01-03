package com.fy.address;

import com.fy.version.Version;

public class ChannelAddressInfo {
	private String loginServerDomain;   //登陆服务器
	private String cdnDomain;			//cdn地址
	private String cdnBackUpDomain;		//cdn备份地址
	private String logServerAddress;	//日志服务器
	
	private String checkServerURL;   	//审核服地址
	
	private String checkServerPayURL;  	//审核服支付地址
	
	private String backUrl;      		//审核时记得填写，否则有可能在审核过程中请求到正式服
	
	private String channel;
	
	private String luaVerifySwitch = "true";

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getLoginServerDomain() {
		return loginServerDomain;
	}

	public void setLoginServerDomain(String loginServerDomain) {
		this.loginServerDomain = loginServerDomain;
	}

	public String getCdnDomain() {
		return cdnDomain;
	}

	public void setCdnDomain(String cdnDomain) {
		this.cdnDomain = cdnDomain;
	}

	public String getCdnBackUpDomain() {
		return cdnBackUpDomain;
	}

	public void setCdnBackUpDomain(String cdnBackUpDomain) {
		this.cdnBackUpDomain = cdnBackUpDomain;
	}

	public String getLogServerAddress() {
		return logServerAddress;
	}

	public void setLogServerAddress(String logServerAddress) {
		this.logServerAddress = logServerAddress;
	}

	public String getCheckServerURL() {
		return checkServerURL;
	}

	public void setCheckServerURL(String checkServerURL) {
		this.checkServerURL = checkServerURL;
	}

	public String getCheckServerPayURL() {
		return checkServerPayURL;
	}

	public void setCheckServerPayURL(String checkServerPayURL) {
		this.checkServerPayURL = checkServerPayURL;
	}

	public String getBackUrl() {
		return backUrl;
	}

	public void setBackUrl(String backUrl) {
		this.backUrl = backUrl;
	}

	public String getLuaVerifySwitch() {
		return luaVerifySwitch;
	}

	public void setLuaVerifySwitch(String luaVerifySwitch) {
		this.luaVerifySwitch = luaVerifySwitch;
	}
}
