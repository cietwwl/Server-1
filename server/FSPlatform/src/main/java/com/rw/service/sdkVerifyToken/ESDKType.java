package com.rw.service.sdkVerifyToken;

/**
 * SDK 枚举类型
 * @author lida
 *
 */
public enum ESDKType {
	NONE_SDK(0),
	YINHAN_SDK(1);
	
	private int type;
	private ESDKType(int type){
		this.type = type;
	}
	public int getType() {
		return type;
	}
	
	private static ESDKType[] allValue;
	public static ESDKType getSDKType(int type){
		if(allValue == null){
			allValue = ESDKType.values();
		}
		
		for (ESDKType esdkType : allValue) {
			if(esdkType.getType() == type){
				return esdkType;
			}
		}
		return YINHAN_SDK;
	}
}
