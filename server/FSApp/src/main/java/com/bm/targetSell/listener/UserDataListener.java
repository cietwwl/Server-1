package com.bm.targetSell.listener;

import com.rw.fsutil.dao.cache.trace.SignleChangedEvent;
import com.rw.fsutil.dao.cache.trace.SingleChangedListener;
import com.rwbase.dao.user.User;

/**
 * 玩家数据变化监听器，这里监听exp,vip, level
 * @author Alex
 * 2016年9月13日 下午3:56:55
 */
public class UserDataListener implements SingleChangedListener<User>{

	@Override
	public void notifyDataChanged(SignleChangedEvent<User> event) {
		User oldRecord = event.getOldRecord();
		User currentRecord = event.getCurrentRecord();
		if(oldRecord.getLevel() != currentRecord.getLevel()){
			System.err.println("role level change$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$  old level:" + oldRecord.getLevel() 
					+", new level:" + currentRecord.getLevel());
		}
		if(oldRecord.getExp() != currentRecord.getExp()){
			System.err.println("role exp change###################################### old exp:" + oldRecord.getExp()
					+ ",new exp"+ currentRecord.getExp());
		}
		if(oldRecord.getVip() != currentRecord.getVip()){
			System.err.println("role vip change @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ old vip:" + oldRecord.getVip() 
					+ ",new vip:" + currentRecord.getVip());
		}
	}


}
