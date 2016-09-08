package com.fy.address;

import com.fy.SpringContextUtil;

public class AddressInfo {
	private String loginServerDomain;
	private String cdnDomain;
	private String cdnBackUpDomain;
	
	public static AddressInfo getInstance(){
		return SpringContextUtil.getBean("addressInfo");
	}
	
	public void init(){}
	
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
}
