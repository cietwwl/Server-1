package com.rwbase.common.timer;

/**
 * 
 * 在這裡註冊每分鐘執行的任務（整分，即11:01:00、11:02:00這種）
 * 
 * @author CHEN.P
 *
 */
public enum FSPlayerMinuteTaskType {

	;
	private int _type; // 類型：必須是唯一
	private Class<? extends IPlayerOperable> _classOfTask; // 類對象
	
	private FSPlayerMinuteTaskType(int pType, Class<? extends IPlayerOperable> pClassOfTask) {
		this._type = pType;
		this._classOfTask = pClassOfTask;
	}
	
	public int getType() {
		return _type;
	}
	
	public Class<? extends IPlayerOperable> getClassOfTask() {
		return _classOfTask;
	}
}
