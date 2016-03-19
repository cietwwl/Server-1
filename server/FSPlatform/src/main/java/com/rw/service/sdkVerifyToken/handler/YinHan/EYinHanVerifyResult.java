package com.rw.service.sdkVerifyToken.handler.YinHan;

public enum EYinHanVerifyResult {
	YHYZ_000("YHYZ_000", "会话验证成功"),
	YHYZQM_001("YHYZQM_001", "验证sign失败"),
	YHYZQM_002("YHYZQM_002", "appId为空"),
	YHYZQM_003("YHYZQM_003", "appId不存在"),
	YHYZQM_004("YHYZQM_004", "sign为空"),
	YHYZ_001("YHYZ_001", "参数错误"),
	YHYZ_002("YHYZ_002", "请求地址错误"),
	YHYZ_003("YHYZ_003", "连接失败"),
	YHYZ_004("YHYZ_004", "出现HTTP错误"),
	YHYZ_005("YHYZ_005", "无效会话ID"),
	YHYZ_006("YHYZ_006", "数据签名失败"),
	YHYZ_007("YHYZ_007", "sid格式错误"),
	YHYZ_010("YHYZ_010", "不支持的游戏"),
	YHYZ_011("YHYZ_011", "不支持的渠道");
	
	private String code;
	private String desc;
	
	private EYinHanVerifyResult(String code, String desc){
		this.code = code;
		this.desc = desc;
	}

	public String getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}
	
	public static EYinHanVerifyResult[] allValue;
	
	public static EYinHanVerifyResult getEYinHanVerifyResult(String code){
		if(allValue == null){
			allValue =EYinHanVerifyResult.values();
		}
		for (EYinHanVerifyResult eYinHanVerifyResult : allValue) {
			if(eYinHanVerifyResult.getCode().equals(code)){
				return eYinHanVerifyResult;
			}
		}
		return YHYZ_011;
	}
}
