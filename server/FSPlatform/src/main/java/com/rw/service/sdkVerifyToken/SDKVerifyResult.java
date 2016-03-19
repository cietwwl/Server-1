package com.rw.service.sdkVerifyToken;

/**
 * SDK验证结果
 * @author lida
 *
 */
public class SDKVerifyResult {
	private boolean blnSuccess;
	private String msg;
	private String id_uid;
	
	public SDKVerifyResult(){}

	public void setBlnSuccess(boolean blnSuccess) {
		this.blnSuccess = blnSuccess;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public void setId_uid(String id_uid) {
		this.id_uid = id_uid;
	}

	public boolean isBlnSuccess() {
		return blnSuccess;
	}

	public String getMsg() {
		return msg;
	}

	public String getId_uid() {
		return id_uid;
	}
}
