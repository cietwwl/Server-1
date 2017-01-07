package com.rw.service.http.platformResponse;

public enum ServerType {

	/**
	 * 服务器类型：安卓自运营，对应的ServerType要设为"ANDROID_ZYY"
	 */
	ANDROID_ZYY(false),
	/**
	 * 服务器类型：安卓腾讯服，对应的ServerType要设为"ANDROID_TENCENT"
	 */
	ANDROID_TENCENT(false),
	/**
	 * 服务器类型：要玩，对应的ServerType要设为"IOS_YAOWAN"
	 */
	IOS_YAOWAN(true),
	;
	private boolean _ios;
	
	private ServerType(boolean pIos) {
		this._ios = pIos;
	}
	
	public boolean isIos() {
		return this._ios;
	}
}
