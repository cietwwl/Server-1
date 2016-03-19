package com.rwbase.dao.fresherActivity.pojo;

import com.rwbase.common.enu.eActivityType;

/**
 * 开服活动对象的对外接口
 * @author lida
 *
 */
public interface FresherActivityItemIF {
	public int getCfgId();
	public long getStartTime();
	public long getEndTime();
	public boolean isFinish();
	public boolean isGiftTaken();
	public boolean isClosed();
	public String getCurrentValue();
}
