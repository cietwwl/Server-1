package com.rounter.state;

/**
 * UC通讯状态码
 * @author Alex
 *
 * 2016年12月13日 上午10:18:19
 */
public enum UCStateCode {

	STATE_OK(2000000,"成功"),
	STATE_SERVER_ERROR(5000000,"服务器内部错误"),
	STATE_CALLER_ERROR(5000010,"Caller错误"),
	STATE_SIGN_ERROR(5000011,"签名错误"),
	STATE_ENCRYPT_DECRYPT_ERROR(5000012,"AES加/解密错误"),
	STATE_PARAM_ERROR(5000020,"业务参数无效、错误"),
	STATE_GAMEID_ERROR(2000000,"gameId错误"),
	STATE_ACCOUNT_ERROR(5000031,"accountId错误"),
	STATE_GIFTID_ERROR(5000032,"礼包编号错误"),
	STATE_GIFT_NUM_ERROR(5000033,"礼包数量错误"),
	STATE_ROLE_NOT_EXIST(5000034,"角色不存在错误"),
	STATE_ZONE_NOT_EXIST(5000035,"区服不存在或错误"),
	STATE_GIFT_RECV(5000036,"角色已经领取过该礼包"),
	
	;
	
	private int id;
	
	private String msg;
	
	private static UCStateCode[] codes = UCStateCode.values();
	
	UCStateCode(int ordinal, String name) {
		this.id = ordinal;
		this.msg = name;
	}

	public int getId() {
		return id;
	}

	public String getMsg() {
		return msg;
	}
	
	
	public static UCStateCode getCodeByID(int id){
		for (UCStateCode code : codes) {
			if(code.id == id){
				return code;
			}
		}
		return null;
	}
	
	
}
