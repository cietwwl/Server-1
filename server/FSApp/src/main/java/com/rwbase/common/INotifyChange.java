package com.rwbase.common;

import com.common.Action;

public interface INotifyChange {

	public void regChangeCallBack(Action callBack);

	public void delayNotify();

	public void checkDelayNotify();

}