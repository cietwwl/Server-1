package com.rwbase.common.enu;

public enum EServerStatus {
	CLOSE		(-1, "关闭"),
	MAINTAIN	(1, "维护"),
	NORMAL		(2, "正常"),
	HIT			(3, "火爆"),
	NEW			(4, "新服"),
	FIRST_INFORM	(5, "首次通知"),
	;
	private int statusId;
	private String statusDes;
	private EServerStatus(int statusId, String statusDes){
		this.statusId = statusId;
		this.statusDes = statusDes;
	}
	
	public int getStatusId() {
		return statusId;
	}
	
	public String getStatusDes() {
		return statusDes;
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
}
