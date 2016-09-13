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
			System.out.println("role level change$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		}
		if(oldRecord.getExp() != currentRecord.getExp()){
			System.out.println("role exp change######################################");
		}
		if(oldRecord.getVip() != currentRecord.getVip()){
			System.out.println("role vip change @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		}
	}


}
