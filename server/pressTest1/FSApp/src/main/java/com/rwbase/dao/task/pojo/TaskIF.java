package com.rwbase.dao.task.pojo;

import com.rwbase.common.enu.eTaskFinishDef;

public interface TaskIF {
	public int getId();
	public int getDrawState();
	public int getCurProgress();
	public int getTotalProgress();
	public eTaskFinishDef getFinishType();
	public int getSuperType();
}
