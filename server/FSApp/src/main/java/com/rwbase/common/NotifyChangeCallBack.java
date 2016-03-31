package com.rwbase.common;

import java.util.ArrayList;
import java.util.List;

import com.common.Action;

public class NotifyChangeCallBack implements INotifyChange {
	private List<Action> callbackList = new ArrayList<Action>();
	/* (non-Javadoc)
	 * @see com.rwbase.common.INotifyChange#regChangeCallBack(com.common.Action)
	 */
	@Override
	public void regChangeCallBack(Action callBack){
		callbackList.add(callBack);
	}
	
	protected void notifyChange(){
		for (Action action : callbackList) {
			action.doAction();
		}
	}
}
