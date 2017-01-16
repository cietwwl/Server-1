package com.rounter.client;

public enum GmResultStatusCode {
	
	STATUS_SUCCESS(2000000,"成功"),
	STATUS_SERVER_EXCEPTION(5000000, "服务器内部错误"),
	STATUS_CALLER_ERROR(5000010, "Caller错误"),
	STATUS_SIGN_ERROR(5000011, "签名错误"),
	STATUS_ENCODE_DECODE_FAIL(5000012, "AES加/解密错误"),
	STATUS_INVALID_PARAM(5000020, "业务参数无效、错误"),
	STATUS_GAMEID_ERROR(5000030, "gameId错误"),
	STATUS_ACCOUNTID_ERROR(5000031, "accountId错误"),
	STATUS_GIFT_ID_ERROR(5000032, "礼包编号错误"),
	STATUS_COUNT_ERROR(5000033, "数量错误"),
	STATUS_ROLE_ERROR(5000034, "角色信息错误"),
	STATUS_AREA_ERROR(5000035, "区服信息错误"),
	STATUS_ALREADY_GET(5000036, "已经领取"),
	;
	
	private int status;
	private String statusDesc;
	
	private GmResultStatusCode(int _status, String _statusDesc){
		this.status = _status;
		this.statusDesc = _statusDesc;
	}

	public int getStatus() {
		return status;
	}

	public String getStatusDesc() {
		return statusDesc;
	}
}
