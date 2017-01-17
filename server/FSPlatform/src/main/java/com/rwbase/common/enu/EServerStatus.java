package com.rwbase.common.enu;

public enum EServerStatus {
	CLOSE		(-1, "关闭", EColor.Gray),
	NORMAL		(0, "普通", EColor.Green),
	MAINTAIN	(1, "维护", EColor.Gray),
	HIT			(2, "火爆", EColor.Red),
	NEW			(3, "新服", EColor.Green),
	FIRST_INFORM	(5, "首次通知", EColor.Green),
	;
	private int statusId;
	private String statusDes;
	private EColor color;
	private EServerStatus(int statusId, String statusDes, EColor color){
		this.statusId = statusId;
		this.statusDes = statusDes;
		this.color = color;
	}
	
	public int getStatusId() {
		return statusId;
	}
	
	public String getStatusDes() {
		return statusDes;
	}
	
	public EColor getColor() {
		return color;
	}

	private static EServerStatus[] allValue;
	
	public static EServerStatus getStatus(int id){
		if(allValue == null){
			allValue = EServerStatus.values();
		}
		
		for (EServerStatus eServerStatus : allValue) {
			if(eServerStatus.getStatusId() == id){
				return eServerStatus;
			}
		}
		
		return EServerStatus.MAINTAIN;
	}
	
	public static EServerStatus getStatus(String value){
		if(allValue == null){
			allValue = EServerStatus.values();
		}
		
		for (EServerStatus eServerStatus : allValue) {
			if(eServerStatus.getStatusDes().equals(value)){
				return eServerStatus;
			}
		}
		
		return EServerStatus.MAINTAIN;
	}
	
	public static boolean isOpen(int statusId){
		if(statusId == CLOSE.statusId || statusId == MAINTAIN.statusId){
			return false;
		}else{
			return true;
		}
	}
}
