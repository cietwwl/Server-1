package com.rwbase.dao.task;

/**
 * 临时增加一个枚举表示任务完成类型
 * 减少代码中直接用1和2
 * @author Jamaz
 *
 */
public enum DailyFinishType {

	SINGLE(1), 
	MULTIPLE(2)
	;
	private int type;

	DailyFinishType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}
	
}
