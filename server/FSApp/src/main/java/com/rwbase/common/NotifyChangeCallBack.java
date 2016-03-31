package com.rwbase.common;

import java.util.ArrayList;
import java.util.List;

import com.common.Action;

/**
 * 不是线程安全的！
 * @author franky
 *
 */
public class NotifyChangeCallBack implements INotifyChange {
	private List<Action> callbackList = new ArrayList<Action>();
	
	@Override
	public void regChangeCallBack(Action callBack){
		callbackList.add(callBack);
	}
	protected int delayNotifyCount = 0;
	public void delayNotify(){
		delayNotifyCount++;
	}
	
	public void checkDelayNotify(){
		if (delayNotifyCount > 0){
			delayNotifyCount = 0;
			notifyChange();
		}
	}
	
	protected void notifyChange(){
		for (Action action : callbackList) {
			action.doAction();
		}
	}
}
