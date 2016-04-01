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
	
	private int delayNotifyCount = 0;
	public void delayNotify(){
		delayNotifyCount++;
	}
	
	private boolean inChecking = false;
	public void checkDelayNotify(){
		if (!inChecking && delayNotifyCount > 0){
			delayNotifyCount = 0;
			inChecking = true;
			notifyChange();
			inChecking = false;
			delayNotifyCount = 0;//clear even when notification occurs
		}
	}
	
	private void notifyChange(){
		for (Action action : callbackList) {
			action.doAction();
		}
	}
}
